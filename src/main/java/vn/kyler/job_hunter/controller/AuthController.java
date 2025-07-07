package vn.kyler.job_hunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.domain.request.ReqLoginDTO;
import vn.kyler.job_hunter.domain.response.ResLoginDTO;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.service.UserService;
import vn.kyler.job_hunter.service.exception.NoRefreshTokenInCookieException;
import vn.kyler.job_hunter.util.SecurityUtil;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        @Value("${kyler.jwt.access-token-validity-in-seconds}")
        private long accessTokenExpiration;

        @Value("${kyler.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);// import org.springframework.security.core.Authentication;

                // nạp thông tin (nếu xử lý thành công) vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO restLoginDTO = new ResLoginDTO();
                User user = this.userService.handleGetUserByEmail(loginDTO.getUsername());
                if (user != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(),
                                        user.getName(),user.getRole());
                        restLoginDTO.setUserLogin(userLogin);
                }

                // Create access token
                String accessToken = this.securityUtil.createAccessToken(authentication.getName(), restLoginDTO);
                restLoginDTO.setAccessToken(accessToken);

                // Create refresh token
                String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), restLoginDTO);

                // update user refresh token
                this.userService.handleUpdateUserToken(authentication.getName(), refreshToken);

                // set cookies
                ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .domain("localhost").build();

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(restLoginDTO);
        }

        @GetMapping("/auth/account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User user = this.userService.handleGetUserByEmail(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
                if (user != null) {
                        userLogin = new ResLoginDTO.UserLogin(user.getId(),
                                        user.getEmail(),
                                        user.getName(),user.getRole());
                        userGetAccount.setUser(userLogin);
                }
                return ResponseEntity.status(HttpStatus.OK).body(userGetAccount);
        }

        @GetMapping("/auth/refresh")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refreshToken", defaultValue = "refreshTokenDefault") String refreshToken)
                        throws UsernameNotFoundException, NoRefreshTokenInCookieException {

                // Check if the refresh token is default value
                if (refreshToken.equals("refreshTokenDefault")) {
                        throw new NoRefreshTokenInCookieException("Refresh token is not found in cookies");
                }
                // Check if the refresh token is valid
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
                String email = decodedToken.getSubject();

                // Check if the user exists with the given email and refresh token in the
                // database
                User user = this.userService.handleGetUserByRefreshTokenAndEmail(refreshToken, email);
                if (user == null) {
                        throw new UsernameNotFoundException("Refresh token is invalid or user not found");
                }

                // Check if refresh token and user email match
                ResLoginDTO restLoginDTO = new ResLoginDTO();
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(),
                        user.getName(),user.getRole());
                restLoginDTO.setUserLogin(userLogin);

                // Create access token
                String accessToken = this.securityUtil.createAccessToken(email, restLoginDTO);
                restLoginDTO.setAccessToken(accessToken);

                // Create refresh token
                String newRefreshToken = this.securityUtil.createRefreshToken(email, restLoginDTO);

                // update user refresh token
                this.userService.handleUpdateUserToken(email, newRefreshToken);

                // set cookies
                ResponseCookie responseCookie = ResponseCookie.from("refreshToken",
                                newRefreshToken).httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .domain("localhost").build();

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(restLoginDTO);
        }

        @PostMapping("/auth/logout")
        public ResponseEntity<?> logout() throws UsernameNotFoundException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (email == null) {
                        throw new UsernameNotFoundException("Access token is invalid or user not found");
                }
                this.userService.handleUpdateUserToken(email, null);

                // Delete cookies
                ResponseCookie responseCookie = ResponseCookie.from("refreshToken",
                                null).httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .domain("localhost").build();

                RestResponse<String> restResponse = new RestResponse<>();
                restResponse.setStatusCode(HttpStatus.OK.value());
                restResponse.setMessage("Logout successfully");

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(restResponse);
        }
}

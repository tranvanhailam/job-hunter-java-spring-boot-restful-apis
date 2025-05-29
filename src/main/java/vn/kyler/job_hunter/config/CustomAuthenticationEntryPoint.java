package vn.kyler.job_hunter.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.kyler.job_hunter.domain.RestResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");
        RestResponse<Object> restResponse = new RestResponse<>();

        if(authException instanceof InsufficientAuthenticationException) {
            restResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        } else {
            restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }

        // String errorMessage =
        // Optional.ofNullable(authException.getCause()).map(Throwable::getMessage)
        // .orElse(authException.getMessage());
        // restResponse.setError(errorMessage);

        restResponse.setError(authException.getMessage());
        restResponse.setMessage("Token is invalid or expired");

        // ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restResponse);
        mapper.writeValue(response.getWriter(), restResponse);
    }

}

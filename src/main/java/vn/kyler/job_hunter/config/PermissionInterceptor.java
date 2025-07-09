package vn.kyler.job_hunter.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.kyler.job_hunter.domain.Permission;
import vn.kyler.job_hunter.domain.Role;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.service.UserService;
import vn.kyler.job_hunter.service.exception.AccessDenyException;
import vn.kyler.job_hunter.util.SecurityUtil;

import java.util.List;
import java.util.stream.Collectors;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email == null && email.isEmpty()) {
            throw new UsernameNotFoundException("Access token is invalid or user not found");
        }
        User user = this.userService.handleGetUserByEmail(email);
        Role role = user.getRole();
        if (role != null) {
            List<Permission> permissions = role.getPermissions();
            boolean allowAccess = permissions.stream().anyMatch(permission -> {
                String p1 = permission.getApiPath();
                String p2 = permission.getMethod();
                return permission.getApiPath().equals(path)
                        && permission.getMethod().equals(httpMethod);
            });
            System.out.println(">>> allowAccess= " + allowAccess);
            if (!allowAccess) {
                throw new AccessDenyException("No access to this endpoint");
            }
        } else throw new AccessDenyException("No access to this endpoint");
        return true;
    }

}
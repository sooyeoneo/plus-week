package com.example.demo.interceptor;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.dto.Authentication;
import com.example.demo.entity.Role;
import com.example.demo.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws UnauthorizedException {

        // 세션이 존재하는지 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "세션이 만료되었거나 존재하지 않습니다.");
        }

        // 사용자 인증 정보 확인
        Authentication authentication = (Authentication) session.getAttribute(GlobalConstants.USER_AUTH);
        if (authentication == null) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        // 권한 확인: ADMIN 권한만 접근 허용
        Role role = authentication.getRole();
        if (role != Role.ADMIN) {
            throw new UnauthorizedException(HttpStatus.FORBIDDEN, "ADMIN 권한이 필요합니다.");
        }

        // ADMIN 권한 확인 완료 → 요청 계속 진행
        return true;
    }
}

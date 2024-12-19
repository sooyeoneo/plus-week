package com.example.demo.config;

import com.example.demo.entity.Role;
import com.example.demo.filter.AuthFilter;
import com.example.demo.filter.RoleFilter;
import com.example.demo.interceptor.AdminRoleInterceptor;
import com.example.demo.interceptor.AuthInterceptor;
import com.example.demo.interceptor.UserRoleInterceptor;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // TODO: 2. 인가에 대한 이해
    private static final String[] AUTH_REQUIRED_PATH_PATTERNS = {"/users/logout", "/admins/*", "/items/*"};
    private static final String[] USER_ROLE_REQUIRED_PATH_PATTERNS = {"/reservations/*"};
    private static final String[] ADMIN_ROLE_REQUIRED_PATH_PATTERNS = {"/admins/*"};

    private final AuthInterceptor authInterceptor;
    private final UserRoleInterceptor userRoleInterceptor;
    private final AdminRoleInterceptor adminRoleInterceptor;

    /*
     * Interceptor 는 order 값을 기준으로 실행 순서 결정
     * Ordered.HIGHEST_PRECEDENCE 는 가장 높은 우선순위(값이 낮음)를 의미 (0)
     * 값이 낮을수록 먼저 실행, 높을수록 나중에 실행 (+1 순서를 1만큼 미룸)
     * 실행 순서 - authInterceptor > adminRoleInterceptor > userRoleInterceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 인증 확인 인터셉터 : 모든 요청에 대해 로그인 여부 확인
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(AUTH_REQUIRED_PATH_PATTERNS)
                .order(Ordered.HIGHEST_PRECEDENCE);

        // USER 권한 확인 인터셉터
        registry.addInterceptor(userRoleInterceptor)
                .addPathPatterns(USER_ROLE_REQUIRED_PATH_PATTERNS)
                .order(Ordered.HIGHEST_PRECEDENCE + 2);

        // ADMIN 권한 확인 인터셉터 : 인증 후 바로 실행
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns(ADMIN_ROLE_REQUIRED_PATH_PATTERNS)
                .order(Ordered.HIGHEST_PRECEDENCE + 1); // authInterceptor 보다 나중에 실행 되도록 + 1
    }

    @Bean
    public FilterRegistrationBean authFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new AuthFilter());
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.addUrlPatterns(AUTH_REQUIRED_PATH_PATTERNS);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean userRoleFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RoleFilter(Role.USER));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        filterRegistrationBean.addUrlPatterns(USER_ROLE_REQUIRED_PATH_PATTERNS);
        return filterRegistrationBean;
    }
}
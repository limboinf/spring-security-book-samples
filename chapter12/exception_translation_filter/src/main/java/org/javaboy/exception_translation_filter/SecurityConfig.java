package org.javaboy.exception_translation_filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 异常处理
 * <br>
 * 主要两方面： AuthenticationException 认证异常处理、AccessDeniedException 权限异常处理
 * 在ExceptionTranslationFilter过滤器中分别对AuthenticationException和AccessDeniedException类型的异常进行处理，
 * 如果异常不是这两种类型的，则将异常抛出交给上层容器处理。
 *
 * AuthenticationException和AccessDeniedException两种不同类型的异常，分别对应了AuthenticationEntryPoint和AccessDeniedHandler两种不同的异常处理器。
 * 如果系统提供的异常处理器不能满足需求，开发者也可以自定义异常处理器，并且可以为不同的请求指定不同的异常处理器
 *
 * <ul>
 *     <li>defaultAuthenticationEntryPointFor 配置自定义认证异常</li>
 *     <li>defaultAccessDeniedHandlerFor 配置自定义权限异常</li>
 * </ul>
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AntPathRequestMatcher matcher1 = new AntPathRequestMatcher("/qq/**");
        AntPathRequestMatcher matcher2 = new AntPathRequestMatcher("/wx/**");

        http.authorizeRequests()
                .antMatchers("/wx/**").hasRole("wx")
                .antMatchers("/qq/**").hasRole("qq")
                .anyRequest().authenticated()
                .and()
                // exceptionHandling() 调用ExceptionHandlingConfigurer配置 ExceptionTranslationFilter
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor((req, resp, e) -> {
                    resp.setContentType("text/html;charset=utf-8");
                    resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                    resp.getWriter().write("请登录，QQ 用户");
                }, matcher1)
                .defaultAuthenticationEntryPointFor((req, resp, e) -> {
                    resp.setContentType("text/html;charset=utf-8");
                    resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                    resp.getWriter().write("请登录，WX 用户");
                }, matcher2)
                .defaultAccessDeniedHandlerFor((req, resp, e) -> {
                    resp.setContentType("text/html;charset=utf-8");
                    resp.setStatus(HttpStatus.FORBIDDEN.value());
                    resp.getWriter().write("权限不足，QQ 用户");
                }, matcher1)
                .defaultAccessDeniedHandlerFor((req, resp, e) -> {
                    resp.setContentType("text/html;charset=utf-8");
                    resp.setStatus(HttpStatus.FORBIDDEN.value());
                    resp.getWriter().write("权限不足，WX 用户");
                }, matcher2)
                .and()
                .formLogin()
                .and()
                .csrf().disable();

    }
}

package org.javaboy.sessionmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * session manager
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaboy")
                .password("{noop}123")
                .roles("admin");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .csrf().disable()
                .sessionManagement()    // 开启会话配置
                .maximumSessions(1)
//                .maxSessionsPreventsLogin(true) // 默认是被挤下线，还有一种是禁止后来者登录，即一旦当前用户登录成功，后来者无法再次使用相同的用户登录(设置true）
//                .expiredUrl("/login")  // 自定义会话销毁后行为，如登陆
                // 对于前后端分离的项目则自定义会话销毁后行为提示
                .expiredSessionStrategy(event -> {
                    HttpServletResponse response = event.getResponse();
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    Map<String, Object> res = new HashMap<>();
                    res.put("status", 500);
                    res.put("msg", "当前会话已失效，请重新登录");
                    String s = new ObjectMapper().writeValueAsString(res);
                    response.getWriter().print(s);
                    response.flushBuffer();
                })
        ;    // 设置会话并发数为1（也就是用户只能在一个设备登录）
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .and()
//                .csrf()
//                .disable()
//                .sessionManagement()
//                .sessionFixation()
//                .none()
//                .maximumSessions(1)
//                .expiredSessionStrategy(event -> {
//                    HttpServletResponse response = event.getResponse();
//                    response.setContentType("application/json;charset=utf-8");
//                    Map<String, Object> result = new HashMap<>();
//                    result.put("status", 500);
//                    result.put("msg", "当前会话已经失效，请重新登录");
//                    String s = new ObjectMapper().writeValueAsString(result);
//                    response.getWriter().print(s);
//                    response.flushBuffer();
//                });
//    }

    /**
     * http会话事件发布者
     * <br>
     * Spring Security中通过一个Map集合来维护当前的HttpSession记录，实现会话的并发管理。
     * 当用户登录成功时，就向集合中添加一条HttpSession记录；当会话销毁时，就从集合中移除一条HttpSession记录。
     * HttpSessionEventPublisher实现了HttpSessionListener接口，可以监听到HttpSession的创建和销毁事件，并将HttpSession的创建／销毁事件发布出去，
     * 这样，当有HttpSession销毁时，Spring Security就可以感知到该事件了
     *
     * @return {@link HttpSessionEventPublisher}
     */
    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}

package org.javaboy.formlogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/index.html")
                .successHandler(new MyAuthenticationSuccessHandler())
                .failureHandler(new MyAuthenticationFailureHandler())
                .usernameParameter("uname")
                .passwordParameter("passwd")
                .permitAll()
                .and()
                .logout()   // logout url默认： /logout
                .logoutRequestMatcher(new OrRequestMatcher(  // 配置多个注销请求路径
                        new AntPathRequestMatcher("/logout1", "GET"),
                        new AntPathRequestMatcher("/logout2", "POST")
                ))
//                .logoutSuccessUrl("")
                .and()
                .csrf().disable();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/mylogin.html")
//                .loginProcessingUrl("/doLogin")
//                .defaultSuccessUrl("/index.html")
//                .failureHandler(new MyAuthenticationFailureHandler())
//                .usernameParameter("uname")
//                .passwordParameter("passwd")
//                .permitAll()
//                .and()
//                .logout()
//                .logoutRequestMatcher(new OrRequestMatcher(
//                        new AntPathRequestMatcher("/logout1", "GET"),
//                        new AntPathRequestMatcher("/logout2", "POST")))
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .defaultLogoutSuccessHandlerFor((req,resp,auth)->{
//                    resp.setContentType("application/json;charset=utf-8");
//                    Map<String, Object> result = new HashMap<>();
//                    result.put("status", 200);
//                    result.put("msg", "使用 logout1 注销成功!");
//                    ObjectMapper om = new ObjectMapper();
//                    String s = om.writeValueAsString(result);
//                    resp.getWriter().write(s);
//                },new AntPathRequestMatcher("/logout1","GET"))
//                .defaultLogoutSuccessHandlerFor((req,resp,auth)->{
//                    resp.setContentType("application/json;charset=utf-8");
//                    Map<String, Object> result = new HashMap<>();
//                    result.put("status", 200);
//                    result.put("msg", "使用 logout2 注销成功!");
//                    ObjectMapper om = new ObjectMapper();
//                    String s = om.writeValueAsString(result);
//                    resp.getWriter().write(s);
//                },new AntPathRequestMatcher("/logout2","POST"))
//                .and()
//                .csrf().disable();
//    }

}

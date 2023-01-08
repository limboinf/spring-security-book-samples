package org.javaboy.objectpostprocessor;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**

 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                // ObjectPostProcessor 对象后置处理器
                .withObjectPostProcessor(new ObjectPostProcessor<UsernamePasswordAuthenticationFilter>() {
                    @Override
                    public <O extends UsernamePasswordAuthenticationFilter> O postProcess(O object) {
                        System.out.println("Object post processor ....");
                        object.setUsernameParameter("username");
                        object.setPasswordParameter("password");
                        object.setAuthenticationSuccessHandler((req,resp,auth)->{
                            System.out.println("登陆成功 ....");
                            resp.getWriter().write("hello, login successful!!!");
                        });
                        return object;
                    }
                })
                .and()
                .csrf().disable();
    }
}

package org.javaboy.formlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 配置多数据源
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Primary
    UserDetailsService us1() {
        return new InMemoryUserDetailsManager(User.builder().username("javaboy").password("{noop}123").roles("admin").build());
    }

    @Bean
    UserDetailsService us2() {
        return new InMemoryUserDetailsManager(User.builder().username("sang").password("{noop}123").roles("user").build());
    }

    /**
     * 身份验证管理器
     * <p>
     * AuthenticationManager 认证管理器，只有一个 authenticate 方法
     * 该方法传入Authentication参数只有用户名／密码等简单的属性，如果认证成功，返回的Authentication的属性会得到完全填充，包括用户所具备的角色信息。
     * ProviderManager是其最常用的实现类
     *
     * @return {@link AuthenticationManager}
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() {
        // AuthenticationProvider 针对不同的身份类型执行具体的身份认证
        // 其实现类常见的：DaoAuthenticationProvide(支持用户名密码）,RememberMeAuthenticationProvider(支持'记住我'的认证)
        DaoAuthenticationProvider dao1 = new DaoAuthenticationProvider();
        dao1.setUserDetailsService(us1());

        DaoAuthenticationProvider dao2 = new DaoAuthenticationProvider();
        dao2.setUserDetailsService(us2());

        // ProviderManager 用于管理多个认证方式(AuthenticationProvider)
        ProviderManager manager = new ProviderManager(dao1, dao2);
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/mylogin.html")
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/index.html")
                .usernameParameter("uname")
                .passwordParameter("passwd")
                .permitAll()
                .and()
                .csrf().disable();
    }
}

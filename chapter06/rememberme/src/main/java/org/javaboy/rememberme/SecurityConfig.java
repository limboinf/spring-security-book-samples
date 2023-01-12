package org.javaboy.rememberme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Remember me
 * <br>
 * TokenBasedRememberMeServices 实现
 *
 * 这里所说的RememberMe是一种服务器端的行为。传统的登录方式基于Session会话，一旦用户关闭浏览器重新打开，就要再次登录，这样太过于烦琐。 <br>
 * 如果能有一种机制，让用户关闭并重新打开浏览器之后，还能继续保持认证状态，就会方便很多，RememberMe就是为了解决这一需求而生的。<br>
 *
 * 具体的实现思路就是通过Cookie来记录当前用户身份。<br>
 * 当用户登录成功之后，会通过一定的算法，将用户信息、时间戳等进行加密，加密完成后，通过响应头带回前端存储在Cookie中，<br>
 * 当浏览器关闭之后重新打开，如果再次访问该网站，会自动将Cookie中的信息发送给服务器，服务器对Cookie中的信息进行校验分析，进而确定出用户的身份，Cookie中所保存的用户信息也是有时效的，例如三天、一周等。
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaboy")
                .password("123")
                .roles("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 勾选remember me 后，在响应头中给出了一个remember-me字符串(cookie: remember-me)。
        // 以后所有请求的请求头Cookie字段，都会自动携带上这个令牌，服务端利用该令牌可以校验用户身份是否合法。
        // 这种方式安全隐患很大，一旦remember-me令牌泄漏，恶意用户就可以拿着这个令牌去随意访问系统资源。持久化令牌和二次校验可以在一定程度上降低该问题带来的风险。
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                // 调用了HttpSecurity中的rememberMe方法并配置了一个key，该方法最终会向过滤器链中添加RememberMeAuthenticationFilter过滤器
                .rememberMe()   // 引入了配置类RememberMeConfigurer
                .key("javaboy")
                .and()
                .csrf().disable();
    }
}
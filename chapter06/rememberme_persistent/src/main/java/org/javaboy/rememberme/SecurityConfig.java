package org.javaboy.rememberme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import javax.sql.DataSource;

/**
 * 持久化令牌实现 remember me
 * <br>
 * PersistentTokenBasedRememberMeServices 的实现
 * security06.sql -> 持久化数据对象 PersistentRememberMeToken中
 *
 * 原理：
 * 持久化令牌在普通令牌的基础上，新增了series和token两个校验参数 <br>
 * <ul>
 *     <li>当使用用户名／密码的方式登录时，series才会自动更新</li>
 *     <li>而一旦有了新的会话，token就会重新生成</li>
 * </ul>
 * 如果令牌被盗用，一旦对方基于RememberMe登录成功后，会生成新token，你自己的登录令牌就会失效，这样就能及时发现账户泄漏并作出处理，比如清除自动登录令牌、通知用户账户泄漏等。
 *
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    /**
     * Spring Security 持久化令牌主要JdbcTokenRepositoryImpl来实现
     */
    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

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

//    /**
//     * 配置
//     * 登录后会在数据库 persistent_logins表中插入一条记录
//     * 如果关闭浏览器，再次打开则不需要登录，但 persistent_logins 表中token会发生变化
//     * logout则记录被清除
//     *
//     * @param http http
//     * @throws Exception 异常
//     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .and()
//                .rememberMe()
//                .tokenRepository(jdbcTokenRepository())
//                .and()
//                .csrf().disable();
//    }

    /**
     * 二次校验
     * <br>
     * 二次校验就是将系统中的资源分为敏感的和不敏感的，如果用户使用了RememberMe的方式登录，则访问敏感资源时会自动跳转到登录页面，要求用户重新登录
     *
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 登录后，访问 /hello, /admin OK，访问/rememberme 会抛403权限异常
        // 此时关闭浏览器再重新打开, 访问 /hello, /rememberme OK, 访问/admin 则系统会自动跳转登录页面
        http.authorizeRequests()
                .antMatchers("/admin").fullyAuthenticated()     // /admin 认证后才能访问，但必须通过用户名、密码的方式认证
                .antMatchers("/rememberme").rememberMe()    // /rememberme 认证后才能访问，但必须是通过rememberme方式认证
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .rememberMe()
//                .key("javaboy")
                .tokenRepository(jdbcTokenRepository())
                .and()
                .csrf().disable();
    }
}
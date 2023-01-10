package org.javaboy.json_login;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义登录过滤器-实现json格式登录
 * <br>
 * Spring Security默认是表单登录（k/v), 如果要实现json的需要自定义登录过滤器
 *
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        // 如果是json则走自定义逻辑
        if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)
                || request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE)) {

            Map<String, String> userInfo;
            try {
                // 分别从请求中提取用户名及密码并构造 UsernamePasswordAuthenticationToken 对象
                userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String username = userInfo.get(getUsernameParameter());
                String password = userInfo.get(getPasswordParameter());
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                // setDetails
                setDetails(request, authRequest);
                // 然后调用 authenticate 认证
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 如果非json则直接调用基类的attemptAuthentication认证方法即可
        return super.attemptAuthentication(request, response);
    }
}

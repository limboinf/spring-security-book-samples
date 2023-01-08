package org.javaboy.formlogin.config;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * kaptcha验证码身份验证提供者
 *
 */
public class KaptchaAuthenticationProvider extends DaoAuthenticationProvider {

    /**
     * 进行身份验证
     *
     * 从 RequestContextHolder 获取当前请求
     * 进而获取到验证码参数和存储在HttpSession中的验证码文本进行比较，比较通过则继续执行父类的authenticate方法，比较不通过，就抛出异常
     *
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String kaptcha = req.getParameter("kaptcha");
        String sessionKaptcha = (String) req.getSession().getAttribute("kaptcha");
        if (kaptcha != null && sessionKaptcha != null && kaptcha.equalsIgnoreCase(sessionKaptcha)) {
            return super.authenticate(authentication);
        }
        throw new AuthenticationServiceException("验证码输入错误");
    }
}
package org.javaboy.based_on_url;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义权限验证表达式
 * <br>
 * 假设 userId 是偶数，username是 javaboy 才通过验证
 */
@Component
public class PermissionExpression {
    public boolean checkId(Authentication authentication, Integer userId) {
        if (authentication.isAuthenticated()) {
            return userId % 2 == 0;
        }
        return false;
    }

    public boolean check(HttpServletRequest req) {
        return "javaboy".equals(req.getParameter("username"));
    }
}

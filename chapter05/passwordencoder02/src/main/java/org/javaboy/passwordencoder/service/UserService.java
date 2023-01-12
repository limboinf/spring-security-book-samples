package org.javaboy.passwordencoder.service;

import org.javaboy.passwordencoder.mapper.UserMapper;
import org.javaboy.passwordencoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * UserService实现了UserDetailsPasswordService该接口中的updatePassword方法。
 * 当系统判断密码加密方案需要升级的时候，就会自动调用updatePassword方法去修改数据库中的密码。
 */
@Configuration
public class UserService implements UserDetailsService, UserDetailsPasswordService {
    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Integer result = userMapper.updatePassword(user.getUsername(), newPassword);
        if (result == 1) {
            ((User) user).setPassword(newPassword);
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.loadUserByUsername(username);
    }
}

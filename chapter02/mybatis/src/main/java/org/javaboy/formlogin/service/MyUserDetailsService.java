package org.javaboy.formlogin.service;

import lombok.extern.slf4j.Slf4j;
import org.javaboy.formlogin.mapper.UserMapper;
import org.javaboy.formlogin.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * 自定义用户详细信息服务 实现 UserDetailsService接口
 */
@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("开始认证 username: {} ...", username);
        User user = userMapper.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        user.setRoles(userMapper.getRolesByUid(user.getId()));
        log.info("认证成功 user: {} ...", user);
        return user;
    }
}

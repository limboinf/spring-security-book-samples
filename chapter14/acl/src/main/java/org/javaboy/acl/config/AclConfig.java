package org.javaboy.acl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

/**
 * ACL配置类
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AclConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        // 指定一个角色能修改认证主体的权限(3种权限：修改ACL的owner、修改ACL的审计信息、修改ACE本身)
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        // PermissionGrantingStrategy的isGranted方法是真正的权限对比方法
        // 指定权限对比时审计日志ConsoleAuditLogger 仅控制台打印
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    // ~ Acl Cache
    // ===================================================================================================
    @Bean
    public AclCache aclCache() {
        // 引入了Ehcache做缓存，避免数据库压力
        return new EhCacheBasedAclCache(aclEhCacheFactoryBean().getObject(), permissionGrantingStrategy(), aclAuthorizationStrategy());
    }

    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean() {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        return new EhCacheManagerFactoryBean();
    }

    // ===================================================================================================

    @Bean
    public LookupStrategy lookupStrategy() {
        // LookupStrategy可以通过ObjectIdentity解析出对应的ACL
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
    }

    @Bean
    public JdbcMutableAclService aclService() {
        // AclService接口中主要定义了一些解析Acl对象的方法
        JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
        // for MySQL ONLY, 解决 PROCEDURE acls.identity does not exist 问题
        // https://stackoverflow.com/questions/54859029/spring-security-acl-object
        jdbcMutableAclService.setClassIdentityQuery("SELECT @@IDENTITY");
        jdbcMutableAclService.setSidIdentityQuery("SELECT @@IDENTITY");
        return jdbcMutableAclService;
    }

    @Bean
    PermissionEvaluator permissionEvaluator() {
        // PermissionEvaluator是为表达式hasPermission提供支持的
        return new AclPermissionEvaluator(aclService());
    }
}
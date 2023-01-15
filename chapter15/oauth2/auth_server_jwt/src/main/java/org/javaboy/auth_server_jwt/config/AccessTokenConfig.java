package org.javaboy.auth_server_jwt.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.HashMap;
import java.util.Map;

/**
 非对称加密有两种使用场景：
 · 加密场景：公钥负责加密，私钥负责解密。
 · 签名场景：私钥负责签名，公钥负责验证。

 在JWT中使用的非对称加密属于签名场景
 */
@Configuration
public class AccessTokenConfig {

    @Bean
    TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * jwt访问令牌转换器
     * 配置一下签名以及验证者即可
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {

        // 签名（私钥）
        RsaSigner signer = new RsaSigner(KeyConfig.getSignerKey());

        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigner(signer);
        // 验证（公钥）
        converter.setVerifier(new RsaVerifier(KeyConfig.getVerifierKey()));
        return converter;
    }

    /**
     * 提供一个包含公钥的JWKSet对象，该对象接下来要暴露给资源服务器。
     */
    @Bean
    public JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder(KeyConfig.getVerifierKey())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256);
        return new JWKSet(builder.build());
    }
}

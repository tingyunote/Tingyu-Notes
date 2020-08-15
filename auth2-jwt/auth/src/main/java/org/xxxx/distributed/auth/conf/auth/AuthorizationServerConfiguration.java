package org.bifu.distributed.auth.conf.auth;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.fastjson.JSONObject;
import org.bifu.distributed.auth.constant.AuthContants;
import org.bifu.distributed.auth.dto.SecurityUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * 认证授权服务端
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(AuthorizationServerConfiguration.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(this.authenticationManager);
        endpoints.accessTokenConverter(accessTokenConverter());
        endpoints.tokenStore(tokenStore());
        endpoints.reuseRefreshTokens(false);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')");
        oauthServer.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // jdbc方式 获取配置信息，oauth_client_details给谁发令牌，有效时间、授权模式、刷新令牌等等
        clients.withClientDetails(clientDetails());
    }

    /**
     * token converter
     * 
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
            /***
             * 重写增强token方法,用于自定义一些token返回的信息
             */
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                    OAuth2Authentication authentication) {
            	SecurityUserDTO securityUserDTO =
                      (SecurityUserDTO) authentication.getUserAuthentication().getPrincipal(); //获取当前用户信息
                logger.info("重写增强token方法= {}", JSONObject.toJSONString(securityUserDTO));
            	final Map<String, Object> additionalInformation = new HashMap<>(16);
            	additionalInformation.put("userId", securityUserDTO.getId());
                // 将用户信息添加到token额外信息中
            	((DefaultOAuth2AccessToken) accessToken)
            			.setAdditionalInformation(additionalInformation);
                OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
                return enhancedToken;
            }
        };
        // 非对称加密
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(AuthContants.JKS_FILE),
                		AuthContants.JKS_PASSWORD.toCharArray());
        accessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(AuthContants.JKS_NAME));
        logger.info("重写增强token方法结束");
        return accessTokenConverter;
    }

    /**
     * 定义clientDetails存储的方式-》Jdbc的方式，注入DataSource
     *
     * @return
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * token store
     * 返回jwt格式token
     * @param
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        TokenStore tokenStore = new JwtTokenStore(accessTokenConverter());
        return tokenStore;
    }

}

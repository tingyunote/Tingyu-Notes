package org.bifu.user.center.config.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 认证授权资源端
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Autowired
	private MyAccessDeniedHandler myAccessDeniedHandler;
	
	@Autowired
	private Auth2ResponseExceptionTranslator auth2ResponseExceptionTranslator;
	
	@Autowired
	private SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId("client2");
		resources.tokenServices(defaultTokenServices());
		// 定义异常转换类生效
		AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		((OAuth2AuthenticationEntryPoint) authenticationEntryPoint)
				.setExceptionTranslator(this.auth2ResponseExceptionTranslator);
		resources.authenticationEntryPoint(authenticationEntryPoint);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		
//		// 放行路径在这写
		http
		//自定义校验拦截器----需要验证用户短信验证或者google验证的接口
//		.addFilterAfter(new VerifyFilter(new AntPathRequestMatcher("/**/detection/**")),WebAsyncManagerIntegrationFilter.class)
		
		
		
		.csrf().disable().exceptionHandling().authenticationEntryPoint(this.securityAuthenticationEntryPoint)
		.accessDeniedHandler(myAccessDeniedHandler).and().authorizeRequests()
		.antMatchers("/swagger-resources/**", "/v2/**", "/swagger/**", "/swagger**", "/webjars/**",
		        "/test/**","/api/**",
				"/front/instant/**","/home/**","/system/**"
				//开发测试
				,"/strict/**"
				)
		.permitAll().anyRequest().authenticated().and().httpBasic().disable();
		// ifream的跨域设置
		http.headers().frameOptions().sameOrigin();
	}

//	private static class OauthRequestedMatcher implements RequestMatcher {
//		@Override
//		public boolean matches(HttpServletRequest request) {
//			String auth = request.getHeader("Authorization");
//			boolean haveOauth2Token = (auth != null) && auth.startsWith("Bearer");
//			boolean haveAccessToken = request.getParameter("access_token") != null;
//			return haveOauth2Token || haveAccessToken;
//		}
//	}

	// ===================================================以下代码与认证服务器一致=========================================
	/**
	 * token存储,这里使用jwt方式存储
	 * 
	 * @param accessTokenConverter
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		TokenStore tokenStore = new JwtTokenStore(accessTokenConverter());
		return tokenStore;
	}

	/**
	 * 创建一个默认的资源服务token
	 * 
	 * @return
	 */
	@Bean
	public ResourceServerTokenServices defaultTokenServices() {
		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenEnhancer(accessTokenConverter());
		defaultTokenServices.setTokenStore(tokenStore());
		return defaultTokenServices;
	}

	/**
	 * Token转换器必须与认证服务一致
	 * 
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
		};
		Resource resource = new ClassPathResource("public_key.txt");
		String publicKey = null;
		try {
			publicKey = inputStream2String(resource.getInputStream());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		accessTokenConverter.setVerifierKey(publicKey);
		return accessTokenConverter;
	}
	// ===================================================以上代码与认证服务器一致=========================================

	private String inputStream2String(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

}

package org.bifu.distributed.auth.conf.auth;

import javax.transaction.Transactional;

import org.bifu.distributed.auth.constant.AuthContants;
import org.bifu.distributed.auth.dao.UserMapper;
import org.bifu.distributed.auth.domain.User;
import org.bifu.distributed.auth.dto.SecurityUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

	@Autowired
	private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		// 配置用户来源于数据库
//		auth.userDetailsService(userDetailsService()).passwordEncoder(new MyPasswordEncoder());
		auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
//		auth.userDetailsService(userDetailsService()).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
//		auth.userDetailsService(userDetailsService()).passwordEncoder(MyPasswordEncoderFactories.createDelegatingPasswordEncoder());
	}

	/**
	 * authorizeRequests()配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
	 * formLogin()对应表单认证相关的配置
	 * logout()对应了注销相关的配置
	 * httpBasic()可以配置basic登录
	 *  配置登陆页/login并允许访问
	 * 由于使用的是JWT，我们这里不需要csrf
	 * @param http
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginProcessingUrl("/auth/login").successHandler(myAuthenticationSuccessHandler)
				.failureHandler(myAuthenticationFailureHandler)
				.and().csrf().disable()
				.sessionManagement()
				.maximumSessions(1).expiredUrl("/expiredSession");
		logger.info("test是否生成token");
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * 用户验证
	 * @return
	 */
	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			@Transactional(rollbackOn = Exception.class)
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				logger.info("登录手机号或邮箱：======"+username);
				// 查用户
				User user = userMapper.selectByPhoneOrEmail(username, username);
				if (user == null) {
					throw new UsernameNotFoundException(AuthContants.USER_NOT_EXIST);
				}
				SecurityUserDTO dto = new SecurityUserDTO();
				dto.setId(user.getId());
				dto.setUsername(username);
				dto.setPassword(user.getPassword());
				dto.setDisable(user.getDisable());
				// 创建securityUserDTO
//				SecurityUserDTO securityUserDTO = new SecurityUserDTO(user);
				return dto;
			}
		};
	}

}

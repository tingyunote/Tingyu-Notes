package org.bifu.user.center.config.security;

import org.bifu.user.center.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * token失效时的异常处理器
 *
 */
@Component
public class Auth2ResponseExceptionTranslator implements WebResponseExceptionTranslator<BusinessException> {

	@Override
	public ResponseEntity<BusinessException> translate(Exception e) throws Exception {
		Throwable throwable = e.getCause();
		if (throwable.getMessage().indexOf("Access token expired") != -1) {
			return new ResponseEntity<BusinessException>(new BusinessException(602, "用户token已过期"), HttpStatus.OK);
		}
		if (throwable instanceof InvalidTokenException) {
			return new ResponseEntity<BusinessException>(new BusinessException(601, "用户token不正确"), HttpStatus.OK);
		}
		return new ResponseEntity<BusinessException>(new BusinessException(405, "方法不被允许"),
				HttpStatus.METHOD_NOT_ALLOWED);
	}

}

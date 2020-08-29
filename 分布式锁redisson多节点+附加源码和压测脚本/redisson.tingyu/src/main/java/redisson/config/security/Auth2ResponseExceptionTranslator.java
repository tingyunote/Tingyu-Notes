package redisson.config.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;
import redisson.contants.Codes;
import redisson.contants.Datas;
import redisson.contants.Messages;
import redisson.exception.BusinessException;

@Component
public class Auth2ResponseExceptionTranslator implements WebResponseExceptionTranslator<BusinessException> {

	@Override
	public ResponseEntity<BusinessException> translate(Exception e) throws Exception {
		Throwable throwable = e.getCause();
		if (throwable.getMessage().indexOf(Datas.ACCESS_TOKEN_EXPIRED) != -1) {
			return new ResponseEntity<BusinessException>(
					new BusinessException(Codes.CODE_602, Messages.USER_TOKEN_EXPIRED), HttpStatus.OK);
		}
		if (throwable instanceof InvalidTokenException) {
			return new ResponseEntity<BusinessException>(
					new BusinessException(Codes.CODE_601, Messages.USER_TOKEN_ERROR), HttpStatus.OK);
		}
		return new ResponseEntity<BusinessException>(new BusinessException(Codes.CODE_405, Messages.METHED_NOT_ALLOW),
				HttpStatus.METHOD_NOT_ALLOWED);
	}

}

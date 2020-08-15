package org.bifu.distributed.auth.conf.auth;

import org.bifu.distributed.auth.util.Md5Util;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MyPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		String encPassword = Md5Util.getMD5(Md5Util.getMD5(rawPassword.toString()) + rawPassword);
		return encPassword;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encode(rawPassword).equals(encodedPassword);
	}

}

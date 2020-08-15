package org.bifu.distributed.auth.conf.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bifu.distributed.auth.constant.AuthContants;
import org.bifu.distributed.auth.dto.ResultDTO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String message = AuthContants.LOGIN_FAILE;
		int code = AuthContants.CODE_500;
		if (AuthContants.VERIFY_CODE_ERROR.equals(exception.getMessage())) {
			message = AuthContants.VERIFY_CODE_ERROR;
			code = AuthContants.CODE_603;
		}
		// 返回
		ResultDTO<?> resultDTO = new ResultDTO<>(code, message);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(JSONObject.toJSONString(resultDTO).toCharArray());
	}

}

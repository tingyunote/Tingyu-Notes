package org.bifu.user.center.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bifu.user.center.bean.vo.ResultVO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String code = exception.getMessage().substring(0, exception.getMessage().indexOf("-"));
		String msg = exception.getMessage().substring(exception.getMessage().indexOf("-")+1);
		ResultVO<?> resultDTO = new ResultVO<>(Integer.valueOf(code), msg);//token非本人
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(JSONObject.toJSONString(resultDTO).toCharArray());
	}

}

package org.bifu.user.center.config.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 没有权限时的异常处理器
 *
 */
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/json; charset=utf-8");
		response.getWriter().append("{\"code\":\"602\",\"message\":\"认证不通过\"}");
	}

}

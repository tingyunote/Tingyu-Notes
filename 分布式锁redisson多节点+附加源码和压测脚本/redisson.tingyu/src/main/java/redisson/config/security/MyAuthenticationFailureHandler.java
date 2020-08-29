package redisson.config.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import redisson.bean.vo.ResultVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String code = exception.getMessage().substring(0, exception.getMessage().indexOf("-"));
		String msg = exception.getMessage().substring(exception.getMessage().indexOf("-") + 1);
		// token非本人
		ResultVO<?> resultVO = new ResultVO<>(Integer.valueOf(code), msg);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(JSONObject.toJSONString(resultVO).toCharArray());
	}

}

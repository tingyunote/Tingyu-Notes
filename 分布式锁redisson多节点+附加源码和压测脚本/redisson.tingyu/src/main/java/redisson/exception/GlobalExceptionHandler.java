package redisson.exception;

import com.dyuproject.protostuff.ProtobufException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import redisson.bean.vo.ResultVO;
import redisson.contants.Codes;
import redisson.contants.Messages;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

	private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * 自定义异常
	 * 
	 * @param request
	 * @param exception
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = BusinessException.class)
	public Object businessHandler(HttpServletRequest request, BusinessException exception, Model model)
			throws Exception {
		return new ResultVO<String>(exception.getCode(), exception.getMessage());
	}

	/**
	 * Protobuf序列化/反序列化异常
	 * 
	 * @param request
	 * @param exception
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = ProtobufException.class)
	public Object protobufHandler(HttpServletRequest request, ProtobufException exception, Model model)
			throws Exception {
		logger.error(Messages.DESERIALIZATION_ERROR, exception);
		return new ResultVO<String>(Codes.CODE_500, exception.getMessage());
	}

	/**
	 * 参数验证异常
	 * 
	 * @param request
	 * @param exception
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = BindException.class)
	public Object validationHandler(HttpServletRequest request, BindException exception, Model model) throws Exception {
		logger.error(Messages.VALIDATION_ERROR, exception);
		List<ObjectError> allErrors = exception.getAllErrors();
		ObjectError error = allErrors.get(0);
		String defaultMessage = error.getDefaultMessage();
		return new ResultVO<String>(Codes.CODE_500, defaultMessage);
	}

}

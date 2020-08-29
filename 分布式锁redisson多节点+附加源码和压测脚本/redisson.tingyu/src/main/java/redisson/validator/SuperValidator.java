package redisson.validator;

import org.springframework.util.StringUtils;
import redisson.contants.Codes;
import redisson.contants.Messages;
import redisson.exception.BusinessException;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class SuperValidator {

	/**
	 * 验证对象每个属性都不为空值
	 * 
	 * @param t
	 */
	public static <T> void validateParams(T t) {
		try {
			for (Field f : t.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.get(t) == null || f.get(t).equals("")) {
					throw new BusinessException(Codes.CODE_500, Messages.PARAMS_NULL);
				}
			}
		} catch (BusinessException e) {
			throw new BusinessException(Codes.CODE_500, e.getMessage());
		} catch (Exception e) {
			throw new BusinessException(Codes.CODE_500, Messages.SYSTEM_EXCEPTION);
		}
	}

	/**
	 * 验证参数空值
	 * 
	 * @param map
	 */
	public static void validateParams(Object... obj) {
		for (Object o : obj) {
			if (o == null) {
				throw new BusinessException(Codes.CODE_500, Messages.PARAMS_NULL);
			}
		}
	}

	/**
	 * 验证参数空值
	 * 
	 * @param map
	 */
	public static void validateParams(String... strs) {
		for (String s : strs) {
			if (StringUtils.isEmpty(s)) {
				throw new BusinessException(Codes.CODE_500, Messages.PARAMS_NULL);
			}
		}
	}

	/**
	 * 验证数字有效性（不能小于等于0）
	 * 
	 * @param map
	 */
	public static <T> void validateNumber(Object... obj) {
		for (Object o : obj) {
			if (o instanceof BigDecimal) {
				if (new BigDecimal(o.toString()).compareTo(BigDecimal.ZERO) != 1) {
					throw new BusinessException(Codes.CODE_500, Messages.NUMBER_INVALID);
				}
			} else if (o instanceof Integer) {
				if (Integer.parseInt(o.toString()) <= 0) {
					throw new BusinessException(Codes.CODE_500, Messages.NUMBER_INVALID);
				}
			} else if (o instanceof Double) {
				if (Double.parseDouble(o.toString()) <= 0) {
					throw new BusinessException(Codes.CODE_500, Messages.NUMBER_INVALID);
				}
			} else {
				throw new BusinessException(Codes.CODE_500, Messages.NUMBER_INVALID);
			}
		}
	}

}

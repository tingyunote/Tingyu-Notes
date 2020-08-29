package redisson.util;

import redisson.contants.Codes;
import redisson.contants.Messages;
import redisson.exception.BusinessException;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AideUtil {

	public static String createUUId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * key拼接日期yyyyMMddHH
	 * 
	 * @param key
	 * @return
	 */
	public static String redisKeyAppenNowDate(String key) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		String time = df.format(calendar.getTime());
		return key + time;
	}

	public static Long getCustomTimestamp() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime dateTime = LocalDateTime.parse(formatter.format(LocalDateTime.now()), formatter);
		return Timestamp.valueOf(dateTime).getTime();
	}

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

}

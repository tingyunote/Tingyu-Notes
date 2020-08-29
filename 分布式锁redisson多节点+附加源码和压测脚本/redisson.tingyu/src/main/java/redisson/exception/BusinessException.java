package redisson.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public int code;

	private String message;

	public BusinessException() {
		super();
	}

	public BusinessException(String msg) {
		super(msg);
		this.message = msg;
	}

	public BusinessException(int code, String msg) {
		super(msg);
		this.code = code;
		this.message = msg;
	}

	public BusinessException(String msg, Throwable cause) {
		super(msg, cause);
		this.message = msg;
	}

	public BusinessException(Throwable cause, String msg) {
		super(msg, cause);
		this.message = msg;
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

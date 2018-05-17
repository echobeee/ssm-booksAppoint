package cn.echo.books.exception;

/**
 * 
 * @Description: 预约时会出现的异常
 * 如：库存不足、重复预约、其他异常
 * @CreateTime: 2018-5-15 下午12:04:56 
 * @author: bee
 */
public class AppointException extends RuntimeException {

	public AppointException() {
		super();
	}

	public AppointException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppointException(String message) {
		super(message);
	}

	public AppointException(Throwable cause) {
		super(cause);
	}
	 
}

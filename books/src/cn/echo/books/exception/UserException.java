package cn.echo.books.exception;

/**
 * 
 * @Description: 用户异常
 * 暂时：登录失败
 * @CreateTime: 2018-5-15 下午12:04:56 
 * @author: bee
 */
public class UserException extends RuntimeException {

	public UserException() {
		super();
	}

	public UserException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserException(String message) {
		super(message);
	}

	public UserException(Throwable cause) {
		super(cause);
	}
	 
}

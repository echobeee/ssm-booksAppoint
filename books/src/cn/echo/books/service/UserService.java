package cn.echo.books.service;

import cn.echo.books.pojo.Student;

public interface UserService {
	public Student login(long id, long password) throws Exception;
}

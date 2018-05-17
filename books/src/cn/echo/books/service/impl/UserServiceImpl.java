package cn.echo.books.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.echo.books.exception.UserException;
import cn.echo.books.mapper.StudentMapper;
import cn.echo.books.pojo.Student;
import cn.echo.books.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private StudentMapper studentMapper;

	@Override
	public Student login(long id, long password) throws Exception {
		Student student = studentMapper.selectByPrimaryKey(id);
		if(student == null || !student.getPassword().equals(password)){
			throw new UserException("用户名或密码错误！");
		} else {
			return student;
		}
		
	}
	
	

}

package cn.echo.books.test;

import static org.junit.Assert.*;

import java.util.List;

import oracle.net.aso.n;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.pagehelper.PageInfo;

import cn.echo.books.mapper.BookMapper;
import cn.echo.books.pojo.Appointment;
import cn.echo.books.pojo.Book;
import cn.echo.books.pojo.Student;
import cn.echo.books.service.AppointService;
import cn.echo.books.service.BookService;
import cn.echo.books.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class BookServiceImplTest {

	@Autowired
	private BookMapper bookMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private AppointService appointService;
	
	@Test
	public void test() throws Exception {
	
		Student student = userService.login(1, 123);
		System.out.println(student);
		
	}
	@Test
	public void test1() {
		Book book = new Book();
		for(int i = 0; i < 100; i++){
			book.setName("高等数学" + i);
			book.setIntrod("广义地说，初等数学之外的数学都是高等数学，也有将中学较深入的代数、几何以及简单的集合论初步balbal");
			book.setBookId(null);
			book.setNumber(10);
			bookMapper.insert(book);
		}
		
	}
	
	@Test
	public void test2() {
		PageInfo<Book> books = bookService.findAll(2, 10);
		System.out.println(books.getPages());
		for(int i = 0; i < books.getList().size(); i++){
			System.out.println(books.getList().get(i).getBookId());
		}
	}
	@Test
	public void test3() {
		Appointment appointment = appointService.appoint(3211200803L, 1000L);
		System.out.println(appointment.getAppointTime());
	}


}

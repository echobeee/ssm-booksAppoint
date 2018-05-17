package cn.echo.books.controller;


import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import sun.util.logging.resources.logging;

import com.github.pagehelper.PageInfo;

import cn.echo.books.exception.AppointException;
import cn.echo.books.exception.UserException;
import cn.echo.books.pojo.Appointment;
import cn.echo.books.pojo.Book;
import cn.echo.books.pojo.Student;
import cn.echo.books.service.AppointService;
import cn.echo.books.service.BookService;
import cn.echo.books.service.UserService;

@Controller()
public class BookController {

	//注入service
	//该bean在service.xml中注册，才能被autowired
	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AppointService appointService;
	
	@RequestMapping("inlogin")
	public String in(Model model, HttpServletRequest request){
		return "login";
	}
	
	/**
	 * 
	 * @Description: 登录功能
	 * @author: bee
	 * @CreateTime: 2018-5-15 下午2:39:45 
	 */
	@RequestMapping("login")
	public String login(Model model, HttpSession session, HttpServletResponse response, 
			@RequestParam(required=false,defaultValue="1")long id, 
			@RequestParam(required=false)long password, 
			@RequestParam(required=false,defaultValue="0")int remember) {
		try {
			Student student = userService.login(id, password);
			PageInfo<Book> bookList = bookService.findAll(1, 7);
			model.addAttribute("bookList", bookList);
			session.setAttribute("stu", student);
			Cookie cookie1 = new Cookie("id", Long.toString(id));
			Cookie cookie2 = new Cookie("password", Long.toString(password));
			Cookie cookie3 = new Cookie("stuid", Long.toString(id));// 让jsp页面使用的
			if(remember == 1){//保存cookie			
				cookie1.setMaxAge(6 * 60 * 60);//6h
				cookie2.setMaxAge(6 * 60 * 60);//6h
				cookie3.setMaxAge(6 * 60 * 60);

			} else { //不同帐号就清楚缓存  
				cookie1.setMaxAge(0);//0.5h
				cookie2.setMaxAge(0);//0.5h
				cookie3.setMaxAge(30 * 60);
			}
				response.addCookie(cookie1);
				response.addCookie(cookie2);
				response.addCookie(cookie3);
			return "books";
			
		} catch (UserException e) {
			model.addAttribute("msg", e.getMessage());
			return "login";
		} catch (Exception e1) {
			model.addAttribute("msg","内部错误！");
			e1.printStackTrace();
			return "error";
		} 
	}
	
	/**
	 * 页面内登录
	 */
	@RequestMapping("logon")
	public @ResponseBody String logon(HttpServletResponse response, HttpSession session,
			@RequestParam(required=false)long id, 
			@RequestParam(required=false)long password){
		try {
			Student student = userService.login(id, password);
			session.setAttribute("stu", student);
			Cookie cookie1 = new Cookie("id", Long.toString(id));
			Cookie cookie2 = new Cookie("password", Long.toString(password));
			Cookie cookie3 = new Cookie("stuid", Long.toString(id));
			cookie1.setMaxAge(0);//清空登录缓存
			cookie2.setMaxAge(0);
			cookie3.setMaxAge(30 * 60);//0.5h
			response.addCookie(cookie1);
			response.addCookie(cookie2);
			response.addCookie(cookie3);
			return "SUCCESS";
		} catch (UserException e) {
			return "FALSE";
		} catch (Exception e1) {
			return "FALSE";
		} 
	}
	
	
	/**
	 * 
	 * @Description: 注销功能
	 * @author: bee
	 * @CreateTime: 2018-5-15 下午2:40:09 
	 */
	@RequestMapping("logout")
	public String logout(HttpSession session, HttpServletResponse response) {
		Cookie cookie1 = new Cookie("stuid", "0");//为了清空缓存
		cookie1.setMaxAge(0);
		response.addCookie(cookie1);
		session.invalidate();
		return "login";
	}
	
	/**
	 * 查询所有书目
	 */
	@RequestMapping("findAllBooks")
	public String findAllBooks(Model model,
						@RequestParam(required=false,defaultValue="1")int page,
						@RequestParam(required=false,defaultValue="7")int pageSize) {
		/*
		 * 得到分页的list
		 * 传出去的是一个pageInfo的列表
		 * 
		 */
		PageInfo<Book> bookList = bookService.findAll(page, pageSize);
		model.addAttribute("bookList", bookList);
		return "books";
	}
	
	/**
	 * 查找某本书
	 */
	@RequestMapping("findBook")
	public String findBook(Model model, long bookId) {
		Book book = bookService.findById(bookId);
		model.addAttribute("book", book);
		
		return "detail";
	}
	
	/**
	 * 预约书目
	 * @param  model   
	 * @param  session 
	 * @param  bookId  
	 * @return        
	 */
	@RequestMapping("appoint")
	public String bookAppointment(Model model, HttpSession session, long bookId) {
		Student student = (Student) session.getAttribute("stu");
		long stu_id = student.getStudentId();
		try {
			Appointment appointment = appointService.appoint(stu_id, bookId);
			model.addAttribute("appoint", appointment);
			return "confirm";
		} catch (AppointException e) {
			model.addAttribute("msg",e.getMessage());
			model.addAttribute("book",bookService.findById(bookId));
			return "detail";
		}
	}
	
	/**
	 * 查看我的预约
	 * @param  model   [description]
	 * @param  session [description]
	 * @return         [description]
	 */
	@RequestMapping("myAppoint")
	public String myAppointment(Model model, HttpSession session) {
		Student student = (Student) session.getAttribute("stu");
		long stuId = student.getStudentId();
		List<Appointment> myAppointments = appointService.findAppointmentsByStudent(stuId);
		model.addAttribute("appointList", myAppointments);
		return "myAppointment";
	}
	
	@RequestMapping("findBooks") 
	public String findBooksBykey(Model model, String keyword,
			@RequestParam(required=false,defaultValue="1")int page,
			@RequestParam(required=false,defaultValue="7")int pageSize) {
		PageInfo<Book> bookList = bookService.findBooksByKey(keyword, page, pageSize);
		model.addAttribute("bookList", bookList);
		return "books";
	}
}

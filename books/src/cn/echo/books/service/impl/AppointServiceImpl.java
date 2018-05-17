package cn.echo.books.service.impl;

import java.util.Date;
import java.util.List;

import oracle.net.aso.a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.echo.books.exception.AppointException;
import cn.echo.books.mapper.AppointmentMapper;
import cn.echo.books.mapper.BookMapper;
import cn.echo.books.pojo.Appointment;
import cn.echo.books.pojo.AppointmentExample;
import cn.echo.books.pojo.AppointmentKey;
import cn.echo.books.pojo.BookExample.Criteria;
import cn.echo.books.service.AppointService;
import cn.echo.books.service.BookService;

@Service
public class AppointServiceImpl implements AppointService {

	@Autowired
	AppointmentMapper appointmentMapper;
	
	@Autowired
	BookService bookService;
	
	@Override
	public List<Appointment> findAppointmentsByStudent(long id) {
		AppointmentExample appointmentExample = new AppointmentExample();
		AppointmentExample.Criteria criteria = appointmentExample.createCriteria();
		criteria.andStudentIdEqualTo(id);
		return appointmentMapper.selectByExample(appointmentExample);	
	}

	@Override
	@Transactional
	public Appointment appoint(long stu_id, long book_id) {
		/*
		 * 查看库存
		 * 事务会回滚
		 */
		try {
		int update = bookService.reduceOneBook(book_id);
		if(update < 0){
				throw new AppointException("库存不足！");
		} else {
				AppointmentKey key = new AppointmentKey();
				key.setBookId(book_id);
				key.setStudentId(stu_id);
					/*
				 * 查看是否预约过
				 */
				Appointment appointment	= appointmentMapper.selectByPrimaryKey(key);
				if(appointment != null){
					throw new AppointException("重复预约！");
				}
				/*
				 * 插入预约！、
				 */
				appointment = new Appointment();
				appointment.setBookId(book_id);
				appointment.setStudentId(stu_id);
				Date appointTime = new Date();
				appointment.setAppointTime(appointTime);
				appointmentMapper.insert(appointment);
				return appointment;
			}
		} catch(AppointException e) {
			throw e;
		} catch(Exception e1) {
			throw new AppointException("未知错误：" + e1.getMessage());
		}
	}
}

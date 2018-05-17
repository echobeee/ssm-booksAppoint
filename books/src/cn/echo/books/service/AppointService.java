package cn.echo.books.service;

import java.util.List;

import cn.echo.books.pojo.Appointment;

public interface AppointService {
	/*
	 * 查找学生的预约请求
	 */
	public List<Appointment> findAppointmentsByStudent(long id);
	/*
	 * 预约请求
	 */
	public Appointment appoint(long stu_id, long book_id);
}

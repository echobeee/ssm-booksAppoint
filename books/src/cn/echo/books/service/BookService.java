package cn.echo.books.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import cn.echo.books.pojo.Book;

public interface BookService {
	/*
	 * 查询一本书
	 */
	public Book findById(long bookId);
	/*
	 * 查询所有书（分页）
	 */
	public PageInfo<Book> findAll(int page, int pageSize);
	/*
	 * 关键词查询书
	 */
	public PageInfo<Book> findBooksByKey(String keyword, int page, int pageSize);
	/*
	 * 减少书的数量
	 * 返回值为库存量
	 */
	public int reduceOneBook(long bookId);
}

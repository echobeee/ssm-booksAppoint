package cn.echo.books.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.echo.books.mapper.BookMapper;
import cn.echo.books.pojo.Book;
import cn.echo.books.pojo.BookExample;
import cn.echo.books.service.BookService;

@Service
public class BookServiceImpl implements BookService {
	@Autowired
	@Qualifier(value="bookMapper")
	private BookMapper bookMapper;
	
	/**
	 * 通过id查找书
	 * @param  id 
	 */
	@Override
	public Book findById(long id) {
	 	return bookMapper.selectByPrimaryKey(id);
	}

	/**
	 * 查询所有的书目
	 * @param  page     [页号]
	 * @param  pageSize [一页的记录数]
	 */
	@Override
	public PageInfo<Book> findAll(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<Book> list = bookMapper.selectAll();
		PageInfo<Book> pages = new PageInfo<Book>(list);
		return pages;
	}

	/**
	 * 通过关键词查找书目
	 * @param  keyword  [关键词]
	 * @param  page     [页号]
	 * @param  pageSize [一页的记录数]
	 */
	@Override
	public PageInfo<Book> findBooksByKey(String keyword, int page, int pageSize) {
		BookExample example = new BookExample();
		BookExample.Criteria criteria = example.createCriteria();
		criteria.andNameLike("%" + keyword +"%");
		BookExample.Criteria criteria1 = example.createCriteria();
		criteria1.andIntrodLike("%" + keyword +"%");
		example.or(criteria1);
		PageHelper.startPage(page, pageSize);
		PageInfo<Book> books = new PageInfo<Book>(bookMapper.selectByExample(example));
		return  books;
	}

	/**
	 * 借出一本书,返回剩余库存量
	 * @param  bookId [description]
	 * @return        [description]
	 */
	@Override
	public int reduceOneBook(long bookId) {
		Book book = bookMapper.selectByPrimaryKey(bookId);
		book.setNumber(book.getNumber()-1);
		bookMapper.updateByPrimaryKey(book);
		return book.getNumber();
	}

}

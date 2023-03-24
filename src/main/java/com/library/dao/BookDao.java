package com.library.dao;

import com.library.bean.Book;
import com.library.dto.BookDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;

@Repository("bookDao")
public class BookDao {

    private final static String NAMESPACE = "com.library.dao.BookDao.";
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public int matchBook(final Book book){
        return sqlSessionTemplate.selectOne(NAMESPACE + "matchBook", book);
    }

    public void changeStruc(){
        sqlSessionTemplate.update(NAMESPACE+"changeStruc");
    }

    public int countBook(final BookDto bookDto){
        return sqlSessionTemplate.selectOne(NAMESPACE + "countBook", bookDto);
    }

    public ArrayList<Book> queryBook(BookDto book) {
        if(book.getPageNum() != null&& book.getPageSize()!= null) {
            book.setOffset((book.getPageNum() - 1) * book.getPageSize());
        }
        List<Book> result = sqlSessionTemplate.selectList(NAMESPACE + "queryBook", book);
        return (ArrayList<Book>) result;
    }

    public ArrayList<Book> getAllBooks(BookDto bookDto) {
        List<Book> result = sqlSessionTemplate.selectList(NAMESPACE + "getAllBooks",bookDto);
        return (ArrayList<Book>) result;
    }

    public int addBook(final Book book) {
        return sqlSessionTemplate.insert(NAMESPACE + "addBook", book);
    }

    public Book getBook(final long bookId) {
        return sqlSessionTemplate.selectOne(NAMESPACE + "getBook", bookId);
    }

    public int editBook(final Book book) {
        return sqlSessionTemplate.update(NAMESPACE + "editBook", book);
    }

    public int deleteBook(final long bookId) {
        return sqlSessionTemplate.delete(NAMESPACE + "deleteBook", bookId);
    }
}

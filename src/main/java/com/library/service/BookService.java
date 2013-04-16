package com.library.service;

import com.library.bean.Book;
import com.library.dao.BookDao;
import com.library.dto.BookDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service

public class BookService {
    @Autowired
    private BookDao bookDao;
    private Logger log = Logger.getLogger(this.getClass());

    public List<Book> queryBook(String isbn){
     return bookDao.queryBookByIsbn(isbn);
    }
    public ArrayList<Book> queryBookForReader(String searchWord,Integer choice,Integer pageSize,Integer pageNum){
        String search = null;
        if(searchWord != null){
            search="%" + searchWord.trim() + "%";
        }
        Book book = new Book();
        if(choice == null){
            book.setIsbn(search);
        }else {
            if (choice == 1) {
                book.setIsbn(search);
            } else {
                book.setName(search);
            }
        }
        BookDto bookDto=new BookDto();
        if(pageSize == 0){
            pageSize=10;
        }
        if(pageNum==0){
            pageNum=1;
        }
        bookDto.setName(book.getName());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPageNum(pageNum);
        bookDto.setPageSize(pageSize);
        ArrayList<Book> arr = bookDao.queryBook(bookDto);
        log.info("arr: "+ arr.size());
        System.out.println("arr: "+ arr.size());
        return arr;
    }

    public ArrayList<Book> queryBook(String searchWord,Integer choice,Integer status,int pageSize,int pageNum) {
        String search = null;
        if(searchWord != null){
            searchWord = searchWord.trim();
            search="%" + searchWord + "%";
        }
        Book book = new Book();
        if(choice == null){
            book.setIsbn(search);
        }else {
            if (choice == 1) {
                book.setIsbn(search);
            } else {
                book.setName(search);
            }
        }
        if(pageSize == 0){
            pageSize=10;
        }
        if(pageNum==0){
            pageNum=1;
        }
        BookDto bookDto=new BookDto();
        bookDto.setName(book.getName());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPageNum(pageNum);
        bookDto.setPageSize(pageSize);
        bookDto.setStatus(status);
        ArrayList<Book> arr = bookDao.queryBook(bookDto);
        log.info("arr: "+ arr.size());
        System.out.println("arr: "+ arr.size());
        return arr;
    }
    public int queryBookCount(String searchWord,Integer choice,Integer status) {
        String search =null;
        if(null != searchWord){
            searchWord = searchWord.trim();
            search= "%" + searchWord + "%";
        }
        Book book = new Book();
        if(choice != null) {
            if (choice == 1) {
                book.setIsbn(search);
            } else {
                book.setName(search);
            }
        }else{//默认，是isbn查询
            book.setIsbn(search);
        }
        BookDto bookDto=new BookDto();
        bookDto.setName(book.getName());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setStatus(status);
        if(status == null)
            return bookDao.countBook(bookDto);
        else
            return bookDao.countNotBackBook(bookDto);
    }


    public ArrayList<Book> getAllBooks(BookDto bookDto) {
        return bookDao.getAllBooks(bookDto);
    }

    public boolean matchBook(String searchWord,Integer choice){
        String search = null;
        if(searchWord != null){
            search= "%" + searchWord.trim() + "%";
        }
        Book book = new Book();
        if(choice != null) {
            if (choice.equals(1)) {
                book.setIsbn(search);
            } else {
                book.setName(search);
            }
        }else{
            book.setIsbn(search);
        }
        int x =bookDao.matchBook(book) ;
        log.info("matchBook= "+ x+", search:"+searchWord);
        System.out.println("matchBook= "+ x);
        return x > 0 ;
    }

    public boolean addBook(Book book) {
        return bookDao.addBook(book) > 0;
    }

    public Book getBook(Long bookId) {
        return bookDao.getBook(bookId);
    }

    public boolean editBook(Book book) {
        return bookDao.editBook(book) > 0;
    }

    public boolean deleteBook(Long bookId) {
        return bookDao.deleteBook(bookId) > 0;
    }

}

package com.library.controller;

import com.alibaba.fastjson.JSON;
import com.library.bean.*;
import com.library.dto.AjaxResp;
import com.library.dto.BookDto;
import com.library.service.*;
import com.library.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class BookController {
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    @Autowired
    private BookService bookService;
    @Autowired
    private LendService lendService;
    @Autowired
    private IsBnApiService isBnApiService;
    @Autowired
    private ClazzService clazzService;
    @Autowired
    private BatchService batchService;

    private String getDate(String pubstr) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d= df.parse(pubstr);
            return df.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return df.format(new Date());
        }
    }

    @RequestMapping(value = "/query/book/{isbn}",method = RequestMethod.GET )
    public void queryBook(@PathVariable String isbn, HttpServletResponse response){
        try {
            BookAutoRequestVo vo = isBnApiService.queryByIsbn(isbn);
            AjaxResp<Book> ajaxResp = null;
            if(vo.getCode() != null && vo.getCode().equals(100))
                 ajaxResp = new AjaxResp<>(200, "ok", vo.getBook());
            else
                ajaxResp=new AjaxResp<>(500,vo.getMsg(),null);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(ajaxResp));
        }catch (Exception e){
            log.error("query book failed",e);
            try {
                response.getWriter().print(JSON.toJSONString( new AjaxResp<>(500,"fail",null)));
            } catch (IOException ioException) {
               log.error("exception", ioException);
            }
        }
    }

    @RequestMapping("/querybook.html")
    public ModelAndView queryBookDo(String searchWord,Integer choice,int pageNum,int pageSize) {
        if (bookService.matchBook(searchWord,choice)) {
            ArrayList<Book> books = bookService.queryBook(searchWord,choice,pageSize,pageNum);
            ModelAndView modelAndView = new ModelAndView("admin_books");
            modelAndView.addObject("books", books);
            modelAndView.addObject("choice",choice);
            int total = bookService.queryBookCount(searchWord,choice);
            modelAndView.addObject("total",total);
            modelAndView.addObject("pageNum",pageNum);
            modelAndView.addObject("pageSize", pageSize);
            modelAndView.addObject("pages", total%pageSize==0? total/pageSize : (total/pageSize)+1);
            return modelAndView;
        } else {
            ModelAndView mv= new ModelAndView("admin_books", "error", "没有匹配的图书");
            mv.addObject("choice",choice);
            return mv;
        }
    }

    @RequestMapping("/reader_querybook_do.html")
    public ModelAndView readerQueryBookDo(String searchWord,Integer choice) {
        if (bookService.matchBook(searchWord, choice)) {
            ArrayList<Book> books = bookService.queryBookForReader(searchWord,choice);
            ModelAndView modelAndView = new ModelAndView("reader_books");
            modelAndView.addObject("books", books);
            return modelAndView;
        } else {
            return new ModelAndView("reader_books", "error", "没有匹配的图书");
        }
    }

    @RequestMapping(value = "/query/book/exists/{isbn}",method = RequestMethod.GET )
    public void queryIsbnBook(@PathVariable String isbn, HttpServletResponse response){
        try {
            List<Book> vo = bookService.queryBook(isbn);
            AjaxResp<Book> ajaxResp = null;
            if(CollectionUtils.isEmpty(vo))
                ajaxResp = new AjaxResp<>(200, "ok", null);
            else
                ajaxResp=new AjaxResp<>(500, "exists",null);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(ajaxResp));
        }catch (Exception e){
            log.error("query book failed",e);
            try {
                response.getWriter().print(JSON.toJSONString( new AjaxResp<>(501,"fail",null)));
            } catch (IOException ioException) {
                log.error("exception", ioException);
            }
        }
    }

    @RequestMapping("/admin_books_look_progress.html")
    public ModelAndView lookProgress(){
        ModelAndView mv= new ModelAndView("look_progress");
        List<BatchResult> batchResult= batchService.findResult();
        mv.addObject("batchProgress",batchResult);
        return mv;
    }

    @RequestMapping("/admin_books.html")
    public ModelAndView adminBooks() {
        final int pageSize=10;
        final int pageNum=1;
        BookDto book=new BookDto();
        book.setOffset(0);
        book.setPageSize(pageSize);

        ArrayList<Book> books = bookService.getAllBooks(book);
        ModelAndView modelAndView = new ModelAndView("admin_books");
        modelAndView.addObject("books", books);
        int total = bookService.queryBookCount(null,null);

        modelAndView.addObject("total",total);
        modelAndView.addObject("pageNum",pageNum);
        modelAndView.addObject("pageSize",pageSize);
        modelAndView.addObject("pages", total%pageSize==0? total/pageSize : (total/pageSize)+1);
        return modelAndView;
    }

    @RequestMapping("/book_add.html")
    public ModelAndView addBook() {
        ModelAndView mv= new ModelAndView("admin_book_add");
        mv.addObject("clazzList",clazzService.getAll());
        return mv;
    }
    @RequestMapping("/book_add_batch.html")
    public ModelAndView addBatchBook(){
        return new ModelAndView("admin_book_add_batch");
    }

    @RequestMapping("/book_batch_add_do.html")
    public String batchAdd(HttpServletRequest req,RedirectAttributes redirectAttributes) {
        try {
            ServletInputStream inputStream = req.getInputStream();
            try {
                List<String> list = FileUtils.readFile(inputStream);
                list = batchService.insertBatchLst(list);
                isBnApiService.batchQueryByIsbn(list);
                redirectAttributes.addFlashAttribute("succ", "已经提交队列！");
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                redirectAttributes.addFlashAttribute("succ", "提交队列失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            redirectAttributes.addFlashAttribute("succ", "提交队列失败！");
        }
        return "redirect:/admin_books_look_progress.html";
    }



    @RequestMapping("/book_add_do.html")
    public String addBookDo(@RequestParam(value = "pubstr") String pubstr, Book book, RedirectAttributes redirectAttributes) {
        book.setPubdate(getDate(pubstr));
        if (bookService.addBook(book)) {
            redirectAttributes.addFlashAttribute("succ", "图书添加成功！");
        } else {
            redirectAttributes.addFlashAttribute("succ", "图书添加失败！");
        }
        return "redirect:/admin_books.html";
    }

    @RequestMapping("/updatebook.html")
    public ModelAndView bookEdit(HttpServletRequest request) {
        long bookId = Long.parseLong(request.getParameter("bookId"));
        Book book = bookService.getBook(bookId);
        ModelAndView modelAndView = new ModelAndView("admin_book_edit");
        modelAndView.addObject("detail", book);
        modelAndView.addObject("clazzList",clazzService.getAll());
        return modelAndView;
    }

    @RequestMapping("/book_edit_do.html")
    public String bookEditDo(@RequestParam(value = "pubstr") String pubstr, Book book, RedirectAttributes redirectAttributes) {
        book.setPubdate(getDate(pubstr));
        if (bookService.editBook(book)) {
            redirectAttributes.addFlashAttribute("succ", "图书修改成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "图书修改失败！");
        }
        return "redirect:/admin_books.html";
    }

    @RequestMapping("/admin_book_detail.html")
    public ModelAndView adminBookDetail(HttpServletRequest request) {
        long bookId = Long.parseLong(request.getParameter("bookId"));
        Book book = bookService.getBook(bookId);
        ClassInfo clz= clazzService.getByID(book.getClassId());
        book.setClassName(clz == null ? "" : clz.getClass_name());
        ModelAndView modelAndView = new ModelAndView("admin_book_detail");
        modelAndView.addObject("detail", book);
        return modelAndView;
    }

    @RequestMapping("/reader_book_detail.html")
    public ModelAndView readerBookDetail(HttpServletRequest request) {
        long bookId = Long.parseLong(request.getParameter("bookId"));
        Book book = bookService.getBook(bookId);
        ClassInfo clz = clazzService.getByID(book.getClassId());
        book.setClassName(clz == null ? "": clz.getClass_name());
        ModelAndView modelAndView = new ModelAndView("reader_book_detail");
        modelAndView.addObject("detail", book);
        return modelAndView;
    }

    @RequestMapping("/admin_header.html")
    public ModelAndView admin_header() {
        return new ModelAndView("admin_header");
    }

    @RequestMapping("/reader_header.html")
    public ModelAndView reader_header() {
        return new ModelAndView("reader_header");
    }

    @RequestMapping("/reader_books.html")
    public ModelAndView readerBooks(HttpServletRequest request) {
        BookDto bookDto = new BookDto();
        ArrayList<Book> books = bookService.getAllBooks(bookDto);
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        ArrayList<Lend> myAllLendList = lendService.myLendList(readerCard.getReaderId());
        ArrayList<Long> myLendList = new ArrayList<>();
        for (Lend lend : myAllLendList) {
            // 是否已归还
            if (lend.getBackDate() == null) {
                myLendList.add(lend.getBookId());
            }
        }
        ModelAndView modelAndView = new ModelAndView("reader_books");
        modelAndView.addObject("books", books);
        modelAndView.addObject("myLendList", myLendList);
        return modelAndView;
    }
}

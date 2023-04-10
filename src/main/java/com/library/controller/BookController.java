package com.library.controller;

import com.alibaba.fastjson.JSON;
import com.library.bean.*;
import com.library.dto.AjaxResp;
import com.library.dto.BookDto;
import com.library.service.*;
import com.library.utils.FileUtils;
import com.library.vo.BookVo;
import com.library.vo.LendVo;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private ReaderInfoService readerInfoService;

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
            ArrayList<BookVo> bookVos = new ArrayList<>();
            for(Book bk : books){
                BookVo bookVo = new BookVo();
                BeanUtils.copyProperties(bk, bookVo);
                bookVo.setHasLendRecord(lendService.lendOrNot(bk.getBookId()));
                bookVos.add(bookVo);
            }
            modelAndView.addObject("books", bookVos);
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
    public ModelAndView readerQueryBookDo(HttpServletRequest request,String searchWord,Integer choice,int pageNum,int pageSize) {
        if (bookService.matchBook(searchWord, choice)) {
            ArrayList<Book> books = bookService.queryBookForReader(searchWord,choice,pageSize,pageNum);

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
            modelAndView.addObject("choice",choice);
            int total = bookService.queryBookCount(searchWord,choice);
            modelAndView.addObject("total",total);
            modelAndView.addObject("pageNum",pageNum);
            modelAndView.addObject("pageSize", pageSize);
            modelAndView.addObject("pages", total%pageSize==0? total/pageSize : (total/pageSize)+1);
            return modelAndView;
        } else {
            ModelAndView mv= new ModelAndView("reader_books", "error", "没有匹配的图书");
            mv.addObject("choice",choice);
            return mv;
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
        ArrayList<BookVo> bookVos = new ArrayList<>();
        int total = bookService.queryBookCount(null,null);

        for(Book bk : books){
            BookVo bookVo=new BookVo();
            BeanUtils.copyProperties(bk, bookVo);
            bookVo.setHasLendRecord(lendService.lendOrNot(bk.getBookId()));
            bookVos.add(bookVo);
        }
        modelAndView.addObject("books", bookVos);

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
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                final String batchNo = sdf.format(new Date());
                List<String> list = FileUtils.readFile(inputStream);
                list = batchService.insertBatchLst(batchNo,list);
                isBnApiService.batchQueryByIsbn(batchNo,list);
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

    @RequestMapping("/admin_book_lend.html")
    public ModelAndView adminBookLend(HttpServletRequest req){
        ModelAndView mv = new ModelAndView("admin_lend");
        try{
            String bookIdStr = req.getParameter("bookId");
            Book bk =bookService.getBook(Long.parseLong(bookIdStr));
            mv.addObject("detail",  bk);
            if(bk.getNumber() < 1){
                mv.addObject("succ", "没有库存了，借阅失败");
                mv.setViewName("admin_books");
            }else{
                mv.addObject("readList",readerInfoService.readerInfos());
            }
            return mv;
        }catch (Exception ex){
            log.warn("admin book lend failed",ex);
        }
        return mv;
    }

    @RequestMapping("/admin_book_back.html")
    public ModelAndView adminBookBack(HttpServletRequest req){
        Long bookId = Long.parseLong(req.getParameter("bookId"));
        Book bk = bookService.getBook(bookId);
        ArrayList<Lend> lst = lendService.lendListOfBook(bookId);
        ArrayList<LendVo> list = new ArrayList<>();
        for(Lend lend : lst){
            LendVo lendVo = new LendVo();
            BeanUtils.copyProperties(lend, lendVo);
            ReaderInfo ri = readerInfoService.getReaderInfo(lend.getReaderId());
            if(ri != null)
                lendVo.setReaderName(ri.getName());
            lendVo.setReaderCardNo(lend.getReaderId());
            list.add(lendVo);
        }
        ModelAndView mv = new ModelAndView("admin_reader_lend_list","list", list);
        mv.addObject("detail",bk);
        return mv;
    }

    @RequestMapping("/admin_back_of_book.html")
    public String backOfBook(HttpServletRequest request,RedirectAttributes redirectAttributes){
        long bookId = Long.parseLong(request.getParameter("bookId"));
        long readerId = Long.parseLong(request.getParameter("readerId"));
        if (lendService.returnBook(bookId, readerId)) {
            redirectAttributes.addFlashAttribute("succ", "归还成功");
            return "redirect:/admin_books.html";
        }else {
            redirectAttributes.addFlashAttribute("succ", "归还失败");
            return "redirect:/admin_books.html";
        }
    }

    @RequestMapping("/book_admin_lend_do.html")
    public String adminBookLd(HttpServletRequest request,RedirectAttributes redirectAttributes) {
        String bookIdStr = request.getParameter("bookId");
        long bookId = Long.parseLong(bookIdStr);
        Book bk = bookService.getBook(bookId);
        if(bk.getNumber() < 1){
            redirectAttributes.addFlashAttribute("succ", "没有库存了，借阅失败");
            return "redirect:/admin_books.html";
        }else {
            long readerId = Long.parseLong(request.getParameter("readerId"));
            if (lendService.lendBook(bookId, readerId)) {
                redirectAttributes.addFlashAttribute("succ", "借阅成功");
            }
        }
        return "redirect:/admin_books.html";
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

        final int pageSize=10;
        final int pageNum=1;
        BookDto book=new BookDto();
        book.setOffset(0);
        book.setPageSize(pageSize);


        ArrayList<Book> books = bookService.getAllBooks(book);
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
        int total = bookService.queryBookCount(null,null);

        modelAndView.addObject("total",total);
        modelAndView.addObject("pageNum",pageNum);
        modelAndView.addObject("pageSize",pageSize);
        modelAndView.addObject("pages", total%pageSize==0? total/pageSize : (total/pageSize)+1);
        return modelAndView;
    }
}

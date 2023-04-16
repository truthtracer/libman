package com.library.controller;

import com.library.bean.Book;
import com.library.bean.Lend;
import com.library.bean.ReaderCard;
import com.library.bean.ReaderInfo;
import com.library.service.BookService;
import com.library.service.LendService;
import com.library.service.ReaderInfoService;
import com.library.vo.LendVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LendController {
    @Autowired
    private LendService lendService;

    @Autowired
    private ReaderInfoService readerInfoService;
    @Autowired
    private BookService bookService;

    @RequestMapping("/deletebook.html")
    public String deleteBook(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        long bookId = Long.parseLong(request.getParameter("bookId"));
        if (bookService.deleteBook(bookId)) {
            redirectAttributes.addFlashAttribute("succ", "图书删除成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "图书删除失败！");
        }
        return "redirect:/admin_books.html";
    }

    @RequestMapping("/lendlist.html")
    public ModelAndView lendList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin_lend_list");
        ArrayList<LendVo> arrayList = new ArrayList<>();
        List<Lend> ls= lendService.lendList();
        for(Lend ld : ls){
            LendVo ldv =new LendVo();
            BeanUtils.copyProperties(ld,ldv);
            Book b = bookService.getBook(ld.getBookId());
            if(b != null)
                ldv.setBookName(b.getName());
            ReaderInfo rdf = readerInfoService.getReaderInfo(ld.getReaderId());
            if(rdf!=null){
                ldv.setReaderNo(rdf.getReaderNo());
            }
            arrayList.add(ldv);
        }
        modelAndView.addObject("list",arrayList);
        return modelAndView;
    }

    @RequestMapping("/mylend.html")
    public ModelAndView myLend(HttpServletRequest request) {
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        ModelAndView modelAndView = new ModelAndView("reader_lend_list");
        ArrayList<Lend> ls=lendService.myLendList(readerCard.getReaderId());
        List<LendVo> result = new ArrayList<>();
        for(Lend ld : ls){
            LendVo ldv = new LendVo();
            BeanUtils.copyProperties(ld,ldv);
            Book bk = bookService.getBook(ld.getBookId());
            if(bk != null) {
                ldv.setBookName(bk.getName());
            }
            result.add(ldv);
        }
        modelAndView.addObject("list", result);
        return modelAndView;
    }

    @RequestMapping("/deletelend.html")
    public String deleteLend(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        long serNum = Long.parseLong(request.getParameter("serNum"));
        if (lendService.deleteLend(serNum) > 0) {
            redirectAttributes.addFlashAttribute("succ", "记录删除成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "记录删除失败！");
        }
        return "redirect:/lendlist.html";
    }

    @RequestMapping("/lendbook.html")
    public String bookLend(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        long bookId = Long.parseLong(request.getParameter("bookId"));
        long readerId = ((ReaderCard) request.getSession().getAttribute("readercard")).getReaderId();
        if (lendService.lendBook(bookId, readerId)) {
            redirectAttributes.addFlashAttribute("succ", "图书借阅成功！");
        } else {
            redirectAttributes.addFlashAttribute("succ", "图书借阅成功！");
        }
        return "redirect:/reader_books.html";
    }

    @RequestMapping("/returnbook.html")
    public String bookReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        long bookId = Long.parseLong(request.getParameter("bookId"));
        long readerId = ((ReaderCard) request.getSession().getAttribute("readercard")).getReaderId();
        if (lendService.returnBook(bookId, readerId)) {
            redirectAttributes.addFlashAttribute("succ", "图书归还成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "图书归还失败！");
        }
        return "redirect:/reader_books.html";
    }
}

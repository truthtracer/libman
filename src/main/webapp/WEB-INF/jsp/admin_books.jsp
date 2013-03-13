<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>全部图书信息</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <script src="js/jquery-3.2.1.js"></script>
    <script src="js/bootstrap.min.js" ></script>
    <script>
        $(function () {
            $('#header').load('admin_header.html');
        })
    </script>
</head>
<body background="img/book1.jpg" style=" background-repeat:no-repeat ;
background-size:100% 100%;
background-attachment: fixed;">

<div id="header"></div>

<div style="padding: 70px 550px 10px">
    <form   method="post" action="querybook.html" class="form-inline"  id="searchform">
        <div class="input-group" style="width:500px">
            <select name="choice" class="form-control" style="border-radius:4px;width:150px;margin-right:30px">
                <option value="1"
                        <c:if test="${(!empty choice) && choice==1}">
                            selected="selected"
                        </c:if> >按ISBN查
                </option>
                <option value="2"
                   <c:if test="${(!empty choice) && choice==2}">
                    selected="selected"
                    </c:if>>按其他关键词查
                </option>
            </select>
           <input type="text" placeholder="输入关键词" class="form-control" style="width:190px; border-radius:4px;margin-right:30px" id="search" name="searchWord" class="form-control">
            <input type="hidden" id="hdnPage" name="pageNum"/>
            <input type="hidden" id="hdnPageSize" name="pageSize"/>
            <input type="button"  style="width:80px;height:34px;background-color:#e7e7e7;border:none;border-radius:4px;" value="搜索" onclick="subm()" class="btn btn-default">
        </div>
    </form>
    <script>
         function subMit(){
             $("#hdnPage").val(1);
             $("#hdnPageSize").val($("#selPageSize").val());
             $("#searchform").submit();
         }
         function subm(){
           subMit();
         }

        function  sub() {
            // var val=$("#search").val();
            $("#hdnPage").val($("#selPgNm").val());
            $("#hdnPageSize").val($("#selPageSize").val());
            $("#searchform").submit();
        }
    </script>
</div>
<div style="position: relative;top: 10%">
<c:if test="${!empty succ}">
    <div class="alert alert-success alert-dismissable">
        <button type="button" class="close" data-dismiss="alert"
                aria-hidden="true">
            &times;
        </button>
        ${succ}
    </div>
</c:if>
<c:if test="${!empty error}">
    <div class="alert alert-danger alert-dismissable">
        <button type="button" class="close" data-dismiss="alert"
                aria-hidden="true">
            &times;
        </button>
        ${error}
    </div>
</c:if>
</div>
<div class="panel panel-default" style="width: 90%;margin-left: 5%">
    <div class="panel-heading" style="background-color: #fff">
        <h3 class="panel-title">
            全部图书
        </h3>
    </div>
    <div class="panel-body">
        <table class="table table-hover">
            <thead>
            <tr>
                <th>书名</th>
                <th>作者</th>
                <th>出版社</th>
                <th>ISBN</th>
                <th>价格</th>
                <th>剩余数量</th>
                <th>详情</th>
                <th>编辑</th>
                <th>删除</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${books}" var="book">
            <tr>
                <td><c:out value="${book.name}"></c:out></td>
                <td><c:out value="${book.author}"></c:out></td>
                <td><c:out value="${book.publish}"></c:out></td>
                <td><c:out value="${book.isbn}"></c:out></td>
                <td><c:out value="${book.price}"></c:out></td>
                <td><c:out value="${book.number}"></c:out></td>
                <td><a href="admin_book_detail.html?bookId=<c:out value="${book.bookId}"></c:out>">
                    <button type="button" class="btn btn-success btn-xs">详情</button>
                </a></td>
                <td><a href="updatebook.html?bookId=<c:out value="${book.bookId}"></c:out>"><button type="button" class="btn btn-info btn-xs">编辑</button></a></td>
                <td><a href="deletebook.html?bookId=<c:out value="${book.bookId}"></c:out>"><button type="button" class="btn btn-danger btn-xs">删除</button></a></td>
            </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<div style="width:100%;text-align: center;height:80px">
 页容量：<select name="pageSize" id="selPageSize" onchange="subMit()" style="width:50px;height:25px;border-radius: 4px">
     <option value="10" <c:if test="${pageSize == 10}">selected='selected'</c:if>>10</option>
     <option value="20" <c:if test="${pageSize == 20}">selected='selected'</c:if>>20</option>
    <option value="50" <c:if test="${pageSize == 50}">selected='selected'</c:if>>50</option>
 </select>
<label>&nbsp;&nbsp;|&nbsp;&nbsp;</label>页码：
<select id="selPgNm" onchange="sub()" style="width:50px;height:25px;border-radius:4px">
    <c:forEach begin="1" end="${pages}" step="1" var="i">
       <option value="${i}" <c:if test="${i == pageNum}">selected="selected"</c:if>>${i}</option>
    </c:forEach>
</select>
   <label>&nbsp;&nbsp;|&nbsp;&nbsp;</label> 总页数：${pages}
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix ="fmt" uri ="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
    <title>借出尚未归还记录《 ${detail.name}》</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <script src="js/jquery-3.2.1.js"></script>
    <script src="js/bootstrap.min.js" ></script>
    <script>
        $(function () {
            $('#header').load('admin_header.html');
        })
    </script>
</head>
<body background="../../static/img/book2.jpg" style=" background-repeat:no-repeat ;
background-size:100% 100%;
background-attachment: fixed;">

<div id="header" style="padding-bottom: 40px"></div>

<div class="panel panel-default" style="width: 90%;margin-left: 5%;margin-top: 5%">
    <div class="panel-heading">
        <h3 class="panel-title">
            借出尚未归还记录《 ${detail.name}》
        </h3>
    </div>
    <div class="panel-body">
        <table class="table table-hover">
            <thead>
            <tr>
                <th>图书号</th>
                <th>借出日期</th>
                <th>读者姓名</th>
                <th>读者卡号</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${list}" var="alog">
                <tr>
                    <td><c:out value="${alog.bookId}"></c:out></td>
                    <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${alog.lendDate}"/> </td>
                    <td><c:out value="${alog.readerName}"/> </td>
                    <td><c:out value="${alog.readerCardNo}"/> </td>
                    <td>
                        <a href="admin_back_of_book.html?bookId=<c:out value='${detail.bookId}'></c:out>&readerId=<c:out value='${alog.readerCardNo}'/>">
                            <button type="button" class="btn btn-danger btn-xs">归还</button>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
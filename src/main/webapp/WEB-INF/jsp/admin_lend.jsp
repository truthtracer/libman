<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>借阅《 ${detail.name}》</title>
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

<div id="header" style="padding-bottom: 80px"></div>

<div class="col-xs-6 col-md-offset-3" style="position: relative;">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">借阅《 ${detail.name}》</h3>
        </div>
        <div class="panel-body">
            <form action="book_admin_lend_do.html?bookId=${detail.bookId}" method="post" id="lendbook" >
                <div class="input-group">
                    <span  class="input-group-addon">读者</span>
                    <select  class="form-control" name="readerId" id="readerId">
                        <option value="">--选择读者--</option>
                        <c:forEach items="${readList}" var="rd">
                            <option value="${rd.readerId}" >${rd.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <input type="submit" value="确定" class="btn btn-success btn-sm" class="text-left">
                <script>
                    $("#lendbook").submit(function () {
                        if($("#readerId").val()==''){
                            alert("请选择读者信息！");
                            return false;
                        }
                    })
                </script>
            </form>
        </div>
    </div>

</div>

</body>
</html>

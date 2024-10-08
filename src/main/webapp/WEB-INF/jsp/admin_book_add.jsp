<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图书信息添加</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <script src="js/jquery-3.2.1.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <style>
        .form-group {
            margin-bottom: 0;
        }
    </style>
    <script>
        $(function () {
            $('#header').load('admin_header.html');
        })
    </script>
</head>
<body background="img/sky.jpg" style=" background-repeat:no-repeat ;
background-size:100% 100%;
background-attachment: fixed;">

<div id="header"></div>
<div style="position: relative;padding-top: 60px; width: 80%;margin-left: 10%">
    <form action="book_add_do.html" method="post" id="addbook">
        <div class="form-group">
            <label for="name">图书名</label>
            <input type="text" class="form-control" name="name" id="name" placeholder="请输入书名">
        </div>
        <div class="form-group">
            <label for="author">作者</label>
            <input type="text" class="form-control" name="author" id="author" placeholder="请输入作者名">
        </div>
        <div class="form-group">
            <label for="publish">出版社</label>
            <input type="text" class="form-control" name="publish" id="publish" placeholder="请输入出版社">
        </div>
        <div class="form-group">
            <label for="isbn">ISBN</label>
            <input type="text" oninput="check()" class="form-control" name="isbn" id="isbn" placeholder="请输入ISBN">
            <input type="button" name="btnAuto" id="btnAuto" onclick="queryBook()" value="自动填充"/>
        </div>
        <div class="form-group">
            <label for="introduction">简介</label>
            <textarea class="form-control" rows="3" name="introduction" id="introduction"
                      placeholder="请输入简介"></textarea>
        </div>
        <div class="form-group">
            <label for="language">语言</label>
            <input type="text" class="form-control" name="language" id="language" value="中文" placeholder="请输入语言">
        </div>
        <div class="form-group">
            <label for="price">价格</label>
            <input type="text" class="form-control" name="price" id="price" placeholder="请输入价格">
        </div>
        <div class="form-group">
            <label for="pubstr">出版日期</label>
            <input type="date" class="form-control" name="pubstr" id="pubstr" placeholder="请输入出版日期">
        </div>
        <div class="form-group">
            <label for="classId">分类</label>
            <select  class="form-control" name="classId" id="classId">
                    <option value="">--选择分类--</option>
                    <c:forEach items="${clazzList}" var="clz">
                        <option value="${clz.class_id}">${clz.class_name}</option>
                    </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="number">数量</label>
            <input type="text" class="form-control" name="number" id="number" value="1" placeholder="请输入图书数量">
        </div>

        <input type="submit" value="添加" class="btn btn-success btn-sm" class="text-left">
        <script>
            $("#addbook").submit(function () {
                if ($("#name").val() == '' ) {
                    alert("请填入图书名称！");
                    return false;
                }
            })
            function check(){
                var isbn=$("#isbn").val().trim();
                if(isbn === "" ){
                    return;
                }
                if(isbn.length < 10){
                    return;
                }
                $.ajax({
                    url:"query/book/exists/"+isbn,
                    method:"get",
                    dataType:"json",
                    contentType:"application/json;charset=utf-8",
                    success:function(data){
                        if(data.code == 500){
                            alert("图书已存在");
                        }else if(data.code==501){
                            alert("fail");
                        }
                    }
                });
            }

            function queryBook(){
                var isbn=$("#isbn").val().trim();
                if(isbn === ""){
                    alert("请输入ISBN,再自动填充");
                    return;
                }
                $.ajax({
                   url:"query/book/"+isbn,
                   method:"get",
                   dataType:"json",
                   contentType:"application/json;charset=utf-8",
                   success:function(data){
                       if(data.code == 200){
                           if(data.data) {
                               $("#author").val(data.data.author);
                               $("#name").val(data.data.name);
                               $("#publish").val(data.data.publish);
                               $("#introduction").text(data.data.introduction);
                               if (data.data.language) {
                                   $("#language").val(data.data.language);
                               }
                               $("#price").val(data.data.price);
                               $("#pubstr").val(data.data.pubdate);
                           }else{
                               alert("未查到书籍信息");
                           }
                       }else{
                           alert(data.msg);
                       }
                    }
                });
            }
        </script>
    </form>
</div>
</body>
</html>

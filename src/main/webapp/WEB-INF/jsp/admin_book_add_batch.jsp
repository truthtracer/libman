<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图书信息批量添加</title>
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
    <form action="book_batch_add_do.html" id="addbook"  enctype="multipart/form-data" method="post">
        <div class="form-group">
            <label>ISBN文件(按照每行一个ISBN的格式)</label>
            <input type="file" name="isbnFile"/>
        </div>
        <input type="submit" value="提交" class="btn btn-success btn-sm" class="text-left">
        <script>
            $("#addbook").submit(function () {
                if ($("#name").val() == '') {
                    alert("请填入图书名称！");
                    return false;
                }
            })

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
                           $("#author").val(data.data.author);
                           $("#name").val(data.data.name);
                           $("#publish").val(data.data.publish);
                           $("#introduction").text(data.data.introduction);
                           if(data.data.language) {
                               $("#language").val(data.data.language);
                           }
                           $("#price").val(data.data.price);
                           $("#pubstr").val(data.data.pubdate);
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

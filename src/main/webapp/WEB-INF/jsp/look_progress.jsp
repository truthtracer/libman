<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="refresh" content="30">
    <title>图书信息批量添加-查看进度</title>
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
    <style>
        table {
            border-right: 1px solid #000000;
            border-bottom: 1px solid #000000;
            text-align: center;
        }

        table th {
            border-left: 1px solid #000000;
            border-top: 1px solid #000000;
        }

        table td {
            padding:10px;
            border-left: 1px solid #000000;
            border-top: 1px solid #000000;
        }

    </style>
</head>
<body background="img/sky.jpg" style=" background-repeat:no-repeat ;
background-size:100% 100%;
background-attachment: fixed;">

<div id="header"></div>
<c:if test="${!empty succ}">
    <div class="alert alert-success alert-dismissable">
        <button type="button" class="close" data-dismiss="alert"
                aria-hidden="true">
            &times;
        </button>
            ${succ}
    </div>
</c:if>
<div style="position: relative;padding-top: 60px; width: 80%;margin-left: 10%">
    <c:if test="${batchProgress!= null && batchProgress.size()>0}">
    <table style="width:100%;">
        <tr>
            <td>批次</td>
            <td>总数</td>
            <td>处理中</td>
            <td>成功</td>
            <td>失败</td>
        </tr>
    <c:forEach items="${batchProgress}" var="p">
        <tr>
            <td>${p.batchNo}</td>
            <td>${p.total}</td>
            <td>${p.handling}</td>
            <td>${p.success}</td>
            <td>${p.fail}</td>
        </tr>
    </c:forEach>
    </table>
    </c:if>
</div>
</body>
</html>

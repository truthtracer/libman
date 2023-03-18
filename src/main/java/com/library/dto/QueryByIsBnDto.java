package com.library.dto;

import java.io.Serializable;

public class QueryByIsBnDto implements Serializable {

  private Integer code;
  private String msg;
  private String taskNo;
  private DataDetail data;

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getTaskNo() {
    return taskNo;
  }

  public void setTaskNo(String taskNo) {
    this.taskNo = taskNo;
  }

  public DataDetail getData() {
    return data;
  }

  public void setData(DataDetail data) {
    this.data = data;
  }
  /*
    {
  "code": 200, //成功为200，其他为失败返回码（非http返回状态码）
  "msg": "成功", //code对应的描述
  "taskNo": "65171553403304103621", //唯一业务号
  "data": {
    "details": [
      {
        "series": "",//丛书信息（不是丛书为空字符串）
        "title": "2018百校联盟-1年语文上(人教版)",//书名
        "author": " 靳俊针主编",//作者（编者、译者）信息
        "publisher": "吉林教育出版社",//出版社
        "pubDate": "",//出版日期
        "pubPlace": "长春",//出版地
        "isbn": "9787555357902",//13位isbn号
        "isbn10": "7555357909",//10位isbn号
        "price": "28.80",//定价
        "genus": "TP311.132.3",//中图分类号
        "levelNum": "",//读者评分
        "heatNum": "0",//图书热度（即：购买或评论总人次）
        "format": "",//纸张开数
        "binding": "平装",//装帧信息
        "page": "",//页数
        "wordNum": "",//字数
        "edition": "1版",//版次
        "yinci": "1",//印次
        "paper": "",//书籍纸张类型
        "language": "",//语言
        "keyword": "|小学语文课|习题集",//图书关键词
        "img": "",//封面图片大图链接
        "bookCatalog": "",//目录
        "gist": "本书的特点是紧扣新课标实验教材，按单元进行编写；每单元都由基础知识和重难点梯度安排，重点考查学生掌握基础知识和基本技能的情况；融合思考性、实践性、综合性较强的题目，适当增加了难度，进一步考查学生思维发展和综合运用知识的能力。",//图书内容简介
        "cipTxt": "    百校联考冲刺100分. 语文一年级. 上 : RJ / 靳俊\n针主编. -- 长春 : 吉林教育出版社, 2018.5 \n    ISBN 978-7-5553-5790-2\n \n    Ⅰ. ①百… Ⅱ. ①靳… Ⅲ. ①小学语文课－习题集 \nⅣ. ①G624\n \n    中国版本图书馆CIP数据核字(2018)第109776号",//cip信息
        "annotation": "",//一般附注
        "subject": "",//主题
        "batch": ""//丛编信息
      }
    ]
  }
}
     */
}

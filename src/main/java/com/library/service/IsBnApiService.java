package com.library.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;

import com.library.bean.Batch;
import com.library.bean.Book;
import com.library.bean.BookAutoRequestVo;
import com.library.dao.BatchDao;
import com.library.dao.BookDao;
import com.library.dto.QueryByIsBnDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class IsBnApiService {
    Timer timer = new Timer();
    @Autowired
    private Propert propert;
    @Autowired
    private BookDao bookDao;

    @Autowired
    private BatchDao batchDao;
    @Resource
    private RestTemplate restTemplate;
    private Logger log = Logger.getLogger(this.getClass());
//    CompletionService<BookAutoRequestVo> completionService= new ExecutorCompletionService<>(Executors.newFixedThreadPool(5));

//    public String init(String datetime) {
//        try {
//            String signStr = "x-date: " + datetime + "\n" + "x-source: " + source;
//            Mac mac = Mac.getInstance("HmacSHA1");
//            Key sKey = new SecretKeySpec(propert.getSecKey().getBytes("UTF-8"), mac.getAlgorithm());
//            mac.init(sKey);
//            byte[] hash = mac.doFinal(signStr.getBytes("UTF-8"));
//            String sig = new BASE64Encoder().encode(hash);
//
//            String auth = "hmac id=\"" + propert.getSecId() + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-source\", signature=\"" + sig + "\"";
//            return auth;
//        } catch (Exception e) {
//            log.error("init isbn query api failed", e);
//            return null;
//        }
//    }

    public String urlencode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")
            ));
        }
        return sb.toString();
    }


    public boolean batchQueryByIsbn(final List<String> isbns){
        if(isbns.size()==0){
            return false;
        }
        try {
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            final int perBatch = 4;
            int pgsize = isbns.size() % perBatch == 0 ? isbns.size() / perBatch : isbns.size() / perBatch + 1;
            List<List<String>> listList = new ArrayList<>();
            for (int i = 0; i < pgsize; i++) {
                int end = (i + 1) * perBatch;
                if (end >= isbns.size()) {
                    end = isbns.size() ;
                }
                listList.add(isbns.subList(i * perBatch, end));
            }
            TimerTask t=new TimerTask() {
                @Override
                public void run() {
                    int idx=atomicInteger.getAndAdd(1);
                    List<String> lst = listList.get( idx == listList.size() ? idx-1
                            : idx);
                    if(atomicInteger.get() > listList.size()){
                        this.cancel();
                    }
                    for (String temp : lst) {
                        BookAutoRequestVo bookAutoRequestVo = CollectionUtils.isEmpty(bookDao.queryBookByIsbn(temp))?
                                queryByIsbn(temp) : null;
                        if(bookAutoRequestVo == null){
                            Batch batch = new Batch();
                            batch.setIsbn(temp);
                            batch.setStatus(1);
                            batch.setFailCause("");
                            batchDao.updateByIsbn(batch);
                            continue;
                        }
                        if (bookAutoRequestVo.getCode().equals(100)) {
                            if (bookAutoRequestVo.getBook() != null) {
                                Book bk = bookAutoRequestVo.getBook();
                                bk.setLanguage("中文");
                                bk.setNumber(1);
                                if(CollectionUtils.isEmpty(bookDao.queryBookByIsbn(temp))) {
                                    bookDao.addBook(bk);
                                }

                                Batch batch = new Batch();
                                batch.setIsbn(temp);
                                batch.setStatus(1);
                                batch.setFailCause("");
                                batchDao.updateByIsbn(batch);
                            }
                        }else{
                            Batch batch=new Batch();
                            batch.setIsbn(temp);
                            batch.setStatus(0);//.getCode());
                            batch.setFailCause(bookAutoRequestVo.getMsg());
                            batchDao.updateByIsbn(batch);
                        }
                    }
                }
            };
            timer.schedule(t,500,1000);
//            sc.scheduleWithFixedDelay(() -> {
//
//            }, 1000, 1000, TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return false;
        }
    }

    public BookAutoRequestVo queryByIsbn(final String isbn){
        BookAutoRequestVo bvo = new BookAutoRequestVo();
        try {
            Book bk = new Book();
            ResponseEntity<Map> resp = restTemplate.getForEntity("https://api.gugudata.com/text/isbn?appkey=" + propert.getAppkey() + "&isbn=" + isbn, Map.class);
            if(resp.getStatusCode().equals(HttpStatus.OK)){
                Map<?,?> mp=resp.getBody();
                if(mp.get("DataStatus")!= null){
                    Map m=(Map)mp.get("DataStatus");
                    Integer status=Integer.parseInt(m.get("StatusCode").toString());
                    if(status==100){
                        if(mp.get("Data")!= null){
                            Map<?,?> map = (Map<?,?>)mp.get("Data");
                            bk.setName(map.get("Title").toString());
                            if(bk.getName()!= null && bk.getName().length()>50){
                                bk.setName(bk.getName().substring(0,50));
                            }
                            bk.setIsbn(map.get("ISBN").toString());
                            bk.setAuthor(map.get("Author").toString());
                            if(bk.getAuthor().contains("\n")){
                                String[] arr=bk.getAuthor().split("\n");
                                StringBuilder sb=new StringBuilder();
                                for(String s:arr) {
                                    sb.append(s.trim());
                                }
                                bk.setAuthor(sb.toString());
                            }
                            bk.setPublish(map.get("Publisher").toString());
                            if(map.get("PublisherDateTime")!= null) {
                                bk.setPubdate(map.get("PublisherDateTime").toString());
                            }
                            if(map.get("BriefIntroduction")!=null){
                                bk.setIntroduction(map.get("BriefIntroduction").toString());
                            }
                            if(map.get("Price")!=null){
                                bk.setPrice(new BigDecimal(map.get("Price").toString()));
                            }

                        }
                        bvo.setBook(bk);
                        bvo.setCode(status);
                        bvo.setMsg("OK");
                    }else if(status== -1){
                        log.error("request failed,isbn="+isbn);
                        bvo.setCode(status);
                        bvo.setMsg("request failed");
                    }else if(status== 501){
                        log.error("params error,isbn="+isbn);
                        bvo.setCode(status);
                        bvo.setMsg("params error");
                    }else if(status==502){
                        log.error("request frequency is be limited,isbn="+isbn);
                        bvo.setCode(status);
                        bvo.setMsg("request frequency is be limited");
                    }else if(status==503){
                        log.error("APPKEY security out of gauge, order expired,isbn="+isbn);
                        bvo.setCode(status);
                        bvo.setMsg("APPKEY security out of gauge, order expired");
                    }else if(status==504){
                        log.error("APPKEY error, appkey="+propert.getAppkey()+",isbn="+isbn);
                        bvo.setCode(status);
                        bvo.setMsg("APPKEY error");
                    }else if(status==505){
                        log.error("The number of requests exceeds the interface limit,isbn="+isbn);
                        bvo.setCode(status);
                        bvo.setMsg("The number of requests exceeds the interface limit");
                    }else if(status== 900){
                        log.error("Interface internal response error,isbn="+isbn);
                        bvo.setCode(status);
                        bvo.setMsg("Interface internal response error");
                    }
                }
            }
            return bvo;
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            bvo.setCode(0);
            bvo.setMsg(e.getMessage());
            return bvo;
        }
    }

//    public Book queryByIsbn(final String isbn) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
//        Calendar cd = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//        String datetime = sdf.format(cd.getTime());
//        // 签名
//        String auth = init(datetime);
//        // 请求方法
//        String method = "POST";
//        // 请求头
//        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("X-Source", source);
//        headers.put("X-Date", datetime);
//        headers.put("Authorization", auth);
//        headers.put("Content-Type","application/json;charset=UTF-8");
//        // 查询参数
//        Map<String, String> queryParams = new HashMap<String, String>();
//
//        // body参数
//        Map<String, String> bodyParams = new HashMap<String, String>();
//        bodyParams.put("isbn", isbn);
//        // url参数拼接
//        String url = propert.getUrl();
//        if (!queryParams.isEmpty()) {
//            url += "?" + urlencode(queryParams);
//        }
//
//        BufferedReader in = null;
//        try {
//            URL realUrl = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setReadTimeout(5000);
//            conn.setRequestMethod(method);
//
//            // request headers
//            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                conn.setRequestProperty(entry.getKey(), entry.getValue());
//            }
//
//            // request body
//            Map<String, Boolean> methods = new HashMap<>();
//            methods.put("POST", true);
//            methods.put("PUT", true);
//            methods.put("PATCH", true);
//            Boolean hasBody = methods.get(method);
//            if (hasBody != null) {
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//                conn.setDoOutput(true);
//                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//                out.writeBytes(urlencode(bodyParams));
//                out.flush();
//                out.close();
//            }
//
//            // 定义 BufferedReader输入流来读取URL的响应
//            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
//            String line;
//            String result = "";
//            while ((line = in.readLine()) != null) {
//                result += line;
//            }
//            if(!StringUtils.isEmpty(result)){
//                QueryByIsBnDto queryByIsBnDto=JSON.parseObject(result, QueryByIsBnDto.class);
//                if(queryByIsBnDto.getCode()==200){
//                    SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Book bk = new Book();
//                    if(!CollectionUtils.isEmpty(queryByIsBnDto.getData().getDetails())) {
//                        Detail detail = queryByIsBnDto.getData().getDetails().get(0);
//                        bk.setIsbn(detail.getIsbn());
//                        bk.setName(detail.getTitle());
//                        bk.setAuthor(detail.getAuthor());
//                        bk.setPublish(detail.getPublisher());
//                        bk.setIntroduction(detail.getGist());
//                        bk.setLanguage(detail.getLanguage());
//                        bk.setPrice(detail.getPrice());
//                        bk.setPubdate(org.springframework.util.StringUtils.isEmpty(detail.getPubDate()) ?
//                                "" :
//                                (detail.getPubDate().length() == 6 ?
//                                    detail.getPubDate().substring(0,4)+"-"+ detail.getPubDate().substring(4, detail.getPubDate().length())+"-01"
//                                    : detail.getPubDate().substring(0,4)+"-"+ detail.getPubDate().substring(4, 6)+"-"+  detail.getPubDate().substring(6, detail.getPubDate().length())
//                                ));
//                        if(!StringUtils.isEmpty(detail.getSubject())){
//                            log.info("subject :"+detail.getSubject()+" of isbn:"+isbn);
//                        }
//                        return bk;
//                    }
//                }
//
//            }
//            return null;
//        } catch (Exception e) {
//            System.out.println(e);
//            log.error("query book failed",e);
//            return null;
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
//    }
}

package com.library.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSON;
import com.library.bean.Book;
import com.library.dto.Detail;
import com.library.dto.QueryByIsBnDto;
import com.mysql.jdbc.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Encoder;

@Service
public class IsBnApiService {
    @Autowired
    private Propert propert;
    String source = "market";
//    @Value("${secretKey}")
//    private String secretKey;
//    @Value("${secretId}")
//    private String secretId;
//    @Value("${url:https://service-ed62t9xo-1305308687.gz.apigw.tencentcs.com/release/isbn/query}")
//    private String queryurl;
    private Logger log = Logger.getLogger(this.getClass());

    public String init(String datetime) {
        try {
            String signStr = "x-date: " + datetime + "\n" + "x-source: " + source;
            Mac mac = Mac.getInstance("HmacSHA1");
            Key sKey = new SecretKeySpec(propert.getSecKey().getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(sKey);
            byte[] hash = mac.doFinal(signStr.getBytes("UTF-8"));
            String sig = new BASE64Encoder().encode(hash);

            String auth = "hmac id=\"" + propert.getSecId() + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-source\", signature=\"" + sig + "\"";
            return auth;
        } catch (Exception e) {
            log.error("init isbn query api failed", e);
            return null;
        }
    }

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

    public Book queryByIsbn(final String isbn) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(cd.getTime());
        // 签名
        String auth = init(datetime);
        // 请求方法
        String method = "POST";
        // 请求头
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Source", source);
        headers.put("X-Date", datetime);
        headers.put("Authorization", auth);
        // 查询参数
        Map<String, String> queryParams = new HashMap<String, String>();

        // body参数
        Map<String, String> bodyParams = new HashMap<String, String>();
        bodyParams.put("isbn", isbn);
        // url参数拼接
        String url = propert.getUrl();
        if (!queryParams.isEmpty()) {
            url += "?" + urlencode(queryParams);
        }

        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);

            // request headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // request body
            Map<String, Boolean> methods = new HashMap<>();
            methods.put("POST", true);
            methods.put("PUT", true);
            methods.put("PATCH", true);
            Boolean hasBody = methods.get(method);
            if (hasBody != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(urlencode(bodyParams));
                out.flush();
                out.close();
            }

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
            if(!StringUtils.isNullOrEmpty(result)){
                QueryByIsBnDto queryByIsBnDto=JSON.parseObject(result, QueryByIsBnDto.class);
                if(queryByIsBnDto.getCode()==200){
                    SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Book bk = new Book();
                    if(!CollectionUtils.isEmpty(queryByIsBnDto.getData().getDetails())) {
                        Detail detail = queryByIsBnDto.getData().getDetails().get(0);
                        bk.setIsbn(detail.getIsbn());
                        bk.setName(detail.getTitle());
                        bk.setAuthor(detail.getAuthor());
                        bk.setPublish(detail.getPublisher());
                        bk.setIntroduction(detail.getGist());
                        bk.setLanguage(detail.getLanguage());
                        bk.setPrice(detail.getPrice());
                        bk.setPubdate(ssdf.parse(detail.getPubDate()));
                        if(!StringUtils.isNullOrEmpty(detail.getSubject())){
                            log.info("subject :"+detail.getSubject()+" of isbn:"+isbn);
                        }
                        return bk;
                    }
                }

            }
            return null;
        } catch (Exception e) {
            System.out.println(e);
            log.error("query book failed",e);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}

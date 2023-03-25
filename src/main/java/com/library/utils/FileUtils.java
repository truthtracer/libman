package com.library.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileUtils.class);

    /**
     * 以utf-8的编码读取文件，拿到所有的行内容
     * @param inputStream
     * @return 每行内容成为list里的一个元素，最后返回List集合
     */
    public static List<String> readFile(InputStream inputStream){
        if(null == inputStream){
            return Collections.emptyList();
        }
        List<String> fileLines = new ArrayList<>();
        InputStreamReader read=null;
        BufferedReader reader = null;
        try{
            read = new InputStreamReader(inputStream,"utf-8");
            reader = new BufferedReader(read);
            String line ;
            while((line= reader.readLine()) != null){
                fileLines.add(line);
            }
            return fileLines;
        }catch (Exception e){
            log.error("读取文件error",e);
            return fileLines;
        }finally {
            if(read!=null){
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(reader !=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

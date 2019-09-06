package com.remote.pageutil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    
    /**
     * 随机生成六位数验证码 
     * @return
     */
    public static int getRandomNum(){
         Random r = new Random();
         return r.nextInt(900000)+100000;//(Math.random()*(999999-100000)+100000)
    }
    
    /**
     * 检测字符串是否不为空(null,"","null")
     * @param s
     * @return 不为空则返回true，否则返回false
     */
    public static boolean notEmpty(String s){
        return s!=null && !"".equals(s) && !"null".equals(s);
    }
    
    /**
     * 检测字符串是否为空(null,"","null")
     * @param s
     * @return 为空则返回true，不否则返回false
     */
    public static boolean isEmpty(String s){
        return s==null || "".equals(s) || "null".equals(s);
    }
    

    /**
     * 写txt里的单行内容
     * @param filePath  文件路径
     * @param content  写入的内容
     */
    public static void writeFile(String fileP,String content){
        String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";  //项目路径
        filePath = (filePath.trim() + fileP.trim()).substring(6).trim();
        if(filePath.indexOf(":") != 1){
            filePath = File.separator + filePath;
        }
        try {
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filePath),"utf-8");      
            BufferedWriter writer=new BufferedWriter(write);          
            writer.write(content);      
            writer.close(); 

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
      * 验证邮箱
      * @param email
      * @return
      */
     public static boolean checkEmail(String email){
      boolean flag = false;
      try{
        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        flag = matcher.matches();
       }catch(Exception e){
        flag = false;
       }
      return flag;
     }
    
     /**
      * 验证手机号码
      * @param mobiles
      * @return
      */
     public static boolean checkMobileNumber(String mobileNumber){
      boolean flag = false;
      try{
        Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
        Matcher matcher = regex.matcher(mobileNumber);
        flag = matcher.matches();
       }catch(Exception e){
        flag = false;
       }
      return flag;
     }
     
     
    /**
     * 读取txt里的单行内容
     * @param filePath  文件路径
     */
    public static String readTxtFile(String fileP) {
        try {
            
//          String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";  //项目路径
            String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""));   //项目路径
            filePath = filePath.replaceAll("file:/", "");
            filePath = filePath.replaceAll("%20", " ");
            filePath = filePath.trim() + fileP.trim();
            if(filePath.indexOf(":") != 1){
                filePath = File.separator + filePath;
            }
            String encoding = "utf-8";
            System.out.println("filePath:"+filePath);
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {       // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file), encoding);   // 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    return lineTxt;
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件,查看此路径是否正确:"+filePath);
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
        }
        return "";
    }
    
}
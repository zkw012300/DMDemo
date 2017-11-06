package com.zspirytus.dmdemo.JavaSource.WebServiceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ZSpirytus on 2017/11/3.
 */

public class WebServiceConnector {

    private static final String TAG = "WebServiceConnector";
    private static final String WSDL_URI = "http://39.108.113.13/DMS.asmx?wsdl";
    private static final String NAMESPACE = "http://zspirytus.org/";
    private static final String METHOD_GETBASICINFOBYSNO = "getBasicInfoBySno";
    public static final String PARAM_SNO = "sno";
    private static final String SOAPACTION = NAMESPACE + METHOD_GETBASICINFOBYSNO;

    private static final String SOAP_HEADER =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>   \n" +
            "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"   \n" +
            "  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"   \n" +
            "  xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">   \n" +
            "  <soap12:Body>";
    private static final String SOAP_END=
            "  </soap12:Body>   \n" +
            "</soap12:Envelope>  ";

    public static InputStream getBasicInfoBySno(ArrayList<String> paramType,ArrayList<String> param) {
        /*String m = "<" + METHOD_GETBASICINFOBYSNO +" xmlns=\"" + NAMESPACE + "\"> ";
        String t = "<" + paramType + ">" + param + "</" + paramType + "> ";
        m = m + t;
        m = m + "</"+ METHOD_GETBASICINFOBYSNO + ">";
        String request = SOAP_HEADER + m  + SOAP_END;*/
        String request = getRequset(METHOD_GETBASICINFOBYSNO,paramType,param);
        try{
            URL url = new URL(WSDL_URI);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            byte[] bytes = request.getBytes("utf-8");
            con.setDoInput (true); //指定该链接是否可以输入
            con.setDoOutput (true); //指定该链接是否可以输出
            con.setUseCaches (false); //指定该链接是否只用caches
            con.setConnectTimeout (6000); // 设置超时时间
            con.setRequestMethod ("POST"); //指定发送方法名，包括Post和Get。
            con.setRequestProperty ("Content-Type", "text/xml;charset=utf-8"); //设置（发送的）内容类型
            con.setRequestProperty ("SOAPAction", SOAPACTION); //指定soapAction
            con.setRequestProperty ("Content-Length", "" + bytes.length); //指定内容长度

            //发送数据
            OutputStream outStream = con.getOutputStream();
            outStream.write (bytes);
            outStream.flush();
            outStream.close();

            //获取数据
            InputStream inputStream = con.getInputStream();
            return inputStream;
        }catch (IOException e){
            return  null;
        }
    }

    private static String getRequset(String methodName,ArrayList<String> paramType,ArrayList<String> params){
        String command = "<" + methodName +" xmlns=\"" + NAMESPACE + "\"> ";
        for(int i = 0;i<params.size();i++)
            command = command + "<" + paramType.get(i) + ">" + params.get(i) + "</" + paramType.get(i) + "> ";
        command = command + "</"+ methodName + ">";
        return SOAP_HEADER + command + SOAP_END;
    }

}
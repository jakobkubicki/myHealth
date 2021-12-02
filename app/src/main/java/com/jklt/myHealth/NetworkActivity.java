package com.jklt.myHealth;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkActivity {
    URL url1 = null;
    HttpURLConnection httpURLConnection= null;
    BufferedReader reader;
    String json=null;
    public String connect(String url){
        try {
            url1= new URL(url);
            httpURLConnection=(HttpURLConnection)url1.openConnection();
            httpURLConnection.connect();
            InputStream in = httpURLConnection.getInputStream();
            reader=new BufferedReader(new InputStreamReader(in));
            StringBuffer buffer= new StringBuffer();
            String line="";
            while((line=reader.readLine())!=null){
                buffer.append(line);
            }
            json=buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return json;
    }
}
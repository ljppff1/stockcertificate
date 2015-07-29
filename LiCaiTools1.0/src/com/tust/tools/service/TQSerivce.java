package com.tust.tools.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

import com.tust.tools.activity.TQMainActivity;
import com.tust.tools.bean.Weather;

public class TQSerivce {
    // 最近四天的天气列表
    public static ArrayList<Weather> weathers = null;
    /*
     * 判断网络连接状态
     */
    public static boolean isNetworkAvailable(Context context) {
           ConnectivityManager cm = (ConnectivityManager)
           context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo info = cm.getActiveNetworkInfo();
           return (info != null && info.isConnected());
     }
    public void getWeather(final Context context, final Intent intent, final String chengshi) {
        if (isNetworkAvailable(context) == true) {
            new Thread() {
                public void run() {
                    try {
                        File file = getInfo(chengshi);
                        if (file != null && file.length() > 30) {
                            weathers = parseJSON(new FileInputStream(file));
                        }
                        if (weathers != null) {
                            Message msg = Message.obtain();
                            msg.what = TQMainActivity.secss;
                            msg.obj = weathers;
                            TQMainActivity.mh.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = TQMainActivity.fail;
                            TQMainActivity.mh.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        Message msg = Message.obtain();
                        msg.what = TQMainActivity.fail;
                        TQMainActivity.mh.sendMessage(msg);
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            Message msg = Message.obtain();
            msg.what = TQMainActivity.nonet;
            TQMainActivity.mh.sendMessage(msg);
        }
    }

    private File getInfo(String city) throws Exception {
        File file = new File(SDrw.SDPATH + "weather/weather.txt");
        URL url = new URL("http://m.weather.com.cn/data/" + city + ".html");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // 设置连接超时为30秒
        conn.setConnectTimeout(30000);
        conn.connect();
        System.out.println("conn.getResponseCode()==" + conn.getResponseCode());
        if (200 == conn.getResponseCode()) {
            InputStream is = conn.getInputStream();
            byte data[] = readInputStream(is);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
            is.close();
            System.out.println("file==" + file.length());
            return file;
        } else {
            Message msg = Message.obtain();
            msg.what = TQMainActivity.fail;
            TQMainActivity.mh.sendMessage(msg);
        }
        return file;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            byteOutputStream.write(buffer, 0, len);
        }
        inputStream.close();
        byte[] data = byteOutputStream.toByteArray();
        byteOutputStream.close();
        return data;
    }

    /*
     * 解析json
     */
    public ArrayList<Weather> parseJSON(InputStream inputStream) throws Exception {
        ArrayList<Weather> weathers = new ArrayList<Weather>();
        byte data[] = readInputStream(inputStream);
        String dataString = new String(data);
        JSONObject jsonData = new JSONObject(dataString);
        if (jsonData != null && jsonData.has("weatherinfo")) {
            Weather weather1 = new Weather();
            JSONObject info = jsonData.getJSONObject("weatherinfo");
            String day = info.getString("date_y");
            String week = info.getString("week");
            String chuanyi = info.getString("index_d");// 穿衣
            String chenlian = info.getString("index_cl");// 晨练
            String city = info.getString("city");// 城市
            weather1.setLoaction(city);
            weather1.setDay(day);
            weather1.setWeek(week);
            weather1.setChuanyi(chuanyi);
            weather1.setChenlian(chenlian);
            for (int i = 1; i <= 6; i++) {
                String tmepString = info.getString("temp" + i);
                String weatherString = info.getString("weather" + i);
                String img1String = info.getString("img" + (i * 2 - 1));
                String img2String = info.getString("img" + (i * 2));
                String windString = info.getString("wind" + i);
                if (i == 1) {
                    weather1.setTemp(tmepString);
                    weather1.setCondition(weatherString);
                    weather1.setImg1("/b" + img1String + ".gif");
                    weather1.setImg2("/b" + img2String + ".gif");
                    weather1.setWind(windString);
                    weathers.add(weather1);
                } else {
                    Weather weather = new Weather();
                    weather.setTemp(tmepString);
                    weather.setCondition(weatherString);
                    weather.setImg1("/b" + img1String + ".gif");
                    weather.setImg2("/b" + img2String + ".gif");
                    weather.setWind(windString);
                    weathers.add(weather);
                }
            }
        }
        return weathers;
    }


}

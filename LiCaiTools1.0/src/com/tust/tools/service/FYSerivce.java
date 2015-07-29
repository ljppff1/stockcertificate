package com.tust.tools.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.widget.Toast;

import com.tust.tools.activity.FYMainActivity;

public class FYSerivce {
	// 当前网络状态
	private boolean ishasNet = false;
	private Context context;

	public FYSerivce(Context context) {
		this.context = context;
		this.ishasNet = isNetworkAvailable(context);
	}

	public void getResult(final String content) {
		if (ishasNet) {
			new Thread() {
				public void run() {
					try {
						String res = getInfo(content);
						if (res != null && !res.equals("")) {
							Message msg = Message.obtain();
							msg.what = FYMainActivity.secss;
							msg.obj = res;
							FYMainActivity.mh.sendMessage(msg);
						} else {
							Message msg = Message.obtain();
							msg.what = FYMainActivity.fail;
							FYMainActivity.mh.sendMessage(msg);
						}
					} catch (Exception e) {
						Message msg = Message.obtain();
						msg.what = FYMainActivity.fail;
						FYMainActivity.mh.sendMessage(msg);
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			Message msg = Message.obtain();
			msg.what = FYMainActivity.nonet;
			FYMainActivity.mh.sendMessage(msg);
		}
	}

	private String getInfo(String content) throws Exception {
		// http://fanyi.youdao.com/openapi.do?keyfrom=wei54544545&key=86156187&type=data&doctype=json&version=1.1&q=%E4%BB%80%E4%B9%88&
		URL url = new URL(
				"http://fanyi.youdao.com/openapi.do?keyfrom=wei54544545" 
		                + "&key=86156187&type=data&doctype=json&version=1.1&q="
						+ URLEncoder.encode(content));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		// 设置连接超时为15秒
		conn.setConnectTimeout(15000);
		conn.connect();
		if (200 == conn.getResponseCode()) {
			InputStream is = conn.getInputStream();
			byte data[] = readInputStream(is);
			JSONArray jsonArray = new JSONArray("[" + new String(data) + "]");
			String message = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject != null) {
					String errorCode = jsonObject.getString("errorCode");
					if (errorCode.equals("20")) {
						Toast.makeText(context, "翻译的文本过长", Toast.LENGTH_SHORT);
					} else if (errorCode.equals("30 ")) {
						Toast.makeText(context, "无法进行有效的翻译", Toast.LENGTH_SHORT);
					} else if (errorCode.equals("40")) {
						Toast.makeText(context, "不支持的语言类型", Toast.LENGTH_SHORT);
					} else if (errorCode.equals("50")) {
						Toast.makeText(context, "无效的key", Toast.LENGTH_SHORT);
					} else {
						// 要翻译的内容
						String query = jsonObject.getString("query");
						message = query;
						// 翻译内容
						String translation = jsonObject
								.getString("translation");
						message = message + "\n" + translation;
						// 有道词典-基本词典
						if (jsonObject.has("basic")) {
							JSONObject basic = jsonObject
									.getJSONObject("basic");
							if (basic.has("phonetic")) {
								String phonetic = basic.getString("phonetic");
								message += "\n\n" + "音标：["+ phonetic +"]";
							}
							if (basic.has("explains")) {
								String explains = basic.getString("explains");
								message += "\n\n基础释义：\n" + explains;
							}
						}
						// 有道词典-网络释义
						if (jsonObject.has("web")) {
							String web = jsonObject.getString("web");
							JSONArray webString = new JSONArray("[" + web + "]");
							message += "\n\n网络释义：";
							JSONArray webArray = webString.getJSONArray(0);
							int count = 0;
							while (!webArray.isNull(count)) {
								if (webArray.getJSONObject(count).has("key")) {
									String key = webArray.getJSONObject(count)
											.getString("key");
									message += "\n\t<" + (count + 1) + ">"
											+ key;
								}
								if (webArray.getJSONObject(count).has("value")) {
									String value = webArray
											.getJSONObject(count).getString(
													"value");
									message += "\n\t   " + value;
								}
								count++;
							}
						}
					}
				}
			}
			System.out.println("message==" + message);
			return message;
		} else {
			Message msg = Message.obtain();
			msg.what = FYMainActivity.fail;
			FYMainActivity.mh.sendMessage(msg);
		}
		return null;
	}

	public byte[] readInputStream(InputStream inputStream) throws IOException {
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
	 * 判断网络连接状态
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

}

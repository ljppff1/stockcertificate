package com.tust.tools.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.tust.tools.R;
import com.tust.tools.service.ImageDownloadHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class StockActivity extends Activity {
	/** Called when the activity is first created. */

	private Button button;
	private EditText editText;
	// private static final String baseUrl =
	// "http://www.webxml.com.cn/WebServices/ChinaStockWebService.asmx/getStockInfoByCode";
	// private static final String imageUrl =
	// "http://www.webxml.com.cn/WebServices/ChinaStockWebService.asmx/getStockImageByCode";
	private static final String baseUrl = "http://hq.sinajs.cn/list=";
	private static final String imageUrl = "http://image.sinajs.cn/newchart/min/n/";
	private HttpResponse httpResponse = null;
	private HttpEntity httpEntity = null;
	private ImageView imageView;

	
	public boolean onKeyDown(int kCode, KeyEvent kEvent) {
		switch (kCode) {
		case KeyEvent.KEYCODE_BACK: {
			//exitDialog();
		//	return false;
		}
		}
		return super.onKeyDown(kCode, kEvent);
	}

	public void exitDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String confrimStr = "";
		String cancelStr = "";
		builder.setTitle("�Ƿ�ȷ���˳���");
		confrimStr = "�˳�С����";
		cancelStr = "�˳���Ʊ��Ϣ";
		builder.setPositiveButton(confrimStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				});

		builder.setNeutralButton(cancelStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(StockActivity.this,
								ToolsMainActivity.class);
						StockActivity.this.startActivity(intent);
						StockActivity.this.finish();
					}
				});
		builder.create().show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stock);
		editText = (EditText) findViewById(R.id.stockEditText);
		button = (Button) findViewById(R.id.stockButton);

		imageView = (ImageView) findViewById(R.id.stockImageView);
		
		((TextView) findViewById(R.id.stockText_tixing))
		.setText("��Ʊ��������ע��������Ϻ���Ʊ�����ڹ�Ʊ�ڴ��������ص��� �����������Ϻ���Ʊ���ڴ���ǰ�� SH�����ڼ� SZ�������ִ�Сд����������ָ֤�� sh000001���չA sz000001��");
		
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// button.performClick();
				// TODO Auto-generated method stub
				String theStockCode = editText.getText().toString();
				String url = baseUrl + theStockCode;
				final String urlimage = imageUrl + theStockCode + ".gif";
				// ����һ���������
				final HttpGet httpGet = new HttpGet(url);
				// ����һ���ͻ���
				final HttpClient httpClient = new DefaultHttpClient();
				// ʹ�ÿͻ��˷�������
				new Thread() {
					public void run() {
						try {
							httpResponse = httpClient.execute(httpGet);
							httpEntity = httpResponse.getEntity();
							InputStream is = null;
							is = httpEntity.getContent();
						  //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
							BufferedReader reader = new BufferedReader(new InputStreamReader(is, "gbk")); 
							String result = "";
							String line = "";
							while ((line = reader.readLine()) != null) {
								result = result + line;
							}
							//System.out.println(result);
							List<String> list = new ArrayList<String>();
							String[] temp = result.split(",");
							Pattern pattern = Pattern
									.compile(".*_([a-z]{2}\\d{6})=\"(.*)$");
							Matcher matcher = pattern.matcher(temp[0]);
							if (matcher.find()) {
								list.add(matcher.group(1));
								list.add(matcher.group(2));
							}
							for (int i = 1; i < temp.length; i++)
								list.add(temp[i]);

							Bitmap bmp = new ImageDownloadHandler()
									.loadImageFromUrl(urlimage);
							imageView.setImageBitmap(bmp);
									
							((TextView) findViewById(R.id.stockText2))
									.setText("��Ʊ���룺" + list.get(0));						
							((TextView) findViewById(R.id.stockText3))
									.setText("��Ʊ���ƣ�" + list.get(1));
							((TextView) findViewById(R.id.stockText4))
									.setText("���տ��̼�(Ԫ)��" + list.get(2));
							((TextView) findViewById(R.id.stockText5))
									.setText("�������̼�(Ԫ)��" + list.get(3));
							((TextView) findViewById(R.id.stockText6))
									.setText("��ǰ�۸� (Ԫ)��" + list.get(4));
							((TextView) findViewById(R.id.stockText7))
									.setText("������߼�(Ԫ)��" + list.get(5));
							((TextView) findViewById(R.id.stockText8))
									.setText("������ͼ�(Ԫ)��" + list.get(6));
							((TextView) findViewById(R.id.stockText9))
									.setText("����� (Ԫ)��" + list.get(7));
							((TextView) findViewById(R.id.stockText10))
									.setText("������ (Ԫ)��" + list.get(8));
							((TextView) findViewById(R.id.stockText11))
									.setText("�ɽ��Ĺ�Ʊ��(��)��" + list.get(9));
							((TextView) findViewById(R.id.stockText12))
									.setText("�ɽ���� (Ԫ)��" + list.get(10));
							((TextView) findViewById(R.id.stockText13))
									.setText("��һ(��)��" + list.get(11));
							((TextView) findViewById(R.id.stockText14))
									.setText("��һ (Ԫ)��" + list.get(12));
							((TextView) findViewById(R.id.stockText15))
									.setText("���(��)��" + list.get(13));
							((TextView) findViewById(R.id.stockText16))
									.setText("��� (Ԫ)��" + list.get(14));
							((TextView) findViewById(R.id.stockText17))
									.setText("����(��)��" + list.get(15));
							((TextView) findViewById(R.id.stockText18))
									.setText("���� (Ԫ)��" + list.get(16));
							((TextView) findViewById(R.id.stockText19))
									.setText("����(��)��" + list.get(17));
							((TextView) findViewById(R.id.stockText20))
									.setText("���� (Ԫ)��" + list.get(18));
							((TextView) findViewById(R.id.stockText21))
									.setText("����(��)��" + list.get(19));
							((TextView) findViewById(R.id.stockText22))
									.setText("����(Ԫ)��" + list.get(20));
							((TextView) findViewById(R.id.stockText23))
									.setText("��һ(��)��" + list.get(21));
							((TextView) findViewById(R.id.stockText24))
									.setText("��һ (Ԫ)��" + list.get(22));
							((TextView) findViewById(R.id.stockText25))
									.setText("����(��)��" + list.get(23));
							((TextView) findViewById(R.id.stockText26))
									.setText("���� (Ԫ)��" + list.get(24));
							((TextView) findViewById(R.id.stockText27))
									.setText("����(��)��" + list.get(25));
							((TextView) findViewById(R.id.stockText28))
									.setText("���� (Ԫ)��" + list.get(26));
							((TextView) findViewById(R.id.stockText29))
									.setText("����(��)��" + list.get(27));
							((TextView) findViewById(R.id.stockText30))
									.setText("���� (Ԫ)��" + list.get(28));
							((TextView) findViewById(R.id.stockText31))
									.setText("����(��)��" + list.get(29));
							((TextView) findViewById(R.id.stockText32))
									.setText("����(Ԫ)��" + list.get(30));
							((TextView) findViewById(R.id.stockText33))
									.setText("���ڣ�" + list.get(31));
							((TextView) findViewById(R.id.stockText34))
									.setText("ʱ�䣺" + list.get(32));

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}.run();
			};

		});

	}
}
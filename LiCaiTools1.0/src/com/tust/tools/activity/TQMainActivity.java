package com.tust.tools.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tust.tools.R;
import com.tust.tools.bean.CityCode;
import com.tust.tools.bean.Weather;
import com.tust.tools.service.GetTime;
import com.tust.tools.service.LoaderImage;
import com.tust.tools.service.TQSerivce;
import com.tust.tools.service.LoaderImage.ImageCallback;

public class TQMainActivity extends Activity implements OnClickListener{
	//TextView���⣨������������ǰ����״̬����ǰ�¶ȣ���ǰʪ�ȣ����ݸ���ʱ��
	private TextView city,condition,wendu,shidu,fengsu;
    //��������,����ͼƬ
	private ImageView sousuo_bt,ivTQ;
	//�����������list
	private ListView listView;
    //�������еı༭��
	private EditText chengshi_et;
	//��ǰ���������ݵ�LinerLayout
	private LinearLayout dangqianll,ll;
	//Handler������̨���½���
    public static MessageHandler mh;
    //��ȡ��������
    private TQSerivce service;

    TelephonyManager tm;
    //�����б�������
    private TQAdatper adatper = null;
    //����ʱ����ʾ����
    private ProgressDialog pd;
    //�������list����
    private ArrayList<Weather> weathers;
    //�첽��������ͼƬ
    LoaderImage loaderImage =null;
    //��ȡ���뷨������
    InputMethodManager imm= null;
    public static final int secss = 1010;//��ȡ�ɹ�
    public static final int fail = 1020;//��ȡʧ��
    public static final int nonet = 1030;//û������
    public static final int SECSSGETWEIZI = 1040;//û������
    private static HashMap<String, String> hm ;
    //�洢�����б����
    ArrayList<CityCode> codeList=null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tq_main);
        service =new TQSerivce();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        loaderImage =new LoaderImage();
        listView = (ListView)this.findViewById(R.id.tq_list);
        imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //��ʼ����������
        init();
        mh = new MessageHandler();
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        TextView gg=(TextView)this.findViewById(R.id.tq_gg_text);
        if(!ToolsMainActivity.isShow){
            gg.setVisibility(View.INVISIBLE);
        }
	}

    public void init() {
    	sousuo_bt = (ImageView) this.findViewById(R.id.tq_mian_serch_ib);
    	sousuo_bt.setOnClickListener(this);
    	city = (TextView)this.findViewById(R.id.tq_dangqian_city_txt);
    	condition = (TextView) this.findViewById(R.id.tq_dangqian_condition_text);
    	wendu = (TextView) this.findViewById(R.id.tq_dangqian_wendu_text);
    	shidu = (TextView) this.findViewById(R.id.tq_dangqian_fengsu_text);
    	fengsu = (TextView) this.findViewById(R.id.tq_dangqian_gengxintime_text);
    	ivTQ = (ImageView)this.findViewById(R.id.tq_dangqian_pic_iv);
    	dangqianll = (LinearLayout)this.findViewById(R.id.tq_dangqian_ll);
    	dangqianll.setVisibility(View.INVISIBLE);
    	ll = (LinearLayout)this.findViewById(R.id.tq_ll);
    	ll.getBackground().setAlpha(200);
    	chengshi_et = (EditText)findViewById(R.id.tq_chengshi_et);
    	chengshi_et.getBackground().setAlpha(200);
    	parseCode();
    }
    
    //����code.txt����HashMap�з����ѯ
    public void parseCode(){
    	codeList = new ArrayList<CityCode>();
    	hm = new HashMap<String, String>();
    	new Thread(){
    		public void run(){
		    	try {
		    		AssetManager assetManager = getAssets();
					InputStream inputStream = assetManager.open("code.txt");
					byte data[] = TQSerivce.readInputStream(inputStream);
					String string=new String(data);
					String s[]=string.split("\\|");
					hm.put("����","101010100");//������forѭ����û�м��룬�����ֶ�����
					for(String code:s){
						String codeOne[] = code.split(",");
						hm.put(codeOne[0], codeOne[1]);
						CityCode cityCode = new CityCode();
						cityCode.setCity(codeOne[0]);
						cityCode.setCode(codeOne[1]);
						codeList.add(cityCode);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	}.start();
    }

	public String getCode(String city){
		if(hm.containsKey(city)){
			return hm.get(city);
		}else{
			if(city.length()>=2){
				for(CityCode code : codeList){
					if(code.getCity().endsWith(city)){
						return code.getCode();
					}
				}
				return "";
			}else{
				return "";
			}
		}
	}
	
    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.tq_mian_serch_ib://������ť
            //�����������
            imm.hideSoftInputFromWindow(chengshi_et.getWindowToken(), 0);
        	//��ȡ����������
//        	String chengshiString = chengshi_et.getText().toString().trim().length()!=0?chengshi_et.getText().toString().trim():"��������";
        	String cityString = chengshi_et.getText().toString().trim();

        	    showDialog("����");//��ʾ����
            	chengshi_et.setText(cityString);
            	Editable ea = chengshi_et.getText();//���ù��������ĩβ
    			Selection.setSelection((Spannable) ea, ea.length());
            	//�����ǰ�б����������������
            	if(weathers!=null){
            		weathers.clear();
            	}
            	//ȥ��������������к���
            	if((cityString.endsWith("��")||cityString.endsWith("��"))&&cityString.length()>=3){
            	    cityString=cityString.substring(0, cityString.length()-1);
            	}
            	String citycode= getCode(cityString);
            	if(citycode==null||citycode.equals("")){
                    if(pd!=null&&pd.isShowing()){
                        pd.cancel();
                    }
            		showMsg("�ó��в�����");
            		return;
            	}
            	//������̨����
            	service.getWeather(this,new Intent(),citycode);
            break;
        }
    }
    
    
    public void showDialog(String info){
        pd = ProgressDialog.show(this, "", "��������"+info+"�����Ժ�...", true, true);
    }
    
    public boolean onKeyDown(int kCode, KeyEvent kEvent) {
        switch (kCode) {
        case KeyEvent.KEYCODE_BACK: {
                exitDialog();
            return false;
        }
        }
        return super.onKeyDown(kCode, kEvent);
    }

    /*
     * �˳�������
     */
    public void exitDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String confrimStr = "";
        String cancelStr = "";
        builder.setTitle("�Ƿ�ȷ���˳���");
        confrimStr = "�˳�С����";
        cancelStr = "�˳�����Ԥ��";
        builder.setPositiveButton(confrimStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
            }
        });
        builder.setNeutralButton(cancelStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(TQMainActivity.this, ToolsMainActivity.class);
                TQMainActivity.this.startActivity(intent);
                TQMainActivity.this.finish();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 200, 0, "�������");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 200:
        	chengshi_et.setText("");
        	if(adatper!=null){
	        	weathers.clear();
	        	adatper.notifyDataSetChanged();
        	}
        	dangqianll.setVisibility(View.INVISIBLE);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MessageHandler extends Handler{
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case secss:
                if(pd!=null&&pd.isShowing()){
                    pd.cancel();
                }
            	weathers =(ArrayList<Weather>)msg.obj;
            	if(weathers==null||weathers.size()<=1){
            		showMsg("������������������");
            		return;
            	}
            	Weather nowWeather =TQSerivce.weathers.get(0);
             	dangqianll.setVisibility(View.VISIBLE);
             	adatper =new TQAdatper();
            	listView.setAdapter(adatper);
            	city.setText(nowWeather.getLoaction());
            	condition.setText("״̬��"+nowWeather.getCondition());
            	wendu.setText("�¶ȣ�"+nowWeather.getTemp());
            	shidu.setText("���£�"+nowWeather.getChuanyi());
            	fengsu.setText("������"+nowWeather.getWind());
            	String imgurl = "";
				if(nowWeather.getImg1().contains("99")){
					imgurl = nowWeather.getImg2();
				}else{
					imgurl = nowWeather.getImg1();
				}
            	if(imgurl!=null&&imgurl.length()>1){
    				Drawable drawable = loaderImage.loadDrawable("http://m.weather.com.cn/img"+imgurl, ivTQ, new ImageCallback() {
    					public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
    						ivTQ.setImageDrawable(imageDrawable);
    					}
    				});
    				if (drawable == null) {
    				} else {
    					ivTQ.setImageDrawable(drawable);
    				}
    			}
            	break;
            case fail:
                if(pd!=null&&pd.isShowing()){
                    pd.cancel();
                }
                showMsg("�������������Ժ�����");
                break;
            case SECSSGETWEIZI:
                if(pd!=null&&pd.isShowing()){
                    pd.cancel();
                }
                showDialog("����");//��ʾ����
                String city = (String)msg.obj;
                chengshi_et.setText(city);
                String citycode= getCode(city);
                if(citycode==null||citycode.equals("")){
                    if(pd!=null&&pd.isShowing()){
                        pd.cancel();
                    }
                    showMsg("�ó��в�����");
                    return;
                }
                //������̨����
                service.getWeather(TQMainActivity.this,new Intent(),citycode);
                break;
            case nonet:
                showMsg("û�����ӵ�����");
                if(pd!=null&&pd.isShowing()){
                    pd.cancel();
                }
                break;
            }
            super.handleMessage(msg);
        }
    }
    
    /*
     * ���ͼƬ�Ժ�Ķ���Ч��
     */
    public void dongHua(View v) {
        v.setAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    private class TQAdatper extends BaseAdapter{

		@Override
		public int getCount() {
			return weathers.size();
		}

		@Override
		public Object getItem(int position) {
			return weathers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(TQMainActivity.this).inflate(R.layout.tq_list_item,null);
			TextView week =(TextView)convertView.findViewById(R.id.tq_item_week);
			TextView date =(TextView)convertView.findViewById(R.id.tq_item_date);
			TextView wendu =(TextView)convertView.findViewById(R.id.tq_item_wendu);
			TextView condition =(TextView)convertView.findViewById(R.id.tq_item_condition);
			final ImageView pic =(ImageView)convertView.findViewById(R.id.tq_item_pic_iv);
			Weather weather = weathers.get(position);
			//��Ϊ����֮��û�����ڣ�ֻ�ܵõ���ǰ����Ȼ������������������
			int today =GetTime.getDay()+position;
			int cdyear = GetTime.getYear();
			int cdmonth = 0;
			int cdday = 0;
			if(GetTime.getMonth()==4||GetTime.getMonth()==6||GetTime.getMonth()==11){
				if(today>30){
					cdmonth = GetTime.getMonth()+1;
					cdday = today-30;
				}else{
					cdmonth = GetTime.getMonth();
					cdday = today;
				}
			}else if(GetTime.getMonth()==2){
				//�ж϶���ʱ�䣨�������꣩
				if(GetTime.getYear()%4!=0&&GetTime.getYear()%1000!=0){
					if(today>28){
						cdmonth = GetTime.getMonth()+1;
						cdday = today-28;
					}else{
						cdmonth = GetTime.getMonth();
						cdday = today;
					}
				}else{
					if(today>29){
						cdmonth = GetTime.getMonth()+1;
						cdday = today-29;
					}else{
						cdmonth = GetTime.getMonth();
						cdday = today;
					}
				}
			}else{
				if(today>31){
					cdmonth = GetTime.getMonth()+1;
					cdday = today-31;
				}else{
					cdmonth = GetTime.getMonth();
					cdday = today;
				}
			}
			//����ʱ�� ��������
			Calendar cd = Calendar.getInstance();
			cd.set(cdyear, cdmonth-1, cdday-1);//��������  �º��ձ����һ
			int weekday = cd.get(Calendar.DAY_OF_WEEK);
			String day="";
			if(weekday==1){
				day="����һ";
			}else if(weekday==2){
				day="���ڶ�";
			}else if(weekday==3){
				day="������";
			}else if(weekday==4){
				day="������";
			}else if(weekday==5){
				day="������";
			}else if(weekday==6){
				day="������";
			}else if(weekday==7){
				day="������";
			}
			date.setText(cdmonth+"��"+cdday+"��");	
			week.setText(day);
			wendu.setText(weather.getTemp());
			condition.setText(weather.getCondition());
			String imgurl = "";
			if(weather.getImg1().contains("99")){
				imgurl = weather.getImg2();
			}else{
				imgurl = weather.getImg1();
			}
			if(imgurl!=null&&imgurl.length()>1){
				Drawable drawable = loaderImage.loadDrawable("http://m.weather.com.cn/img"+imgurl, pic, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl) {
						pic.setImageDrawable(imageDrawable);
					}
				});
				if (drawable == null) {
				} else {
					pic.setImageDrawable(drawable);
				}
			}
			return convertView;
		}
    	
    }

}
package com.hxsmart.icrtest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hxsmart.KTVtest.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginActivity extends Activity {

	private static final boolean isHideSetting = true;//是否隐藏【设置】按钮
	private static final boolean isHideCompanyName = true;//是否隐藏 【公司名称】
	
    private  static final String KEY_IP = "KEYIP" ;
    private  static final String MyPREFERENCES = "LIUJIEWEN" ;
    private  static final String MyPREFERENCES_BAK = "LIUJIEWEN_BAK" ;
    private SharedPreferences sharedpreferences;
    private SharedPreferences sharedpreferencesBak;
    
	private Button connectBtn;
	private Button settingBtn;
	private Button CloseSocketBtn;
	private Button clearFlagBtn;
	private Button PreviewBtn;
	private TextView loadingLog;
	private Handler handler;
	
	private String IP;
	private final static int PORT=8080;
	public static SocketThread socket;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("zbh", "onCreate");
		super.onCreate(savedInstanceState);
		//设置无标题  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        setContentView(R.layout.activity_splash);

        connectBtn = (Button)findViewById(R.id.connectBtn);
        loadingLog =  (TextView)findViewById(R.id.splashLog);
        settingBtn = (Button)findViewById(R.id.settingBtn);
        CloseSocketBtn = (Button)findViewById(R.id.breaksocket);
        clearFlagBtn = (Button)findViewById(R.id.clearFlagBtn);
        PreviewBtn = (Button)findViewById(R.id.PreviewBtn);
        
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sharedpreferencesBak = getSharedPreferences(MyPREFERENCES_BAK, Context.MODE_PRIVATE);
        IP = sharedpreferences.getString(KEY_IP, "192.168.11.254");
        
        //隐藏按钮
        connectBtn.setVisibility(View.INVISIBLE);
        settingBtn.setVisibility(View.INVISIBLE);
        CloseSocketBtn.setVisibility(View.INVISIBLE);
        clearFlagBtn.setVisibility(View.INVISIBLE);
        PreviewBtn.setVisibility(View.INVISIBLE);
        
        settingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
	            intent.setClass(LoginActivity.this, MainActivity.class);
	            startActivity(intent);
	            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 切换动画
			}
		});
        
        CloseSocketBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CloseSocketBtn.setVisibility(View.INVISIBLE);
				socket.socketClose();
				loadingLog.setText("已经断开连接！");
			}
		});
        
        connectBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(socket.SocketConnectStatus())
				{
					loadingLog.setText("已经连接，不能重复连接！\n 请先断开之前的连接");
					CloseSocketBtn.setVisibility(View.VISIBLE);
				}else{
					execFunction();
				}
			}
		});
        
        
        clearFlagBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				SharedPreferences.Editor editor = sharedpreferences.edit();
				String key = null;
				String KEY = null;
				for(int i=1;i<=3;i++)
				{
					for (int j = 0; j < 10; j++) {
						for (int k = 1; k <= 15; k++) {
							key = String.format("%02d", k);
							KEY = ""+(i*10+j)+key;
							//清除状态
							editor.putString(KEY, "\n"+key+"\n");
						}
					}
				}
                editor.commit();
                clearFlagBtn.setTextColor(Color.GRAY);
			}
		});
        
        clearFlagBtn.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				SharedPreferences.Editor editor = sharedpreferences.edit();

				String key = null;
				String KEY = null;
				for(int i=1;i<=3;i++)
				{
					for (int j = 0; j < 10; j++) {
						for (int k = 1; k <= 15; k++) {
							key = String.format("%02d", k);
							KEY = ""+(i*10+j)+key;
							//读备份，再恢复原先的值
							editor.putString(KEY, sharedpreferencesBak.getString(KEY, "\n"+key+"\n"));
						}
					}
				}
                editor.commit();
                clearFlagBtn.setTextColor(Color.BLACK);
				return true;
			}
		});
        
        //直接进入工作界面预览一下
        PreviewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connectBtn.setVisibility(View.VISIBLE);
				clearFlagBtn.setVisibility(View.VISIBLE);
		        settingBtn.setVisibility(View.VISIBLE);
		        if(isHideSetting)
		        {
		        	settingBtn.setVisibility(View.INVISIBLE);
		        }
				Intent intent = new Intent();
	            intent.setClass(LoginActivity.this, WorkingActivity.class);
	            startActivity(intent);
	            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 切换动画
			}
		});
        
       
        handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				if(msg.what==520)
				{
					loadingLog.setText("连接成功！");
					connectBtn.setVisibility(View.VISIBLE);
					clearFlagBtn.setVisibility(View.VISIBLE);
					
			        settingBtn.setVisibility(View.VISIBLE);
			        if(isHideSetting)
			        {
			        	settingBtn.setVisibility(View.INVISIBLE);
			        }
					Intent intent = new Intent();
		            intent.setClass(LoginActivity.this, WorkingActivity.class);
		            startActivity(intent);
		            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 切换动画
				}else{
					loadingLog.setText("连接失败！"+/*(String)msg.obj*/"换个姿势，我们可以再来一次！");
					connectBtn.setVisibility(View.VISIBLE);
					clearFlagBtn.setVisibility(View.VISIBLE);
					PreviewBtn.setVisibility(View.VISIBLE);
					
			        settingBtn.setVisibility(View.VISIBLE);
			        if(isHideSetting)
			        {
			        	settingBtn.setVisibility(View.INVISIBLE);
			        }
				}
				super.handleMessage(msg);
			}
        };
        
        if(socket==null)
        {
        	socket = SocketThread.getSingleInstance();
        	socket.setHandlerAndContext(handler, this);
        }
        //2s后启动 execFunction
        
        handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(socket.SocketConnectStatus())
				{
					loadingLog.setText("已经连接，不能重复连接！\n 请先断开之前的连接");
					connectBtn.setVisibility(View.VISIBLE);
					clearFlagBtn.setVisibility(View.VISIBLE);
					PreviewBtn.setVisibility(View.VISIBLE);
			        CloseSocketBtn.setVisibility(View.VISIBLE);
				}else{
					execFunction();
				}
			}
		}, 2000);
        
        
	}
	
	private void execFunction()
	{
		connectBtn.setVisibility(View.INVISIBLE);
		clearFlagBtn.setVisibility(View.INVISIBLE);
		PreviewBtn.setVisibility(View.INVISIBLE);
        settingBtn.setVisibility(View.INVISIBLE);
		loadingLog.setText("正在尝试连接设备...");
		loadingLog.setText("开始建立socket...");
		startConnectThread();
	}
	
	private void startConnectThread(){
		
		socket.socketClose();
		SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_IP, IP);
        editor.commit();
		if(!socket.SocketConnectStatus()){
			loadingLog.setText("正在连接...");
			socket.setIpAndPort(IP, PORT);
			socket.setHandlerAndContext(handler, this);
	        socket.socketOpen();
		}
	}
	
	private boolean testIP(String IP)
	{
		boolean isReach = false;
		try {
			InetAddress inet = InetAddress.getByName(IP);
			isReach = inet.isReachable(4000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isReach;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i("zbh", "onPause");
		if(!isHideCompanyName)
			Crouton.cancelAllCroutons();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.i("zbh", "onResume");
		loadingLog.setText("欢迎来到掌中2010S");
		IP = sharedpreferences.getString(KEY_IP, "192.168.11.254");
		if(!isHideCompanyName)
			Crouton.makeText(this, "广州市谱凌光电科技有限公司", Style.CONFIRM, R.layout.activity_splash).show();
		super.onResume();
	}

	

}

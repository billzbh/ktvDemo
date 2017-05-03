package com.hxsmart.icrtest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.hxsmart.KTVtest.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;

public class MainActivity extends Activity {

    private SharedPreferences sharedpreferences;
    private SharedPreferences sharedpreferencesBak;
    
    private  static final String MyPREFERENCES = "LIUJIEWEN" ;
    private  static final String MyPREFERENCES_BAK = "LIUJIEWEN_BAK" ;
    private  static final String KEY_IP = "KEYIP";
    private  static final String KEY_MODE = "KEYMODE" ;
    private  static final String KEY_CHANNEL = "KEYCHANNEL" ;
    private  static final int PORT = 8080; 
    private  static final String IP = "192.168.11.254" ;

    private EditText ip_et = null;
    private Button clearButton = null;
    private SeekBar seekBar = null;
    private TextView textView = null;
    private Button bt = null;
    private ProgressDialog processDialog;
    private Spinner spinnerForChannel;
    private static final String[] m_arr = {"  0","  1","  2","  3","  4","  5","  6","  7","  8","  9"};

    private String valueIP = null;
    private int valueCHANNEL = 0;
	public static SocketThread socket;
	


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		clearButton.setEnabled(true);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		
		if(processDialog!=null)
			processDialog.hide();
		super.onPause();
	}
	
	
	
    @Override
	public void onBackPressed() {
    	String hintIP = ip_et.getText().toString();
    	if(hintIP.replaceAll(" ", "").equals(""))
    		hintIP = ip_et.getHint().toString();
    	SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_IP, hintIP);
        editor.commit();
		super.onBackPressed();
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //设置全屏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        setContentView(R.layout.activity_main);
        
        sharedpreferencesBak = getSharedPreferences(MyPREFERENCES_BAK, Context.MODE_PRIVATE);
        
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        valueIP = sharedpreferences.getString(KEY_IP, IP);
        valueCHANNEL = sharedpreferences.getInt(KEY_CHANNEL, 0);

        ip_et = (EditText)findViewById(R.id.editText1);
        clearButton = (Button)findViewById(R.id.clearbutton);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        textView =  (TextView)findViewById(R.id.textView3);
        bt = (Button)findViewById(R.id.button1);
        spinnerForChannel = (Spinner)findViewById(R.id.spinner1);
        
        
        ip_et.setHint(valueIP);
        ip_et.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        seekBar.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        seekBar.setProgress(valueCHANNEL);
        textView.setText(""+valueCHANNEL);
        bt.setVisibility(View.INVISIBLE);
        //杰文的要求，隐藏IP和信号通道设置
//        ip_et.setEnabled(false);
//        seekBar.setVisibility(View.INVISIBLE);
        
        ip_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(KEY_IP, ip_et.getText().toString());
                    editor.commit();
                    valueIP = ip_et.getText().toString();
                    return true;
                }
                return false;
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
			
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
                clearButton.setTextColor(Color.GRAY);
			}
		});
        
        clearButton.setOnLongClickListener(new OnLongClickListener() {
			
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
                clearButton.setTextColor(Color.BLACK);
				return true;
			}
		});

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    valueCHANNEL = progress;
                    textView.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(KEY_CHANNEL, valueCHANNEL);
                editor.commit();

            }
        });
        
        
        ArrayAdapter<String> ada = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_arr);
        ada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForChannel.setAdapter(ada);
        spinnerForChannel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				valueCHANNEL = position;
                textView.setText(""+position);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(KEY_CHANNEL, valueCHANNEL);
                editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});


        Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				if(msg.what==520)
				{
					bt.setEnabled(true);
					processDialog.dismiss();
					Intent intent = new Intent();
		            intent.setClass(MainActivity.this, WorkingActivity.class);
		            startActivity(intent);
		            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 切换动画
				}else{
					processDialog.hide();
					bt.setEnabled(true);
					Toast tosat = Toast.makeText(MainActivity.this, "connect fail："+(String)msg.obj, Toast.LENGTH_SHORT);
					tosat.show();
				}
				
				
				super.handleMessage(msg);
			}
        	
        };
        
        
        
        if(socket==null)
        {
            if(valueIP==null||!valueIP.equalsIgnoreCase(IP))
            	valueIP = IP;
            socket = SocketThread.getSingleInstance();
        	socket.setHandlerAndContext(handler, this);
        }
        
        bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
//				Intent intent = new Intent();
//	            intent.setClass(MainActivity.this, WorkingActivity.class);
//	            startActivity(intent);
//	            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// 切换动画
				
				socket.socketClose();
	            SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(KEY_IP, ip_et.getText().toString());
                editor.commit();
                valueIP = ip_et.getText().toString();
				if(processDialog==null)
					processDialog = ProgressDialog.show(MainActivity.this, "connect device", "connecting"+valueIP+"...", true, false);
				else
				{
					processDialog.setMessage("connecting"+valueIP);
					processDialog.show();
				}
				if(!socket.SocketConnectStatus()){
					socket.setIpAndPort(valueIP, PORT);
			        socket.socketOpen();
				}
			}
		});
    }
}

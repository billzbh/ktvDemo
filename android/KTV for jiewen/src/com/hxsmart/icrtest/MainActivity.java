package com.hxsmart.icrtest;

import com.hxsmart.KTVtest.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;

public class MainActivity extends Activity {

    private SharedPreferences sharedpreferences;
    private  static final String MyPREFERENCES = "LIUJIEWEN" ;
    private  static final String KEY_IP = "KEYIP" ;
    private  static final String KEY_MODE = "KEYMODE" ;
    private  static final String KEY_CHANNEL = "KEYCHANNEL" ;

    private EditText ip_et = null;
    private Button clearButton = null;
    private SeekBar seekBar = null;
    private TextView textView = null;
    private Button bt = null;
    private ProgressDialog processDialog;

    private String valueIP = null;
    private int valueCHANNEL = 0;
	public static SocketThread socket;
	


	@Override
	protected void onDestroy() {
//		socket.socketClose();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        valueIP = sharedpreferences.getString(KEY_IP, "192.168.11.254");
        valueCHANNEL = sharedpreferences.getInt(KEY_CHANNEL, 0);

        ip_et = (EditText)findViewById(R.id.editText1);
        clearButton = (Button)findViewById(R.id.clearbutton);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        textView =  (TextView)findViewById(R.id.textView3);
        bt = (Button)findViewById(R.id.button1);

        ip_et.setText(valueIP);
        ip_et.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        seekBar.setProgress(valueCHANNEL);
        textView.setText(""+valueCHANNEL);


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
				clearButton.setEnabled(false);
				SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("btn1", "\n1\n");
                editor.putString("btn2", "\n2\n");
                editor.putString("btn3", "\n3\n");
                editor.putString("btn4", "\n4\n");
                editor.putString("btn5", "\n5\n");
                editor.putString("btn6", "\n6\n");
                editor.putString("btn7", "\n7\n");
                editor.putString("btn8", "\n8\n");
                editor.putString("btn9", "\n9\n");
                editor.putString("btn10", "\n10\n");
                editor.putString("btn11", "\n11\n");
                editor.putString("btn12", "\n12\n");
                editor.putString("btn13", "\n13\n");
                editor.putString("btn14", "\n14\n");
                editor.putString("btn15", "\n15\n");
                editor.commit();
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
					Toast tosat = Toast.makeText(MainActivity.this, "连接失败："+(String)msg.obj, Toast.LENGTH_SHORT);
					tosat.show();
				}
				
				
				super.handleMessage(msg);
			}
        	
        };
        
        
        
        if(socket==null)
        {
            if(valueIP==null)
            	valueIP = "192.168.100.100";
        	socket = SocketThread.getSingleInstance(handler , this);
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
				bt.setEnabled(false);
	            SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(KEY_IP, ip_et.getText().toString());
                editor.commit();
                valueIP = ip_et.getText().toString();
				if(processDialog==null)
					processDialog = ProgressDialog.show(MainActivity.this, "连接网络", "正在连接"+valueIP+"...", true, false);
				else
				{
					processDialog.setMessage("正在连接"+valueIP);
					processDialog.show();
				}
				if(!socket.SocketConnectStatus()){
					socket.setIpAndPort(valueIP, 1200);
			        socket.socketOpen();
				}
			}
		});

    }
}

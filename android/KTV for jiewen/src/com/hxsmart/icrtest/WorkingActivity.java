package com.hxsmart.icrtest;

import java.util.ArrayList;

import me.imid.view.SwitchButton;

import com.hxsmart.KTVtest.R;
import com.thunder.wheel.view.CharWheelAdapter;
import com.thunder.wheel.view.NumericWheelAdapter;
import com.thunder.wheel.view.OnWheelChangedListener;
import com.thunder.wheel.view.OnWheelClickedListener;
import com.thunder.wheel.view.OnWheelLongPressListener;
import com.thunder.wheel.view.WheelScroller.SpeedListener;
import com.thunder.wheel.view.WheelView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


public class WorkingActivity extends Activity {
	
	private static SocketThread socket;
	private byte[] sendBytes = new byte[3];
	private byte[] sendBytes2 = new byte[2];
	
	private int channel;
	ArrayList<View> viewContainter = new ArrayList<View>();
	private ViewPager pager;
	
	
	private PopupWindow pwMyPopWindow;// popupwindow
	private TextView tv;// popupwindow中的ListView
	private int NUM_OF_VISIBLE_LIST_ROWS = 4;// 指定popupwindow中Item的数目
	
	
	private ToggleButton tmpButton;
	private byte tmpByte;
	private boolean isDOWN3 = false;
	private int count = 0;
	
	private int whichG = 1;//翻页按钮位置
	private int GValue;//滚筒的值
	
	private int lastProcess1 = 0;
//	private int lastProcess2 = 0;
//	private int lastProcess3 = 0;
//	private int lastProcess4 = 0;
//	private int lastProcess5 = 0;
//	private int lastProcess6 = 0;
//	private int lastProcess7 = 0;
//	private int lastProcess8 = 0;
//	private int lastProcess9 = 0;
//	private int lastProcess10 = 0;
//	private int lastProcess11 = 0;
//	private int lastProcess12 = 0;
//	private int lastProcess13 = 0;
//	private int lastProcess14 = 0;
//	private int lastProcess15 = 0;

	private String btnOnText1;
	private String btnOnText2;
	private String btnOnText3;
	private String btnOnText4;
	private String btnOnText5;
	private String btnOnText6;
	private String btnOnText7;
	private String btnOnText8;
	private String btnOnText9;
	private String btnOnText10;
	private String btnOnText11;
	private String btnOnText12;
	private String btnOnText13;
	private String btnOnText14;
	private String btnOnText15;
	
	//view元素
	
	private VerticalSeekBar verticalSeekBar1 = null;
	private VerticalSeekBar verticalSeekBar2 = null;
	private VerticalSeekBar verticalSeekBar3 = null;
	private VerticalSeekBar verticalSeekBar4 = null;
	private VerticalSeekBar verticalSeekBar5 = null;
	private VerticalSeekBar verticalSeekBar6 = null;
	private VerticalSeekBar verticalSeekBar7 = null;
	private VerticalSeekBar verticalSeekBar8 = null;
	private VerticalSeekBar verticalSeekBar9 = null;
	private VerticalSeekBar verticalSeekBar10 = null;
	private VerticalSeekBar verticalSeekBar11 = null;
	private VerticalSeekBar verticalSeekBar12 = null;
	private VerticalSeekBar verticalSeekBar13 = null;
	private VerticalSeekBar verticalSeekBar14 = null;
	private VerticalSeekBar verticalSeekBar15 = null;
	
	private TextView tip1;
	private TextView tip2;
	private TextView tip3;
	private TextView tip4;
	private TextView tip5;
	private TextView tip6;
	private TextView tip7;
	private TextView tip8;
	private TextView tip9;
	private TextView tip10;
	private TextView tip11;
	private TextView tip12;
	private TextView tip13;
	private TextView tip14;
	private TextView tip15;
	
	private ToggleButton btn1;
	private ToggleButton btn2;
	private ToggleButton btn3;
	private ToggleButton btn4;
	private ToggleButton btn5;
	private ToggleButton btn6;
	private ToggleButton btn7;
	private ToggleButton btn8;
	private ToggleButton btn9;	
	private ToggleButton btn10;
	private ToggleButton btn11;
	private ToggleButton btn12;	
	private ToggleButton btn13;
	private ToggleButton btn14;
	private ToggleButton btn15;
	
	private WheelView wheelA;
	private WheelView wheelB;
	private WheelView wheelG;
	
	private EditText et;
	private SwitchButton st;
	private TextView mode_tv;
	private RadioGroup radioGroup;
	
    private SharedPreferences sharedpreferences;
    private  static final String MyPREFERENCES = "LIUJIEWEN" ;
    private  static final String KEY_IP = "KEYIP" ;
    private  static final String KEY_MODE = "KEYMODE" ;
    private  static final String KEY_CHANNEL = "KEYCHANNEL" ;
    private int DMpixels;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      //设置无标题  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        setContentView(R.layout.activity_fullscreen);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        DMpixels = dm.heightPixels/250;
        
        iniPopupWindow();

     
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				if(checkedId==R.id.radioButton1){
					whichG = 1;

					sendBytes2[1] = (byte)GValue;
					wheelG.setViewAdapter(new CharWheelAdapter(WorkingActivity.this, 0, 9,1));
					
				}else if(checkedId==R.id.radioButton2){
					whichG = 2;
					sendBytes2[1] = (byte)(GValue+10);
					wheelG.setViewAdapter(new CharWheelAdapter(WorkingActivity.this, 0, 9,2));
				}else if(checkedId==R.id.radioButton3){
					whichG = 3;
					
					sendBytes2[1] = (byte)(GValue+20);
					wheelG.setViewAdapter(new CharWheelAdapter(WorkingActivity.this, 0, 9,3));
				}
				
				socket.socketSendData(sendBytes2);
//				System.out.format("Sended===换页按钮RadioGroup==:");
//				for (int i = 0; i < sendBytes2.length; i++) {
//					System.out.format("%02X ", sendBytes2[i]);
//				}
//				System.out.println();
			}
		});
        
        //获取状态
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        channel = sharedpreferences.getInt(KEY_CHANNEL, 0);
        
        btnOnText1 = sharedpreferences.getString("btn1", null);
        btnOnText2 = sharedpreferences.getString("btn2", null);
        btnOnText3 = sharedpreferences.getString("btn3", null);
        btnOnText4 = sharedpreferences.getString("btn4", null);
        btnOnText5 = sharedpreferences.getString("btn5", null);
        btnOnText6 = sharedpreferences.getString("btn6", null);
        btnOnText7 = sharedpreferences.getString("btn7", null);
        btnOnText8 = sharedpreferences.getString("btn8", null);
        btnOnText9 = sharedpreferences.getString("btn9", null);
        btnOnText10 = sharedpreferences.getString("btn10", null);
        btnOnText11 = sharedpreferences.getString("btn11", null);
        btnOnText12 = sharedpreferences.getString("btn12", null);
        btnOnText13 = sharedpreferences.getString("btn13", null);
        btnOnText14 = sharedpreferences.getString("btn14", null);
        btnOnText15 = sharedpreferences.getString("btn15", null);
        
        
        pager = (ViewPager) this.findViewById(R.id.viewpager);
        View view1 = LayoutInflater.from(this).inflate(R.layout.fiel1, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.fiel2, null);
        View view3 = LayoutInflater.from(this).inflate(R.layout.fiel3, null);
        //viewpager开始添加view
        viewContainter.add(view1);
        viewContainter.add(view2);
        viewContainter.add(view3);
        
        pager.setOffscreenPageLimit(2);//预加载2页
        pager.setAdapter(new PagerAdapter() {
			
        	//viewpager中的组件数量
            @Override
            public int getCount() {
                return viewContainter.size();
            }
          //滑动切换的时候销毁当前的组件
            @Override
            public void destroyItem(ViewGroup container, int position,
                    Object object) {
                ((ViewPager) container).removeView(viewContainter.get(position));
            }
          //每次滑动的时候生成的组件
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
            }
 
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
 
            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

		});
        
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            	
            	//TODO 是否为触摸模式
            	if(mode_tv.getText().equals("触摸模式"))
            	{
            		if(isDOWN3)
                	{
                		count = 0;
                		isDOWN3 = false;
                	}else
                	{
                		count ++;
//                    	System.out.printf("AAAAAAAAAAAAAAAAA== %d",count);
                    	if(count%2 == 0)
                    	{
                    		if(tmpButton==null)
                        		return;
                        	tmpButton.setChecked(false);
            				sendBytes[1]=tmpByte;
            				sendBytes[2]=(byte)lastProcess1;
            				socket.socketSendData(sendBytes);
//            				System.out.format("===========================");
//            				for (int i = 0; i < sendBytes.length; i++) {
//            					System.out.format("%02X ", sendBytes[i]);
//            				}
//            				System.out.println();
                    	}
                	}
            	}
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            	
            }
 
            @Override
            public void onPageSelected(int arg0) {
            	if(mode_tv.getText().equals("触摸模式"))
            		isDOWN3 = true;
            }
        });
        
        verticalSeekBar1 = (VerticalSeekBar) view1.findViewById(R.id.verticalSeekBar1);
        verticalSeekBar2 = (VerticalSeekBar) view1.findViewById(R.id.verticalSeekBar2);
        verticalSeekBar3 = (VerticalSeekBar) view1.findViewById(R.id.verticalSeekBar3);
        verticalSeekBar4 = (VerticalSeekBar) view1.findViewById(R.id.verticalSeekBar4);
        verticalSeekBar5 = (VerticalSeekBar) view1.findViewById(R.id.verticalSeekBar5);
        verticalSeekBar6 = (VerticalSeekBar) view2.findViewById(R.id.verticalSeekBar6);
        verticalSeekBar7 = (VerticalSeekBar) view2.findViewById(R.id.verticalSeekBar7);
        verticalSeekBar8 = (VerticalSeekBar) view2.findViewById(R.id.verticalSeekBar8);
        verticalSeekBar9 = (VerticalSeekBar) view2.findViewById(R.id.verticalSeekBar9);
        verticalSeekBar10 = (VerticalSeekBar) view2.findViewById(R.id.verticalSeekBar10);
        verticalSeekBar11 = (VerticalSeekBar) view3.findViewById(R.id.verticalSeekBar11);
        verticalSeekBar12 = (VerticalSeekBar) view3.findViewById(R.id.verticalSeekBar12);
        verticalSeekBar13 = (VerticalSeekBar) view3.findViewById(R.id.verticalSeekBar13);
        verticalSeekBar14 = (VerticalSeekBar) view3.findViewById(R.id.verticalSeekBar14);
        verticalSeekBar15 = (VerticalSeekBar) view3.findViewById(R.id.verticalSeekBar15);
        
        tip1 = (TextView)view1.findViewById(R.id.tip1);
        tip2 = (TextView)view1.findViewById(R.id.tip2);
        tip3 = (TextView)view1.findViewById(R.id.tip3);
        tip4 = (TextView)view1.findViewById(R.id.tip4);
        tip5 = (TextView)view1.findViewById(R.id.tip5);
        tip6 = (TextView)view2.findViewById(R.id.tip6);
        tip7 = (TextView)view2.findViewById(R.id.tip7);
        tip8 = (TextView)view2.findViewById(R.id.tip8);
        tip9 = (TextView)view2.findViewById(R.id.tip9);
        tip10 = (TextView)view2.findViewById(R.id.tip10);
        tip11 = (TextView)view3.findViewById(R.id.tip11);
        tip12 = (TextView)view3.findViewById(R.id.tip12);
        tip13 = (TextView)view3.findViewById(R.id.tip13);
        tip14 = (TextView)view3.findViewById(R.id.tip14);
        tip15 = (TextView)view3.findViewById(R.id.tip15);
        
        
        btn1 = (ToggleButton) view1.findViewById(R.id.button1);
        btn2 = (ToggleButton) view1.findViewById(R.id.button2);
        btn3 = (ToggleButton) view1.findViewById(R.id.button3);
        btn4 = (ToggleButton) view1.findViewById(R.id.button4);
        btn5 = (ToggleButton) view1.findViewById(R.id.button5);
        btn6 = (ToggleButton) view2.findViewById(R.id.button6);
        btn7 = (ToggleButton) view2.findViewById(R.id.button7);
        btn8 = (ToggleButton) view2.findViewById(R.id.button8);
        btn9 = (ToggleButton) view2.findViewById(R.id.button9);
        btn10 = (ToggleButton) view2.findViewById(R.id.button10);
        btn11 = (ToggleButton) view3.findViewById(R.id.button11);
        btn12 = (ToggleButton) view3.findViewById(R.id.button12);
        btn13 = (ToggleButton) view3.findViewById(R.id.button13);
        btn14 = (ToggleButton) view3.findViewById(R.id.button14);
        btn15 = (ToggleButton) view3.findViewById(R.id.button15);
        
        btn1.setChecked(false);
        btn2.setChecked(false);
        btn3.setChecked(false);
        btn4.setChecked(false);
        btn5.setChecked(false);
        btn6.setChecked(false);
        btn7.setChecked(false);
        btn8.setChecked(false);
        btn9.setChecked(false);
        btn10.setChecked(false);
        btn11.setChecked(false);
        btn12.setChecked(false);
        btn13.setChecked(false);
        btn14.setChecked(false);
        btn15.setChecked(false);
        
        btn1.setTextOn(btnOnText1==null?"\n1\n":btnOnText1);
        btn2.setTextOn(btnOnText2==null?"\n2\n":btnOnText2);
        btn3.setTextOn(btnOnText3==null?"\n3\n":btnOnText3);
        btn4.setTextOn(btnOnText4==null?"\n4\n":btnOnText4);
        btn5.setTextOn(btnOnText5==null?"\n5\n":btnOnText5);
        btn6.setTextOn(btnOnText6==null?"\n6\n":btnOnText6);
        btn7.setTextOn(btnOnText7==null?"\n7\n":btnOnText7);
        btn8.setTextOn(btnOnText8==null?"\n8\n":btnOnText8);
        btn9.setTextOn(btnOnText9==null?"\n9\n":btnOnText9);
        btn10.setTextOn(btnOnText10==null?"\n10\n":btnOnText10);
        btn11.setTextOn(btnOnText11==null?"\n11\n":btnOnText11);
        btn12.setTextOn(btnOnText12==null?"\n12\n":btnOnText12);
        btn13.setTextOn(btnOnText13==null?"\n13\n":btnOnText13);
        btn14.setTextOn(btnOnText14==null?"\n14\n":btnOnText14);
        btn15.setTextOn(btnOnText15==null?"\n15\n":btnOnText15);
        
        btn1.setTextOff(btnOnText1==null?"\n1\n":btnOnText1);
        btn2.setTextOff(btnOnText2==null?"\n2\n":btnOnText2);
        btn3.setTextOff(btnOnText3==null?"\n3\n":btnOnText3);
        btn4.setTextOff(btnOnText4==null?"\n4\n":btnOnText4);
        btn5.setTextOff(btnOnText5==null?"\n5\n":btnOnText5);
        btn6.setTextOff(btnOnText6==null?"\n6\n":btnOnText6);
        btn7.setTextOff(btnOnText7==null?"\n7\n":btnOnText7);
        btn8.setTextOff(btnOnText8==null?"\n8\n":btnOnText8);
        btn9.setTextOff(btnOnText9==null?"\n9\n":btnOnText9);
        btn10.setTextOff(btnOnText10==null?"\n10\n":btnOnText10);
        btn11.setTextOff(btnOnText11==null?"\n11\n":btnOnText11);
        btn12.setTextOff(btnOnText12==null?"\n12\n":btnOnText12);
        btn13.setTextOff(btnOnText13==null?"\n13\n":btnOnText13);
        btn14.setTextOff(btnOnText14==null?"\n14\n":btnOnText14);
        btn15.setTextOff(btnOnText15==null?"\n15\n":btnOnText15);
        
        btn1.setText(btnOnText1==null?"\n1\n":btnOnText1);
        btn2.setText(btnOnText2==null?"\n2\n":btnOnText2);
        btn3.setText(btnOnText3==null?"\n3\n":btnOnText3);
        btn4.setText(btnOnText4==null?"\n4\n":btnOnText4);
        btn5.setText(btnOnText5==null?"\n5\n":btnOnText5);
        btn6.setText(btnOnText6==null?"\n6\n":btnOnText6);
        btn7.setText(btnOnText7==null?"\n7\n":btnOnText7);
        btn8.setText(btnOnText8==null?"\n8\n":btnOnText8);
        btn9.setText(btnOnText9==null?"\n9\n":btnOnText9);
        btn10.setText(btnOnText10==null?"\n10\n":btnOnText10);
        btn11.setText(btnOnText11==null?"\n11\n":btnOnText11);
        btn12.setText(btnOnText12==null?"\n12\n":btnOnText12);
        btn13.setText(btnOnText13==null?"\n13\n":btnOnText13);
        btn14.setText(btnOnText14==null?"\n14\n":btnOnText14);
        btn15.setText(btnOnText15==null?"\n15\n":btnOnText15);
        
        //模式选择
        mode_tv = (TextView)findViewById(R.id.modetv);
        
        st = (SwitchButton)findViewById(R.id.switchButton1);
        st.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					//移除longclick, onChecked 的监听器
					//添加 touchEvent 的监听
					//将15个按键的checked状态置为false 
					tmpButton = null;
					removeToggleButtonListener(btn1);
					initToggleButtonTouchEvent(verticalSeekBar1,btn1,(byte)0x00);
					removeToggleButtonListener(btn2);
					initToggleButtonTouchEvent(verticalSeekBar2,btn2,(byte)0x01);
					removeToggleButtonListener(btn3);
					initToggleButtonTouchEvent(verticalSeekBar3,btn3,(byte)0x02);
					removeToggleButtonListener(btn4);
					initToggleButtonTouchEvent(verticalSeekBar4,btn4,(byte)0x03);
					removeToggleButtonListener(btn5);
					initToggleButtonTouchEvent(verticalSeekBar5,btn5,(byte)0x04);
					removeToggleButtonListener(btn6);
					initToggleButtonTouchEvent(verticalSeekBar6,btn6,(byte)0x05);
					removeToggleButtonListener(btn7);
					initToggleButtonTouchEvent(verticalSeekBar7,btn7,(byte)0x06);
					removeToggleButtonListener(btn8);
					initToggleButtonTouchEvent(verticalSeekBar8,btn8,(byte)0x07);
					removeToggleButtonListener(btn9);
					initToggleButtonTouchEvent(verticalSeekBar9,btn9,(byte)0x08);
					removeToggleButtonListener(btn10);
					initToggleButtonTouchEvent(verticalSeekBar10,btn10,(byte)0x09);
					removeToggleButtonListener(btn11);
					initToggleButtonTouchEvent(verticalSeekBar11,btn11,(byte)0x0a);
					removeToggleButtonListener(btn12);
					initToggleButtonTouchEvent(verticalSeekBar12,btn12,(byte)0x0b);
					removeToggleButtonListener(btn13);
					initToggleButtonTouchEvent(verticalSeekBar13,btn13,(byte)0x0c);
					removeToggleButtonListener(btn14);
					initToggleButtonTouchEvent(verticalSeekBar14,btn14,(byte)0x0d);
					removeToggleButtonListener(btn15);
					initToggleButtonTouchEvent(verticalSeekBar15,btn15,(byte)0x0e);
					mode_tv.setText("触摸模式");
				}else{
					
					//添加 longclick, onChecked 的监听器
					//移除 touchEvent 的监听
					initToggleButton(verticalSeekBar1, btn1, "btn1" ,(byte) 0x00);
					initToggleButton(verticalSeekBar2, btn2, "btn2" ,(byte) 0x01);
					initToggleButton(verticalSeekBar3, btn3, "btn3" ,(byte) 0x02);
					initToggleButton(verticalSeekBar4, btn4, "btn4" ,(byte) 0x03);
					initToggleButton(verticalSeekBar5, btn5, "btn5" ,(byte) 0x04);
					initToggleButton(verticalSeekBar6, btn6, "btn6" ,(byte) 0x05);
					initToggleButton(verticalSeekBar7, btn7, "btn7" ,(byte) 0x06);
					initToggleButton(verticalSeekBar8, btn8, "btn8" ,(byte) 0x07);
					initToggleButton(verticalSeekBar9, btn9, "btn9" ,(byte) 0x08);
					initToggleButton(verticalSeekBar10, btn10, "btn10" ,(byte) 0x09);
					initToggleButton(verticalSeekBar11, btn11, "btn11" ,(byte) 0x0a);
					initToggleButton(verticalSeekBar12, btn12, "btn12" ,(byte) 0x0b);
					initToggleButton(verticalSeekBar13, btn13, "btn13" ,(byte) 0x0c);
					initToggleButton(verticalSeekBar14, btn14, "btn14" ,(byte) 0x0d);
					initToggleButton(verticalSeekBar15, btn15, "btn15" ,(byte) 0x0e);
					removeToggleButtonTouchEvent(btn1);
					removeToggleButtonTouchEvent(btn2);
					removeToggleButtonTouchEvent(btn3);
					removeToggleButtonTouchEvent(btn4);
					removeToggleButtonTouchEvent(btn5);
					removeToggleButtonTouchEvent(btn6);
					removeToggleButtonTouchEvent(btn7);
					removeToggleButtonTouchEvent(btn8);
					removeToggleButtonTouchEvent(btn9);
					removeToggleButtonTouchEvent(btn10);
					removeToggleButtonTouchEvent(btn11);
					removeToggleButtonTouchEvent(btn12);
					removeToggleButtonTouchEvent(btn13);
					removeToggleButtonTouchEvent(btn14);
					removeToggleButtonTouchEvent(btn15);
					mode_tv.setText("按键模式");
				}
			}
		});
        
        
        //转轮相关代码
        wheelA = (WheelView) findViewById(R.id.wheelView1);
        wheelA.setViewAdapter(new NumericWheelAdapter(this, 0, 65532));
        wheelA.setCyclic(true);
        wheelB = (WheelView) findViewById(R.id.wheelView2);
        wheelB.setViewAdapter(new NumericWheelAdapter(this, 0, 65532));
        wheelB.setCyclic(true);
        
        wheelG = (WheelView) findViewById(R.id.charWheel);
        wheelG.setViewAdapter(new CharWheelAdapter(this, 0, 9,1));
        wheelG.setCyclic(true);
		
		wheelA.setSpeedListenner(new SpeedListener() {
			
			@Override
			public void onSpeed(int value) {
				// TODO Auto-generated method stub
				int speed = (Math.abs(value)/(18*DMpixels)+1);
				if(speed>10)
					speed = 10;
				Log.i("zbh", ""+ speed);
			}
		});
		
		wheelB.setSpeedListenner(new SpeedListener() {
			
			@Override
			public void onSpeed(int value) {
				int speed = (Math.abs(value)/(18*DMpixels)+1);
				if(speed>10)
					speed = 10;
				Log.i("zbh", ""+ speed);
			}
		});
		
		wheelG.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.i("zbh", ""+newValue);
				GValue = newValue+1;
				sendBytes2[1]=(byte)(GValue + (whichG-1)*10);
				socket.socketSendData(sendBytes2);
//				System.out.format("wheelG 滚轮=====:");
//				for (int i = 0; i < sendBytes2.length; i++) {
//					System.out.format("%02X ", sendBytes2[i]);
//				}
//				System.out.println();
			}
		});
		
		wheelG.addClickingListener(new OnWheelClickedListener() {
			
			@Override
			public void onItemClicked(WheelView wheel, int itemIndex) {
				// TODO Auto-generated method stub
				Log.i("zbh", "onItemClicked==="+itemIndex);
				String key = ""+(whichG*10+itemIndex);
				tv.setText(""+sharedpreferences.getString(key, "(＞﹏＜)"));
				pwMyPopWindow.showAsDropDown(wheelG);
			}
		});
		
		wheelG.setOnLongClickListener(new OnWheelLongPressListener() {
			
			@Override
			public void onItemLongPressed(View Textview, final int itemIndex) {
				// TODO Auto-generated method stub
				et = new EditText(WorkingActivity.this);

				new AlertDialog.Builder(WorkingActivity.this)
				.setTitle("请输入新标识").setIcon(android.R.drawable.ic_dialog_info)
				.setView(et)
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String key = ""+(whichG*10+itemIndex);
						String text = et.getText().toString();
		                SharedPreferences.Editor editor = sharedpreferences.edit();
		                editor.putString(key, text);
		                editor.commit();
					}
				})
				.setNegativeButton("取消", null).show();
			}
		});

		
		//初始化 seekbar 相关业务
		initSeekbar(verticalSeekBar1, tip1,(byte) 0x00);
		initSeekbar(verticalSeekBar2, tip2,(byte) 0x01);
		initSeekbar(verticalSeekBar3, tip3,(byte) 0x02);
		initSeekbar(verticalSeekBar4, tip4,(byte) 0x03);
		initSeekbar(verticalSeekBar5, tip5,(byte) 0x04);
		initSeekbar(verticalSeekBar6, tip6,(byte) 0x05);
		initSeekbar(verticalSeekBar7, tip7,(byte) 0x06);
		initSeekbar(verticalSeekBar8, tip8,(byte) 0x07);
		initSeekbar(verticalSeekBar9, tip9,(byte) 0x08);
		initSeekbar(verticalSeekBar10, tip10,(byte) 0x09);
		initSeekbar(verticalSeekBar11, tip11,(byte) 0x0a);
		initSeekbar(verticalSeekBar12, tip12,(byte) 0x0b);
		initSeekbar(verticalSeekBar13, tip13,(byte) 0x0c);
		initSeekbar(verticalSeekBar14, tip14,(byte) 0x0d);
		initSeekbar(verticalSeekBar15, tip15,(byte) 0x0e);
		
		//初始化 toggle 相关业务
		initToggleButton(verticalSeekBar1, btn1, "btn1" ,(byte) 0x00);
		initToggleButton(verticalSeekBar2, btn2, "btn2" ,(byte) 0x01);
		initToggleButton(verticalSeekBar3, btn3, "btn3" ,(byte) 0x02);
		initToggleButton(verticalSeekBar4, btn4, "btn4" ,(byte) 0x03);
		initToggleButton(verticalSeekBar5, btn5, "btn5" ,(byte) 0x04);
		initToggleButton(verticalSeekBar6, btn6, "btn6" ,(byte) 0x05);
		initToggleButton(verticalSeekBar7, btn7, "btn7" ,(byte) 0x06);
		initToggleButton(verticalSeekBar8, btn8, "btn8" ,(byte) 0x07);
		initToggleButton(verticalSeekBar9, btn9, "btn9" ,(byte) 0x08);
		initToggleButton(verticalSeekBar10, btn10, "btn10" ,(byte) 0x09);
		initToggleButton(verticalSeekBar11, btn11, "btn11" ,(byte) 0x0a);
		initToggleButton(verticalSeekBar12, btn12, "btn12" ,(byte) 0x0b);
		initToggleButton(verticalSeekBar13, btn13, "btn13" ,(byte) 0x0c);
		initToggleButton(verticalSeekBar14, btn14, "btn14" ,(byte) 0x0d);
		initToggleButton(verticalSeekBar15, btn15, "btn15" ,(byte) 0x0e);

		
        socket = MainActivity.socket;
        if(socket != null)
        	Log.i("zbh", "socket != null ");
    }
    
    
    private void initSeekbar(VerticalSeekBar verticalSeekBar,final TextView tip,final byte sendbyte1)
    {
    	verticalSeekBar.setMax(255);
    	verticalSeekBar.setEnabled(true);
    	verticalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				tip.setText(""+progress);

				sendBytes[1]=sendbyte1;
				sendBytes[2]=(byte)(progress/2);
				socket.socketSendData(sendBytes);		
//				System.out.format("Sended=推杆====:");
//				for (int i = 0; i < sendBytes.length; i++) {
//					System.out.format("%02X ", sendBytes[i]);
//				}
//				System.out.println();

			}
		});
    }
    
    private void initToggleButton(final VerticalSeekBar verticalSeekBar,final ToggleButton btn,final String key,final byte sendbyte1)
    {
    	btn.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				et = new EditText(WorkingActivity.this);
				
				new AlertDialog.Builder(WorkingActivity.this)
				.setTitle("请输入新标识").setIcon(android.R.drawable.ic_dialog_info)
				.setView(et)
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String tipString = null;
						String text = et.getText().toString();
						
						if(text.length()<=4)
						{
							tipString = "\n"+text+"\n";
							
						}else if(text.length()>4 && text.length() <= 8)
						{
							String substring = text.substring(4);
							tipString = text.substring(0,4)+"\n"+substring+"\n";
						}else if(text.length() > 8 && text.length() <= 12)
						{
							String substring1 = text.substring(4,8);
							String substring2 = text.substring(8);
							tipString = text.substring(0,4)+"\n"+substring1+"\n"+substring2;
						}else{
							String text1 = text.substring(0, 12);
							String substring1 = text1.substring(4,8);
							String substring2 = text1.substring(8);
							tipString = text1.substring(0,4)+"\n"+substring1+"\n"+substring2;
						}
							
						btn.setTextOn(tipString);
						btn.setTextOff(tipString);
						btn.setText(tipString);
		                SharedPreferences.Editor editor = sharedpreferences.edit();
		                editor.putString(key, tipString);
		                editor.commit();
					}
				})
				.setNegativeButton("取消", null).show();

				return true;
			}
		});
    	
    	
		
		btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {			
				if(isChecked){
					sendBytes[1]=sendbyte1;
					sendBytes[2]=(byte)0x7F;
					socket.socketSendData(sendBytes);
//					System.out.format("Sended==press===:");
//					for (int i = 0; i < sendBytes.length; i++) {
//						System.out.format("%02X ", sendBytes[i]);
//					}
//					System.out.println();
					
		
				}else{			
					sendBytes[1]=sendbyte1;
					sendBytes[2]=(byte)verticalSeekBar.getProgress();
					socket.socketSendData(sendBytes);
//					System.out.format("Sended==press===:");
//					for (int i = 0; i < sendBytes.length; i++) {
//						System.out.format("%02X ", sendBytes[i]);
//					}
//					System.out.println();
				}
			}
		});
    }
    
    private void removeToggleButtonListener(ToggleButton btn)
    {
    	btn.setOnLongClickListener(null);
    	btn.setOnCheckedChangeListener(null);
    	
    }
    
    private void initToggleButtonTouchEvent(final VerticalSeekBar verticalSeekBar,final ToggleButton btn,final byte sendbyte1)
    {
    	btn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					btn.setChecked(true);
					sendBytes[1]=sendbyte1;
					sendBytes[2]=0x7f;
					socket.socketSendData(sendBytes);
					System.out.format("Sended==触摸按下===:");
					for (int i = 0; i < sendBytes.length; i++) {
						System.out.format("%02X ", sendBytes[i]);
					}
					System.out.println();
					tmpButton = btn;
					tmpByte = sendbyte1;
					lastProcess1 = verticalSeekBar.getProgress();
					break;
				case MotionEvent.ACTION_UP:
					btn.setChecked(false);
					sendBytes[1]=sendbyte1;
					sendBytes[2]=(byte)verticalSeekBar.getProgress();
					socket.socketSendData(sendBytes);
					System.out.format("Sended===触摸离开==:");
					for (int i = 0; i < sendBytes.length; i++) {
						System.out.format("%02X ", sendBytes[i]);
					}
					System.out.println();
					break;
				}
				return true;
			}
		});
    }
    
    private void removeToggleButtonTouchEvent(ToggleButton btn){
    	btn.setOnTouchListener(null);
    }
    
	private void iniPopupWindow() {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.tipview, null);

		tv = (TextView) layout.findViewById(R.id.tv_item);
		pwMyPopWindow = new PopupWindow(layout);
//		pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		

		// 控制popupwindow的宽度和高度自适应
		tv.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		pwMyPopWindow.setWidth(tv.getMeasuredWidth());
		pwMyPopWindow.setHeight((tv.getMeasuredHeight() + 20)
				* NUM_OF_VISIBLE_LIST_ROWS);

		// 控制popupwindow点击屏幕其他地方消失
		pwMyPopWindow.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.bg_popupwindow));// 设置背景图片，不能在布局中设置，要�?�过代码来设
		pwMyPopWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上
	}
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        //信号通道
        sendBytes[0]= (byte) (0x90 | (byte)channel);
        sendBytes2[0] = (byte) (0xc0 | (byte)channel);

      //      //初始设置滚筒的值
      GValue = 1;
      sendBytes2[1]= (byte)0x01;
      socket.socketSendData(sendBytes2);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//    	socket.socketClose();
        super.onDestroy();
    }
}

package com.hxsmart.icrtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SocketThread{
	
	private final boolean DEBUG_MODE = false;
	
	private static Handler mHandler;
	private Handler sendRecvHandler;
	private Context mContext;
	private String IP;
	private int Port;
	private volatile boolean mIsConnected = false;
	private Socket mSocket = null;
	private InputStream mInStream = null;
	private OutputStream mOutStream = null;
	private Message Msg = null;
	private byte[] recvBytes = null;
	BufferedReader reader ;
	
	private volatile static SocketThread Single;  
    private SocketThread ()
    {}
    
	public static SocketThread getSingleInstance() {
		if (Single == null) {
			synchronized (SocketThread.class) {
				if (Single == null) {
					Single = new SocketThread();
				}
			}
		}
		return Single;
	}
	
	public void setHandlerAndContext(Handler handler, Context context){
		this.mContext = context;
    	this.mHandler = handler;
	}
	
	public boolean SocketConnectStatus()
	{
		return mIsConnected;
	}
	
	public void setIpAndPort(String IP,int Port)
	{
    	this.IP=IP;
    	this.Port = Port;
	}
	
	public void socketOpen()
	{

		Thread socketThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i("SocketThread", this + " runing...");

				delay(500);
				while(true){
					try {
						mSocket = new Socket();
						mSocket.setTcpNoDelay(true);
						mSocket.connect(new InetSocketAddress(IP, Port), 4000);
						delay(500);
					} catch (UnknownHostException e) {
						Msg = Message.obtain(mHandler);
						Msg.obj = e.getMessage();
						mHandler.sendMessage(Msg);
						Log.e("mSocket UnknownHostException", e.getMessage());
						return;
					} catch (SocketTimeoutException e) {
						Msg = Message.obtain(mHandler);
						Msg.obj = e.getMessage();
						mHandler.sendMessage(Msg);
						Log.e("mSocket SocketTimeoutException", e.getMessage());
						return;
					} catch (IOException e) {
						Msg = Message.obtain(mHandler);
						Msg.obj = e.getMessage();
						mHandler.sendMessage(Msg);
						Log.e("mSocket IOException", e.getMessage());
						return;
					}

					try {
						mInStream = mSocket.getInputStream();
						mOutStream = mSocket.getOutputStream();
						reader = new BufferedReader(new InputStreamReader(mInStream));
					} catch (IOException e) {
						socketClose();
						Log.e("In or Out Stream IOException", e.getMessage());
						Msg = Message.obtain(mHandler);
						Msg.obj = e.getMessage();
						mHandler.sendMessage(Msg);
						delay(1000);
						return;
					}
					// 连接成功，发送消息
					mIsConnected = true;
					Msg = Message.obtain(mHandler);
					Msg.what = 520;
					mHandler.sendMessage(Msg);
		

					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();
							
							sendRecvHandler = new Handler();
							
							Looper.loop();
						}
					}).start();
					
					return;	
				}
			}
		});
		socketThread.start();
	}

	
	public void socketClose()
	{

		mIsConnected = false;

		if (DEBUG_MODE)
			Log.e("SocketThread", "begin SocketClose");
		
		if (mInStream != null) {
			try {
				mInStream.close();
			} catch (IOException e) {
				Log.e("SocketThread", "mInStream.close:" + e.getMessage());
			}
		}
		
		if (mOutStream != null) {
			try {
				mOutStream.close();
			} catch (IOException e) {
				Log.e("SocketThread", "mOutStream.close:" + e.getMessage());
			}
		}
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				Log.e("SocketThread", "mOutStream.close:" + e.getMessage());
			}
		}

		mInStream = null;
		mOutStream = null;
		mSocket = null;
		
		if (DEBUG_MODE)
			Log.e("SocketThread", "done Socket closed");
	}
	
	public synchronized void socketSendData(byte[] inData)
	{
		if(!mIsConnected)
			return;
		final byte[] data = new byte[inData.length];

		System.arraycopy(inData, 0, data, 0, data.length);
		
		sendRecvHandler.post(new Runnable() {
			
			@Override
			public void run() {
				
				if (mOutStream == null){
					socketClose();
					Msg = Message.obtain(mHandler);
					Msg.obj = "mOutStream 为 null";
					mHandler.sendMessage(Msg);
					return;
				}
				
				try {	

					mOutStream.write(data);
					
				} catch (IOException e) {
					Log.e("SocketThread", "mOutStream.write:" + e.getMessage());
					socketClose();
					Msg = Message.obtain(mHandler);
					Msg.obj = "mOutStream.write:" + e.getMessage();
					mHandler.sendMessage(Msg);
					return;
				}
				
				// TODO Auto-generated method stub
//				try {
//					String line = reader.readLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});

		return;
	}
	
	
	public int socketRecvData(byte[] outData)
	{
		int readLength = 0;
		
		if (mInStream == null) {
			socketClose();
			Msg = Message.obtain(mHandler);
			Msg.obj = "mInStream is null";
			mHandler.sendMessage(Msg);
			delay(500);
			return -1;
		}
		
		try {
			delay(40);
			readLength = mInStream.available();
			if (readLength == 0 )
				return 0;
			if (readLength < 0 || readLength == 65535) {
				socketClose();
				return -2;
			}
				
			recvBytes = new byte[readLength];
			mInStream.read(recvBytes, 0, readLength);
			
		} catch (Exception e) {
			socketClose();
			Log.e("SocketThread", "mInStream.read:" + e.getMessage());
			return -3;
		}
		
		if (DEBUG_MODE) {
			System.out.format("SocketThread Received:");
			for (int i = 0; i < readLength; i++) {
				System.out.format("%02X ", recvBytes[i]);
			}
			System.out.println();
		}
		return readLength;
	}

	private void delay(int time)
	{
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/** 将十六进制字符串转换成字节数组 */
	public static byte[] toByteArray(String hex) {
		if (hex == null)
			return null;
		if (hex.length() % 2 != 0) {
			return null;
		}
		int length = hex.length() / 2;
		byte[] data = new byte[length];
		for (int i = 0, index = 0; i < length; i++) {
			index = i * 2;
			String oneByte = hex.substring(index, index + 2);
			data[i] = (byte) (Integer.valueOf(oneByte, 16) & 0xFF);
		}
		return data;
	}
	
	
	/**
	 * 将字节数组转换成十六进制字符串(OneTwo)
	 * @param src   字节数组
	 * @param len   字节数组长度
	 * @return      十六进制字符串
	 */
    public static String bytesToHexString(byte[] src, int len) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || len <= 0) {
			return null;		
		}
	    for (int i = 0; i < len; i++) {	
	    	int v = src[i] & 0xFF;	
	    	String hv = Integer.toHexString(v);	
	    	if (hv.length() < 2) {	
		    	stringBuilder.append(0);		
		    }	
	    	stringBuilder.append(hv);	
	    }
	    return stringBuilder.toString();
    }
}

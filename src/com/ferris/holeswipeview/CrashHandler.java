package com.ferris.holeswipeview ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "NorrisInfo" ;
	private Thread.UncaughtExceptionHandler mDefaultHandler ;
	private static CrashHandler mInstance = new CrashHandler() ;
	private WeakReference<Context> mContext ;
	
	private Map<String , String> mLogInfo = new HashMap<String , String>() ;

	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss") ;
	private CrashHandler() {
	}
	public static CrashHandler getInstance() {
		return mInstance ;
	}
	public void init(Context paramContext) {
		mContext = new WeakReference<Context>(paramContext) ;
		// ��ȡϵͳĬ�ϵ�UncaughtException������
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler() ;
		// ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
		Thread.setDefaultUncaughtExceptionHandler(this) ;
	}
	public void uncaughtException(Thread paramThread , Throwable paramThrowable) {
		if( ! handleException(paramThrowable) && mDefaultHandler != null) {
			// ����Զ����û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(paramThread , paramThrowable) ;
		}
		else {
			try {
				// ������ˣ��ó����������1�����˳�����֤�ļ����沢�ϴ���������
				paramThread.sleep(1000) ;
			}
			catch(InterruptedException e) {
				e.printStackTrace() ;
			}
			// �˳�����
			android.os.Process.killProcess(android.os.Process.myPid()) ;
			System.exit(1) ;
		}
	}

	public boolean handleException(Throwable paramThrowable) {
		if(paramThrowable == null)
			return false ;
		new Thread() {
			public void run() {
				Looper.prepare() ;
				if(mContext!=null&&mContext.get()!=null){
					Toast.makeText(mContext.get() , "I'm sorry, the program appears unusual, be about to exit" , 0).show() ;
				}
				
				Looper.loop() ;
			}
		}.start() ;
		// ��ȡ�豸������Ϣ
		if(mContext!=null&&mContext.get()!=null){
			getDeviceInfo(mContext.get()) ;
		}
		
		// ������־�ļ�
		saveCrashLogToFile(paramThrowable) ;
		return true ;
	}
	public void getDeviceInfo(Context paramContext) {
		try {
			// ��ð������
			PackageManager mPackageManager = paramContext.getPackageManager() ;
			// �õ���Ӧ�õ���Ϣ������Activity
			PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
					paramContext.getPackageName() , PackageManager.GET_ACTIVITIES) ;
			if(mPackageInfo != null) {
				String versionName = mPackageInfo.versionName == null ? "null"
						: mPackageInfo.versionName ;
				String versionCode = mPackageInfo.versionCode + "" ;
				mLogInfo.put("versionName" , versionName) ;
				mLogInfo.put("versionCode" , versionCode) ;
			}
		}
		catch(NameNotFoundException e) {
			e.printStackTrace() ;
		}
		// �������
		Field[] mFields = Build.class.getDeclaredFields() ;
		// ���Build���ֶ�key-value  �˴�����Ϣ��Ҫ��Ϊ���ڷ��������ֻ���ְ汾�ֻ���ԭ��
		for(Field field : mFields) {
			try {
				field.setAccessible(true) ;
				mLogInfo.put(field.getName() , field.get("").toString()) ;
				Log.d(TAG , field.getName() + ":" + field.get("")) ;
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace() ;
			}
			catch(IllegalAccessException e) {
				e.printStackTrace() ;
			}
		}
	}
	private String saveCrashLogToFile(Throwable paramThrowable) {
		StringBuffer mStringBuffer = new StringBuffer() ;
		for(Map.Entry<String , String> entry : mLogInfo.entrySet()) {
			String key = entry.getKey() ;
			String value = entry.getValue() ;
			mStringBuffer.append(key + "=" + value + "\r\n") ;
		}
		Writer mWriter = new StringWriter() ;
		PrintWriter mPrintWriter = new PrintWriter(mWriter) ;
		paramThrowable.printStackTrace(mPrintWriter) ;
		paramThrowable.printStackTrace();
		Throwable mThrowable = paramThrowable.getCause() ;
		// ���ջ���а����е��쳣��Ϣд��writer��
		while(mThrowable != null) {
			mThrowable.printStackTrace(mPrintWriter) ;
			// ����  ÿ�����쳣ջ֮�任��
			mPrintWriter.append("\r\n") ;
			mThrowable = mThrowable.getCause() ;
		}
		//�ǵùر�
		mPrintWriter.close() ;
		String mResult = mWriter.toString() ;
		mStringBuffer.append(mResult) ;
		// �����ļ��������ļ���
		String mTime = mSimpleDateFormat.format(new Date()) ;
		String mFileName = "CrashLog-" + mTime + ".txt" ;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				File mDirectory = new File(Environment.getExternalStorageDirectory()
						+ "/swipeView") ;
				Log.v(TAG , mDirectory.toString()) ;
				if( ! mDirectory.exists())
					mDirectory.mkdir() ;
				FileOutputStream mFileOutputStream = new FileOutputStream(mDirectory + "/"
						+ mFileName) ;
				mFileOutputStream.write(mStringBuffer.toString().getBytes()) ;
				mFileOutputStream.close() ;
				return mFileName ;
			}
			catch(FileNotFoundException e) {
				e.printStackTrace() ;
			}
			catch(IOException e) {
				e.printStackTrace() ;
			}
		}
		return null ;
	}
}

package org.spoofer.channels.channel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.os.Debug;
import android.os.FileObserver;
import android.util.Log;

public class SystemLogChannel extends AbstractChannel {
	
	private static final String LOG_TAG = SystemLogChannel.class.getSimpleName();
	
	private static final String FIELD_ = "random_number";

	private static final String LOG_LOCATION =  "/sdcard/systemlogs/system"; // although Debug defaults to /sdcard/, FileObserver does not, so absolute path used.
	

	public SystemLogChannel(Context context) {
		super(context);
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public void start() {
		super.start();
		Debug.startMethodTracing(LOG_LOCATION);
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}

	protected void addEvent(EventData data) {
		// TODO Auto-generated method stub
		super.addEvent(data);
	}
	
	

	class LogObserver extends FileObserver {
		
		private final FileInputStream logFile;
		
		public LogObserver(String path, int mask) {
			super(LOG_LOCATION, MODIFY);
			try {
				logFile = new FileInputStream(LOG_LOCATION);
			
			} catch (FileNotFoundException e) {
				Log.wtf(LOG_TAG, "LOG_LOCATION:" + LOG_LOCATION + " is invalid!");
				throw new IllegalStateException("LOG_LOCATION:" + LOG_LOCATION + " is invalid!");
			}
		}

		@Override
		public void onEvent(int event, String path) {
			logFile.getChannel();
			
		}
		
		
		
	}
	
}

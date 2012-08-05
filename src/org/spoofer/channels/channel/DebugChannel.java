package org.spoofer.channels.channel;

import java.util.Random;

import org.spoofer.channels.impl.EventDataImpl;

import android.content.Context;

public class DebugChannel extends AbstractChannel {

	private Thread workerThread;
	
	private static final String LOG_TAG = DebugChannel.class.getSimpleName();
	
	private static final String FIELD_RANDOM_NUMBER = "random_number";

	protected static final long DELAY_TIME = 3000;
	
	
	public DebugChannel(Context context) {
		super(context);
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	
	@Override
	public void start() {
		synchronized (this) {
			super.start();

			if (null == workerThread)
				workerThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						while (null != workerThread) {
							addEvent();
							
							synchronized (DebugChannel.this) {
								try {
									DebugChannel.this.wait(DELAY_TIME);
								} catch (InterruptedException e) {
									workerThread = null;
									break;
								}
							}
						}
					}
				});
			workerThread.start();
		}
	}

	@Override
	public void stop() {
		synchronized (this) {
			super.stop();
			
			if (null != workerThread) {
				workerThread.interrupt();
				workerThread = null;
			}
		}
	}

	
	private void addEvent() {
		EventDataImpl eventData = new EventDataImpl(LOG_TAG);
		eventData.put(FIELD_RANDOM_NUMBER, new Random().nextLong());
		addEvent(eventData);
	}
}

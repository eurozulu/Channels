package org.spoofer.channels;


import java.io.IOException;
import java.util.List;

import org.spoofer.channels.database.EventData;
import org.spoofer.channels.database.EventDatabase;
import org.spoofer.channels.database.ExportAgent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ChannelService extends Service {


	private static final String NAMESPACE = ChannelService.class.getPackage().getName() + ".";
	public static final String SERVICE_NAME = NAMESPACE + "CHANNEL_SERVICE";

	private static final int POLL_TIME_SECONDS = 10;

	
	private static final String LOG_TAG = ChannelService.class.getSimpleName();


	private final DataCollector dataCollector = new DataCollector();

	private final IBinder binder = new LocalBinder();	

	private EventDatabase database;

	private ChannelManager channelMgr;
	
	private ExportAgent exportAgent;
	
	private ChannelServiceListener stateListener = null;

	
	
	/**
	 * Checks if the service has been started.
	 * @return true if the service has started and is reading channels.
	 * 
	 */
	public boolean isStarted() {
		return dataCollector.isStarted();
	}
	
	/**
	 * Collects any cached data from the given Channel and adds that data to the database.
	 * The channel will always be returned empty.
	 * 
	 * @param channel
	 */
	public void flushChannel(Channel channel) {
		String channelName = channel.getName();
		Log.v(LOG_TAG, "flushing channel " + channelName);

		if (!channel.isEmpty()) {

			List<EventData> cachedData = channel.fetchData();
			Log.d(LOG_TAG, "channel " + channelName + " has " + cachedData.size() + " cached events");
			synchronized(database) {
				database.addEvents(cachedData);
			}
			
			if (null != stateListener)
				stateListener.dataLogged(cachedData);
			
		}else
			Log.d(LOG_TAG, "channel " + channelName + " has no cached data");

	}
	
	
	/**
	 * Erases the existing database.  Any data already stored in the database is cleared out.
	 * The cache of every Channel is also cleared.
	 * If the service is currently running, it will be first stopped.
	 * 
	 */
	public void eraseAllData() {

		stopSelf();

		// Clear cache data in channels
		for (Channel ch : channelMgr.getMonitoredChannels()) {
			ch.stop();
			ch.clearData();
		}
		synchronized (database) {
			database.clearData();
		}
	}


	public void startExport() {
		exportAgent.startExport(database);
	}

	public void stopExport() {
		exportAgent.stopExport(database);
	}
	public void export() throws IOException {
		exportAgent.exportDatabase(database);
	}

	
	@Override
	public void onCreate() {
		Log.v(LOG_TAG, "Creating " + LOG_TAG + " service");

		super.onCreate();

		Context context = getApplicationContext();
		channelMgr = new ChannelManager(context);
		database = new EventDatabase(context);
		
		Log.d(LOG_TAG, LOG_TAG + " service loaded " + 
				channelMgr.getAvailableChannels().size() + " available channels with " + 
				channelMgr.getMonitoredChannelNames().size() + " channels being monitored");
	
		exportAgent = new ExportAgent(context);
	}


	@Override
	public void onDestroy() {
		dataCollector.stop();
		
		super.onDestroy();
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.v(LOG_TAG, "Channel service has received a start request " + startId);

		super.onStartCommand(intent, flags, startId);

		if (!isStarted())
			dataCollector.start();
		else
			Log.d(LOG_TAG, "Ignoring start request for " + startId + " as service is already running");

		Log.v(LOG_TAG, "Channel service has completed the start request " + startId);
		
		return Service.START_STICKY;
	}

	@Override
	public boolean stopService(Intent name) {
		Log.v(LOG_TAG, "stop Service requested");	
		dataCollector.stop();

		return super.stopService(name);
	}


	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}





	class DataCollector implements Runnable {
		
		private final String LOG_TAG = DataCollector.class.getSimpleName();

		private static final long WAIT_TIMEOUT = POLL_TIME_SECONDS * 1000; // 6 seconds.

		private boolean isRunning = false;

		/**
		 * Checks if the service has been started.
		 * @return true if the service has started and is reading channels.
		 * 
		 */
		public synchronized boolean isStarted() {
			return isRunning;
		}

		public synchronized void start() {

			if (!isRunning) {
				Log.d(LOG_TAG, "Requesting collector service to start");
				new Thread(this, "data collector").start();
				isRunning = true;
			}
		}

		public synchronized void stop() {

			if (isRunning) {
				Log.d(LOG_TAG, "Requesting collector service to stop");
				isRunning = false;
				dataCollector.notify();
			}
		}


		@Override
		public void run() {

			Log.d(LOG_TAG, "collector service thread is now starting");

			if (null != stateListener)
				stateListener.serviceStateChange(ChannelService.this, isRunning);

			List<Channel> channels = channelMgr.getMonitoredChannels();

			if (channels.size() > 0) {
				Log.d(LOG_TAG, "collector service thread is starting " + channels.size() + " channels");
				for (Channel channel : channels)  // fire up all the channels
					channel.start();
				
				//Context context = getApplicationContext();
				//ExportAgent exporter = new ExportAgent();
				
				while (isRunning) {
					Log.d(LOG_TAG, "collector service thread is checking the channel caches");

					for (Channel channel : channelMgr.getMonitoredChannels()) {

						if (!isRunning) {
							Log.d(LOG_TAG, "collector service is aborting the collection as the service has been stopped");
							break;
						}

						flushChannel(channel);						
					}
					
					Log.v(LOG_TAG, "collector service thread has completed checking the channel caches");
					
					/*
					try {
						exporter.exportDatabase(context, database);
					
					} catch (IOException e) {
						Log.e(LOG_TAG, "Exporter has failed to export database.", e);
						Toast.makeText(context, "Exporter has failed to export database." + e.getMessage(), Toast.LENGTH_LONG).show();
					}*/
					
					if (isRunning)
						synchronized (dataCollector) {
							try {
								Log.v(LOG_TAG, "collector service thread sleeping...");

								dataCollector.wait(WAIT_TIMEOUT);

							} catch (InterruptedException e) {
								Log.d(LOG_TAG, "Data Collector has been inturrupted, indicating it should shut down.", e);
								stop();
							}
						}
				}

				for (Channel channel : channelMgr.getMonitoredChannels())  // close down all the channels
					channel.stop();

			}else
				Log.d(LOG_TAG, "aborting collector service thread as no channels selected for moniotring");

			isRunning = false;

			if (null != stateListener)
				stateListener.serviceStateChange(ChannelService.this, isRunning);


			Log.d(LOG_TAG, "completed collector service thread");

		}

	}


	/**
	 * Binder implementation to return this local Service.
	 *
	 */
	public class LocalBinder extends Binder {
		public ChannelService getService() {
			return ChannelService.this;
		}
	}


	public void setStateListener(ChannelServiceListener stateListener) {
		this.stateListener = stateListener;
	}
	public ChannelServiceListener getStateListener() {
		return this.stateListener;
	}

	public interface ChannelServiceListener {
		public void serviceStateChange(ChannelService svc, boolean isRunning);
		public void dataLogged(List<EventData> cachedData);
	}


}

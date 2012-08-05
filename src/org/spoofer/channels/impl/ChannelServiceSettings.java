package org.spoofer.channels.impl;

import java.io.IOException;
import java.util.List;

import org.spoofer.channels.Channel;
import org.spoofer.channels.ChannelManager;
import org.spoofer.channels.ChannelService;
import org.spoofer.channels.R;
import org.spoofer.channels.database.EventData;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

public class ChannelServiceSettings extends Activity {

	private static final String LOG_TAG = ChannelServiceSettings.class.getSimpleName();
	
	
	private CompoundButton.OnCheckedChangeListener changeListener;	
	private ChannelManager channelMgr;

	/** Called when the activity is first created. */

	private static final Intent channelServiceIntent = new Intent(ChannelService.SERVICE_NAME);
	private ChannelService channelSvc = null;
	
	private final ServiceConnection svcConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v(LOG_TAG, "service disconnecting");
			channelSvc = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(LOG_TAG, "service connecting " + service.toString());
			channelSvc = ((ChannelService.LocalBinder)service).getService();
			
			final LedView icon = (LedView)findViewById(R.id.status_light);
			
			channelSvc.setStateListener(new ChannelService.ChannelServiceListener() {
				@Override
				public void serviceStateChange(ChannelService svc, boolean isRunning) {
					int state = isRunning ? LedView.LIGHT_GREEN : LedView.LIGHT_OFF;
					icon.setLightState(state);
				}

				@Override
				public void dataLogged(List<EventData> cachedData) {
					icon.setLightState(LedView.LIGHT_RED);
					
				}
			});
			
			int lightState =  channelSvc.isStarted() ? LedView.LIGHT_GREEN : LedView.LIGHT_OFF;
			icon.setLightState(lightState);
			
		}
	};
	
	
	@Override
	protected void onDestroy() {
		Log.v(LOG_TAG, "unloading");
		super.onDestroy();
		
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(LOG_TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		
		final Context context = getApplicationContext();
		
		channelMgr = new ChannelManager(context);

		// Start button to kick off service
		((Button)findViewById(R.id.button_start)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				startService(channelServiceIntent);

			}
		}); 

		((Button)findViewById(R.id.button_stop)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				channelSvc.stopService(channelServiceIntent);

			}
		});

		Button btnReset = ((Button)findViewById(R.id.button_reset));
		btnReset.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				channelSvc.eraseAllData();
				Toast.makeText(getApplicationContext(), "data erased", Toast.LENGTH_SHORT).show();
			}
		});

		Button btnExport = ((Button)findViewById(R.id.button_export));
		btnExport.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					channelSvc.export();
					Toast.makeText(getApplicationContext(), "exported data", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "export Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		

		if (!context.bindService(channelServiceIntent, svcConnection, Context.BIND_AUTO_CREATE)) {
			Toast.makeText(context, "failed to bind to service. No rest available", Toast.LENGTH_LONG).show();
			btnReset.setEnabled(false);
			btnExport.setEnabled(false);
		}
			
		refreshList();
		
		Log.v(LOG_TAG, "onCreate completed");

	}



	private void refreshList() {
		
		final Context context = getApplicationContext();

		List<Channel> availableChannels = channelMgr.getAvailableChannels();
		List<String> selectedNames = channelMgr.getMonitoredChannelNames();

		if (null == changeListener)
			changeListener = new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	
					String name = buttonView.getText().toString();
	
					// Change listener to update the checked states in the data model.
					if (isChecked)  // turning on, so add the channel t selected.
						channelMgr.addMonitorChannel(name);
	
					else   // its turning off.
						channelMgr.removeMonitorChannel(name);
	
					refreshList();
				}
		};

		ListView availableChannelsList = (ListView)findViewById(R.id.available_channels);
		availableChannelsList.setAdapter(new ChannelListAdapter(context, R.layout.pref_channel_list_item, 
				availableChannels, selectedNames, changeListener));

	}
	
	
	
	
	
}
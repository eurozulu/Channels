package org.spoofer.channels.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spoofer.channels.Channel;
import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.util.Log;
import static org.spoofer.channels.database.DatabaseConstants.FIELD_TIMESTAMP;

/**
 * Base class for a channel
 * Implements the event cache for channels to write to.
 * 
 * @author robgilham
 *
 */
public abstract class AbstractChannel implements Channel {
	

	public static final String FIELD_ACCURACY = "accuracy";
	

	protected static final String LOG_TAG = AbstractChannel.class.getSimpleName();
	private static final long DEFAULT_ACCURACY = 0;
	
	private final List<EventData> dataCache = Collections.synchronizedList(new ArrayList<EventData>());
	
	private String description;
	
	/**
	 * This is the 'default' constructor for the base Channel.
	 * 
	 * @param context
	 */
	public AbstractChannel(Context context) {
		super();	
	}

	
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	
	@Override
	public String getDescription() {
		return this.description;
	}
	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	@Override
	public abstract boolean isSupported();


	@Override
	public void clearData() {
		synchronized (dataCache) {
				dataCache.clear();
		}
	}



	@Override
	public List<EventData> fetchData() {
		List<EventData> result = null;
		
		synchronized (dataCache) {
			if (!dataCache.isEmpty()) {
				
				result = new ArrayList<EventData>(dataCache.size());
				result.addAll(dataCache);
				dataCache.clear();
			}
		}
		
		return result;
	}

	@Override
	public boolean isEmpty() {
		return dataCache.isEmpty();
	}

	
	@Override
	public void start() {
		Log.d(LOG_TAG, "starting channel " + getName());
	}
	
	@Override
	public void stop() {
		Log.d(LOG_TAG, "stopping channel " + getName());
	}

	
	protected void addEvent(EventData data) {
		
		// Enforce the two required values
		long nowTime = System.currentTimeMillis();
		if (!data.containsKey(FIELD_TIMESTAMP))
			data.put(FIELD_TIMESTAMP, nowTime);
		
		if (!data.containsKey(FIELD_ACCURACY))
			data.put(FIELD_ACCURACY, DEFAULT_ACCURACY);
		
		dataCache.add(data);
	}






	@Override
	public boolean equals(Object o) {
		if (o instanceof Channel)
			return this.getName().equals(((Channel)o).getName());
		else
			return super.equals(o);
	}



	@Override
	public int hashCode() {
		return getName().hashCode();
	}



}

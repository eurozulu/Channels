package org.spoofer.channels.channel;


import org.spoofer.channels.database.EventData;
import org.spoofer.channels.impl.EventDataImpl;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import static org.spoofer.channels.database.DatabaseConstants.FIELD_TIMESTAMP;


public class GPSChannel extends AbstractChannel {

	public static final String FIELD_ALTITUDE = "altitude";
	public static final String FIELD_BEARING = "bearing";
	public static final String FIELD_LATITUDE = "latitude";
	public static final String FIELD_LONGITUDE = "longitude";
	public static final String FIELD_SPEED = "speed";
	public static final String FIELD_PROVIDER = "provider";
	
	
	private final LocationManager locationManager;
	
	public GPSChannel(Context context) {
		super(context);
		locationManager = (LocationManager)context.getSystemService(Service.LOCATION_SERVICE);
	}
	
	@Override
	public void start() {
		super.start();
		locationManager.requestLocationUpdates(getName(), 0, 0, gpsListener);
	}

	@Override
	public void stop() {
		super.stop();
		locationManager.removeUpdates(gpsListener);
	}

	@Override
	public boolean isSupported() {
		return locationManager.isProviderEnabled(getName());
	}


	private LocationListener gpsListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			Log.v(LOG_TAG, "GPS location update " + location.toString());
			addEvent(location);
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
	};
	

	
	/**
	 * Add our own 'addEvent' to accept a Sensor event.
	 * This will extract the data from the sensor event and place it in a new EventData block,
	 * passing that to the super class 'addEvent'
	 * 
	 * @param event
	 */
	public void addEvent(Location event) {
		EventData eventData = new EventDataImpl(this.getName());
		
		eventData.put(FIELD_TIMESTAMP, event.getTime());
		eventData.put(FIELD_ACCURACY, event.getAccuracy());
		eventData.put(FIELD_ALTITUDE, event.getAltitude());
		eventData.put(FIELD_BEARING, event.getBearing());
		eventData.put(FIELD_LATITUDE, event.getLatitude());
		eventData.put(FIELD_LONGITUDE, event.getLongitude());
		eventData.put(FIELD_SPEED, event.getSpeed());
		eventData.put(FIELD_PROVIDER, event.getProvider());
		
		super.addEvent(eventData);
	}

	
}

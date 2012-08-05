package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;
import org.spoofer.channels.impl.EventDataImpl;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import static org.spoofer.channels.database.DatabaseConstants.FIELD_TIMESTAMP;

public abstract class AbstractSensorChannel extends AbstractChannel {

	public static final String FIELD_SENSOR = "sensor";
	
	
	
	protected static final String LOG_TAG = AbstractSensorChannel.class.getSimpleName();

	protected static int DEFAULT_UPDATE_RATE = SensorManager.SENSOR_DELAY_NORMAL;


	protected final SensorManager sensorManager;

	private final Sensor sensor;

	private int updateRate = DEFAULT_UPDATE_RATE;
	

	/**
	 * Places the values in the Sensor event (values), into the given eventData Map,
	 * adding names to each value.
	 * 
	 * @param eventData  The Event Data to recieve the data
	 * @param values  The values to write into the eventData
	 */
	protected abstract void addSensorValues(EventData eventData, float[] values);
	
	/**
	 * Gets the Type of Sensor the implementation is using.
	 * @see android.hardware.Sensor for types of Sensors.
	 * @return
	 */
	protected abstract int getSensorType();

	
	
	/**
	 * This is the 'default' constructor for the base Channel.
	 * Implementations should provide their own, public constructors with just a context parameter,
	 * defining the sensor type in a constant.
	 * 
	 * @param sensorType the sensor type to gather data from. See 	android.hardware.Sensor for a description of valid sensor types.
	 * 
	 * @param context
	 */
	public AbstractSensorChannel(Context context) {
		super(context);
		
		int sensorType = getSensorType();
		Log.v(LOG_TAG, "Checking for sensor service");
		sensorManager = (SensorManager)context.getSystemService(Service.SENSOR_SERVICE);

		Log.v(LOG_TAG, "Checking for sensor of type: " + sensorType);
		sensor = sensorManager.getDefaultSensor(sensorType);

		if (!isSupported())
			Log.d(LOG_TAG, "Channel " + getName() + " can not find a suitable sensor");
		else
			Log.d(LOG_TAG, "Channel " + getName() + " set with sensor type " + sensor.getName() );
	}


	@Override
	public void start() {
		if (isSupported()) {
			super.start();
			sensorManager.registerListener(eventListener, sensor, updateRate);
		}else
			Log.w(LOG_TAG, "Failed to start service " + getName() + " as it is not supported");
	}

	@Override
	public void stop() {
		if (isSupported()) {
			super.stop();
			sensorManager.unregisterListener(eventListener, sensor);
		} else
			Log.w(LOG_TAG, "Failed to stop service " + getName() + " as it is not supported.  (Should always be paired with failed to start)");
			
	}


	public int getUpdateRate() {
		return updateRate;
	}
	public void setUpdateRate(int updateRate) {
		this.updateRate = updateRate;
	}


	@Override
	public boolean isSupported() {
		return null != sensor;
	}


	/**
	 * The Sensor Listener passes on each SensorEvent to Channel
	 */
	private SensorEventListener eventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			//Log.v(LOG_TAG, "onSensorChanged for sensor: " + sensor.getName());
			addEvent(event);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// Not used
			Log.v(LOG_TAG, "onAccuracyChanged for sensor: " + sensor.getName() + " to accuracy of " + accuracy);
		}
	};


	/**
	 * Add our own 'addEvent' to accept a Sensor event.
	 * This will extract the data from the sensor event and place it in a new EventData block,
	 * passing that to the super class 'addEvent'
	 * 
	 * @param event
	 */
	public void addEvent(SensorEvent event) {
		EventData eventData = new EventDataImpl(this.getName());

		eventData.put(FIELD_TIMESTAMP, event.timestamp);
		eventData.put(FIELD_ACCURACY, event.accuracy);
		eventData.put(FIELD_SENSOR, event.sensor.getName());

		addSensorValues(eventData, event.values);
		
		super.addEvent(eventData);
	}
	
	
}

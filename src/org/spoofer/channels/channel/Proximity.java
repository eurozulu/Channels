package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.hardware.Sensor;

public class Proximity extends AbstractSensorChannel {
	
	private static final String FIELD_DISTANCE = "cm";
	
	public Proximity(Context context) {
		super(context);
	}

	@Override
	protected void addSensorValues(EventData eventData, float[] values) {
		eventData.put(FIELD_DISTANCE, values[0]);
	}
	@Override
	protected int getSensorType() {
		return Sensor.TYPE_PROXIMITY;
	}

}

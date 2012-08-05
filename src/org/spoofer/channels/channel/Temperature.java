package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.hardware.Sensor;

public class Temperature extends AbstractSensorChannel {
	
	private static final String FIELD_DEGREES = "celsius";
	
	public Temperature(Context context) {
		super(context);
	}

	@Override
	protected void addSensorValues(EventData eventData, float[] values) {
		eventData.put(FIELD_DEGREES, values[0]);
	}
	@Override
	protected int getSensorType() {
		return Sensor.TYPE_TEMPERATURE;
	}

}

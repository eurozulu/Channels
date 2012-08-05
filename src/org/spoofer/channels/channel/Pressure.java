package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.hardware.Sensor;

public class Pressure extends AbstractSensorChannel {
	
	private static final String FIELD_LUX_LEVEL = "hPi";
	
	public Pressure(Context context) {
		super(context);
	}

	@Override
	protected void addSensorValues(EventData eventData, float[] values) {
		eventData.put(FIELD_LUX_LEVEL, values[0]);
	}
	@Override
	protected int getSensorType() {
		return Sensor.TYPE_PRESSURE;
	}

}

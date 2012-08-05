package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.hardware.Sensor;

public class Accelerometer extends AbstractSensorChannel {
	
	public static final String FIELD_MS_X = "X_m_s2";
	public static final String FIELD_MS_Y = "Y_m_s2";
	public static final String FIELD_MS_Z = "Z_m_s2";
	
	private static String[] FIELDS = new String[]{FIELD_MS_X, FIELD_MS_Y, FIELD_MS_Z};
	
	
	public Accelerometer(Context context) {
		super(context);
	}

	@Override
	protected void addSensorValues(EventData eventData, float[] values) {
		for (int index=0; index < values.length && index < FIELDS.length; index++)
			eventData.put(FIELDS[index], values[index]);

	}

	@Override
	protected int getSensorType() {
		return Sensor.TYPE_ACCELEROMETER;
	}

}

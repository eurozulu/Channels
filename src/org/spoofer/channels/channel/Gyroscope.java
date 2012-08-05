package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.hardware.Sensor;

public class Gyroscope extends AbstractSensorChannel {
	
	public static final String FIELD_RS_X = "X_rad-sec";
	public static final String FIELD_RS_Y = "Y_rad-sec";
	public static final String FIELD_RS_Z = "Z_rad-sec";
	
	private static String[] FIELDS = new String[]{FIELD_RS_X, FIELD_RS_Y, FIELD_RS_Z};
	
	
	public Gyroscope(Context context) {
		super(context);
	}

	@Override
	protected void addSensorValues(EventData eventData, float[] values) {
		for (int index=0; index < values.length && index < FIELDS.length; index++)
			eventData.put(FIELDS[index], values[index]);

	}
	
	@Override
	protected int getSensorType() {
		return Sensor.TYPE_GYROSCOPE;
	}


}

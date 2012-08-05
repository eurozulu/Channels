package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.hardware.Sensor;

public class MagneticSensor extends AbstractSensorChannel {
	
	public static final String FIELD_MT_X = "X_uT";
	public static final String FIELD_MT_Y = "Y_uT";
	public static final String FIELD_MT_Z = "Z_uT";
	
	private static String[] FIELDS = new String[]{FIELD_MT_X, FIELD_MT_Y, FIELD_MT_Z};
	
	
	public MagneticSensor(Context context) {
		super(context);
	}

	@Override
	protected void addSensorValues(EventData eventData, float[] values) {
		for (int index=0; index < values.length && index < FIELDS.length; index++)
			eventData.put(FIELDS[index], values[index]);
	}

	
	@Override
	protected int getSensorType() {
		return Sensor.TYPE_MAGNETIC_FIELD;
	}


}

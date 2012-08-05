package org.spoofer.channels.channel;

import org.spoofer.channels.database.EventData;

import android.content.Context;
import android.hardware.Sensor;

public class Rotation extends AbstractSensorChannel {
	
	public static final String FIELD_DG_X = "X_deg";
	public static final String FIELD_DG_Y = "Y_deg";
	public static final String FIELD_DG_Z = "Z_deg";
	
	private static String[] FIELDS = new String[]{FIELD_DG_X, FIELD_DG_Y, FIELD_DG_Z};
	
	
	public Rotation(Context context) {
		super(context);
	}

	@Override
	protected void addSensorValues(EventData eventData, float[] values) {
		for (int index=0; index < values.length && index < FIELDS.length; index++)
			eventData.put(FIELDS[index], values[index]);

	}
	
	@Override
	protected int getSensorType() {
		return Sensor.TYPE_ROTATION_VECTOR;
	}


}

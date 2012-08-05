package org.spoofer.channels.database;

import android.content.ContentValues;

public class UnTypedContentValues {

	private final ContentValues values;

	
	
	
	public UnTypedContentValues(ContentValues values) {
		super();
		
		this.values = values;
	}
	
	public UnTypedContentValues() {
		this(new ContentValues());
	}
	

	
	public ContentValues getContentValues() {
		return values;
	}
	
	/**
	 * Adds a value to the set.
	 * The Given object is accessed for its type and inserted into the ContentValues as the relevant type.
	 * Only types supported by the Content values are converted. All other Object types will be stored as strings,
	 * or specifically the result of their 'toString' method.
	 * 
	 * 
	 * @param key	the name of the value to put
	 * @param value	the data for the value to put
	 */
	public void put(String key, Object value) {

		if (Boolean.class.isAssignableFrom(value.getClass()))
			values.put(key, (Boolean)value);

		else if (Long.class.isAssignableFrom(value.getClass()))
			values.put(key, (Long)value);

		else if (Integer.class.isAssignableFrom(value.getClass()))
			values.put(key, (Integer)value);

		else if (Byte.class.isAssignableFrom(value.getClass()))
			values.put(key, (Byte)value);

		else if (byte[].class.isAssignableFrom(value.getClass()))
			values.put(key, (byte[])value);

		else if (Double.class.isAssignableFrom(value.getClass()))
			values.put(key, (Double)value);

		else if (Float.class.isAssignableFrom(value.getClass()))
			values.put(key, (Float)value);
		else
			values.put(key, value.toString());
	}


}

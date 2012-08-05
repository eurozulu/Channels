package org.spoofer.channels.database;


/**
 * A data field describes a single field of data to the database.
 * It consists of two key pieces of data:
 * name		The Field name
 * type		The type of data the field holds
 * 
 * @author rob gilham
 *
 */
public class DataField {
	public static final String TYPE_INT = "INTEGER";
	public static final String TYPE_REAL = "REAL";
	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_DATE = "DATE";

	public static final String TYPE_DEFAULT = TYPE_TEXT;
	

	/**
	 * The field name
	 */
	public final String name;
	/**
	 * The data type the field contains.
	 * Valid names are:
	 * 
	 */
	public final String type;
	

	public DataField(String name, Class<?> type) {
		this.name = name;
		this.type = getType(type);
	}
	
	public DataField(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	
	/**
	 * Gets the database type of the given Object class.
	 * Converts Integer to INTEGER, Float to REAL etc.
	 * Default is TEXT for any unknown class. (or string, obviously)
	 * 
	 * @param fieldClass the class of the data to insert in a column
	 * 
	 * @return the database type mapping to that type.
	 * 
	 */
	public static String getType(Class<? extends Object> fieldClass) {
		String type;

		if (Boolean.class.isAssignableFrom(fieldClass))
			type = TYPE_INT;

		else if (Long.class.isAssignableFrom(fieldClass))
			type = TYPE_INT;

		else if (Integer.class.isAssignableFrom(fieldClass))
			type = TYPE_INT;

		else if (Byte.class.isAssignableFrom(fieldClass))
			type = TYPE_INT;

		else if (byte[].class.isAssignableFrom(fieldClass))
			type = TYPE_INT;

		else if (Double.class.isAssignableFrom(fieldClass))
			type = TYPE_REAL;

		else if (Float.class.isAssignableFrom(fieldClass))
			type = TYPE_REAL;
		else
			type = TYPE_TEXT;

		return type;
	}




}

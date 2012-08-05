package org.spoofer.channels.database;

import java.util.List;
import java.util.Map;


public interface EventData extends Map<String, Object> {

	/**
	 * Gets the Channel that generated this event.
	 * @return
	 */
	public String getChannel();

	/**
	 * Gets the data about each field in the EventData and the type of data that field contains.
	 * The resulting Map is key'ed by the field names with a Class object in the value, representing the
	 * type of data.
	 * Basic types supported are:
	 * Long, Boolean, Byte, byte[], Float, Short and Date.
	 * All others, incuding String, are treated as Strings.
	 * 
	 * @return a Map of the Fields in the data and their object types.
	 */
	public List<DataField> getFieldInfo();
	
	public EventData clone();

}

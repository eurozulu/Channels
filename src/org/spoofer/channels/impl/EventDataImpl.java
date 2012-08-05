package org.spoofer.channels.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.spoofer.channels.database.DataField;
import org.spoofer.channels.database.EventData;


public class EventDataImpl extends LinkedHashMap<String, Object> implements EventData {

	private static final long serialVersionUID = -4070875315353392545L;
	
	private final String channel;
	
	
	public EventDataImpl(String channel) {
		super();
		this.channel = channel;
	}
		

	@Override
	public String getChannel() {
		return this.channel;
	}

	
	@Override
	public List<DataField> getFieldInfo() {
		List<DataField> fieldInfo = new ArrayList<DataField>();
		
		for (Map.Entry<String, Object> field : this.entrySet()) {
			fieldInfo.add(new DataField(field.getKey(), field.getValue().getClass()));
		}
		
		return fieldInfo;
	}


	@Override
	public EventData clone() {
		EventDataImpl cloneEvent = new EventDataImpl(getChannel());
		cloneEvent.putAll(this);
		return cloneEvent;
	}

}

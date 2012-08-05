package org.spoofer.channels;

import java.util.List;

import org.spoofer.channels.database.EventData;



/**
 * Represents a single source of information
 * A Channel is a set of data particular to a given source of information.
 * 
 * Channel Feeders push data into a Channel from one or more sensors to collate the data from those sensors into a single table
 * 
 * @author rob gilham
 *
 */
public interface Channel {

	
	/**
	 * Gets the name this channel will be publishing to.
	 * All Channels broadcasting to this name will be logged in the same loation.
	 * @return
	 */
	public String getName();

	/**
	 * Gets a description text of what the channel does.
	 * @return
	 */
	public String getDescription();
	public void setDescription(String description);

	/**
	 * gets the field names this channel generates
	 * @return
	 */
	//public ChannelField[] getFields();
	//public void setFields(ChannelField[] fields);

	/**
	 * Collects any cached data the channel has generated.
	 * The data is returned in the List and removed from the Channels Cache.
	 * 
	 * @return All the data currently cached in the Channel.
	 */
	public List<EventData> fetchData();

	/**
	 * Empties the cache, discards any cached data the channel has generated. 
	 */
	public void clearData();

	/**
	 * Checks if the Channel has any cached data for collection.
	 * @return true if the Channel is not holding data to be collected
	 */
	public boolean isEmpty();

	/**
	 * Checks if the Channel is supported by this device.
	 * @return true if the Channel can collect data, false if not.
	 */
	public boolean isSupported();


	/**
	 * Starts the Channel collecting data.
	 * The Channel will notify its Sensor to begin sending data which the Channel
	 * will buffer in its cache.
	 * 
	 * The channel will continue to collect data until it is stopped or the cache overflows*
	 * 
	 * * by default the cache will overwrite older data when it overflows, but this can be
	 * turned off, causing an exception to be thrown. (not yet though!)
	 *  
	 */
	public void start();

	/**
	 * Stops the Channel from gathering data.
	 * The sensor is notified and no further events are registered.
	 * Data in the cache will remain there until it is fetched.
	 * 
	 */
	public void stop();

}


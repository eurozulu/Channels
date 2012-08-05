package org.spoofer.channels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spoofer.channels.impl.ChannelXMLParser;
import org.spoofer.utils.NameStore;
import org.spoofer.utils.Strings;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


/**
 * Channel manager is a container and factory for Channel Objects.
 * The Manager controls the available channels:
 * Channels that are supported on the device
 * 
 * and monitored Channels, Channels that are selected to log data.
 * 
 * The monitored channels are always a sub set of the availabale channels.
 * Should a monitored channel be no longer supported or enabled by the device, i.e.
 * no longer in the available channels, then it will be removed from the monitored channels.
 * 
 * @author robgilham
 *
 */
public class ChannelManager {

	private static final String LOG_TAG = ChannelManager.class.getSimpleName();


	private static List<Channel> availableChannels;
	
	private final NameStore monitoredChannels;

	
	public ChannelManager(Context context) {
		super();
		
		Log.v(LOG_TAG, "creating new Channel manager");

		if (null == availableChannels)
			availableChannels = Collections.unmodifiableList(loadAvailableChannels(context));

		monitoredChannels = new NameStore(context);
	}


	/**
	 * Gets all the available Channels.
	 * Available Channels must be defined in the channels configuration AND
	 * supported by the current device.
	 * 
	 * @return All the available channels this device supports
	 */
	public List<Channel> getAvailableChannels() {
		return availableChannels;
	}



	public List<Channel> getMonitoredChannels() {

		monitoredChannels.refresh();
		
		int size = monitoredChannels.size();

		List<Channel> loadedChannels = new ArrayList<Channel>(size);

		if (size > 0)  {  // if any names are selected, get their channels
			for (Channel ch : availableChannels)
				if (monitoredChannels.contains(ch.getName()))
					loadedChannels.add(ch);
		}

		return loadedChannels;
	}


	/**
	 * Gets the named channel from the available channels List.
	 * IF the given name is the name of a known channel, that channel is returned.
	 * IF the name is unknown or null, null is returned.
	 * 
	 * @param name the name of the channel
	 * @return the channel of the given name.
	 */
	public Channel getChannel(String name) {
		Channel ch = null;

		if (Strings.hasText(name))
			for (Channel c : availableChannels)
				if (name.equals(c.getName())) {
					ch = c;
					break;
				}

		return ch;
	}


	public Channel addMonitorChannel(String name) {
		Channel ch = getChannel(name);
		if (null != ch)
			monitoredChannels.add(name);
		
		return ch;
	}

	public Channel removeMonitorChannel(String name) {
		Channel ch = getChannel(name);
		if (null != ch)
			monitoredChannels.remove(name);
		
		return ch;
	}


	/**
	 * Gets a list of pre-selected channels, as defined by the users preferences.
	 * All Channels named by the preference list and in the available channels are returned.
	 * Unknown or unsupport names / channels are ignored.
	 * 
	 * @return a List of Channels from the pre-stored preferences.
	 * @see ChannelManager#getNamedChannels(List)
	 */
	public List<String> getMonitoredChannelNames() {
		monitoredChannels.refresh();
		return Collections.unmodifiableList(monitoredChannels);
	}


	/**
	 * loads the available channel set from the channels.xml file.
	 * Only Channels defined in that file, and available on the device are returned in the set.
	 * @return
	 */
	private static List<Channel> loadAvailableChannels(Context context) {

		Log.d(LOG_TAG, "loading available channels");

		List<Channel> availChnls = new ArrayList<Channel>();

		try {
			availChnls = ChannelXMLParser.parseChannelXML(context);

			Log.d(LOG_TAG, "found " + availChnls.size() + " supported channels");

		} catch (XmlPullParserException e) {
			String msg = "channel xml resource failed to parse";
			Log.e(LOG_TAG, msg, e);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			String msg = "channel xml resource failed to parse";
			Log.e(LOG_TAG, msg, e);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}

		return availChnls;
	}



}

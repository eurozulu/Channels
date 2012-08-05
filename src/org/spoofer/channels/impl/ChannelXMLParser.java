package org.spoofer.channels.impl;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.spoofer.channels.Channel;
import org.spoofer.channels.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

/**
 * parses the XML Channels file to create a Channel of each type defined in the XML.
 * 
 * @author robgilham
 *
 */
public class ChannelXMLParser {

	private static final String LOG_TAG = ChannelXMLParser.class.getSimpleName();

	private static final String ELEMENT_CHANNEL = "channel";
	private static final String ELEMENT_DESCRIPTION = "description";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_CLASS = "class";



	public static List<Channel> parseChannelXML(Context context) throws XmlPullParserException, IOException {

		Log.d(LOG_TAG, "parsing channel XML file starting");

		XmlResourceParser parser = context.getResources().getXml(R.xml.channels);
		List<Channel> channels = new ArrayList<Channel>();

		int eventType = parser.next();
		Channel ch = null;
		
		while (XmlPullParser.END_DOCUMENT != eventType) {

			switch (eventType) {
			case XmlPullParser.START_TAG : 

				String tagName = parser.getName();
				Log.v(LOG_TAG, "parsing tag " + tagName);

				if (ELEMENT_CHANNEL.equals(tagName)) {

					String className = parser.getAttributeValue(null, ATTR_CLASS);
					String channelName = parser.getAttributeValue(null, ATTR_NAME);

					Log.v(LOG_TAG, "parsing channel " + channelName + " with class " + className);

					try {
						ch = createChannel(channelName, className, context);

					} catch (ClassNotFoundException e) {
						String err = "channel " + channelName + " failed to load as the class " + className + " was not a valid " + Channel.class.getName();
						Log.e(LOG_TAG, err, e);
						throw new IOException(err, e);
					}

				} else if (ELEMENT_DESCRIPTION.equals(tagName)) {
					if (null == ch)
						throw new XmlPullParserException("Description found outside channel definition");

					String desc = parser.nextText();
					Log.v(LOG_TAG, "parsing description '" + desc + "'");

					ch.setDescription(desc);

				}

				break;

			case XmlPullParser.END_TAG :

				String closeTagName = parser.getName();

				if (ELEMENT_CHANNEL.equals(closeTagName)) {
					
					if (null != ch && ch.isSupported()) {
						Log.d(LOG_TAG, "Channel " + ch.getName() + " is suported." );
						channels.add(ch);
					} else
						Log.d(LOG_TAG, "Channel " + ch.getName() + " is not supported");
					
					ch  = null;
				}

				break;
			}

			eventType = parser.next();
		}
		
		return channels;

	}
	
	
	
	/**
	 * create a new Channel object of the given name and context using the given class name.
	 * @param channelName	The unique name of the Channel.
	 * @param className		the class name the Channel will be created as.
	 * @param context
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Channel createChannel(String channelName, String className, Context context) throws IOException, ClassNotFoundException {

		Channel newChannel;

		Class<? extends Object> rawClass = Class.forName(className);
		Log.v(LOG_TAG, "found class " + rawClass.getName());

		if (null == rawClass || !Channel.class.isAssignableFrom(rawClass))
			throw new NotFoundException("Failed to load channel " + className + " as it is not a " + Channel.class.getSimpleName() + " class.");

		Class<? extends Channel> channelClass = rawClass.asSubclass(Channel.class);

		try {
		
			Constructor<? extends Channel> constructor = channelClass.getConstructor(Context.class);
			Log.v(LOG_TAG, "class " + className + " is a valid " + Channel.class.getSimpleName() + " class, creating new instance named " + channelName);
			newChannel = constructor.newInstance(context);

		} catch (Exception e) {
			String err = "Failed to load channel " + channelName + ". " + e.getMessage();
			Log.e(LOG_TAG, err, e);
			throw new IOException(err, e);
		}

		return newChannel;
	}


	
	
}

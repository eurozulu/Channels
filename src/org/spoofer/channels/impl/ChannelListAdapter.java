package org.spoofer.channels.impl;

import java.util.List;

import org.spoofer.channels.Channel;
import org.spoofer.channels.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Channel List Adapter implements the Android ListAdapter interface to present Channels
 * to a list.
 * All channels that are available (supported) are presented in the list as checkable options.
 * The state of the checkbox selecting each channel is controlled with the given list of selected channel names.
 * 
 * @author robgilham
 *
 */
public class ChannelListAdapter extends BaseAdapter {

	private final LayoutInflater inflater;
	private final List<Channel> availableChannels;
	private final List<String> selectedNames;
	private final CompoundButton.OnCheckedChangeListener changeListener;

	/**
	 * 
	 * @param context
	 * @param viewID
	 * @param channels
	 * @param selectedNames
	 */
	public ChannelListAdapter(Context context, long viewID, List<Channel> channels, List<String> selectedNames, CompoundButton.OnCheckedChangeListener changeListener) {
		super();
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.availableChannels = channels;
		this.selectedNames = selectedNames;
		this.changeListener = changeListener;
	}

	
	@Override
	public int getCount() {
		return availableChannels.size();
	}

	@Override
	public Object getItem(int position) {
		return availableChannels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		//final ChannelSelector channelSelect = new ChannelSelector(getApplicationContext());

		final CheckBox chkChannel = (CheckBox)inflater.inflate(R.layout.pref_channel_list_item, null, false);
		final Channel ch = availableChannels.get(position);
		final String name = ch.getName();

		chkChannel.setText(name);
		chkChannel.setChecked(null != selectedNames && selectedNames.contains(name));

		if (!ch.isSupported())
			chkChannel.setEnabled(false);
		
		else if (null != this.changeListener)  // Set the change listener for check box changes.
			chkChannel.setOnCheckedChangeListener(changeListener);

		return chkChannel;
	}
	
	
	
}
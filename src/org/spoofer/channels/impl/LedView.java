package org.spoofer.channels.impl;

import org.spoofer.channels.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LedView extends ImageView {

	public static final int LIGHT_OFF = 0;
	public static final int LIGHT_GREEN = 1;
	public static final int LIGHT_RED = 2;
	
	private int lastIDSet = 0;
	private int IDSet = 0;
	
	
	public LedView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public LedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public LedView(Context context) {
		super(context);
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (IDSet != lastIDSet) { // Image has changed
			setImageResource(IDSet);
			lastIDSet = IDSet;
		}
		super.onDraw(canvas);
	}
	
	public int getLightState() {
		return lastIDSet;
	}
	
	public void setLightState(int lightState) {
		
		int imageID = -1;
		
		switch (lightState) {
		case LIGHT_OFF :
			imageID = R.drawable.light_off;
			break;
			
		case LIGHT_GREEN :
			imageID = R.drawable.light_green;
			break;
			
		case LIGHT_RED :
			imageID = R.drawable.light_red;
			break;
		
		}
		
		if (imageID >= 0) {
			this.IDSet = imageID;
			this.postInvalidate();
		}
	}


}

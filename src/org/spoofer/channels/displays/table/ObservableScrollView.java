package org.spoofer.channels.displays.table;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * A Scroll view that implements a listener for scroll events.
 * 
 * @author robgilham
 *
 */
public class ObservableScrollView extends ScrollView {

	
	private ScrollViewListener scrollViewListener = null;
	
	
	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
	
	public ScrollViewListener getScrollViewListener() {
        return this.scrollViewListener;
    }
	
	
	@Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        
        if(null != scrollViewListener)
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
    }
	
	
	
	
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		
		if (null != scrollViewListener)
			scrollViewListener.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		
	}




	/**
	 * Listener Interface that is notified when scroll events occur.
	 */
	public interface ScrollViewListener {
		public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
		
		public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
	}



}

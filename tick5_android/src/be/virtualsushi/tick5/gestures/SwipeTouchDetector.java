package be.virtualsushi.tick5.gestures;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;

public class SwipeTouchDetector extends SimpleOnGestureListener implements OnTouchListener {

	public enum SwipeDirection {

		LEFT,
		RIGHT,
		UP,
		DOWN;

	}

	public interface SwipeListener {

		void onSwipe(SwipeDirection direction);

	}

	private final int mSwipeMinDistance;
	private final int mSwipeMaxOffPath;
	private final int mSwipeTresholdVelocity;

	private GestureDetectorCompat gestureDetector;
	private SwipeListener mListener;

	public SwipeTouchDetector(Context context, SwipeListener listener) {
		gestureDetector = new GestureDetectorCompat(context, this);
		ViewConfiguration vc = ViewConfiguration.get(context);
		mSwipeMinDistance = vc.getScaledPagingTouchSlop();
		mSwipeTresholdVelocity = vc.getScaledMinimumFlingVelocity();
		mSwipeMaxOffPath = vc.getScaledMinimumFlingVelocity();
		mListener = listener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		try {

			boolean verticalIsOff = Math.abs(e1.getX() - e2.getX()) > mSwipeMaxOffPath;

			if (verticalIsOff) {
				return false;
			}

			if (Math.abs(velocityY) > mSwipeTresholdVelocity) {
				if (e1.getY() - e2.getY() > mSwipeMinDistance) {
					mListener.onSwipe(SwipeDirection.UP);
					return true;
				}
				if (e2.getY() - e1.getY() > mSwipeMinDistance) {
					mListener.onSwipe(SwipeDirection.DOWN);
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

}

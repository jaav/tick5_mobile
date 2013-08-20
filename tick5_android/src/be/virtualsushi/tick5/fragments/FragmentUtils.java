package be.virtualsushi.tick5.fragments;

import android.app.Activity;

public class FragmentUtils {

	public static <T> T tryActivityCast(Activity activity, Class<T> clazz, boolean noFail) {
		try {
			return clazz.cast(activity);
		} catch (Exception e) {
			if (!noFail) {
				new IllegalStateException(activity.getClass().getSimpleName() + " should implement " + clazz);
			}
			return null;
		}
	}

}

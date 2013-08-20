package be.virtualsushi.tick5;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Typeface;
import android.util.SparseArray;
import be.virtualsushi.tick5.backend.ImageManager;
import be.virtualsushi.tick5.backend.ImageManagerProvider;
import be.virtualsushi.tick5.backend.RequestQueueProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaceProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaces;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Tick5Application extends Application implements RobotoTypefaceProvider, RequestQueueProvider, ImageManagerProvider {

	public static final String TICK5_PREFERENCES = "tick5_preferences";
	public static final String DEFAULT_FILTER_NAME_PREFERENCE = "default_filter";

	private static final String ROBOTO_TYPEFACE_NAME_PATTERN = "Roboto-%s.ttf";

	private SparseArray<Typeface> mRobotoTypefaces;
	private RequestQueue mRequestQueue;
	private ImageManager mImageManager;

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate() {
		super.onCreate();
		mRobotoTypefaces = new SparseArray<Typeface>(RobotoTypefaces.values().length);
		mRequestQueue = Volley.newRequestQueue(this);
		mImageManager = new ImageManager(this);
	}

	@Override
	public Typeface getRobotoTypeface(RobotoTypefaces name) {
		int typefaceIndex = name.ordinal();
		Typeface fromCache = mRobotoTypefaces.get(typefaceIndex);
		if (fromCache == null) {
			fromCache = Typeface.createFromAsset(getAssets(), String.format(ROBOTO_TYPEFACE_NAME_PATTERN, name.getmTypefaceNamePostfix()));
			mRobotoTypefaces.put(typefaceIndex, fromCache);
		}
		return fromCache;
	}

	@Override
	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	@Override
	public ImageManager getImageManager() {
		return mImageManager;
	}

}

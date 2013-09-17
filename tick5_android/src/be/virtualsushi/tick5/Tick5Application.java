package be.virtualsushi.tick5;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

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

@ReportsCrashes(formKey = "", mode = ReportingInteractionMode.DIALOG, mailTo = "pavel@pavel.st", resDialogText = R.string.crash_dialog_text, resDialogIcon = android.R.drawable.ic_dialog_info, resDialogTitle = R.string.crash_dialog_title, resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, resDialogOkToast = R.string.crash_dialog_ok_toast, customReportContent = {
		ReportField.USER_COMMENT, ReportField.ANDROID_VERSION, ReportField.PACKAGE_NAME, ReportField.STACK_TRACE, ReportField.APP_VERSION_NAME })
public class Tick5Application extends Application implements RobotoTypefaceProvider, RequestQueueProvider, ImageManagerProvider {

	public static final String TICK5_PREFERENCES = "tick5_preferences";
	public static final String DEFAULT_FILTER_NAME_PREFERENCE = "default_filter";
	public static final String SAVED_TWEETS_PREFERENCE = "tweets";
	public static final String SAVED_KEY_PREFERENCE = "key";

	private static final String ROBOTO_TYPEFACE_NAME_PATTERN = "Roboto-%s.ttf";

	private SparseArray<Typeface> mRobotoTypefaces;
	private RequestQueue mRequestQueue;
	private ImageManager mImageManager;

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
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

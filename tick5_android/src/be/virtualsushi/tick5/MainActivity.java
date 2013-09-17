package be.virtualsushi.tick5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.Toast;
import be.virtualsushi.tick5.backend.ImageManager;
import be.virtualsushi.tick5.backend.ImageManagerProvider;
import be.virtualsushi.tick5.backend.PublicKeyRequest;
import be.virtualsushi.tick5.backend.RequestQueueProvider;
import be.virtualsushi.tick5.backend.Tick5Request;
import be.virtualsushi.tick5.fragments.PagerFragment;
import be.virtualsushi.tick5.fragments.ProgressBarFragment;
import be.virtualsushi.tick5.fragments.TickFragment.TickFragmentListener;
import be.virtualsushi.tick5.gestures.ShakeDetector;
import be.virtualsushi.tick5.gestures.ShakeDetector.ShakeDetectorListener;
import be.virtualsushi.tick5.model.Tick;
import be.virtualsushi.tick5.model.Tick5Response;
import be.virtualsushi.tick5.model.Tick5Response.ResponseStatuses;
import be.virtualsushi.tick5.roboto.RobotoTypefaceProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaces;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class MainActivity extends SherlockFragmentActivity implements RobotoTypefaceProvider, RequestQueueProvider, ErrorListener, ImageManagerProvider, TickFragmentListener, OnPageChangeListener, ShakeDetectorListener {

	public static final String FILTER_NAME_EXTRA = "filter_name";
	public static final String TICKS_EXTRA = "ticks";
	public static final String LATEST_POSITION_EXTRA = "latest_position";
	public static final String LATEST_KEY_EXTRA = "latest_key";

	private static final int CHANGE_SETTINGS_REQUEST_CODE = 1;

	private String mLatestKey;

	private Tick[] mTweets;
	private int mLatestPosition = -1;

	private ShakeDetector mShakeDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		mShakeDetector = new ShakeDetector((SensorManager) getSystemService(SENSOR_SERVICE), this);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(TICKS_EXTRA)) {
				mTweets = Tick.fromJsonStringsArray(savedInstanceState.getStringArray(TICKS_EXTRA));
			}
			if (savedInstanceState.containsKey(LATEST_POSITION_EXTRA)) {
				mLatestPosition = savedInstanceState.getInt(LATEST_POSITION_EXTRA);
			}
			if (savedInstanceState.containsKey(LATEST_KEY_EXTRA)) {
				mLatestKey = savedInstanceState.getString(LATEST_KEY_EXTRA);
			}
		}
		if (mTweets == null) {
			String jsonString = getTick5Preferences().getString(Tick5Application.SAVED_TWEETS_PREFERENCE, null);
			if (jsonString != null) {
				mTweets = Tick.jsonToArray(jsonString);
			}
		}

		if (mLatestKey == null) {
			mLatestKey = getTick5Preferences().getString(Tick5Application.SAVED_KEY_PREFERENCE, null);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mTweets != null && mTweets.length > 0) {
			outState.putStringArray(TICKS_EXTRA, Tick.toJsonStringsArray(mTweets));
			outState.putInt(LATEST_POSITION_EXTRA, mLatestPosition);
			outState.putString(LATEST_KEY_EXTRA, mLatestKey);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTweets != null) {
			showContent(false);
		}
		updateData();
		mShakeDetector.onRegister();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mShakeDetector.onUnregister();
		Editor editor = getTick5Preferences().edit();
		editor.putString(Tick5Application.SAVED_TWEETS_PREFERENCE, Tick.arrayToJson(mTweets));
		editor.putString(Tick5Application.SAVED_KEY_PREFERENCE, mLatestKey);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			updateData();
			break;

		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;

		case R.id.menu_settings:
			startActivityForResult(new Intent(this, SettingsActivity.class).putExtra(FILTER_NAME_EXTRA, getTick5Preferences().getString(Tick5Application.DEFAULT_FILTER_NAME_PREFERENCE, "cartoon")), CHANGE_SETTINGS_REQUEST_CODE);
			break;
		}
		return true;
	}

	private void updateData() {
		if (mTweets == null) {
			showProgressBar();
		}
		getRequestQueue().add(new PublicKeyRequest(new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("Tick5", "Key:" + response);
				if (mLatestKey == null || !response.equals(mLatestKey) || mTweets == null) {
					mLatestKey = response;
					loadTicks(mLatestKey);
				}
			}
		}, this));
	}

	private Tick5Application getTick5Application() {
		return (Tick5Application) getApplication();
	}

	@Override
	public RequestQueue getRequestQueue() {
		return getTick5Application().getRequestQueue();
	}

	@Override
	public Typeface getRobotoTypeface(RobotoTypefaces name) {
		return getTick5Application().getRobotoTypeface(name);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show();
	}

	private void loadTicks(String key) {
		getRequestQueue().add(new Tick5Request(key, new Listener<Tick5Response>() {

			@Override
			public void onResponse(Tick5Response response) {
				if (ResponseStatuses.OK.equals(response.status)) {
					mTweets = response.tweets;
					showContent(false);
				}
			}

		}, this));
	}

	private void showProgressBar() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.container, new ProgressBarFragment());
		fragmentTransaction.commit();
	}

	private void showContent(boolean preservePosition) {
		if (mTweets != null) {
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			if (preservePosition) {
				fragmentTransaction.replace(R.id.container, PagerFragment.getInstance(mTweets, mLatestPosition));
			} else {
				fragmentTransaction.replace(R.id.container, PagerFragment.getInstance(mTweets));
			}
			fragmentTransaction.commit();
		}
	}

	@Override
	public ImageManager getImageManager() {
		return getTick5Application().getImageManager();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == CHANGE_SETTINGS_REQUEST_CODE) {
			String filterName = data.getExtras().getString(FILTER_NAME_EXTRA);
			getImageManager().setFilterName(filterName);
			Editor editor = getTick5Preferences().edit();
			editor.putString(Tick5Application.DEFAULT_FILTER_NAME_PREFERENCE, filterName);
			editor.commit();
		}
	}

	private SharedPreferences getTick5Preferences() {
		return getSharedPreferences(Tick5Application.TICK5_PREFERENCES, MODE_PRIVATE);
	}

	@Override
	public void onFilterChange() {
		showContent(true);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int pageNum) {
		mLatestPosition = pageNum;
	}

	@Override
	public void onShake() {
		Log.d("SHAKE", "SHAKE");
		getImageManager().nextFilter();
		showContent(true);
	}

}

package be.virtualsushi.tick5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import be.virtualsushi.tick5.backend.ImageManager;
import be.virtualsushi.tick5.backend.ImageManagerProvider;
import be.virtualsushi.tick5.backend.PublicKeyRequest;
import be.virtualsushi.tick5.backend.RequestQueueProvider;
import be.virtualsushi.tick5.backend.Tick5Request;
import be.virtualsushi.tick5.backend.TicksAdapter;
import be.virtualsushi.tick5.fragments.TickFragment.TickFragmentListener;
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

public class MainActivity extends SherlockFragmentActivity implements RobotoTypefaceProvider, RequestQueueProvider, ErrorListener, ImageManagerProvider, SensorEventListener, TickFragmentListener {

	public static final String FILTER_NAME_EXTRA = "filter_name";
	public static final String TICKS_EXTRA = "ticks";

	private static final int CHANGE_SETTINGS_REQUEST_CODE = 1;

	private String mCurrentKey;

	private ViewPager mPager;
	private TicksAdapter mTicksAdapter;
	private RelativeLayout mProgressContainer;
	private SensorManager mSensorManager;
	private Sensor mSensor;

	private float mAccelCurrent;
	private float mAccel;

	private Tick[] mTweets;

	private boolean mShakeFreeze;

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayUseLogoEnabled(false);

		mProgressContainer = (RelativeLayout) findViewById(R.id.progress_container);
		mPager = (ViewPager) findViewById(R.id.pager);
		mTicksAdapter = new TicksAdapter(getSupportFragmentManager(), new Tick[0]);
		mPager.setAdapter(mTicksAdapter);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		if (savedInstanceState != null && savedInstanceState.containsKey(TICKS_EXTRA)) {
			mTweets = (Tick[]) savedInstanceState.get(TICKS_EXTRA);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mTweets != null && mTweets.length > 0) {
			outState.putSerializable(TICKS_EXTRA, mTweets);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTweets != null) {
			updatePager(false);
		} else {
			updateData();
		}
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
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
		getRequestQueue().add(new PublicKeyRequest(new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("Tick5", "Key:" + response);
				mProgressContainer.setVisibility(View.VISIBLE);
				mCurrentKey = response;
				loadTicks(mCurrentKey);
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
					updatePager(false);
				}
			}

		}, this));
	}

	private void updatePager(boolean preservePosition) {
		if (mTweets != null) {
			int position = -1;
			if (preservePosition) {
				position = mPager.getCurrentItem();
			}
			mTicksAdapter.refreshData(mTweets);
			if (position < 0) {
				mPager.setCurrentItem(mPager.getChildCount() * TicksAdapter.LOOPS_COUNT / 2, false);
			} else {
				mPager.setCurrentItem(position);
			}
			mProgressContainer.setVisibility(View.INVISIBLE);
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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (!mShakeFreeze && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			float accelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - accelLast;
			mAccel = mAccel * 0.9f + delta;

			if (mAccel > 10) {
				mShakeFreeze = true;
				Log.d("SHAKE", "SHAKE");
				getImageManager().nextFilter();
				updatePager(true);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mShakeFreeze = false;
					}
				}, 2000);
			}
		}
	}

	@Override
	public void onFilterChange() {
		updatePager(true);
	}

}

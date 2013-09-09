package be.virtualsushi.tick5.gestures;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

	public interface ShakeDetectorListener {

		void onShake();

	}

	private class DataPoint {
		public float x, y, z;
		public long atTimeMilliseconds;

		public DataPoint(float x, float y, float z, long atTimeMilliseconds) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.atTimeMilliseconds = atTimeMilliseconds;
		}
	}

	private static final int SHAKE_CHECK_THRESHOLD = 200;
	private static final int IGNORE_EVENTS_AFTER_SHAKE = 1000;
	private static final long KEEP_DATA_POINTS_FOR = 1500;
	private static final long MINIMUM_EACH_DIRECTION = 3;
	private static final float POSITIVE_COUNTER_THRESHHOLD = (float) 2.0;
	private static final float NEGATIVE_COUNTER_THRESHHOLD = (float) -2.0;

	private final SensorManager mSensorManager;
	private final ShakeDetectorListener mListener;
	private Sensor mSensor;

	private List<DataPoint> dataPoints = new ArrayList<DataPoint>();

	/**
	 * After we detect a shake, we ignore any events for a bit of time. We don't
	 * want two shakes to close together.
	 */
	private long lastUpdate;
	private long lastShake = 0;

	private float lastX = 0, lastY = 0, lastZ = 0;

	public ShakeDetector(SensorManager sensorManager, ShakeDetectorListener listener) {
		mSensorManager = sensorManager;
		mListener = listener;
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	public void onRegister() {
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);

	}

	public void onUnregister() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			long curTime = System.currentTimeMillis();
			// if a shake in last X seconds ignore.
			if (lastShake != 0 && (curTime - lastShake) < IGNORE_EVENTS_AFTER_SHAKE)
				return;

			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			if (lastX != 0 && lastY != 0 && lastZ != 0 && (lastX != x || lastY != y || lastZ != z)) {
				DataPoint dp = new DataPoint(lastX - x, lastY - y, lastZ - z, curTime);
				dataPoints.add(dp);

				if ((curTime - lastUpdate) > SHAKE_CHECK_THRESHOLD) {
					lastUpdate = curTime;
					checkForShake();
				}
			}
			lastX = x;
			lastY = y;
			lastZ = z;
		}
	}

	public void checkForShake() {
		long curTime = System.currentTimeMillis();
		long cutOffTime = curTime - KEEP_DATA_POINTS_FOR;
		while (dataPoints.size() > 0 && dataPoints.get(0).atTimeMilliseconds < cutOffTime)
			dataPoints.remove(0);

		int x_pos = 0, x_neg = 0, x_dir = 0, y_pos = 0, y_neg = 0, y_dir = 0, z_pos = 0, z_neg = 0, z_dir = 0;
		for (DataPoint dp : dataPoints) {
			if (dp.x > POSITIVE_COUNTER_THRESHHOLD && x_dir < 1) {
				++x_pos;
				x_dir = 1;
			}
			if (dp.x < NEGATIVE_COUNTER_THRESHHOLD && x_dir > -1) {
				++x_neg;
				x_dir = -1;
			}
			if (dp.y > POSITIVE_COUNTER_THRESHHOLD && y_dir < 1) {
				++y_pos;
				y_dir = 1;
			}
			if (dp.y < NEGATIVE_COUNTER_THRESHHOLD && y_dir > -1) {
				++y_neg;
				y_dir = -1;
			}
			if (dp.z > POSITIVE_COUNTER_THRESHHOLD && z_dir < 1) {
				++z_pos;
				z_dir = 1;
			}
			if (dp.z < NEGATIVE_COUNTER_THRESHHOLD && z_dir > -1) {
				++z_neg;
				z_dir = -1;
			}
		}

		if ((x_pos >= MINIMUM_EACH_DIRECTION && x_neg >= MINIMUM_EACH_DIRECTION) || (y_pos >= MINIMUM_EACH_DIRECTION && y_neg >= MINIMUM_EACH_DIRECTION) || (z_pos >= MINIMUM_EACH_DIRECTION && z_neg >= MINIMUM_EACH_DIRECTION)) {
			lastShake = System.currentTimeMillis();
			lastX = 0;
			lastY = 0;
			lastZ = 0;
			dataPoints.clear();
			mListener.onShake();
			return;
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}

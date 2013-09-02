package be.virtualsushi.tick5.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import be.virtualsushi.tick5.R;
import be.virtualsushi.tick5.Tick5Application;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class ImageManager {

	private static final String TICK5_IMAGE_URL_PATTERN = "http://tick5.be/repo/datatracker_images/%s_%s.jpg";

	private class DiskImageCache implements ImageCache {

		private File mRootDirectory;

		public DiskImageCache(File rootDirectory) {
			mRootDirectory = rootDirectory;
		}

		@Override
		public Bitmap getBitmap(String url) {
			String fileName = url.substring(url.lastIndexOf("/"));
			File imageFile = new File(mRootDirectory, fileName);
			if (imageFile.exists()) {
				return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			}
			return null;
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			String fileName = url.substring(url.lastIndexOf("/"));
			File imageFile = new File(mRootDirectory, fileName);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(imageFile);
				bitmap.compress(CompressFormat.JPEG, 100, out);
			} catch (FileNotFoundException e) {
				Log.e("ImageManagerCache", "Unable to cache image: " + e.getMessage());
			} finally {
				try {
					out.close();
				} catch (Exception e) {

				}
			}
		}
	}

	private ImageLoader mImageLoader;
	private String mFilterName;
	private String[] mFilters;
	private ImageCache mImageCache;

	public ImageManager(Context context) {
		mImageCache = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			mImageCache = new DiskImageCache(context.getExternalCacheDir());
		} else {
			mImageCache = new DiskImageCache(context.getCacheDir());
		}
		mImageLoader = new ImageLoader(((Tick5Application) context.getApplicationContext()).getRequestQueue(), mImageCache);
		mFilters = context.getResources().getStringArray(R.array.filters);
		mFilterName = context.getSharedPreferences(Tick5Application.TICK5_PREFERENCES, Context.MODE_PRIVATE).getString(Tick5Application.DEFAULT_FILTER_NAME_PREFERENCE, "cartoon");
	}

	public void loadImage(String imageId, ImageListener imageListener, int maxWidth, int maxHeight) {
		String requestUrl = String.format(TICK5_IMAGE_URL_PATTERN, imageId, mFilterName);
		mImageLoader.get(requestUrl, imageListener, maxWidth, maxHeight);
	}

	public Bitmap loadFromCache(String imageId) {
		return mImageCache.getBitmap(String.format(TICK5_IMAGE_URL_PATTERN, imageId, mFilterName));
	}

	public void nextFilter() {
		int currentFilterIndex = getCurrentFilterIndex();
		currentFilterIndex++;
		if (currentFilterIndex >= mFilters.length) {
			currentFilterIndex = 0;
		}
		mFilterName = mFilters[currentFilterIndex];
	}

	public int getCurrentFilterIndex() {
		for (int i = 0; i < mFilters.length; i++) {
			if (mFilterName.equalsIgnoreCase(mFilters[i])) {
				Log.d("ImageManager", "Filter name:" + mFilters[i]);
				return i;
			}
		}
		Log.d("ImageManager", "Default filter:" + mFilters[0]);
		return 0;
	}

	public void setFilterName(String filterName) {
		mFilterName = filterName;
	}

	public String getFilterName() {
		return mFilterName;
	}

	public void prevFilter() {
		int currentFilterIndex = getCurrentFilterIndex();
		currentFilterIndex--;
		if (currentFilterIndex < 0) {
			currentFilterIndex = mFilters.length - 1;
		}
		mFilterName = mFilters[currentFilterIndex];
	}

	public String[] getFilterNames() {
		return mFilters;
	}
}

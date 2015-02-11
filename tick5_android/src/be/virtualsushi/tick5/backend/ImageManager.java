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

	private static final String TICK5_IMAGE_URL_PATTERN = "http://tick5.be/repo/datatracker_images/%s_squared.jpg";

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
	private ImageCache mImageCache;

	public ImageManager(Context context) {
		mImageCache = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			mImageCache = new DiskImageCache(context.getExternalCacheDir());
		} else {
			mImageCache = new DiskImageCache(context.getCacheDir());
		}
		mImageLoader = new ImageLoader(((Tick5Application) context.getApplicationContext()).getRequestQueue(), mImageCache);
	}

	public void loadImage(String imageId, ImageListener imageListener, int maxWidth, int maxHeight) {
		String requestUrl = String.format(TICK5_IMAGE_URL_PATTERN, imageId);
		mImageLoader.get(requestUrl, imageListener, maxWidth, maxHeight);
	}

	public Bitmap loadFromCache(String imageId) {
		return mImageCache.getBitmap(String.format(TICK5_IMAGE_URL_PATTERN, imageId));
	}
}

package be.virtualsushi.tick5.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class MemorySafeImageLoader {

	public class PreDecodeInfo {

		public int width;
		public int height;

		public PreDecodeInfo(int width, int height) {
			this.width = width;
			this.height = height;
		}

	}

	private Resources mResources;
	private Context mContext;

	private int mDisplayHeight;
	private int mDisplayWidth;

	public MemorySafeImageLoader(Context context) {
		mResources = context.getResources();
		mContext = context;
		DisplayMetrics displayMetrics = mResources.getDisplayMetrics();
		mDisplayHeight = displayMetrics.heightPixels;
		mDisplayWidth = displayMetrics.widthPixels;
	}

	private Options createDecodeOptions() {
		Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[32 * 1024];
		options.inJustDecodeBounds = true;
		return options;
	}

	public Drawable getDrawableFromResource(int resourceId, int desiredWidth, int desiredHeight) {
		return new BitmapDrawable(mResources, decodeFromResource(resourceId, desiredWidth, desiredHeight));
	}

	public Drawable getDrawableFromFilePath(String filePath, int desiredWidth, int desiredHeight) {
		return new BitmapDrawable(mResources, decodeFromFilePath(filePath, desiredWidth, desiredHeight));
	}

	public Bitmap decodeFromResource(int resourceId, int desiredWidth, int desiredHeight) {
		Options options = createDecodeOptions();
		BitmapFactory.decodeResource(mResources, resourceId, options);
		onCalculateOptionsSampleSize(options, desiredWidth, desiredHeight);
		return BitmapFactory.decodeResource(mResources, resourceId, options);
	}

	public Bitmap decodeFromFilePath(String filePath, int desiredWidth, int desiredHeight) {
		Options options = createDecodeOptions();
		BitmapFactory.decodeFile(filePath, options);
		onCalculateOptionsSampleSize(options, desiredWidth, desiredHeight);
		return BitmapFactory.decodeFile(filePath, options);
	}

	public Bitmap decodeFromByteArray(byte[] data, int desiredWidth, int desiredHeight) {
		Options options = createDecodeOptions();
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		onCalculateOptionsSampleSize(options, desiredWidth, desiredHeight);
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public Bitmap decodeFromStream(InputStream input, int desiredWidth, int desiredHeight) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			while (input.read(buffer) != -1) {
				out.write(buffer);
			}
			return decodeFromByteArray(out.toByteArray(), desiredWidth, desiredHeight);
		} catch (IOException e) {
		} finally {
			try {
				input.close();
			} catch (IOException e) {

			}
			try {
				out.close();
			} catch (IOException e) {

			}
		}
		return null;
	}

	public PreDecodeInfo predecodeFromFile(String filePath) {
		Options options = createDecodeOptions();
		BitmapFactory.decodeFile(filePath, options);
		return new PreDecodeInfo(options.outWidth, options.outHeight);
	}

	public Bitmap decodeFromMediaUri(Uri mediaUri, int desiredWidth, int desiredHeight) {
		CursorLoader loader = new CursorLoader(mContext, mediaUri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
		Cursor cursor = loader.loadInBackground();
		cursor.moveToFirst();
		String filePath = cursor.getString(0);
		cursor.close();
		return decodeFromFilePath(filePath, desiredWidth, desiredHeight);
	}

	private void onCalculateOptionsSampleSize(Options options, int desiredWidth, int desiredHeight) {
		if (desiredWidth <= 0) {
			desiredWidth = mDisplayWidth;
		}
		if (desiredHeight <= 0) {
			desiredHeight = mDisplayHeight;
		}
		int realImageHeight = options.outHeight;
		int realImageWidth = options.outWidth;
		int inSampleSize = 1;

		if (realImageHeight > desiredHeight || realImageWidth > desiredWidth) {
			final int heightRatio = Math.round((float) realImageHeight / (float) desiredHeight);
			final int widthRatio = Math.round((float) realImageWidth / (float) desiredWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
	}

	public void setImageDrawableSafely(ImageView imageView, String filePath) {
		Bitmap bitmap = decodeFromFilePath(filePath, imageView.getWidth(), imageView.getHeight());
		Log.d("IMAGE LOADER", bitmap.getHeight() + " " + bitmap.getWidth());
		imageView.setImageBitmap(bitmap);
	}

	public void setImageDrawableSafely(ImageView imageView, Uri mediaUri) {
		Bitmap bitmap = decodeFromMediaUri(mediaUri, imageView.getWidth(), imageView.getHeight());
		Log.d("IMAGE LOADER", bitmap.getHeight() + " " + bitmap.getWidth());
		imageView.setImageBitmap(bitmap);
	}

}

package be.virtualsushi.tick5.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {

	private Resources mResources;

	public AspectRatioImageView(Context context) {
		super(context);
		mResources = context.getResources();
	}

	public AspectRatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mResources = context.getResources();
	}

	public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mResources = context.getResources();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if (mResources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && getDrawable().getIntrinsicWidth() > 0) {
			height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
		}
		if (mResources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && getDrawable().getIntrinsicHeight() > 0) {
			width = height * getDrawable().getIntrinsicWidth() / getDrawable().getIntrinsicHeight();
		}
		setMeasuredDimension(width, height);
	}
}

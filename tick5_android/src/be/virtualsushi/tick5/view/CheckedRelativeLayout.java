package be.virtualsushi.tick5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckedRelativeLayout extends RelativeLayout implements Checkable {

	public CheckedRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckedRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckedRelativeLayout(Context context) {
		super(context);
	}

	@Override
	public boolean isChecked() {
		return isSelected();
	}

	@Override
	public void setChecked(boolean checked) {
		setSelected(checked);
	}

	@Override
	public void toggle() {
		setChecked(!isChecked());
	}

}

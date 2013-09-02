package be.virtualsushi.tick5.backend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import be.virtualsushi.tick5.fragments.TickFragment;
import be.virtualsushi.tick5.model.Tick;

public class TicksAdapter extends FragmentPagerAdapter {

	public static final int LOOPS_COUNT = 30;

	private Tick[] mTicks;

	public TicksAdapter(FragmentManager fm, Tick[] ticks) {
		super(fm);
		mTicks = ticks;
	}

	@Override
	public Fragment getItem(int position) {
		position = position % mTicks.length;
		return TickFragment.getIntance(mTicks[position]);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return mTicks.length * LOOPS_COUNT;
	}

}

package be.virtualsushi.tick5.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.virtualsushi.tick5.R;
import be.virtualsushi.tick5.backend.EventBusProvider;
import be.virtualsushi.tick5.backend.TicksAdapter;
import be.virtualsushi.tick5.events.UpdateTicksEvent;
import be.virtualsushi.tick5.model.Tick;
import de.greenrobot.event.EventBus;

public class PagerFragment extends Fragment {

	private static final String TICKS_ARGUMENT = "ticks";
	private static final String POSITION_ARGUMENT = "position";

	public static PagerFragment getInstance(Tick[] ticks) {
		PagerFragment fragment = new PagerFragment();
		Bundle arguments = new Bundle();
		arguments.putParcelableArray(TICKS_ARGUMENT, ticks);
		fragment.setArguments(arguments);
		return fragment;
	}

	public static PagerFragment getInstance(Tick[] ticks, int position) {
		PagerFragment fragment = getInstance(ticks);
		fragment.getArguments().putInt(POSITION_ARGUMENT, position);
		return fragment;
	}

	private ViewPager mPager;
	private OnPageChangeListener mOnPageChangeListener;
	private EventBus mEventBus;

	private Tick[] mTicks;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mOnPageChangeListener = FragmentUtils.tryActivityCast(getActivity(), OnPageChangeListener.class, true);
		mEventBus = FragmentUtils.tryActivityCast(getActivity(), EventBusProvider.class, false).getEventBus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.fragment_pager, container, false);

		mPager = (ViewPager) result.findViewById(R.id.pager);
		Parcelable[] parcelables = getArguments().getParcelableArray(TICKS_ARGUMENT);
		mTicks = new Tick[parcelables.length];
		int i = 0;
		for (Parcelable parcelable : parcelables) {
			mTicks[i] = (Tick) parcelable;
			i++;
		}
		mPager.setAdapter(new TicksAdapter(getChildFragmentManager(), mTicks));
		if (mOnPageChangeListener != null) {
			mPager.setOnPageChangeListener(mOnPageChangeListener);
		}

		return result;
	}

	@Override
	public void onPause() {
		super.onPause();
		mEventBus.unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mEventBus.register(this);
		Bundle arguments = getArguments();
		if (arguments.containsKey(POSITION_ARGUMENT)) {
			mPager.setCurrentItem(arguments.getInt(POSITION_ARGUMENT));
		} else {
			mPager.setCurrentItem(mTicks.length * TicksAdapter.LOOPS_COUNT / 2);
		}
	}

	public void onEvent(UpdateTicksEvent event) {
		mTicks = event.ticks;
		int position = mTicks.length * TicksAdapter.LOOPS_COUNT / 2;
		if (event.rememberPosition) {
			position = mPager.getCurrentItem();
		}
		mPager.setAdapter(new TicksAdapter(getChildFragmentManager(), mTicks));
		mPager.setCurrentItem(position);
	}

}

package be.virtualsushi.tick5.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import be.virtualsushi.tick5.R;
import be.virtualsushi.tick5.backend.ImageManager;
import be.virtualsushi.tick5.backend.ImageManagerProvider;
import be.virtualsushi.tick5.gestures.SwipeTouchDetector;
import be.virtualsushi.tick5.gestures.SwipeTouchDetector.SwipeDirection;
import be.virtualsushi.tick5.gestures.SwipeTouchDetector.SwipeListener;
import be.virtualsushi.tick5.model.Tick;
import be.virtualsushi.tick5.roboto.RobotoTypefaceProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaces;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class TickFragment extends SherlockFragment implements SwipeListener {

	public interface TickFragmentListener {

		void onFilterChange();

	}

	public static final String TICK_ARGUMENT_NAME = "tick";

	private static final String TWITTER_HASHTAG_URL_PATTERN = "<a href=\"https://twitter.com/search?q=%23%s\">%s</a>";
	private static final String TWITTER_AUTHOR_URL_PATTERN = "<a href=\"https://twitter.com/#!/%s\">@%s</a>";

	private TextView mTweet;
	private LinearLayout mLinksContainer;
	private TextView mAuthor;
	private ImageView mImage;
	private RelativeLayout mContainer;

	private RobotoTypefaceProvider mRobotoTypefaceProvider;
	private ImageManager mImageManager;
	private TickFragmentListener mListener;

	public static TickFragment getIntance(Tick tick) {
		TickFragment fragment = new TickFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable(TICK_ARGUMENT_NAME, tick);
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mRobotoTypefaceProvider = FragmentUtils.tryActivityCast(getActivity(), RobotoTypefaceProvider.class, false);
		mImageManager = FragmentUtils.tryActivityCast(getActivity(), ImageManagerProvider.class, false).getImageManager();
		mListener = FragmentUtils.tryActivityCast(getActivity(), TickFragmentListener.class, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.fragment_tick, container, false);

		SwipeTouchDetector swipeTouchDetector = new SwipeTouchDetector(getActivity(), this);

		Tick tick = (Tick) getArguments().get(TICK_ARGUMENT_NAME);

		mTweet = (TextView) result.findViewById(R.id.tweet);
		mTweet.setMovementMethod(LinkMovementMethod.getInstance());
		mTweet.setTypeface(mRobotoTypefaceProvider.getRobotoTypeface(RobotoTypefaces.REGULAR));
		if (tick.hashtags != null) {
			String formatterTweet = tick.tweet;
			for (String hashTag : tick.hashtags) {
				String fullHashTagString = "#" + hashTag;
				formatterTweet = formatterTweet.replace(fullHashTagString, String.format(TWITTER_HASHTAG_URL_PATTERN, hashTag, fullHashTagString));
			}
			mTweet.setText(Html.fromHtml(formatterTweet));
		} else {
			mTweet.setText(tick.tweet);
		}

		mAuthor = (TextView) result.findViewById(R.id.author);
		mAuthor.setMovementMethod(LinkMovementMethod.getInstance());
		mAuthor.setTypeface(mRobotoTypefaceProvider.getRobotoTypeface(RobotoTypefaces.REGULAR));
		mAuthor.setText(Html.fromHtml(String.format(TWITTER_AUTHOR_URL_PATTERN, tick.author, tick.author)));

		mImage = (ImageView) result.findViewById(R.id.image);
		mImage.setOnTouchListener(swipeTouchDetector);
		mImage.setImageResource(getDrawableResourceIdByFilterName());

		mContainer = (RelativeLayout) result.findViewById(R.id.container);
		mContainer.setOnTouchListener(swipeTouchDetector);

		mLinksContainer = (LinearLayout) result.findViewById(R.id.links_container);
		if (tick.urls != null && tick.urls.length > 0) {
			mLinksContainer.setVisibility(View.VISIBLE);
			for (int i = 0; i < tick.urls.length; i++) {
				TextView linkView = (TextView) inflater.inflate(R.layout.view_url_text, null, false);
				linkView.setMovementMethod(LinkMovementMethod.getInstance());
				if (i > 0) {
					linkView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.tiny), 0, 0);
				}
				linkView.setText(Html.fromHtml(tick.urls[i]));
				mLinksContainer.addView(linkView);
			}
		}

		loadImage(tick);

		return result;
	}

	private int getDrawableResourceIdByFilterName() {
		return getResources().getIdentifier("no_image_" + mImageManager.getFilterName(), "drawable", getActivity().getPackageName());
	}

	private void loadImage(Tick tick) {
		Bitmap cachedBitmap = mImageManager.loadFromCache(tick.image);
		if (cachedBitmap != null) {
			mImage.setImageBitmap(cachedBitmap);
		} else {
			mImageManager.loadImage(tick.image, new ImageListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					mImage.setImageResource(getDrawableResourceIdByFilterName());
				}

				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					mImage.setImageBitmap(response.getBitmap());
				}
			}, mImage.getWidth(), mImage.getHeight());
		}
	}

	@Override
	public void onSwipe(SwipeDirection direction) {
		if (SwipeDirection.DOWN.equals(direction)) {
			mImageManager.prevFilter();
		} else {
			mImageManager.nextFilter();
		}
		mListener.onFilterChange();
	}
}

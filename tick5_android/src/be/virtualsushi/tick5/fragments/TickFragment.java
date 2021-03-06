package be.virtualsushi.tick5.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TickFragment extends Fragment{

	public interface TickFragmentListener {

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

	private int mDummyImageResource;

	private Tick mTick;

	public static TickFragment getIntance(Tick tick) {
		TickFragment fragment = new TickFragment();
		Bundle arguments = new Bundle();
		arguments.putParcelable(TICK_ARGUMENT_NAME, tick);
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mRobotoTypefaceProvider = FragmentUtils.tryActivityCast(getActivity(), RobotoTypefaceProvider.class, false);
		mImageManager = FragmentUtils.tryActivityCast(getActivity(), ImageManagerProvider.class, false).getImageManager();
		mListener = FragmentUtils.tryActivityCast(getActivity(), TickFragmentListener.class, false);
		Resources resources = getResources();
		mDummyImageResource = resources.getIdentifier("no_image_squared", "drawable", activity.getPackageName());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mImageManager = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.fragment_tick, container, false);

		mTick = getArguments().getParcelable(TICK_ARGUMENT_NAME);

		mTweet = (TextView) result.findViewById(R.id.tweet);
		mTweet.setMovementMethod(LinkMovementMethod.getInstance());
		mTweet.setTypeface(mRobotoTypefaceProvider.getRobotoTypeface(RobotoTypefaces.REGULAR));
		if (mTick.hashtags != null) {
			String formatterTweet = mTick.tweet;
			for (String hashTag : mTick.hashtags) {
				String fullHashTagString = "#" + hashTag;
				formatterTweet = formatterTweet.replace(fullHashTagString, String.format(TWITTER_HASHTAG_URL_PATTERN, hashTag, fullHashTagString));
			}
			mTweet.setText(Html.fromHtml(formatterTweet));
		} else {
			mTweet.setText(mTick.tweet);
		}

		mAuthor = (TextView) result.findViewById(R.id.author);
		mAuthor.setText("@"+mTick.author);
		//mAuthor.setMovementMethod(LinkMovementMethod.getInstance());
		mAuthor.setTypeface(mRobotoTypefaceProvider.getRobotoTypeface(RobotoTypefaces.REGULAR));

		Linkify.TransformFilter filter = new Linkify.TransformFilter() {
		    public final String transformUrl(final Matcher match, String url) {
		        return match.group();
		    }
		};

		Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
		String mentionScheme = "http://www.twitter.com/";
		Linkify.addLinks(mAuthor, mentionPattern, mentionScheme, null, filter);


		//mAuthor.setAutoLinkMask(Linkify.ALL);
		//mAuthor.setText(Html.fromHtml(String.format(TWITTER_AUTHOR_URL_PATTERN, mTick.author, mTick.author)));

		mImage = (ImageView) result.findViewById(R.id.image);
		mImage.setImageResource(mDummyImageResource);

		mContainer = (RelativeLayout) result.findViewById(R.id.container);

		mLinksContainer = (LinearLayout) result.findViewById(R.id.links_container);
		if (mTick.urls != null && mTick.urls.length > 0) {
			mLinksContainer.setVisibility(View.VISIBLE);
			for (int i = 0; i < mTick.urls.length; i++) {
				if(mTick.urls[i] != null){
					TextView linkView = (TextView) inflater.inflate(R.layout.view_url_text, null, false);
					linkView.setMovementMethod(LinkMovementMethod.getInstance());
					linkView.setText(Html.fromHtml(mTick.urls[i]));
					mLinksContainer.addView(linkView);
				}
			}
		}

		loadImage(mTick);

		return result;
	}

	private void loadImage(Tick tick) {
		Bitmap cachedBitmap = mImageManager.loadFromCache(tick.image);
		if (cachedBitmap != null) {
			mImage.setImageBitmap(cachedBitmap);
		} else {
			mImageManager.loadImage(tick.image, new ImageListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					if (isVisible()) {
						mImage.setImageResource(mDummyImageResource);
					}
				}

				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					mImage.setImageBitmap(response.getBitmap());
				}
			}, mImage.getWidth(), mImage.getHeight());
		}
	}
}

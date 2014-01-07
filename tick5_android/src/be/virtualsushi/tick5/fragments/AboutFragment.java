package be.virtualsushi.tick5.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.virtualsushi.tick5.R;
import be.virtualsushi.tick5.Tick5Application;
import be.virtualsushi.tick5.roboto.RobotoTypefaceProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaces;

public class AboutFragment extends Fragment implements RobotoTypefaceProvider {

	private TextView mAbout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.activity_about, container, false);

		mAbout = (TextView) result.findViewById(R.id.about);

		BufferedReader reader = null;
		InputStreamReader inputStreamReader = null;
		try {
			StringBuilder htmlBuilder = new StringBuilder();
			inputStreamReader = new InputStreamReader(getActivity().getAssets().open("about.html"), "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = reader.readLine()) != null) {
				htmlBuilder.append(line);
			}
			mAbout.setMovementMethod(LinkMovementMethod.getInstance());
			mAbout.setTypeface(getRobotoTypeface(RobotoTypefaces.REGULAR));
			mAbout.setText(Html.fromHtml(htmlBuilder.toString()));
		} catch (IOException e) {
			Log.e(getClass().getName(), "Unable to read about.html", e);
		} finally {
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

	private Tick5Application getTick5Application() {
		return (Tick5Application) getActivity().getApplication();
	}

	@Override
	public Typeface getRobotoTypeface(RobotoTypefaces name) {
		return getTick5Application().getRobotoTypeface(name);
	}

}

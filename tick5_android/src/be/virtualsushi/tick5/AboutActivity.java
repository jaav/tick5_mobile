package be.virtualsushi.tick5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import be.virtualsushi.tick5.roboto.RobotoTypefaceProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaces;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutActivity extends SherlockActivity implements RobotoTypefaceProvider {

	private TextView mAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);

		mAbout = (TextView) findViewById(R.id.about);

		BufferedReader reader = null;
		InputStreamReader inputStreamReader = null;
		try {
			StringBuilder htmlBuilder = new StringBuilder();
			inputStreamReader = new InputStreamReader(getAssets().open("about.html"), "UTF-8");
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

	}

	private Tick5Application getTick5Application() {
		return (Tick5Application) getApplication();
	}

	@Override
	public Typeface getRobotoTypeface(RobotoTypefaces name) {
		return getTick5Application().getRobotoTypeface(name);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.R.id.home == item.getItemId()) {
			finish();
		}
		return true;
	}

}

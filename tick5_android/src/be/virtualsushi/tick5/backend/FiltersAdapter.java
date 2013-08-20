package be.virtualsushi.tick5.backend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.virtualsushi.tick5.R;
import be.virtualsushi.tick5.roboto.RobotoTypefaceProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaces;

public class FiltersAdapter extends ArrayAdapter<String> {

	private class ItemHolder {

		private TextView mText;

		public ItemHolder(View view) {
			mText = (TextView) view.findViewById(R.id.text);
			mText.setTypeface(mRobotoTypefaceProvider.getRobotoTypeface(RobotoTypefaces.MEDIUM));
		}

		public void update(String text) {
			mText.setText(text);
		}

	}

	private RobotoTypefaceProvider mRobotoTypefaceProvider;
	private LayoutInflater mLayoutInflater;

	public FiltersAdapter(Context context) {
		super(context, R.layout.drop_down_item, context.getResources().getStringArray(R.array.filters));
		mRobotoTypefaceProvider = ((RobotoTypefaceProvider) context.getApplicationContext());
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.spinner_item, parent, false);
			convertView.setTag(new ItemHolder(convertView));
		}
		((ItemHolder) convertView.getTag()).update(getItem(position));

		return convertView;
	}

}

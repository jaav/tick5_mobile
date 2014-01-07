package be.virtualsushi.tick5.backend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.virtualsushi.tick5.R;
import be.virtualsushi.tick5.model.DrawerListItem;
import be.virtualsushi.tick5.roboto.RobotoTypefaceProvider;
import be.virtualsushi.tick5.roboto.RobotoTypefaces;

public class DrawerAdapter extends ArrayAdapter<DrawerListItem> {

	private class ItemTag {

		private ImageView mIcon;
		private TextView mTitle;

		public ItemTag(View view) {
			mIcon = (ImageView) view.findViewById(R.id.icon);
			mTitle = (TextView) view.findViewById(R.id.title);
			mTitle.setTypeface(mRobotoTypefaceProvider.getRobotoTypeface(RobotoTypefaces.LIGHT));
		}

		public void update(DrawerListItem item) {
			mIcon.setImageResource(item.icon);
			mTitle.setText(item.title);
		}

	}

	private LayoutInflater mLayoutInflater;
	private RobotoTypefaceProvider mRobotoTypefaceProvider;

	public DrawerAdapter(Context context, DrawerListItem[] objects, RobotoTypefaceProvider robotoTypefaceProvider) {
		super(context, -1, objects);
		mLayoutInflater = LayoutInflater.from(context);
		mRobotoTypefaceProvider = robotoTypefaceProvider;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_drawer_list, parent, false);
			convertView.setTag(new ItemTag(convertView));
		}
		((ItemTag) convertView.getTag()).update(getItem(position));
		return convertView;
	}
}

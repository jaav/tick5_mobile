package be.virtualsushi.tick5.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Tick implements Parcelable {

	public static final Parcelable.Creator<Tick> CREATOR = new Parcelable.Creator<Tick>() {
		public Tick createFromParcel(Parcel in) {
			return new Tick(in);
		}

		public Tick[] newArray(int size) {
			return new Tick[size];
		}
	};

	public static Tick fromJson(String json) {
		return GSON.fromJson(json, Tick.class);
	}

	public String toJson() {
		return GSON.toJson(this);
	}

	public static Tick[] fromJsonStringsArray(String[] json) {
		Tick[] result = new Tick[json.length];
		for (int i = 0; i < json.length; i++) {
			result[i] = Tick.fromJson(json[i]);
		}
		return result;
	}

	public static String[] toJsonStringsArray(Tick[] ticks) {
		String[] result = new String[ticks.length];
		for (int i = 0; i < ticks.length; i++) {
			result[i] = ticks[i].toJson();
		}
		return result;
	}

	private static final Gson GSON = new Gson();

	public String id;
	public int sequence;
	public String tweet;
	public String author;
	public String[] hashtags;
	public String[] urls;
	public String image;
	public String imageExtension;
	public String style;

	public Tick(Parcel in) {
		id = in.readString();
		sequence = in.readInt();
		tweet = in.readString();
		author = in.readString();
		int length = in.readInt();
		if (length >= 0) {
			hashtags = new String[length];
			in.readStringArray(hashtags);
		}
		length = in.readInt();
		if (length >= 0) {
			urls = new String[length];
			in.readStringArray(urls);
		}
		image = in.readString();
		imageExtension = in.readString();
		style = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeInt(sequence);
		dest.writeString(tweet);
		dest.writeString(author);
		if (hashtags != null) {
			dest.writeInt(hashtags.length);
			dest.writeStringArray(hashtags);
		} else {
			dest.writeInt(-1);
		}
		if (urls != null) {
			dest.writeInt(urls.length);
			dest.writeStringArray(urls);
		} else {
			dest.writeInt(-1);
		}
		dest.writeString(image);
		dest.writeString(imageExtension);
		dest.writeString(style);
	}

}
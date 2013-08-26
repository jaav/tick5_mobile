package be.virtualsushi.tick5.model;

import java.io.Serializable;

import com.google.gson.Gson;

public class Tick implements Serializable {

	private static final long serialVersionUID = 5754063964137771915L;

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

	public static Tick fromJson(String json) {
		return GSON.fromJson(json, Tick.class);
	}

	public String toJson() {
		return GSON.toJson(this);
	}

}
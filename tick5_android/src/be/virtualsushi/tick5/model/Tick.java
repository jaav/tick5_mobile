package be.virtualsushi.tick5.model;

import java.io.Serializable;

public class Tick implements Serializable {

	private static final long serialVersionUID = 5754063964137771915L;

	public String id;
	public int sequence;
	public String tweet;
	public String author;
	public String[] hashtags;
	public String[] urls;
	public String image;
	public String imageExtension;
	public String style;

}
package be.virtualsushi.tick5.model;

public class Tick5Response {

	public enum ResponseStatuses {

		OK,
		ERROR;

	}

	public ResponseStatuses status;
	public Tick[] tweets;

}

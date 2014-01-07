package be.virtualsushi.tick5.events;

import be.virtualsushi.tick5.model.Tick;

public class UpdateTicksEvent {

	public final Tick[] ticks;
	public final boolean rememberPosition;

	public UpdateTicksEvent(Tick[] ticks, boolean rememberPosition) {
		this.ticks = ticks;
		this.rememberPosition = rememberPosition;
	}

}

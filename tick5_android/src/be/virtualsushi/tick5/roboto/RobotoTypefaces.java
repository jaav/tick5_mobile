package be.virtualsushi.tick5.roboto;

public enum RobotoTypefaces {

	REGULAR("Regular"),
	BOLD("Bold"),
	LIGHT("Light"),
	THIN("Thin"),
	MEDIUM("Medium");

	private final String mTypefaceNamePostfix;

	private RobotoTypefaces(String typefaceNamePostfix) {
		mTypefaceNamePostfix = typefaceNamePostfix;
	}

	public String getmTypefaceNamePostfix() {
		return mTypefaceNamePostfix;
	}

}

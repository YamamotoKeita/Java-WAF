package test.mock;

public class SampleClass {
	private static String staticText;

	public static void setText(String text) {
		SampleClass.staticText = text;
	}

	@SuppressWarnings("unused")
	private static void setTextPrivately(String text) {
		SampleClass.staticText = text;
	}

	public static String getText() {
		return SampleClass.staticText;
	}

	public static void init() {
		staticText = null;
	}
}

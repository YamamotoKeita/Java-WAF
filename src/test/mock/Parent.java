package test.mock;

public class Parent {
	String text;
	int i;

	public void setText(String text) {
		this.text = text;
	}

	@SuppressWarnings("unused")
	private void setTextPrivately(String text) {
		this.text = text;
	}

	public void setInt(int i) {
		this.i = i;
	}

	public String getText() {
		return text;
	}

	public int getInt() {
		return i;
	}

}

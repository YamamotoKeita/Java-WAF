package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.reflection.Invoker;

import org.junit.BeforeClass;
import org.junit.Test;

import test.mock.Child;
import test.mock.Parent;
import test.mock.SampleClass;

public class InvokerTest {

	@BeforeClass
	public static void init() {
	}

	@Test
	public void メソッドの実行() {
		List<String> list = new ArrayList<String>();
		new Invoker(list).method("add").args("abc");

		assertEquals("abc", list.get(0));
	}

	@Test
	public void privateメソッドの実行() {
		Parent obj = new Parent();
		new Invoker(obj).method("setTextPrivately").args("abc");

		assertEquals("abc", obj.getText());
	}

	@Test
	public void 継承メソッドの実行() {
		Child obj = new Child();
		new Invoker(obj).method("setInt").args(123);

		assertEquals(123, obj.getInt());
	}

	@Test
	public void staticメソッドの実行() {
		SampleClass.init();
		Invoker.method(SampleClass.class, "setText").args("abc");
		assertEquals("abc", SampleClass.getText());
	}

	@Test
	public void privateかつstaticメソッドの実行() {
		SampleClass.init();
		Invoker.method(SampleClass.class, "setTextPrivately").args("abc");
		assertEquals("abc", SampleClass.getText());
	}

	@Test
	public void 戻り値の確認() {
		Parent obj = new Parent();
		obj.setText("abc");

		Object result = new Invoker(obj).method("getText").args();

		assertEquals("abc", result);
	}

}

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
	public void ���\�b�h�̎��s() {
		List<String> list = new ArrayList<String>();
		new Invoker(list).method("add").args("abc");

		assertEquals("abc", list.get(0));
	}

	@Test
	public void private���\�b�h�̎��s() {
		Parent obj = new Parent();
		new Invoker(obj).method("setTextPrivately").args("abc");

		assertEquals("abc", obj.getText());
	}

	@Test
	public void �p�����\�b�h�̎��s() {
		Child obj = new Child();
		new Invoker(obj).method("setInt").args(123);

		assertEquals(123, obj.getInt());
	}

	@Test
	public void static���\�b�h�̎��s() {
		SampleClass.init();
		Invoker.method(SampleClass.class, "setText").args("abc");
		assertEquals("abc", SampleClass.getText());
	}

	@Test
	public void private����static���\�b�h�̎��s() {
		SampleClass.init();
		Invoker.method(SampleClass.class, "setTextPrivately").args("abc");
		assertEquals("abc", SampleClass.getText());
	}

	@Test
	public void �߂�l�̊m�F() {
		Parent obj = new Parent();
		obj.setText("abc");

		Object result = new Invoker(obj).method("getText").args();

		assertEquals("abc", result);
	}

}

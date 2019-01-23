package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

import jp.co.altonotes.context.ResourceFinder;

public class ResourceFinderTest {
	public ResourceFinder finder = new ResourceFinder();


	@Test
	public void ���̃N���X���擾�ł���() throws Exception {
		List<Class<?>> list = finder.getClassList("test");
		boolean hit = false;
		for (Class<?> klass : list) {
			if (klass.equals(ResourceFinderTest.class)) {
				hit = true;
				break;
			}
		}
		assertEquals(true, hit);
	}

	@Test
	public void junit��jar����N���X���擾�ł���() throws Exception {
		List<Class<?>> list = finder.getClassList("org.junit");
		boolean hit = false;
		for (Class<?> klass : list) {
			if (klass.equals(Test.class)) {
				hit = true;
				break;
			}
		}
		assertEquals(true, hit);
	}

	@Test
	public void �T�u�p�b�P�[�W�̃N���X���擾�ł���() throws Exception {
		List<Class<?>> list = finder.getClassList("org.junit");
		boolean hit = false;
		for (Class<?> klass : list) {
			if (klass.equals(Failure.class)) {
				hit = true;
				break;
			}
		}
		assertEquals(true, hit);
	}

	@Test
	public void �T�u�p�b�P�[�W�̃N���X��S�Ď擾�ł���() throws Exception {
		List<Class<?>> list = finder.getClassList("org.junit.runner.notification");
		assertEquals(4, list.size());
		Class<?>[] expected = {Failure.class, RunListener.class, RunNotifier.class, StoppedByUserException.class};

		for (Class<?> klass : list) {
			boolean hit = false;
			for (Class<?> expectedClass : expected) {
				if (klass.getName().equals(expectedClass.getName())) {
					hit = true;
					continue;
				}
			}

			if (!hit) {
				fail();
			}
		}
	}
}

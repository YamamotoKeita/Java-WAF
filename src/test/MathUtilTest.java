package test;
import static org.junit.Assert.assertEquals;
import jp.co.altonotes.util.MathUtils;

import org.junit.Test;

public class MathUtilTest {

	@Test
	public void ”z—ñ‚©‚ç•s•Î•ªU‚ğ‹‚ß‚é() {
		float[] values = {1, 2};
		float v = MathUtils.unbiasedVariance(values);
		assertEquals(0.5f, v, 0.00001f);

		values = new float[]{-1, 1};
		v = MathUtils.unbiasedVariance(values);
		assertEquals(2f, v, 0.00001f);

		values = new float[]{-1, 1, 3};
		v = MathUtils.unbiasedVariance(values);
		assertEquals(4f, v, 0.00001f);

		values = new float[]{-1, 1, 2, 2};
		v = MathUtils.unbiasedVariance(values);
		assertEquals(2f, v, 0.00001f);

		values = new float[]{-1.5f, 2.5f};
//		System.out.println(MathUtils.unbiasedVariance(values));

		values = new float[]{0.5f, -1.5f};
//		System.out.println(MathUtils.unbiasedVariance(values));
	}

	@Test
	public void ‰ß‹‚Ì•ªU‚È‚Ç‚©‚ç•s•Î•ªU‚ğ‹‚ß‚é() {
//		float oldVariance = 0;
//		float oldAvg = 0;
//		float newAvg = 0;
//		float additional = 0;
//		int n = 0;
//		float v = MathUtils.unbiasedVariance(oldVariance, oldAvg, newAvg, additional, n);
//		System.out.println(v);
	}
}

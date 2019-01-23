package jp.co.altonotes.util;

/**
 * 3�����x�N�g���̃A�t�B���ϊ����s���B
 * 
 * @author Yamamoto Keita
 *
 */
public class AffineTransform3D {

	/**
	 * X���܂��ɉ�]������
	 * @param vector
	 * @param degrees
	 */
	public static void rotateOnXAxis(double[] vector, double degrees) {
		assertLength(vector);
		
		final double radians = Math.toRadians(degrees);
		final double cos = Math.cos(radians);
		final double sin = Math.sin(radians);
		
		double[][] rotationArray = {
				{1.0, 0.0,  0.0, 0.0},
				{0.0, cos, -sin, 0.0},
				{0.0, sin,  cos, 0.0},
				{0.0, 0.0,  0.0, 1.0}
		};
		
		multipule(rotationArray, vector);
	}
	
	/**
	 * Y���܂��ɉ�]������
	 * @param vector
	 * @param degrees
	 */
	public static void rotateOnYAxis(double[] vector, double degrees) {
		assertLength(vector);
		
		final double radians = Math.toRadians(degrees);
		final double cos = Math.cos(radians);
		final double sin = Math.sin(radians);
		
		double[][] rotationArray = {
				{ cos, 0.0, sin, 0.0},
				{ 0.0, 1.0, 0.0, 0.0},
				{-sin, 0.0, cos, 0.0},
				{ 0.0, 0.0, 0.0, 1.0}
		};
		
		multipule(rotationArray, vector);
	}
	
	/**
	 * Z���܂��ɉ�]������
	 * @param vector
	 * @param degrees
	 */
	public static void rotateOnZAxis(double[] vector, double degrees) {
		assertLength(vector);
		
		final double radians = Math.toRadians(degrees);
		final double cos = Math.cos(radians);
		final double sin = Math.sin(radians);
		
		double[][] rotationArray = {
				{cos, -sin, 0.0, 0.0},
				{sin,  cos, 0.0, 0.0},
				{0.0,  0.0, 1.0, 0.0},
				{0.0,  0.0, 0.0, 1.0}
		};
		
		multipule(rotationArray, vector);
	}
	
	/**
	 * @param vector
	 */
	private static void assertLength(double[] vector) {
		if (vector.length != 3) {
			throw new IllegalArgumentException("�����ɃT�C�Y " + vector.length + " �̔z�񂪎w�肳��܂����B�����ɂ̓T�C�Y3�̔z��̂ݎw��ł��܂��B");
		}
	}

	/**
	 * @param rotationArray
	 * @param vector
	 */
	private static void multipule(double[][] rotationArray, double[] vector) {
		double[] srcArray = toSrcArray(vector);
		
		for (int i = 0; i < rotationArray.length; i++) {
			double[] row = rotationArray[i];
			double val = 0.0;
			for (int j = 0; j < row.length; j++) {
				val += row[j] * srcArray[j];
			}
			if (i < 3) {
				vector[i] = val;
			}
		}
	}

	private static double[] toSrcArray(double[] vector) {
		return new double[]{vector[0], vector[1], vector[2], 1.0};
	}
}

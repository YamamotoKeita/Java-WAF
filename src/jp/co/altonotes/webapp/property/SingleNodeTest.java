package jp.co.altonotes.webapp.property;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.webapp.form.ISelectOption;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * PropertyAccessor�̍����e�X�g
 *
 * @author Yamamoto Keita
 *
 */
public class SingleNodeTest {

	/* 
	 * ��Setter���ł��邱��
	 * 
	 * String�̃Z�b�g
	 * ��{�f�[�^�^�֕ϊ����ăZ�b�g
	 * SelectOption�֕ϊ����ăZ�b�g
	 * String�z��̃Z�b�g
	 * String�z���Collection�փZ�b�g
	 * String�z���Array�փZ�b�g
	 * String�z���boolean�ɕϊ����ăZ�b�g�i�`�F�b�N�{�b�N�X�p�j
	 * 
	 */
	
	// Getter�d�l
	
	@BeforeClass
	public static void init() {
	}

	/*--------------------- extractObject -------------------------*/

	@Test
	public void extractObject$�ʏ�() throws Exception {
		Result result = new Result();
		
		// getter, private
		SingleNode node = new SingleNode(new BaseBean(), "privateStringWithAccessor");
		Object obj = node.extractObject(result);
		assertEquals("default1", obj);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();
		
		// nogetter, public
		node = new SingleNode(new BaseBean(), "publicString");
		obj = node.extractObject(result);
		assertEquals("default2", obj);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// getter, public
		node = new SingleNode(new BaseBean(), "publicStringWithAccessor");
		obj = node.extractObject(result);
		assertEquals("getter�ɂ��擾", obj);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();
		
		// nogetter, private
		node = new SingleNode(new BaseBean(), "privateString");
		obj = node.extractObject(result);
		assertEquals(Undefined.VALUE, obj);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"privateString\" ��������܂���B", result.message);
		result.clear();
	}
	
	@Test
	public void extractObject$is�`���̃Q�b�^�[() {
		Result result = new Result();
		
		// getter, private
		SingleNode node = new SingleNode(new BaseBean(), "privateBoolWithAccessor");
		Object obj = node.extractObject(result);
		assertEquals(true, obj);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();
	}
	
	@Test
	public void extractObject$null�l�̎擾() throws Exception {
		Result result = new Result();
		
		// getter, private
		SingleNode node = new SingleNode(new BaseBean(), "privateNullWithAccesor");
		Object obj = node.extractObject(result);
		assertEquals(null, obj);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		//noGetter, public
		node = new SingleNode(new BaseBean(), "publicNull");
		obj = node.extractObject(result);
		assertEquals(null, obj);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();
	}
	
	@Test(expected=NullPointerException.class)
	public void extractObject$property����null���w��() throws Exception {
		Result result = new Result();
		SingleNode node = new SingleNode(new BaseBean(), null);
		node.extractObject(result);
	}

	@Test
	public void extractObject$�����property��() {
		Result result = new Result();

		// �󕶎�
		SingleNode node = new SingleNode(new BaseBean(), "");
		Object obj = node.extractObject(result);
		assertEquals(Undefined.VALUE, obj);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"\" ��������܂���B", result.message);
		result.clear();

		node = new SingleNode(new BaseBean(), ".");
		obj = node.extractObject(result);
		assertEquals(Undefined.VALUE, obj);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \".\" ��������܂���B", result.message);
		result.clear();

		node = new SingleNode(new BaseBean(), "...");
		obj = node.extractObject(result);
		assertEquals(Undefined.VALUE, obj);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"...\" ��������܂���B", result.message);
		result.clear();
	}

	/*--------------------- setNestedProperty -------------------------*/

	@Test
	public void setValue$�ʏ�() throws Exception {
		Result result = new Result();
		BaseBean obj = new BaseBean();

		// setter, private
		SingleNode node = new SingleNode(obj, "privateStringWithAccessor");
		node.setValue("set", result);
		assertEquals("set", obj.privateStringWithAccessor);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// nosetter, public
		node = new SingleNode(obj, "publicString");
		node.setValue("set2", result);
		assertEquals("set2", obj.publicString);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// setter, public
		node = new SingleNode(obj, "publicStringWithAccessor");
		node.setValue("set3", result);
		assertEquals("setter�ɂ��ݒ�", obj.publicStringWithAccessor);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// nogetter, private
		node = new SingleNode(obj, "privateString");
		node.setValue("set4", result);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"privateString\" ��������܂���B", result.message);
		result.clear();
	}

	@Test(expected=NullPointerException.class)
	public void setValue$property����null���w��() throws Exception {
		Result result = new Result();
		BaseBean obj = new BaseBean();
		SingleNode node = new SingleNode(obj, null);
		node.setValue("set", result);
	}

	@Test
	public void setValue$null�Z�b�g() throws Exception {
		Result result = new Result();
		BaseBean obj = new BaseBean();

		// setter, private
		SingleNode node = new SingleNode(obj, "privateStringWithAccessor");
		node.setValue(null, result);
		assertEquals(null, obj.privateStringWithAccessor);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// nosetter, public
		node = new SingleNode(obj, "publicString");
		node.setValue(null, result);
		assertEquals(null, obj.publicString);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// setter, public
		node = new SingleNode(obj, "publicStringWithAccessor");
		node.setValue(null, result);
		assertEquals("setter�ɂ��ݒ�", obj.publicStringWithAccessor);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// nogetter, private
		node = new SingleNode(obj, "privateString");
		node.setValue(null, result);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"privateString\" ��������܂���B", result.message);
		result.clear();
	}
	
	@Test
	public void setValue$����Ȃ��^�̃t�B�[���h�ɖ������}��() {
		Result result = new Result();
		BaseBean obj = new BaseBean();

		// setter, private
		SingleNode node = new SingleNode(obj, "publicInvalidType");
		node.setValue("set", result);
		assertEquals(null, obj.publicInvalidType);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"publicInvalidType\" �ɒl���Z�b�g�ł��܂���B" +
				ByteArrayOutputStream.class.getName() + " �͑Ή����Ă��Ȃ��^�ł��B", result.message);
		result.clear();
	}

	@Test
	public void setValue$����Ȃ��^��setter�ɖ������}��() {
		Result result = new Result();
		BaseBean obj = new BaseBean();

		// setter, private
		SingleNode node = new SingleNode(obj, "privateInvalidTypeWithAccessor");
		node.setValue("set", result);
		assertEquals(null, obj.publicInvalidType);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"privateInvalidTypeWithAccessor\" �ɒl���Z�b�g�ł��܂���B setter �̈������Ή����Ă��Ȃ��^�ł��B", result.message);
		result.clear();
	}
	
	@Test
	public void setValue$enum�ɃZ�b�g() {
		Result result = new Result();
		BaseBean obj = new BaseBean();

		// setter, private
		SingleNode node = new SingleNode(obj, "privateEnumWithAccesor");
		node.setValue("2", result);
		assertEquals(SampleEnum.ENUM2, obj.privateEnumWithAccesor);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// nosetter, public
		node = new SingleNode(obj, "publicEnum");
		node.setValue("1", result);
		assertEquals(SampleEnum.ENUM1, obj.publicEnum);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// setter, public
		node = new SingleNode(obj, "publicEnumWithAccessor");
		node.setValue("1", result);
		assertEquals(SampleEnum.ENUM3, obj.publicEnumWithAccessor);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();

		// nogetter, private
		node = new SingleNode(obj, "privateEnum");
		node.setValue("3", result);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"privateEnum\" ��������܂���B", result.message);
		result.clear();
	}

	@Test
	public void setValue$enum�Ή��l�Ȃ�() {
		Result result = new Result();
		BaseBean obj = new BaseBean();

		// setter, private
		obj.privateEnumWithAccesor = SampleEnum.ENUM1;
		SingleNode node = new SingleNode(obj, "privateEnumWithAccesor");
		node.setValue("4", result);
		assertEquals(null, obj.privateEnumWithAccesor);
		assertEquals(true, result.isFailed);
		assertEquals(SampleEnum.class.getName() + " �� value=\"4\" �̗v�f�����݂��Ȃ����߁A�l���Z�b�g�ł��܂���B", result.message);
		result.clear();
		
		// nosetter, public
		obj.privateEnumWithAccesor = SampleEnum.ENUM1;
		node = new SingleNode(obj, "publicEnum");
		node.setValue("4", result);
		assertEquals(null, obj.publicEnum);
		assertEquals(false, result.isFailed);
		assertEquals(null, result.message);
		result.clear();
	}
	
	@Test
	public void setValue$�����property��() {
		Result result = new Result();
		BaseBean obj = new BaseBean();

		SingleNode node = new SingleNode(obj, "");
		node.setValue("4", result);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \"\" ��������܂���B", result.message);
		result.clear();
		
		node = new SingleNode(obj, ".");
		node.setValue("4", result);
		assertEquals(true, result.isFailed);
		assertEquals(BaseBean.class.getName() + " �̃v���p�e�B \".\" ��������܂���B", result.message);
		result.clear();
	}


	@Test
	public void setValue$String�z���boolean�v���p�e�B�ɃZ�b�g() {
		// �P�̃`�F�b�N�{�b�N�X�� boolean �ɃZ�b�g����P�[�X
		
		// setter
		Result result = new Result();
		BaseBean obj = new BaseBean();

		SingleNode node = new SingleNode(obj, "privateBoolWithAccessor");
		node.setValueArray(new String[]{"true", ""}, result);
		assertEquals(false, result.isFailed);
		assertEquals(obj.privateBoolWithAccessor, true);
		result.clear();
		
		node = new SingleNode(obj, "privateBoolWithAccessor");
		node.setValueArray(new String[]{"", "true"}, result);
		assertEquals(false, result.isFailed);
		assertEquals(obj.privateBoolWithAccessor, true);
		result.clear();

		node = new SingleNode(obj, "privateBoolWithAccessor");
		node.setValueArray(new String[]{"false", ""}, result);
		assertEquals(false, result.isFailed);
		assertEquals(obj.privateBoolWithAccessor, false);
		result.clear();
		
		node = new SingleNode(obj, "privateBoolWithAccessor");
		node.setValueArray(new String[]{"", "false"}, result);
		assertEquals(false, result.isFailed);
		assertEquals(obj.privateBoolWithAccessor, false);
		result.clear();
	}
	
	@Test
	public void setValue$String�z����R���N�V�����ɃZ�b�g() {
		// �����`�F�b�N�{�b�N�X�� �R���N�V���� �ɃZ�b�g����P�[�X
	}

	@Test
	public void setValue$String���R���N�V�����ɃZ�b�g() {
		// ��̕����`�F�b�N�{�b�N�X�� �R���N�V���� �ɃZ�b�g����P�[�X
	}

	@Test
	public void setValue$Primitive�^�̃Z�b�^�[����������() {
	}

//	@Test
//	public void setNestedProperty$��{�f�[�^�^�ϊ�_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleInt", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.exampleInt);
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleBoolean", "true");
//		assertEquals(true, obj.exampleBoolean);
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleBoolean", "false");
//		assertEquals(false, obj.exampleBoolean);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleLong", "1234567890123456789");
//		assertEquals(1234567890123456789L, obj.exampleLong);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleDouble", "1234.5678");
//		assertEquals(1234.5678, obj.exampleDouble, 0.00000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar", "��");
//		assertEquals('��', obj.exampleChar);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleByte", "120");
//		assertEquals(120, obj.exampleByte);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleFloat", "999.999");
//		assertEquals(999.999F, obj.exampleFloat, 0.0000000000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleShort", "1200");
//		assertEquals(1200, obj.exampleShort);
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^�ϊ�_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleInt2", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.exampleInt2);
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleBoolean2", "true");
//		assertEquals(true, obj.exampleBoolean2);
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleBoolean2", "false");
//		assertEquals(false, obj.exampleBoolean2);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleLong2", "1234567890123456789");
//		assertEquals(1234567890123456789L, obj.exampleLong2);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleDouble2", "1234.5678");
//		assertEquals(1234.5678, obj.exampleDouble2, 0.0000000000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar2", "��");
//		assertEquals('��', obj.exampleChar2);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleByte2", "120");
//		assertEquals(120, obj.exampleByte2);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleFloat2", "999.999");
//		assertEquals(999.999F, obj.exampleFloat2, 0.0000000000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleShort2", "1200");
//		assertEquals(1200, obj.exampleShort2);
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^���b�p�[�ϊ�_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "integerWrapper1", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertTrue(1234 == obj.integerWrapper1.intValue());
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "booleanWrapper1", "true");
//		assertTrue(obj.booleanWrapper1);
//		result = PropertyAccessor.setNestedProperty("root", obj, "booleanWrapper1", "false");
//		assertFalse(obj.booleanWrapper1);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "longWrapper1", "1234567890123456789");
//		assertTrue(1234567890123456789L == obj.longWrapper1.longValue());
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "doubleWrapper1", "1234.5678");
//		assertEquals(1234.5678, obj.doubleWrapper1.doubleValue(), 0.0000000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper1", "��");
//		assertEquals('��', obj.charWrapper1.charValue());
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "byteWrapper1", "120");
//		assertEquals(120, obj.byteWrapper1.byteValue());
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "floatWrapper1", "999.999");
//		assertEquals(999.999F, obj.floatWrapper1.floatValue(), 0.00000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "shortWrapper1", "1200");
//		assertEquals(1200, obj.shortWrapper1.shortValue());
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^���b�p�[�ϊ�_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "integerWrapper2", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.integerWrapper2.intValue());
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "booleanWrapper2", "true");
//		assertEquals(true, obj.booleanWrapper2);
//		result = PropertyAccessor.setNestedProperty("root", obj, "booleanWrapper2", "false");
//		assertEquals(false, obj.booleanWrapper2);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "longWrapper2", "1234567890123456789");
//		assertEquals(1234567890123456789L, obj.longWrapper2.longValue());
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "doubleWrapper2", "1234.5678");
//		assertEquals(1234.5678, obj.doubleWrapper2.doubleValue(), 0.00000000000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper2", "��");
//		assertEquals('��', obj.charWrapper2.charValue());
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "byteWrapper2", "120");
//		assertEquals(120, obj.byteWrapper2.byteValue());
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "floatWrapper2", "999.999");
//		assertEquals(999.999F, obj.floatWrapper2.floatValue(), 0.000000001);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "shortWrapper2", "1200");
//		assertEquals(1200, obj.shortWrapper2.shortValue());
//	}


//	@Test
//	public void setNestedProperty$��{�f�[�^�^�ϊ����s_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleInt", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals(0, obj.exampleInt);
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"123A\" �� int�^ �ɕϊ��ł��܂���B", result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleBoolean", "y");
//		assertEquals(false, obj.exampleBoolean);
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"y\" �� boolean�^ �ɕϊ��ł��܂���B�啶���������� \"true\" \"false\" �܂��͋󕶎��A���p�X�y�[�X�̂ݕϊ��ł��܂��B", result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleLong", "������");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"������\" �� long�^ �ɕϊ��ł��܂���B", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleDouble", "1234.5678A");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"1234.5678A\" �� double�^ �ɕϊ��ł��܂���B", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar", "�΂�");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"�΂�\" �� char�^ �ɕϊ��ł��܂���B���� 1 �̕�����̂ݕϊ��ł��܂��B", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleByte", "120A");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"120A\" �� byte�^ �ɕϊ��ł��܂���B", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleFloat", "999.999A");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"999.999A\" �� float�^ �ɕϊ��ł��܂���B", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleShort", "1200A");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"1200A\" �� short�^ �ɕϊ��ł��܂���B", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^�ϊ����s_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar2", "�΂�");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"�΂�\" �� char�^ �ɕϊ��ł��܂���B���� 1 �̕�����̂ݕϊ��ł��܂��B", result.message);
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^�ϊ����s_�C���f�b�N�X�L��_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "intArray[0]", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"123A\" �� int�^ �ɕϊ��ł��܂���B", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^�ϊ����s_�C���f�b�N�X�L��_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "intArray2[0]", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"123A\" �� int�^ �ɕϊ��ł��܂���B", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^���b�p�[�ϊ����s_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper1", "�΂�");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"�΂�\" �� java.lang.Character�^ �ɕϊ��ł��܂���B���� 1 �̕�����̂ݕϊ��ł��܂��B", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$��{�f�[�^�^���b�p�[�ϊ����s_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper2", "�΂�");
//		assertEquals("�^�ϊ��Ɏ��s���܂����B\"�΂�\" �� java.lang.Character�^ �ɕϊ��ł��܂���B���� 1 �̕�����̂ݕϊ��ł��܂��B", result.message);
//	}

//	@Test(expected=AmbiguousSetterException.class)
//	public void setNestedProperty$�����̊�{�f�[�^�^setter������() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		PropertyAccessor.setNestedProperty("root", obj, "exampleInt3", "987");
//	}
//
//	@Test
//	public void getNestedFlag$public_field() {
//		BaseBean obj = new BaseBean();
//		AccessResult result = null;
//		result = PropertyAccessor.getNestedFlag("root", obj, "flag3");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}
//
//	@Test
//	public void getNestedFlag$private_isMethod() {
//		BaseBean obj = new BaseBean();
//		AccessResult result = null;
//		result = PropertyAccessor.getNestedFlag("root", obj, "flag1");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}
//
//	@Test
//	public void getNestedFlag$private_getter() {
//		BaseBean obj = new BaseBean();
//		AccessResult result = null;
//		result = PropertyAccessor.getNestedFlag("root", obj, "flag2");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}

	//	@Test
//	public void doNestedCheckMethod$() {
//		BaseBean obj = new BaseBean();
//		AccessResult result = null;
//		result = PropertyAccessor.doNestedCheckMethod("root", obj, "hasFlag4");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}

	/**
	 * 
	 * @author Yamamoto Keita
	 *
	 */
	private static enum SampleEnum implements ISelectOption {
		ENUM1("1", "enum1"),
		ENUM2("2", "enum2"),
		ENUM3("3", "enum3");

		private String value;
		private String label;
		
		private SampleEnum(String value, String label) {
			this.value = value;
			this.label = label;
		}
		
		public String getValue() {
			return value;
		}
	
		public String getLabel() {
			return label;
		}
	}

	/**
	 * �e�X�g�p�N���X
	 * @author Yamamoto Keita
	 */
	@SuppressWarnings("unused")
	private static class BaseBean {
		// For getter & setter -----------------------------------
		private String privateStringWithAccessor = "default1";
		private String privateNullWithAccesor;
		private SampleEnum privateEnumWithAccesor;
		private boolean privateBoolWithAccessor = true;
		
		public String getPrivateStringWithAccessor() {
			return privateStringWithAccessor;
		}
		public void setPrivateStringWithAccessor(String privateStringWithAccessor) {
			this.privateStringWithAccessor = privateStringWithAccessor;
		}
		public String getPrivateNullWithAccesor() {
			return privateNullWithAccesor;
		}
		public void setPrivateNullWithAccesor(String privateNullWithAccesor) {
			this.privateNullWithAccesor = privateNullWithAccesor;
		}
		public SampleEnum getPrivateEnumWithAccesor() {
			return privateEnumWithAccesor;
		}
		public void setPrivateEnumWithAccesor(SampleEnum privateEnumWithAccesor) {
			this.privateEnumWithAccesor = privateEnumWithAccesor;
		}
		public boolean isPrivateBoolWithAccessor() {
			return privateBoolWithAccessor;
		}
		public void setPrivateBoolWithAccessor(boolean privateBoolWithAccessor) {
			this.privateBoolWithAccessor = privateBoolWithAccessor;
		}
		
		// For field access -----------------------------------------------
		public String publicString = "default2";
		public SampleEnum publicEnum;
		public String publicNull;

		// For accessor & public --------------------------------------------
		public String publicStringWithAccessor;
		private SampleEnum publicEnumWithAccessor;
		
		public void setPublicStringWithAccessor(String publicStringWithAccessor) {
			this.publicStringWithAccessor = "setter�ɂ��ݒ�";
		}
		public void setPublicEnumWithAccessor(SampleEnum publicEnumWithAccessor) {
			this.publicEnumWithAccessor = SampleEnum.ENUM3;
		}
		public String getPublicStringWithAccessor() {
			return "getter�ɂ��擾";
		}
		public SampleEnum getPublicEnumWithAccessor() {
			return publicEnumWithAccessor;
		}

		// no getter, private
		private String privateString;
		private SampleEnum privateEnum;
		
		// invalid format
		public ByteArrayOutputStream publicInvalidType;
		private ByteArrayOutputStream privateInvalidTypeWithAccessor;

		public ByteArrayOutputStream getPrivateInvalidTypeWithAccessor() {
			return privateInvalidTypeWithAccessor;
		}
		public void setPrivateInvalidTypeWithAccessor(
				ByteArrayOutputStream privateInvalidTypeWithAccessor) {
			this.privateInvalidTypeWithAccessor = privateInvalidTypeWithAccessor;
		}
	}

	/**
	 * �e�X�g�p�N���X
	 * @author Yamamoto Keita
	 *
	 */
	@SuppressWarnings("unused")
	private static class PrimitiveBean {

		private int exampleInt;
		private boolean exampleBoolean;
		private long exampleLong;
		private double exampleDouble;
		private char exampleChar;
		private byte exampleByte;
		private float exampleFloat;
		private short exampleShort;

		private int exampleInt3;

		public int exampleInt2;
		public boolean exampleBoolean2;
		public long exampleLong2;
		public double exampleDouble2;
		public char exampleChar2;
		public byte exampleByte2;
		public float exampleFloat2;
		public short exampleShort2;

		private Integer integerWrapper1;
		private Boolean booleanWrapper1;
		private Long longWrapper1;
		private Double doubleWrapper1;
		private Character charWrapper1;
		private Byte byteWrapper1;
		private Float floatWrapper1;
		private Short shortWrapper1;

		public Integer integerWrapper2 = 1;
		public Boolean booleanWrapper2;
		public Long longWrapper2;
		public Double doubleWrapper2;
		public Character charWrapper2;
		public Byte byteWrapper2;
		public Float floatWrapper2;
		public Short shortWrapper2;

		private int[] intArray = {1, 2, 3};
		private Integer[] integerWrapperArray1 = {1, 2, 3};
		private Integer[] integerWrapperArray3 = {1, 2, 3};
		private List<Integer> intList2;

		public List<Integer> intList;
		public int[] intArray2 = {1, 2, 3};
		public Integer[] integerWrapperArray2 = {1, 2, 3};

		public PrimitiveBean() {
			intList = new ArrayList<Integer>();
			intList.add(1);
			intList.add(2);
			intList.add(3);

			intList2 = new ArrayList<Integer>();
			intList2.add(1);
			intList2.add(2);
			intList2.add(3);
		}

		// methods

		public void setExampleInt(int exampleInt) {
			this.exampleInt = exampleInt;
		}
		public void setExampleBoolean(boolean exampleBoolean) {
			this.exampleBoolean = exampleBoolean;
		}
		public void setExampleLong(long exampleLong) {
			this.exampleLong = exampleLong;
		}
		public void setExampleDouble(double exampleDouble) {
			this.exampleDouble = exampleDouble;
		}
		public void setExampleChar(char exampleChar) {
			this.exampleChar = exampleChar;
		}
		public void setExampleByte(byte exampleByte) {
			this.exampleByte = exampleByte;
		}
		public void setExampleFloat(float exampleFloat) {
			this.exampleFloat = exampleFloat;
		}
		public void setExampleShort(short exampleShort) {
			this.exampleShort = exampleShort;
		}

		//Index Setter
		public void setIntArray(int idx, int i) {
			intArray[idx] = i;
		}

		// Wrapper Setter
		public void setIntegerWrapper1(Integer integerWrapper1) {
			this.integerWrapper1 = integerWrapper1;
		}

		public void setBooleanWrapper1(Boolean booleanWrapper1) {
			this.booleanWrapper1 = booleanWrapper1;
		}

		public void setLongWrapper1(Long longWrapper1) {
			this.longWrapper1 = longWrapper1;
		}

		public void setDoubleWrapper1(Double doubleWrapper1) {
			this.doubleWrapper1 = doubleWrapper1;
		}

		public void setCharWrapper1(Character charWrapper1) {
			this.charWrapper1 = charWrapper1;
		}

		public void setByteWrapper1(Byte byteWrapper1) {
			this.byteWrapper1 = byteWrapper1;
		}

		public void setFloatWrapper1(Float floatWrapper1) {
			this.floatWrapper1 = floatWrapper1;
		}

		public void setShortWrapper1(Short shortWrapper1) {
			this.shortWrapper1 = shortWrapper1;
		}

		// Wrapper Getter
		public boolean isExampleBoolean() {
			return exampleBoolean;
		}

		public Integer getIntegerWrapper1() {
			return integerWrapper1;
		}

		public Boolean getBooleanWrapper1() {
			return booleanWrapper1;
		}

		public Long getLongWrapper1() {
			return longWrapper1;
		}

		public Double getDoubleWrapper1() {
			return doubleWrapper1;
		}

		public Character getCharWrapper1() {
			return charWrapper1;
		}

		public Byte getByteWrapper1() {
			return byteWrapper1;
		}

		public Float getFloatWrapper1() {
			return floatWrapper1;
		}

		public Short getShortWrapper1() {
			return shortWrapper1;
		}

		// Wrapper Array
		public void setIntegerWrapperArray1(int idx, Integer integerWrapperArray) {
			this.integerWrapperArray1[idx] = integerWrapperArray;
		}

		public Integer[] getIntegerWrapperArray3() {
			return integerWrapperArray3;
		}

		// Wrapper Collection
		public List<Integer> getIntList2() {
			return intList2;
		}

		public void setExampleInt3(int i) {
			this.exampleInt3 = i;
		}

		public int getExampleInt3() {
			return this.exampleInt3;
		}

		public void setExampleInt3(long l) {
			this.exampleInt3 = (int) l;
		}
	}
}

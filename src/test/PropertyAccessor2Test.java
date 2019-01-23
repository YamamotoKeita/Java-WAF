package test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * PropertyAccessorの高次テスト
 *
 * @author Yamamoto Keita
 *
 */
public class PropertyAccessor2Test {

	/* 
	 * ◆Setter仕様
	 * Stringのセット
	 * 基本データ型への変換セット
	 * SelectOptionへの変換セット
	 * String配列のセット
	 * 
	 * setXXX(obj, i)
	 * 
	 * Collectionへのセット
	 * Arrayへのセット
	 */
	
	// Getter仕様
	
	@BeforeClass
	public static void init() {
	}

	/*--------------------- getNestedProperty -------------------------*/

	/**
	 * 
	 */
	@Test
	public void getNestedProperty$通常() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		//getter, private
//		AccessResult result = PropertyAccessor.getNestedProperty(obj, "string1");
//		assertEquals("default1", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//nogetter, public
//		result = PropertyAccessor.getNestedProperty(obj, "string2");
//		assertEquals("default2", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//getter, public
//		result = PropertyAccessor.getNestedProperty(obj, "string3");
//		assertEquals("getterにより取得", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//nogetter, private
//		result = PropertyAccessor.getNestedProperty(obj, "string4");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"string4\" が見つかりません。", result.message);
	}

//	@Test
//	public void getNestedProperty$null値の取得() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		//getter, private
//		AccessResult result = PropertyAccessor.getNestedProperty(obj, "nullField1");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//noGetter, public
//		result = PropertyAccessor.getNestedProperty(obj, "nullField2");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//	}
//
//	@Test(expected=NullPointerException.class)
//	public void getNestedProperty$property名にnullを指定() throws Exception {
//		PropertyAccessor.getNestedProperty(new BaseBean(), null);
//	}
//
//	@Test
//	public void getNestedProperty$特種なproperty名() {
//		BaseBean obj = new BaseBean();
//
//		AccessResult result = PropertyAccessor.getNestedProperty(obj, "");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"\" が見つかりません。", result.message);
//
//		result = PropertyAccessor.getNestedProperty(obj, ".");
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"\" が見つかりません。", result.message);
//
//		result = PropertyAccessor.getNestedProperty(obj, "...");
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//	}
//
//	@Test
//	public void getNestedProperty$インデックス指定() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		//indexGetter, private
//		AccessResult result = PropertyAccessor.getNestedProperty(obj, "stringArray1[0]");
//		assertEquals("default1[0]", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray1[1]");
//		assertEquals("default1[1]", result.value);
//
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray1[2]");
//		assertEquals("default1[2]", result.value);
//
//		//nogetter, public array
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray2[0]");
//		assertEquals("default2[0]", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray2[1]");
//		assertEquals("default2[1]", result.value);
//
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray2[2]");
//		assertEquals("default2[2]", result.value);
//
//		//indexGetter, public
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray3[0]");
//		assertEquals("getterにより取得", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//arraygetter, private
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray4[0]");
//		assertEquals("default4[0]", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//collectiongetter, private
//		result = PropertyAccessor.getNestedProperty(obj, "stringList1[0]");
//		assertEquals("default1[0]", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//nogetter, public collection
//		result = PropertyAccessor.getNestedProperty(obj, "stringList2[0]");
//		assertEquals("default2[0]", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		result = PropertyAccessor.getNestedProperty(obj, "stringList2[1]");
//		assertEquals("default2[1]", result.value);
//		result = PropertyAccessor.getNestedProperty(obj, "stringList2[2]");
//		assertEquals("default2[2]", result.value);
//
//		//arrayGetter, public
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray5[0]");
//		assertEquals("getterにより取得1", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//indexGetter, arrayGetter, public
//		result = PropertyAccessor.getNestedProperty(obj, "stringArray6[0]");
//		assertEquals("インデックスgetterにより取得", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//notfound
//		result = PropertyAccessor.getNestedProperty(obj, "notexists[0]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"notexists\" が見つかりません。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$インデックスOutOfBounds() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		//nogetter, public array
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "stringArray2[3]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringArray2 にインデックス 3 の要素がありません。", result.message);
//
//		//arraygetter, private
//		result = PropertyAccessor.getNestedProperty("root", obj, "stringArray4[3]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringArray4 にインデックス 3 の要素がありません。", result.message);
//
//		//collectiongetter, private
//		result = PropertyAccessor.getNestedProperty("root", obj, "stringList1[3]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringList1 にインデックス 3 の要素がありません。", result.message);
//
//		//nogetter, public collection
//		result = PropertyAccessor.getNestedProperty("root", obj, "stringList2[3]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringList2 にインデックス 3 の要素がありません。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$インデックス非対応() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		//getter, private
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "string1[0]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_IS_NOT_AVAILABLE, result.code);
//		assertEquals("root.string1 はインデックス指定による要素のアクセスに対応していません。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$二階層通常() throws Exception {
//		ParentBean obj = new ParentBean();
//
//		//getter, private
//		AccessResult result = PropertyAccessor.getNestedProperty(obj, "base1.string1");
//		assertEquals("default1", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//nogetter, public
//		result = PropertyAccessor.getNestedProperty(obj, "base1.string2");
//		assertEquals("default2", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//getter, public
//		result = PropertyAccessor.getNestedProperty(obj, "base1.string3");
//		assertEquals("getterにより取得", result.value);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//nogetter, private
//		result = PropertyAccessor.getNestedProperty(obj, "base1.string4");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"string4\" が見つかりません。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$存在しない二階層property() throws Exception {
//		ParentBean obj = new ParentBean();
//		AccessResult result = PropertyAccessor.getNestedProperty(obj, "base1.string4");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"string4\" が見つかりません。", result.message);
//	}
//
//
//	@Test
//	public void getNestedProperty$途中がnull() throws Exception {
//		ParentBean obj = new ParentBean();
//
//		//getter, private
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "base2.string1");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("root.base2 が null です。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$途中がnotFound() throws Exception {
//		ParentBean obj = new ParentBean();
//
//		//getter, private
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "aaa.string1");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$ParentBean のプロパティ \"aaa\" が見つかりません。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$途中がインデックスアウト() throws Exception {
//		ParentBean obj = new ParentBean();
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "baseArray[3].string1");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.baseArray にインデックス 3 の要素がありません。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$途中がインデックス非対応() throws Exception {
//		ParentBean obj = new ParentBean();
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "string1[0].string1");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_IS_NOT_AVAILABLE, result.code);
//		assertEquals("root.string1 はインデックス指定による要素のアクセスに対応していません。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$インデックス指定_null_arrayGetter() throws Exception {
//		BaseBean obj = new BaseBean();
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "stringArray8[0]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("root.stringArray8 が null です。", result.message);
//	}
//
//	@Test
//	public void getNestedProperty$インデックス指定_null_public_array() throws Exception {
//		BaseBean obj = new BaseBean();
//		AccessResult result = PropertyAccessor.getNestedProperty("root", obj, "stringArray7[0]");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("root.stringArray7 が null です。", result.message);
//	}
//
//	/*--------------------- setNestedProperty -------------------------*/
//
//	@Test
//	public void setNestedProperty$通常() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		//setter, private
//		AccessResult result = PropertyAccessor.setNestedProperty(obj, "string1", "set");
//		assertEquals(null, result.value);
//		assertEquals("set", obj.string1);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//nosetter, public
//		result = PropertyAccessor.setNestedProperty(obj, "string2", "set");
//		assertEquals(null, result.value);
//		assertEquals("set", obj.string2);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//setter, public
//		result = PropertyAccessor.setNestedProperty(obj, "string3", "set");
//		assertEquals(null, result.value);
//		assertEquals("setterにより設定", obj.string3);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		//nogetter, private
//		result = PropertyAccessor.setNestedProperty(obj, "string4", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"string4\" が見つかりません。", result.message);
//	}
//
//	@Test(expected=NullPointerException.class)
//	public void setNestedProperty$property名にnullを指定() throws Exception {
//		PropertyAccessor.setNestedProperty(new BaseBean(), null, "");
//	}
//
//	@Test
//	public void setNestedProperty$入らない型のフィールドに無理やり挿入() {
//		BaseBean obj = new BaseBean();
//		AccessResult result = PropertyAccessor.setNestedProperty(obj, "byteArrayOutputStream", "set");
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals(null, result.value);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"byteArrayOutputStream\" が見つかりません。", result.message);
//		assertEquals(null, obj.byteArrayOutputStream);
//	}
//
//	@Test
//	public void setNestedProperty$入らない型のsetterに無理やり挿入() {
//		BaseBean obj = new BaseBean();
//		AccessResult result = PropertyAccessor.setNestedProperty(obj, "byteArrayOutputStream2", "set");
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals(null, result.value);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"byteArrayOutputStream2\" が見つかりません。", result.message);
//		assertEquals(null, obj.byteArrayOutputStream);
//	}
//
//	@Test
//	public void setNestedProperty$特種なproperty名() {
//		BaseBean obj = new BaseBean();
//
//		AccessResult result = PropertyAccessor.setNestedProperty(obj, "", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"\" が見つかりません。", result.message);
//
//		result = PropertyAccessor.setNestedProperty(obj, ".", "set");
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"\" が見つかりません。", result.message);
//
//		result = PropertyAccessor.setNestedProperty(obj, "...", "set");
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//	}
//
//	@Test
//	public void setNestedProperty$インデックス指定_各種() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		//indexSetter, private
//		AccessResult result = PropertyAccessor.setNestedProperty(obj, "stringArray1[0]", "set[0]");
//		assertEquals(null, result.value);
//		assertEquals("set[0]", obj.stringArray1[0]);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray1[1]", "set[1]");
//		assertEquals("set[1]", obj.stringArray1[1]);
//
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray1[2]", "set[2]");
//		assertEquals("set[2]", obj.stringArray1[2]);
//
//		//noIndexSetter, public array
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray2[0]", "set[0]");
//		assertEquals(null, result.value);
//		assertEquals("set[0]", obj.stringArray2[0]);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray2[1]", "set[1]");
//		assertEquals("set[1]", obj.stringArray2[1]);
//
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray2[2]", "set[2]");
//		assertEquals("set[2]", obj.stringArray2[2]);
//
//		// indexSetter, public
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray3[0]", "set");
//		assertEquals("setterにより設定", obj.stringArray3[0]);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		// noIndexSetter private, arrayGetter
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray4[0]", "set");
//		assertEquals("set", obj.stringArray4[0]);
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		// noIndexGetter, private, collectionGetter
//		result = PropertyAccessor.setNestedProperty(obj, "stringList1[0]", "setList");
//		assertEquals("setList", obj.stringList1.get(0));
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		// noIndexSetter, public collection
//		result = PropertyAccessor.setNestedProperty(obj, "stringList2[0]", "set[0]");
//		assertEquals("set[0]", obj.stringList2.get(0));
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//
//		result = PropertyAccessor.setNestedProperty(obj, "stringList2[1]", "set[1]");
//		assertEquals("set[1]", obj.stringList2.get(1));
//
//		result = PropertyAccessor.setNestedProperty(obj, "stringList2[2]", "set[2]");
//		assertEquals("set[2]", obj.stringList2.get(2));
//
//		// noIndexSetter, public, arrayGetter
//		result = PropertyAccessor.setNestedProperty(obj, "stringArray5[0]", "set");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals("set", obj.stringArray5[0]);
//		assertEquals("getterにより取得2", obj.stringArray5[1]);
//
//		//notfound
//		result = PropertyAccessor.setNestedProperty(obj, "notexists[0]", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.NOT_FOUND, result.code);
//		assertEquals("test.PropertyAccessorHighLevelTest$BaseBean のプロパティ \"notexists\" が見つかりません。", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$インデックス指定_null_public_array() throws Exception {
//		BaseBean obj = new BaseBean();
//		AccessResult result = PropertyAccessor.setNestedProperty("root", obj, "stringArray7[0]", "set[0]");
//		assertEquals(null, result.value);
//		assertEquals(true, obj.stringArray7 == null);
//		assertEquals(AccessResult.ARRAY_IS_NULL, result.code);
//		assertEquals("root.stringArray7 が null です。", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$インデックスOutOfBounds() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		// publicArray
//		AccessResult result = PropertyAccessor.setNestedProperty("root", obj, "stringArray2[3]", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringArray2 にインデックス 3 の要素がありません。", result.message);
//
//		// publicCollection
//		result = PropertyAccessor.setNestedProperty("root", obj, "stringList2[3]", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringList2 にインデックス 3 の要素がありません。", result.message);
//
//		// private, arrayGetter
//		result = PropertyAccessor.setNestedProperty("root", obj, "stringArray4[3]", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringArray4 にインデックス 3 の要素がありません。", result.message);
//
//		// private, collectionGetter
//		result = PropertyAccessor.setNestedProperty("root", obj, "stringList1[3]", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_OUT_OF_BOUNDS, result.code);
//		assertEquals("root.stringList1 にインデックス 3 の要素がありません。", result.message);
//
//	}
//
//	@Test
//	public void setNestedProperty$インデックス非対応() throws Exception {
//		BaseBean obj = new BaseBean();
//
//		// private, getter
//		AccessResult result = PropertyAccessor.setNestedProperty("root", obj, "string1[0]", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_IS_NOT_AVAILABLE, result.code);
//		assertEquals("root.string1 はインデックス指定による要素のアクセスに対応していません。", result.message);
//
//		// public
//		result = PropertyAccessor.setNestedProperty("root", obj, "string2[0]", "set");
//		assertEquals(null, result.value);
//		assertEquals(AccessResult.INDEX_IS_NOT_AVAILABLE, result.code);
//		assertEquals("root.string2 はインデックス指定による要素のアクセスに対応していません。", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換_setter() throws Exception {
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
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar", "笑");
//		assertEquals('笑', obj.exampleChar);
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
//	public void setNestedProperty$基本データ型変換_public_field() throws Exception {
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
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar2", "笑");
//		assertEquals('笑', obj.exampleChar2);
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
//	public void setNestedProperty$基本データ型ラッパー変換_setter() throws Exception {
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
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper1", "笑");
//		assertEquals('笑', obj.charWrapper1.charValue());
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
//	public void setNestedProperty$基本データ型ラッパー変換_public_field() throws Exception {
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
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper2", "笑");
//		assertEquals('笑', obj.charWrapper2.charValue());
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
//
//	@Test
//	public void setNestedProperty$基本データ型変換_インデックス有り_indexSetter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// indexSetter
//		result = PropertyAccessor.setNestedProperty("root", obj, "intArray[1]", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.intArray[1]);
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換_インデックス有り_publicArray() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		//public array
//		result = PropertyAccessor.setNestedProperty("root", obj, "intArray2[1]", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.intArray2[1]);
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換_インデックス有り_publicCollection() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		//public collection
//		result = PropertyAccessor.setNestedProperty("root", obj, "intList[0]", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.intList.get(0).intValue());
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換_インデックス有り_noIndexSetter_private_arrayGetter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		//arrayGetter
//		result = PropertyAccessor.setNestedProperty("root", obj, "intArray2[1]", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.intArray2[1]);
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換_インデックス有り_publicArray() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		//public array
//		result = PropertyAccessor.setNestedProperty("root", obj, "integerWrapperArray2[1]", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.integerWrapperArray2[1].intValue());
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換_インデックス有り_index_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// indexSetter
//		result = PropertyAccessor.setNestedProperty("root", obj, "integerWrapperArray1[1]", "1234");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(1234, obj.integerWrapperArray1[1].intValue());
//		assertEquals(null, result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換失敗_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleInt", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals(0, obj.exampleInt);
//		assertEquals("型変換に失敗しました。\"123A\" を int型 に変換できません。", result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleBoolean", "y");
//		assertEquals(false, obj.exampleBoolean);
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("型変換に失敗しました。\"y\" を boolean型 に変換できません。大文字小文字の \"true\" \"false\" または空文字、半角スペースのみ変換できます。", result.message);
//		assertEquals(null, result.value);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleLong", "がっは");
//		assertEquals("型変換に失敗しました。\"がっは\" を long型 に変換できません。", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleDouble", "1234.5678A");
//		assertEquals("型変換に失敗しました。\"1234.5678A\" を double型 に変換できません。", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar", "笑い");
//		assertEquals("型変換に失敗しました。\"笑い\" を char型 に変換できません。長さ 1 の文字列のみ変換できます。", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleByte", "120A");
//		assertEquals("型変換に失敗しました。\"120A\" を byte型 に変換できません。", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleFloat", "999.999A");
//		assertEquals("型変換に失敗しました。\"999.999A\" を float型 に変換できません。", result.message);
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleShort", "1200A");
//		assertEquals("型変換に失敗しました。\"1200A\" を short型 に変換できません。", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換失敗_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "exampleChar2", "笑い");
//		assertEquals("型変換に失敗しました。\"笑い\" を char型 に変換できません。長さ 1 の文字列のみ変換できます。", result.message);
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換失敗_インデックス有り_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "intArray[0]", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("型変換に失敗しました。\"123A\" を int型 に変換できません。", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型変換失敗_インデックス有り_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "intArray2[0]", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("型変換に失敗しました。\"123A\" を int型 に変換できません。", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換失敗_setter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper1", "笑い");
//		assertEquals("型変換に失敗しました。\"笑い\" を java.lang.Character型 に変換できません。長さ 1 の文字列のみ変換できます。", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換失敗_public_field() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "charWrapper2", "笑い");
//		assertEquals("型変換に失敗しました。\"笑い\" を java.lang.Character型 に変換できません。長さ 1 の文字列のみ変換できます。", result.message);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換失敗_インデックス有り_indexSetter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		// setter
//		result = PropertyAccessor.setNestedProperty("root", obj, "integerWrapperArray1[0]", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("型変換に失敗しました。\"123A\" を java.lang.Integer型 に変換できません。", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパーインデックス非対応_publicArray() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//
//		result = PropertyAccessor.setNestedProperty("root", obj, "integerWrapper2[0]", "123A");
//		assertEquals(AccessResult.INDEX_IS_NOT_AVAILABLE, result.code);
//		assertEquals("root.integerWrapper2 はインデックス指定による要素のアクセスに対応していません。", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換失敗_インデックス有り_publicCollection() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//		result = PropertyAccessor.setNestedProperty("root", obj, "intList[0]", "123A");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("型変換に失敗しました。\"123A\" を java.lang.Integer型 に変換できません。", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換失敗_インデックス有り_private_arrayGetter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//		result = PropertyAccessor.setNestedProperty("root", obj, "integerWrapperArray3[0]", "孫悟空");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("型変換に失敗しました。\"孫悟空\" を java.lang.Integer型 に変換できません。", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test
//	public void setNestedProperty$基本データ型ラッパー変換失敗_インデックス有り_private_collectionGetter() throws Exception {
//		PrimitiveBean obj = new PrimitiveBean();
//		AccessResult result = null;
//		result = PropertyAccessor.setNestedProperty("root", obj, "intList2[0]", "孫悟空");
//		assertEquals(AccessResult.INVALID_FORMAT, result.code);
//		assertEquals("型変換に失敗しました。\"孫悟空\" を java.lang.Integer型 に変換できません。", result.message);
//		assertEquals(null, result.value);
//	}
//
//	@Test(expected=AmbiguousSetterException.class)
//	public void setNestedProperty$同名の基本データ型setterが複数() throws Exception {
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
//
//	@Test
//	public void getNestedFlag$二階層_public_field() {
//		ParentBean obj = new ParentBean();
//		AccessResult result = null;
//		result = PropertyAccessor.getNestedFlag("root", obj, "base1.flag3");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}
//
//	@Test
//	public void getNestedFlag$二階層_private_isMethod() {
//		ParentBean obj = new ParentBean();
//		AccessResult result = null;
//		result = PropertyAccessor.getNestedFlag("root", obj, "base1.flag1");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}
//
//	@Test
//	public void getNestedFlag$二階層_private_getter() {
//		ParentBean obj = new ParentBean();
//		AccessResult result = null;
//		result = PropertyAccessor.getNestedFlag("root", obj, "base1.flag2");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}
//
//	@Test
//	public void doNestedCheckMethod$() {
//		BaseBean obj = new BaseBean();
//		AccessResult result = null;
//		result = PropertyAccessor.doNestedCheckMethod("root", obj, "hasFlag4");
//		assertEquals(AccessResult.SUCCESS, result.code);
//		assertEquals(null, result.message);
//		assertEquals(true, result.value);
//	}

	/*--------------------- setNestedProperty 二階層-------------------------*/

	/**
	 * テスト用クラス
	 * @author Yamamoto Keita
	 */
	private static class BaseBean {
		// For getter
		private String string1 = "default1";
		private String[] stringArray1 = {"default1[0]", "default1[1]", "default1[2]"};
		private List<String> stringList1 = new ArrayList<String>();
		private String nullField1;

		// For field access
		public String string2 = "default2";
		public String[] stringArray2 = {"default2[0]", "default2[1]", "default2[2]"};
		public List<String> stringList2 = new ArrayList<String>();
		public String nullField2;

		// For arrayGetter
		private String[] stringArray4 = {"default4[0]", "default4[1]", "default4[2]"};
		private String[] stringArray8;

		// For double
		public String string3;
		public String[] stringArray3 = {"default3[0]", "default3[1]", "default3[2]"};
		public String[] stringArray5 = {"default5[0]", "default5[1]", "default5[2]"};
		public String[] stringArray6 = {"default6[0]", "default6[1]", "default6[2]"};
		public String[] stringArray7;

		// invalid format
		public ByteArrayOutputStream byteArrayOutputStream;
		private ByteArrayOutputStream byteArrayOutputStream2;

		private boolean flag1 = true;
		private boolean flag2 = true;
		public boolean flag3 = true;
		private boolean flag4 = true;

		public BaseBean() {
			stringList1.add("default1[0]");
			stringList1.add("default1[1]");
			stringList1.add("default1[2]");

			stringList2.add("default2[0]");
			stringList2.add("default2[1]");
			stringList2.add("default2[2]");
		}

		// Accessor----------------------------

		// string
		public String getString1() {
			return string1;
		}
		public void setString1(String string) {
			this.string1 = string;
		}

		// stringArray
		public String getStringArray1(int index) {
			return stringArray1[index];
		}
		public String[] getStringArray1() {
			return stringArray1;
		}
		public void setStringArray1(String[] stringArray1) {
			this.stringArray1 = stringArray1;
		}
		public void setStringArray1(int index, String string) {
			stringArray1[index] = string;
		}

		//string3
		public String getString3() {
			return "getterにより取得";
		}
		public void setString3(String text) {
			this.string3 = "setterにより設定";
		}

		//stringArray3
		public String getStringArray3(int idx) {
			return "getterにより取得";
		}
		public void setStringArray3(int idx, String stringArray3) {
			this.stringArray3[idx] = "setterにより設定";
		}

		//stringArray4
		public String[] getStringArray4() {
			return stringArray4;
		}

		//stringArray5
		public String[] getStringArray5() {
			this.stringArray5 = new String[]{"getterにより取得1", "getterにより取得2", "getterにより取得3"};
			return stringArray5;
		}

		//stringArray6
		public String getStringArray6(int idx) {
			return "インデックスgetterにより取得";
		}
		public String[] getStringArray6() {
			return stringArray6;
		}
		public void setStringArray6(int idx, String[] stringArray) {
			this.stringArray6[idx] = "インデックスsetterにより設定";
		}

		//stringList1
		public List<String> getStringList1() {
			return stringList1;
		}

		//null
		public String getNullField1() {
			return nullField1;
		}
		public void setNullField1(String str) {
			this.nullField1 = str;
		}

		public void setByteArrayOutputStream2(ByteArrayOutputStream byteArrayOutputStream2) {
			this.byteArrayOutputStream2 = byteArrayOutputStream2;
		}

		public ByteArrayOutputStream setByteArrayOutputStream2() {
			return this.byteArrayOutputStream2;
		}

		public void setStringArray8(String[] array) {
			stringArray8 = array;
		}

		public String[] getStringArray8() {
			return stringArray8;
		}

		public boolean isFlag1() {
			return flag1;
		}

		public boolean getFlag2() {
			return flag2;
		}

		public boolean hasFlag4() {
			return flag4;
		}
	}

	/**
	 * テスト用クラス
	 * @author Yamamoto Keita
	 */
	public static class ParentBean {
		private BaseBean base1 = new BaseBean();
		private BaseBean base2;
		public BaseBean[] baseArray = {new BaseBean(), new BaseBean(), new BaseBean()};
		public String string1 = "default1";

		public BaseBean getBase1() {
			return base1;
		}
		public void setBase1(BaseBean base1) {
			this.base1 = base1;
		}
		public BaseBean getBase2() {
			return base2;
		}
		public void setBase2(BaseBean base2) {
			this.base2 = base2;
		}
	}

	/**
	 * テスト用クラス
	 * @author Yamamoto Keita
	 *
	 */
	public static class PrimitiveBean {

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

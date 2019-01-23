package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 全テスト
 *
 * @author Yamamoto Keita
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	CheckerTest.class,
	CSVLineTest.class,
	CSVTest.class,
	DateTimeTest.class,
	FTPTest.class,
	GatewayParserTest.class,
	HTMLDataTest.class,
	HttpClientTest.class,
	InvokerTest.class,
	IPAddressTest.class,
	JapaneseCalendarTest.class,
	MathUtilTest.class,
	MobileAgentTypeTest.class,
	MobileTextTagTest.class,
	PageableRecordListTest.class,
	PropertyAccessorHighLevelTest.class,
	ResourceFinderTest.class,
	TextUtilsTest.class,
	XMLElementTest.class,
	VMSTATTest.class,
	RaidDeviceTest.class,
	DiskStateTest.class,
	ByteUtilsTest.class,
	IDTableTest.class
})
public class _AllTests {

	/**
	 * テスト実行
	 * @param args
	 */
	public static void main(String[] args) {

		JUnitCore.main(_AllTests.class.getName());// 3
	}
}

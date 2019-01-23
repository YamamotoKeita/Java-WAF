package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import jp.co.altonotes.io.IOUtils;
import jp.co.altonotes.xml.XMLElement;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XMLElement�̃e�X�g
 *
 * @author Yamamoto Keita
 *
 */
public class XMLElementTest {

	static File SAMPLE_XML_FILE = null;
	static String SAMPLE_RESOURCE_PATH = "test/sample.xml";
	static String SAMPLE2_RESOURCE_PATH = "test/sample2.xml";
	static String SAMPLE3_RESOURCE_PATH = "test/sample3.xml";
	static String SAMPLE_SJIS_RESOURCE_PATH = "test/sample_sjis.xml";

	@BeforeClass
	public static void init() throws IOException, SAXException {
		URL url = XMLElementTest.class.getClassLoader().getResource(SAMPLE_RESOURCE_PATH);
		String path = url.getPath();
		System.out.println(url.getPath());
		SAMPLE_XML_FILE = new File(path);
	}

	// �C���X�^���X�쐬 ----------------------------------------

	@Test
	public void �t�@�C������C���X�^���X�쐬() throws IOException, SAXException {
		XMLElement xml = XMLElement.createByFile(SAMPLE_XML_FILE);
		assertEquals("element2value", xml.getChildByTagName("element2").getTextContent());
	}

	@Test
	public void �t�@�C���p�X����C���X�^���X�쐬() throws IOException, SAXException {
		XMLElement xml = XMLElement.createByFilePath(SAMPLE_XML_FILE.getAbsolutePath());
		assertEquals("element2value", xml.getChildByTagName("element2").getTextContent());
	}

	@Test
	public void inputstream����C���X�^���X�쐬() throws IOException, SAXException {
		XMLElement xml = XMLElement.createByInputStream(new FileInputStream(SAMPLE_XML_FILE));
		assertEquals("element2value", xml.getChildByTagName("element2").getTextContent());
	}

	@Test
	public void byte�z�񂩂�C���X�^���X�쐬() throws IOException, SAXException {
		byte[] data = IOUtils.read(SAMPLE_XML_FILE);
		XMLElement xml = XMLElement.createByByteArray(data);
		assertEquals("element2value", xml.getChildByTagName("element2").getTextContent());
	}

	@Test
	public void �����񂩂�C���X�^���X�쐬() throws IOException, SAXException {
		XMLElement sourceXml = XMLElement.createByFile(SAMPLE_XML_FILE);

		XMLElement xml = XMLElement.createByString(sourceXml.toString());
		assertEquals("element2value", xml.getChildByTagName("element2").getTextContent());
	}

	@Test
	public void URL����C���X�^���X�쐬() throws IOException, SAXException {
		URL url = SAMPLE_XML_FILE.toURI().toURL();
		XMLElement xml = XMLElement.createByURL(url );
		assertEquals("element2value", xml.getChildByTagName("element2").getTextContent());
	}

	@Test
	public void ���\�[�X�p�X����C���X�^���X�쐬() throws IOException, SAXException {
		XMLElement xml = XMLElement.createByResourcePath(SAMPLE_RESOURCE_PATH);
		assertEquals("element2value", xml.getChildByTagName("element2").getTextContent());
	}

	// �A�y���h ----------------------------------------

	@Test
	public void ���\�[�X�p�X������ꂽ�C���X�^���X��append() throws IOException, SAXException {
		XMLElement xml1 = XMLElement.createByResourcePath(SAMPLE_RESOURCE_PATH);
		XMLElement xml2 = XMLElement.createByResourcePath(SAMPLE2_RESOURCE_PATH);

		xml1.append(xml2);

		XMLElement sample2 = xml1.getChildByTagName("sample2");
		assertEquals("s2node1value", sample2.getChildByTagName("s2node1").getTextContent());
	}

	@Test
	public void �����񂩂���ꂽ�C���X�^���X��append() throws IOException, SAXException {
		XMLElement xml1 = XMLElement.createByString(getXMLString(SAMPLE_RESOURCE_PATH));
		XMLElement xml2 = XMLElement.createByString(getXMLString(SAMPLE2_RESOURCE_PATH));

		xml1.append(xml2);

		XMLElement sample2 = xml1.getChildByTagName("sample2");
		assertEquals("s2node1value", sample2.getChildByTagName("s2node1").getTextContent());
	}

	@Test
	public void element������ꂽ�C���X�^���X��append() throws IOException, SAXException {
		XMLElement xml1 = new XMLElement(getElement(SAMPLE_RESOURCE_PATH));
		XMLElement xml2 = new XMLElement(getElement(SAMPLE2_RESOURCE_PATH));

		xml1.append(xml2);

		XMLElement sample2 = xml1.getChildByTagName("sample2");
		assertEquals("s2node1value", sample2.getChildByTagName("s2node1").getTextContent());
	}

	@Test
	public void �q�v�f��append() throws IOException, SAXException {
		XMLElement xml1 = XMLElement.createByResourcePath(SAMPLE_RESOURCE_PATH);
		XMLElement xml2 = XMLElement.createByResourcePath(SAMPLE2_RESOURCE_PATH);

		XMLElement xml1child = xml1.getElementByXPath("/sample1/element1");
		xml1child.append(xml2);

		assertEquals("s2node1value", xml1.getElementByXPath("/sample1/element1/sample2/s2node1").getTextContent());
	}

	// XPath ----------------------------------------

	@Test
	public void append�����v�f�̃e�L�X�g��XPath�Ŏ擾() throws IOException, SAXException {
		XMLElement xml1 = XMLElement.createByResourcePath(SAMPLE_RESOURCE_PATH);
		XMLElement xml2 = XMLElement.createByResourcePath(SAMPLE2_RESOURCE_PATH);

		XMLElement xml1child = xml1.getElementByXPath("/sample1/element1");
		xml1child.append(xml2);

		assertEquals("s2node1value", xml1.getTextByXPath("/sample1/element1/sample2/s2node1"));
	}

	@Test
	public void append�����v�f��append() throws IOException, SAXException {
		XMLElement xml1 = XMLElement.createByResourcePath(SAMPLE_RESOURCE_PATH);
		XMLElement xml2 = XMLElement.createByResourcePath(SAMPLE2_RESOURCE_PATH);
		XMLElement xml3 = XMLElement.createByResourcePath(SAMPLE3_RESOURCE_PATH);

		XMLElement xml2child = xml2.getElementByXPath("/sample2");
		xml2child.append(xml3);

		XMLElement xml1child = xml1.getElementByXPath("/sample1/element1");
		xml1child.append(xml2);

		assertEquals("s3node1value", xml1.getTextByXPath("/sample1/element1/sample2/sample3/s3node1"));
	}

	@Test
	public void Shift_JIS��XML�ǂݍ���() throws IOException, SAXException {
		XMLElement xml = XMLElement.createByResourcePath(SAMPLE_SJIS_RESOURCE_PATH);

		assertEquals("�q�v�f�P�̒l", xml.getTextByXPath("/sample1/element1/child1"));
	}

	/**
	 * ���\�[�X�p�X���XML���\�����镶������擾����B
	 *
	 * @param resourcePath
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String getXMLString(String resourcePath) throws SAXException, IOException {
		XMLElement xml = XMLElement.createByResourcePath(resourcePath);
		return xml.toString();
	}

	/**
	 * ���\�[�X�p�X���XML���\������Element���擾����B
	 *
	 * @param resourcePath
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Element getElement(String resourcePath) throws SAXException, IOException {
		XMLElement xml = XMLElement.createByResourcePath(resourcePath);
		return xml.toElement();
	}

}

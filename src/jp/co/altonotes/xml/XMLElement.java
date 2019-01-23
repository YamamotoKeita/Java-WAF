package jp.co.altonotes.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * XML�v�f��\���B
 * org.w3c.dom.Element �����b�v���A�قړ����̋@�\��񋟂���B
 * �܂��AXPath�ɂ��v�f�擾�̋@�\��񋟂���B
 *
 * <pre>
 * �Y������item�v�f���P�擾
 * getElementByXPath("/root/item");
 *
 * �Y������Sitem�v�f���擾
 * getElementsByXPath("/root/item");
 *
 * XML���̑Sitem�v�f���擾
 * getElementsByXPath("//item");
 *
 * item�^�O�����̃m�[�h��S�Ď擾
 * getElementsByXPath("/item/*");
 *
 * sex��male��item�^�O���擾
 * getElementByXPath("/item[@sex='male']");
 * </pre>
 *
 * @author Yamamoto Keita
 *
 */
public class XMLElement {

	private Element rootElement;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param element
	 */
	public XMLElement(Element element) {
		rootElement = element;
	}

	/**
	 * �t�@�C���p�X���XMLElement���쐬����B
	 *
	 * @param filePath
	 * @return �t�@�C���p�X���쐬���ꂽXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByFilePath(String filePath) throws SAXException, IOException {
		if (filePath == null) {
			throw new IllegalArgumentException("�����̃t�@�C���p�X��null�ł��B");
		}

		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {// DocumentBuilderFactory�ɉ����ݒ肵�Ă��Ȃ��̂ŋN����͂��͂Ȃ�
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}

		Document doc = builder.parse(new File(filePath));
		Element rootElement = doc.getDocumentElement();

		return new XMLElement(rootElement);
	}

	/**
	 * InputStream���XMLElement���쐬����B
	 *
	 * @param in
	 * @return InputStream���쐬���ꂽXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByInputStream(InputStream in) throws SAXException, IOException {
		if (in == null) {
			throw new IllegalArgumentException("������InputStream��null�ł��B");
		}

		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {// DocumentBuilderFactory�ɉ����ݒ肵�Ă��Ȃ��̂ŋN����͂��͂Ȃ�
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}

		Document doc;
		Element rootElement;
		try {
			doc = builder.parse(in);
			rootElement = doc.getDocumentElement();
		} finally {
			if(in!=null){try {in.close();} catch(Exception e) {}}
		}

		return new XMLElement(rootElement);
	}

	/**
	 * XML��������XMLElement���쐬����B
	 *
	 * @param source
	 * @return XML��������쐬���ꂽXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByString(String source) throws SAXException, IOException {
		if (source == null) {
			throw new IllegalArgumentException("������String��null�ł��B");
		}

		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {// DocumentBuilderFactory�ɉ����ݒ肵�Ă��Ȃ��̂ŋN����͂��͂Ȃ�
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}

		StringReader strReader = new StringReader(source);
		doc = builder.parse(new InputSource(strReader));

		return new XMLElement(doc.getDocumentElement());
	}

	/**
	 * ���\�[�X�p�X���XMLElement���쐬����B
	 *
	 * @param resourcePath
	 * @return ���\�[�X�p�X���쐬���ꂽXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByResourcePath(String resourcePath) throws SAXException, IOException {
		if (resourcePath == null) {
			throw new IllegalArgumentException("�����̃��\�[�X�p�X��null�ł��B");
		}

		ClassLoader loader = getClassLoader();
		InputStream in = loader.getResourceAsStream(resourcePath);
		return createByInputStream(in);
	}

	/**
	 * byte�z����XMLElement���쐬����B
	 *
	 * @param data
	 * @return byte�z����쐬���ꂽXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByByteArray(byte[] data) throws SAXException, IOException {
		if (data == null) {
			throw new IllegalArgumentException("������byte[]��null�ł��B");
		}

		InputStream in = new ByteArrayInputStream(data);
		return createByInputStream(in);
	}

	/**
	 * File���XMLElement���쐬����B
	 *
	 * @param file
	 * @return File���쐬���ꂽXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByFile(File file) throws SAXException, IOException {
		if (file == null) {
			throw new IllegalArgumentException("������File��null�ł��B");
		}

		InputStream in = new FileInputStream(file);
		return createByInputStream(in);
	}

	/**
	 * URL���XMLElement���쐬����B
	 *
	 * @param url
	 * @return URL���쐬���ꂽXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByURL(URL url) throws SAXException, IOException {
		if (url == null) {
			throw new IllegalArgumentException("������URL��null�ł��B");
		}

		InputStream in = url.openStream();
		return createByInputStream(in);
	}

	/**
	 * �w�肵���������擾����
	 *
	 * @param name
	 * @return ����XML�v�f�̈����Ɏw�肵�������l
	 */
	public String getAttribute(String name) {
		return rootElement.getAttribute(name);
	}

	/**
	 * �^�O�����擾����B
	 *
	 * @return ����XML�v�f�̃^�O��
	 */
	public String getTagName() {
		return rootElement.getTagName();
	}

	/**
	 * �����e�L�X�g���擾����B
	 *
	 * @return ����XML�v�f�̓����e�L�X�g
	 */
	public String getTextContent() {
		return rootElement.getTextContent();
	}

	/**
	 * ���̗v�f�̐e�v�f���擾����B
	 *
	 * @return ����XML�v�f�̐e�v�f
	 */
	public XMLElement getParent() {
		Node patentNode = rootElement.getParentNode();
		if (patentNode instanceof Element) {
			return new XMLElement((Element)patentNode);
		}

		return null;
	}

	/**
	 * �q�v�f��S�Ď擾����B
	 *
	 * @return ����XML�v�f�̑S�Ă̎q�v�f
	 */
	public XMLElement[] getChildren() {
		ArrayList<XMLElement> temp = new ArrayList<XMLElement>();

		NodeList list = rootElement.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child instanceof Element) {
				XMLElement element = new XMLElement((Element)child);
				temp.add(element);
			}
		}
		return temp.toArray(new XMLElement[temp.size()]);
	}

	/**
	 * �w�肵���^�O���̍ŏ��̎q�v�f���擾����B
	 *
	 * @param tagName
	 * @return �w�肵���^�O���̍ŏ��̎q�v�f
	 */
	public XMLElement getChildByTagName(String tagName) {
		XMLElement[] children = getChildren();
		for (XMLElement child : children) {
			String childTag = child.getTagName();
			if (childTag != null && childTag.equals(tagName)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * �w�肵���^�O���̎q�v�f��S�Ď擾����B
	 *
	 * @param tagName
	 * @return �w�肵���^�O���̑S�q�v�f
	 */
	public XMLElement[] getChildrenByTagName(String tagName) {
		XMLElement[] children = getChildren();
		ArrayList<XMLElement> list = new ArrayList<XMLElement>();
		for (XMLElement child : children) {
			String childTag = child.getTagName();
			if (childTag != null && childTag.equals(tagName)) {
				list.add(child);
			}
		}
		return list.toArray(new XMLElement[list.size()]);
	}

	/**
	 * �w�肵��XPath�ɊY������ŏ��̗v�f�̓����e�L�X�g���擾����B
	 *
	 * @param xpath
	 * @return �w�肵��XPath�ɊY������ŏ��̗v�f�̓����e�L�X�g
	 */
	public String getTextByXPath(String xpath) {
		return getElementByXPath(xpath).getTextContent();
	}

	/**
	 * �w�肵��XPath�ɊY������A�ŏ��̗v�f����擾����B
	 *
	 * @param expression
	 * @return xpath�ɊY������v�f
	 */
	public XMLElement getElementByXPath(String expression) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		try {
			Node node = (Node) xpath.evaluate(expression, rootElement, XPathConstants.NODE);
			if (node instanceof Element) {
				return new XMLElement((Element) node);
			}
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(e);
		}
		return null;
	}

	/**
	 * �w�肵��XPath�ɊY������A�S�v�f���擾����B
	 *
	 * @param expression
	 * @return xpath�ɊY������S�v�f
	 */
	public XMLElement[] getElementsByXPath(String expression) {
		NodeList list = null;
		ArrayList<XMLElement> tempList = new ArrayList<XMLElement>();
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		try {
			list = (NodeList) xpath.evaluate(expression, rootElement, XPathConstants.NODESET);
			
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node instanceof Element) {
					tempList.add(new XMLElement((Element)node));
				}
			}
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(e);
		}
		return tempList.toArray(new XMLElement[tempList.size()]);
	}

	/**
	 * ������XML������XML�̎q�v�f�Ƃ��Ēǉ�����B
	 *
	 * @param element
	 */
	public void append(XMLElement element) {
		Document owner = rootElement.getOwnerDocument();
		Node imported = owner.importNode(element.toElement(), true);
		rootElement.appendChild(imported);
	}

	/**
	 * ������XML�̃��[�g�ȉ��̗v�f���A����XML�̎q�v�f�Ƃ��Ēǉ�����B
	 *
	 * @param element
	 */
	public void appendBody(XMLElement element) {
		XMLElement[] includeElements = element.getChildren();
		for (XMLElement includeElement : includeElements) {
			append(includeElement);
		}
	}

	/**
	 * ����XML��Element�C���X�^���X�ɕϊ�����
	 *
	 * @return ����XML��\��Element�C���X�^���X
	 */
	public Element toElement() {
		return rootElement;
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return DomUtils.toString(rootElement);
	}

	/**
	 * �N���X���[�_�[���擾����B
	 *
	 * @return
	 */
	private static ClassLoader getClassLoader() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) { }// �X���b�h�̃R���e�L�X�g�N���X���[�_�[�ɃA�N�Z�X�ł��Ȃ��ꍇ�́A�V�X�e���N���X���[�_�[���g��

		if (loader == null) {
			loader = XMLElement.class.getClassLoader();
		}
		return loader;
	}

}

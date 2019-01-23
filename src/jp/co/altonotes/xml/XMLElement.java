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
 * XML要素を表す。
 * org.w3c.dom.Element をラップし、ほぼ同等の機能を提供する。
 * また、XPathによる要素取得の機能を提供する。
 *
 * <pre>
 * 該当するitem要素を１つ取得
 * getElementByXPath("/root/item");
 *
 * 該当する全item要素を取得
 * getElementsByXPath("/root/item");
 *
 * XML内の全item要素を取得
 * getElementsByXPath("//item");
 *
 * itemタグ直下のノードを全て取得
 * getElementsByXPath("/item/*");
 *
 * sexがmaleのitemタグを取得
 * getElementByXPath("/item[@sex='male']");
 * </pre>
 *
 * @author Yamamoto Keita
 *
 */
public class XMLElement {

	private Element rootElement;

	/**
	 * コンストラクタ。
	 *
	 * @param element
	 */
	public XMLElement(Element element) {
		rootElement = element;
	}

	/**
	 * ファイルパスよりXMLElementを作成する。
	 *
	 * @param filePath
	 * @return ファイルパスより作成されたXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByFilePath(String filePath) throws SAXException, IOException {
		if (filePath == null) {
			throw new IllegalArgumentException("引数のファイルパスがnullです。");
		}

		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {// DocumentBuilderFactoryに何も設定していないので起こるはずはない
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}

		Document doc = builder.parse(new File(filePath));
		Element rootElement = doc.getDocumentElement();

		return new XMLElement(rootElement);
	}

	/**
	 * InputStreamよりXMLElementを作成する。
	 *
	 * @param in
	 * @return InputStreamより作成されたXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByInputStream(InputStream in) throws SAXException, IOException {
		if (in == null) {
			throw new IllegalArgumentException("引数のInputStreamがnullです。");
		}

		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {// DocumentBuilderFactoryに何も設定していないので起こるはずはない
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
	 * XML文字列よりXMLElementを作成する。
	 *
	 * @param source
	 * @return XML文字列より作成されたXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByString(String source) throws SAXException, IOException {
		if (source == null) {
			throw new IllegalArgumentException("引数のStringがnullです。");
		}

		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {// DocumentBuilderFactoryに何も設定していないので起こるはずはない
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}

		StringReader strReader = new StringReader(source);
		doc = builder.parse(new InputSource(strReader));

		return new XMLElement(doc.getDocumentElement());
	}

	/**
	 * リソースパスよりXMLElementを作成する。
	 *
	 * @param resourcePath
	 * @return リソースパスより作成されたXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByResourcePath(String resourcePath) throws SAXException, IOException {
		if (resourcePath == null) {
			throw new IllegalArgumentException("引数のリソースパスがnullです。");
		}

		ClassLoader loader = getClassLoader();
		InputStream in = loader.getResourceAsStream(resourcePath);
		return createByInputStream(in);
	}

	/**
	 * byte配列よりXMLElementを作成する。
	 *
	 * @param data
	 * @return byte配列より作成されたXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByByteArray(byte[] data) throws SAXException, IOException {
		if (data == null) {
			throw new IllegalArgumentException("引数のbyte[]がnullです。");
		}

		InputStream in = new ByteArrayInputStream(data);
		return createByInputStream(in);
	}

	/**
	 * FileよりXMLElementを作成する。
	 *
	 * @param file
	 * @return Fileより作成されたXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByFile(File file) throws SAXException, IOException {
		if (file == null) {
			throw new IllegalArgumentException("引数のFileがnullです。");
		}

		InputStream in = new FileInputStream(file);
		return createByInputStream(in);
	}

	/**
	 * URLよりXMLElementを作成する。
	 *
	 * @param url
	 * @return URLより作成されたXMLElement
	 * @throws SAXException
	 * @throws IOException
	 */
	public static XMLElement createByURL(URL url) throws SAXException, IOException {
		if (url == null) {
			throw new IllegalArgumentException("引数のURLがnullです。");
		}

		InputStream in = url.openStream();
		return createByInputStream(in);
	}

	/**
	 * 指定した属性を取得する
	 *
	 * @param name
	 * @return このXML要素の引数に指定した属性値
	 */
	public String getAttribute(String name) {
		return rootElement.getAttribute(name);
	}

	/**
	 * タグ名を取得する。
	 *
	 * @return このXML要素のタグ名
	 */
	public String getTagName() {
		return rootElement.getTagName();
	}

	/**
	 * 内部テキストを取得する。
	 *
	 * @return このXML要素の内部テキスト
	 */
	public String getTextContent() {
		return rootElement.getTextContent();
	}

	/**
	 * この要素の親要素を取得する。
	 *
	 * @return このXML要素の親要素
	 */
	public XMLElement getParent() {
		Node patentNode = rootElement.getParentNode();
		if (patentNode instanceof Element) {
			return new XMLElement((Element)patentNode);
		}

		return null;
	}

	/**
	 * 子要素を全て取得する。
	 *
	 * @return このXML要素の全ての子要素
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
	 * 指定したタグ名の最初の子要素を取得する。
	 *
	 * @param tagName
	 * @return 指定したタグ名の最初の子要素
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
	 * 指定したタグ名の子要素を全て取得する。
	 *
	 * @param tagName
	 * @return 指定したタグ名の全子要素
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
	 * 指定したXPathに該当する最初の要素の内部テキストを取得する。
	 *
	 * @param xpath
	 * @return 指定したXPathに該当する最初の要素の内部テキスト
	 */
	public String getTextByXPath(String xpath) {
		return getElementByXPath(xpath).getTextContent();
	}

	/**
	 * 指定したXPathに該当する、最初の要素一つを取得する。
	 *
	 * @param expression
	 * @return xpathに該当する要素
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
	 * 指定したXPathに該当する、全要素を取得する。
	 *
	 * @param expression
	 * @return xpathに該当する全要素
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
	 * 引数のXMLをこのXMLの子要素として追加する。
	 *
	 * @param element
	 */
	public void append(XMLElement element) {
		Document owner = rootElement.getOwnerDocument();
		Node imported = owner.importNode(element.toElement(), true);
		rootElement.appendChild(imported);
	}

	/**
	 * 引数のXMLのルート以下の要素を、このXMLの子要素として追加する。
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
	 * このXMLをElementインスタンスに変換する
	 *
	 * @return このXMLを表すElementインスタンス
	 */
	public Element toElement() {
		return rootElement;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return DomUtils.toString(rootElement);
	}

	/**
	 * クラスローダーを取得する。
	 *
	 * @return
	 */
	private static ClassLoader getClassLoader() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) { }// スレッドのコンテキストクラスローダーにアクセスできない場合は、システムクラスローダーを使う

		if (loader == null) {
			loader = XMLElement.class.getClassLoader();
		}
		return loader;
	}

}

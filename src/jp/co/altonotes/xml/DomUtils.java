
package jp.co.altonotes.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * DOM�p�̃��[�e�B���e�B�N���X�B
 *
 * [Copied From] org.seasar.framework.util.DomUtil
 *
 * @author higa
 *
 */
public class DomUtils {

    /**
     * �C���X�^���X���\�z���܂��B
     */
    protected DomUtils() {
    }

    /**
     * XML�̓��e�� {@link InputStream}�Ƃ��Ď擾���܂��B
     *
     * @param contents
     * @return {@link InputStream}
     */
    public static InputStream getContentsAsStream(String contents) {
        return getContentsAsStream(contents, null);
    }

    /**
     * XML�̓��e�� {@link InputStream}�Ƃ��Ď擾���܂��B
     *
     * @param contents
     * @param encoding
     * @return {@link InputStream}
     */
    public static InputStream getContentsAsStream(String contents,
            String encoding) {

        if (encoding == null) {
            return new ByteArrayInputStream(contents.getBytes());
        }
        try {
            return new ByteArrayInputStream(contents.getBytes(encoding));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * �����̒l���G���R�[�h���܂��B
     *
     * @param s
     * @return �G���R�[�h���ꂽ�l
     */
    public static String encodeAttrQuot(final String s) {
        if (s == null) {
            return null;
        }
        char[] content = s.toCharArray();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                buf.append("&lt;");
                break;
            case '>':
                buf.append("&gt;");
                break;
            case '&':
                buf.append("&amp;");
                break;
            case '"':
                buf.append("&quot;");
                break;
            default:
                buf.append(content[i]);
            }
        }
        return buf.toString();
    }

    /**
     * �e�L�X�g���G���R�[�h���܂��B
     *
     * @param s
     * @return �G���R�[�h���ꂽ�l
     */
    public static String encodeText(final String s) {
        if (s == null) {
            return null;
        }
        char[] content = s.toCharArray();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                buf.append("&lt;");
                break;
            case '>':
                buf.append("&gt;");
                break;
            case '&':
                buf.append("&amp;");
                break;
            default:
                buf.append(content[i]);
            }
        }
        return buf.toString();
    }

    /**
     * {@link Document}�𕶎���ɕϊ����܂��B
     *
     * @param document
     * @return �ϊ����ꂽ������
     */
    public static String toString(Document document) {
        StringBuffer buf = new StringBuffer();
        appendElement(document.getDocumentElement(), buf);
        return buf.toString();
    }

    /**
     * {@link Element}�𕶎���ɕϊ����܂��B
     *
     * @param element
     * @return �ϊ����ꂽ������
     */
    public static String toString(Element element) {
        StringBuffer buf = new StringBuffer();
        appendElement(element, buf);
        return buf.toString();
    }

    /**
     * {@link Element}�̕�����\����ǉ����܂��B
     *
     * @param element
     * @param buf
     */
    public static void appendElement(Element element, StringBuffer buf) {
        String tag = element.getTagName();
        buf.append('<');
        buf.append(tag);
        appendAttrs(element.getAttributes(), buf);
        buf.append('>');
        appendChildren(element.getChildNodes(), buf);
        buf.append("</");
        buf.append(tag);
        buf.append('>');
    }

    /**
     * {@link NodeList}�̕�����\����ǉ����܂��B
     *
     * @param children
     * @param buf
     */
    public static void appendChildren(NodeList children, StringBuffer buf) {
        int length = children.getLength();
        for (int i = 0; i < length; ++i) {
            appendNode(children.item(i), buf);
        }
    }

    /**
     * {@link NamedNodeMap}�̕�����\����ǉ����܂��B
     *
     * @param attrs
     * @param buf
     */
    public static void appendAttrs(NamedNodeMap attrs, StringBuffer buf) {
        int length = attrs.getLength();
        for (int i = 0; i < length; ++i) {
            Attr attr = (Attr) attrs.item(i);
            buf.append(' ');
            appendAttr(attr, buf);
        }
    }

    /**
     * {@link Attr}�̕�����\����ǉ����܂��B
     *
     * @param attr
     * @param buf
     */
    public static void appendAttr(Attr attr, StringBuffer buf) {
        buf.append(attr.getName());
        buf.append("=\"");
        buf.append(encodeAttrQuot(attr.getValue()));
        buf.append('\"');
    }

    /**
     * {@link Text}�̕�����\����ǉ����܂��B
     *
     * @param text
     * @param buf
     */
    public static void appendText(Text text, StringBuffer buf) {
        buf.append(encodeText(text.getData()));
    }

    /**
     * {@link CDATASection}�̕�����\����ǉ����܂��B
     *
     * @param cdataSection
     * @param buf
     */
    public static void appendCDATASection(CDATASection cdataSection,
            StringBuffer buf) {
        buf.append("<![CDATA[");
        buf.append(cdataSection.getData());
        buf.append("]]>");
    }

    /**
     * {@link EntityReference}��ǉ����܂��B
     *
     * @param entityReference
     * @param buf
     */
    public static void appendEntityReference(EntityReference entityReference,
            StringBuffer buf) {
        buf.append('&');
        buf.append(entityReference.getNodeName());
        buf.append(';');
    }

    /**
     * {@link Node}�̕�����\����ǉ����܂��B
     *
     * @param node
     * @param buf
     */
    public static void appendNode(Node node, StringBuffer buf) {
        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
            appendElement((Element) node, buf);
            break;
        case Node.TEXT_NODE:
            appendText((Text) node, buf);
            break;
        case Node.CDATA_SECTION_NODE:
            appendCDATASection((CDATASection) node, buf);
            break;
        case Node.ENTITY_REFERENCE_NODE:
            appendEntityReference((EntityReference) node, buf);
            break;
        }
    }
}
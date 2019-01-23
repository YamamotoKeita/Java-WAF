package jp.co.altonotes.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import jp.co.altonotes.html.HTMLData;
import jp.co.altonotes.util.TextUtils;


/**
 * HTTP�ʐM���s���N���C�A���g�B
 *
 * @author Yamamoto Keita
 *
 */
public class HttpClient {
	private CookieContainer cookieContainer = new CookieContainer();
	private String currentURL;
	private HttpResponse response;
	private String responseCharset;
	private String requestCharset;
	private byte[] byteData;
	private HTMLData htmlData;
	private int mHeaderType = RequestHeaderFactory.TYPE_UNKNOWN;
	private HashMap<String, ArrayList<String>> mParameters = new HashMap<String, ArrayList<String>>();
	private boolean mDisableSSLVerify = true;

	/**
	 * �R���X�g���N�^
	 */
	public HttpClient() {
		mHeaderType = RequestHeaderFactory.TYPE_PC_IE7;
	}

	/**
	 * �R���X�g���N�^
	 *
	 * @param headerType
	 */
	public HttpClient(int headerType) {
		mHeaderType = headerType;
	}

	/**
	 * ���P�[�V�����𒼐ڎw�肵��URL�ɃA�N�Z�X����B
	 *
	 * @param url
	 * @throws IOException
	 */
	public void accessByLocation(String url) throws IOException {
		access(url, false, null);
	}

	/**
	 * �����N����URL�ɃA�N�Z�X����B
	 * �w�b�_�Ɍ��݃y�[�W��Referer�Ƃ��ăZ�b�g�����B
	 *
	 * @param url
	 * @throws IOException
	 */
	public void accessByLink(String url) throws IOException {
		access(url, true, null);
	}

	/**
	 * �t�H�[���𑗐M����B
	 *
	 * @param url
	 * @throws IOException
	 */
	public void submitForm(String url) throws IOException {
		access(url, true, getPostParameter());
	}

	/**
	 * �t�H�[������͂���B
	 *
	 * @param name
	 * @param value
	 */
	public synchronized void fillForm(String name, String value) {
		ArrayList<String> values = mParameters.get(name);
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(value);
		mParameters.put(name, values);
	}

	/**
	 * URL�ɃA�N�Z�X����B
	 *
	 * @param url
	 * @throws IOException
	 */
	private void access(String url, boolean setReferer, byte[] postData) throws IOException {
		HttpConnector con = null;
		HttpHeader header = null;
		this.currentURL = url;

		if (url == null || url.length() == 0) {
			throw new IllegalArgumentException("url �� null �܂��͋󕶎����w�肳��܂���");
		}

		try {
			con = new HttpConnector(url);
			if (mDisableSSLVerify) {
				con.enableSSLVerify(false);
			}
			header = RequestHeaderFactory.createTemplate(mHeaderType);
			if (setReferer && url != null) {
				header.setParameter(HeaderNames.REFERER, url);
			}

			String cookieValue = null;
			if (url != null) {
				cookieValue = cookieContainer.getCookieString(url);
			}
			if (cookieValue != null) {
				header.setParameter(HeaderNames.COOKIE, cookieValue);
			}
			con.setHeader(header);
			if (postData != null) {
				this.response = con.post(postData);
			} else {
				this.response = con.get();
			}
			cookieContainer.saveCookieStrings(url, response.getCookieValues());
		} catch (MalformedURLException e) {
			throw new IOException(url + "��URL�Ƃ��ĔF���ł��Ȃ��B");
		}
		afterProcess(postData);
	}

	/**
	 * ���X�|���X�擾��̏������s���B
	 * @throws IOException
	 */
	private void afterProcess(byte[] postData) throws IOException {
		HttpHeader responseHeader = response.getHeader();

		//���_�C���N�g
		String code = responseHeader.getStatusCode();
		// 300 Multiple Choices
		// 301 Moved Permanently
		// 302 Found (Method�ύX����)
		// 303 See Other (GET)
		// 304 Not Modified
		// 305 Use Proxy (Method�ύX����)
		if (code != null && code.startsWith("3")) {
			String location = responseHeader.getParameter(HeaderNames.LOCATION);
			if (location != null) {
				if (code.equals("303") || postData == null) {
					access(location, false, null);
				} else {
					access(location, false, postData);
				}
				return;
			}
		}

		//�X�V
		String refresh = responseHeader.getRfreshURL();
		if (refresh != null) {
			String protocol = TextUtils.parseProtocol(currentURL);
			String host = TextUtils.parseHostName(currentURL, true);
			accessByLocation(protocol + "://" + host + refresh);
			return;
		}

		//HTML�擾
		byteData = response.getDecodedBody();//Content-Encoding�̏���
		responseCharset = response.getCharset();
		htmlData = new HTMLData(byteData, responseCharset);

		//�X�V
		refresh = htmlData.getMetaHeader().getRfreshURL();
		if (refresh != null) {
			String protocol = TextUtils.parseProtocol(currentURL);
			String host = TextUtils.parseHostName(currentURL, true);
			accessByLocation(protocol + "://" + host + refresh);
			return;
		}

		HttpHeader metaHeader = htmlData.getMetaHeader();

		if (metaHeader.getCharset() != null) {
			responseCharset = metaHeader.getCharset();
		}

		// HTML��META�^�O����Set-Cookie�ASet-Cookie2���擾
		cookieContainer.saveCookieStrings(currentURL, metaHeader.getResponseCookies());

		//�t�H�[���N���A
		mParameters = new HashMap<String, ArrayList<String>>();
	}

	/**
	 * SSL�ؖ����̐��������؂̗L��/������؂�ւ���B
	 * �f�t�H���g�ł͌��؂������ȏ�ԂɂȂ��Ă���B
	 *
	 * @param flag
	 */
	public void enableSSLVerify(boolean flag) {
		mDisableSSLVerify = !flag;
	}


	/**
	 * ���X�|���X�𕶎���Ƃ��Ď擾����B
	 *
	 * @return HTTP���X�|���X�̕�����
	 */
	public String getResponseString() {
		return htmlData.toString();
	}

	/**
	 * ���X�|���X���o�C�i���f�[�^�Ƃ��Ď擾����B
	 *
	 * @return HTTP���X�|���X�̃o�C�i���f�[�^
	 */
	public byte[] getBinaryData() {
		return response.getDecodedBody();
	}

	/**
	 * HttpResponse���擾����B
	 *
	 * @return HTTP���X�|���X
	 */
	public HttpResponse getResponse() {
		return response;
	}

	/**
	 * POST���M����f�[�^���擾����B
	 *
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private byte[] getPostParameter() throws UnsupportedEncodingException {
		String charset = getRequestCharset();
		if (charset == null) {
			charset = responseCharset;
			if (charset == null) {
				charset = HTMLData.DEFAULT_CHARSET;
			}
		}

		StringBuffer params = new StringBuffer();

		Set<Entry<String, ArrayList<String>>> set = mParameters.entrySet();
		for (Entry<String, ArrayList<String>> entry : set) {
			String key = URLEncoder.encode(entry.getKey(), charset);
			ArrayList<String> values = entry.getValue();
			for (String value : values) {
				params.append(key + "=" + URLEncoder.encode(value, charset) + "&");
			}
		}
		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);//�Ō��&������
		}
		return params.toString().getBytes(charset);
	}

	/**
	 * �ێ�����Cookie��z��őS�Ď擾����B
	 *
	 * @return �ێ�����Cookie�̔z��
	 */
	public HttpCookie[] getCookies() {
		return cookieContainer.getCookies();
	}

	/**
	 * @return the requestCharset
	 */
	public String getRequestCharset() {
		return requestCharset;
	}

	/**
	 * @param requestCharset the requestCharset to set
	 */
	public void setRequestCharset(String requestCharset) {
		this.requestCharset = requestCharset;
	}
}

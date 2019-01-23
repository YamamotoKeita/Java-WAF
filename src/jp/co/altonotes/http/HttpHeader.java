package jp.co.altonotes.http;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.altonotes.util.Checker;

/**
 * Http�̃w�b�_��\���N���X�B
 * ���N�G�X�g�w�b�_�A���X�|���X�w�b�_�����̋@�\�����B
 *
 * @author Yamamoto Keita
 *
 */
public class HttpHeader implements Cloneable {
	private Map<String, List<String>> valueMap;
	private int templateType = RequestHeaderFactory.TYPE_UNKNOWN;

	/**
	 * �R���X�g���N�^�B
	 */
	public HttpHeader() {
		valueMap = new LinkedHashMap<String, List<String>>();
	}


	/**
	 * �R���X�g���N�^�B
	 * �T�[�o�[���瑗��ꂽ���X�|���X�w�b�_�����ɃC���X�^���X���쐬����B
	 *
	 * @param con ���X�|���X�w�b�_���擾����HttpURLConnection�C���X�^���X
	 */
	public HttpHeader(HttpURLConnection con) {
		valueMap = con.getHeaderFields();
	}

	/**
	 * �R���X�g���N�^�B
	 * �N���C�A���g���瑗��ꂽ���N�G�X�g�w�b�_�����ɃC���X�^���X���쐬����B
	 *
	 * @param req ���N�G�X�g�w�b�_���擾����HttpServletRequest�C���X�^���X
	 */
	@SuppressWarnings("unchecked")
	public HttpHeader(HttpServletRequest req) {
		valueMap = new HashMap<String, List<String>>();
		Enumeration<String> names = req.getHeaderNames();
		String name = null;
		Enumeration<String> values = null;
		ArrayList<String> valList = null;

		while (names.hasMoreElements()) {
			name = names.nextElement();
			values = req.getHeaders(name);

			valList = new ArrayList<String>();
			while (values.hasMoreElements()) {
				valList.add(values.nextElement());
			}
			valueMap.put(name, valList);
		}
	}

	/**
	 * �w�b�_�Ƀp�����[�^��ǉ�����B
	 *
	 * @param key
	 * @param value
	 */
	public void setParameter(String key, String value) {
		ArrayList<String> valList = new ArrayList<String>();
		valList.add(value);
		remove(key);
		valueMap.put(key, valList);
	}

	/**
	 * �w�b�_�Ƀp�����[�^���X�g��ǉ�����B
	 *
	 * @param key
	 * @param valList
	 */
	public void setParameterList(String key, ArrayList<String> valList) {
		remove(key);
		valueMap.put(key, valList);
	}

	/**
	 * �w�肵���L�[�̃p�����[�^���폜����B
	 *
	 * @param key
	 */
	public void remove(String key) {
		if (key == null) {
			return;
		}
		//�L�[�̑啶���A����������ʂ����폜���邽�ߊ����L�[�ƈ������r
		Set<String> mkeys = valueMap.keySet();
		for (String mkey : mkeys) {
			if (mkey != null && mkey.equalsIgnoreCase(key)) {
				valueMap.remove(mkey);
				return;
			}
		}
	}

	/**
	 * refresh�����Ɏw�肳�ꂽ�X�V���URL���擾����B
	 *
	 * @return refresh�����Ɏw�肳�ꂽURL
	 */
	public String getRfreshURL() {
		//0; url=http://frog.raindrop.jp
		String value = getParameter(HeaderNames.REFRESH);
		if (value == null) {
			return null;
		}
		String[] params = value.split(";");
		for (String param : params) {
			param = param.trim();
			if (param.toLowerCase().startsWith("url=")) {
				return param.substring(4);
			}
		}
		return null;
	}

	/**
	 * �w�薼�̃p�����[�^���擾����B
	 * �p�����[�^���z��̏ꍇ�́A�J���}��؂�̕�����Ƃ��ĕԂ��B
	 *
	 * @param name
	 * @return �����Ɏw�肵���p�����[�^�[�̒l
	 */
	public String getParameter(String name) {
		List<String> vals = getParameterList(name);
		if (vals == null) {
			return null;
		}

		StringBuilder param = new StringBuilder(50);
		for (int i = 0; i < vals.size(); i++) {
			if (i != 0) {
				param.append(",");
			}
			param.append(vals.get(i));
		}
		return param.toString();
	}

	/**
	 * �w�薼�̃p�����[�^�ꗗ���擾����B
	 *
	 * @param name
	 * @return �����Ɏw�肵���p�����[�^�[�̃��X�g
	 */
	public List<String> getParameterList(String name) {
		if (name == null) {
			return valueMap.get(null);
		}

		//�L�[�̑啶���A����������ʂ����擾���邽�ߊ����L�[�ƈ������r
		Set<String> keys = valueMap.keySet();
		for (String key : keys) {
			if (key != null && key.equalsIgnoreCase(name)) {
				return valueMap.get(key);
			}
		}
		return null;
	}

	/**
	 * �S�Ẵp�����[�^Entry���擾����B
	 *
	 * @return �S�Ẵp�����[�^��ێ�����Entry�̔z��
	 */
	@SuppressWarnings("unchecked")
	public Entry<String, List<String>>[] getEntries() {
		Set<Entry<String, List<String>>> entrySet = valueMap.entrySet();
		Entry<String, List<String>>[] entries = new Entry[entrySet.size()];
		return entrySet.toArray(entries);
	}

	/**
	 * Content-Type��html�܂���xhtml�����肷��B
	 *
	 * @return Content-Type��html�܂���xhtml�̏ꍇ<code>true</code>
	 */
	public boolean isHtml() {
		String type = getParameter(HeaderNames.CONTENT_TYPE);
		if (type != null) {
			String[] params = type.split(";");
			for (String param : params) {
				if (param.trim().equalsIgnoreCase("text/html") || param.trim().equalsIgnoreCase("application/xhtml+xml")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Content-Type�̕����R�[�h���擾����B
	 *
	 * @return Content-Type�̕����R�[�h
	 */
	public String getCharset() {
		String type = getParameter(HeaderNames.CONTENT_TYPE);
		if (type != null) {
			String[] params = type.split(";");
			for (String param : params) {
				param = param.trim();
				//�啶���A����������ʂ������肷��
				if (param.toLowerCase().startsWith("charset=")) {
					return param.substring(8, param.length());
				}
			}
		}
		return null;
	}

	/**
	 * HTTP�̃X�e�[�^�X�R�[�h���擾����B
	 * "HTTP/1.x 200 OK"�Ȃǂ��p�[�X���ăR�[�h���������o���B
	 *
	 * @return HTTP�̃X�e�[�^�X�R�[�h
	 */
	public String getStatusCode() {
		String allStatus = getParameter(null);
		if (allStatus == null) {
			return null;
		}

		String[] statusArray = allStatus.split(" ");
		for (String status : statusArray) {
			if (status.length() == 3 && Checker.isNumber(status)) {
				return status;
			}
		}
		return null;
	}

	/**
	 * �V�X�e���ɕ�����Ƃ��ďo�͂���B
	 *
	 */
	public void print() {
		System.out.print(toString());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		String key = null;
		List<String> values = null;
		Entry<String, List<String>>[] entries = getEntries();

		for (Entry<String, List<String>> entry : entries) {
			key = entry.getKey();
			values = entry.getValue();
			for (String val : values) {
				if (key != null) {
					buf.append(key+": ");
				}
				buf.append(val);
				buf.append("\n");
			}
		}

		return buf.toString();
	}

	/**
	 * �N���C�A���g�ւ̃��X�|���X�ɂ��̃w�b�_��K�p����B
	 *
	 * @param resp
	 */
	public void write(HttpServletResponse resp) {
		String code = getStatusCode();
		//�X�e�[�^�X�R�[�h200(����I��)�ȊO�̏ꍇ
		if (code != null && !"200".equals(code)) {
			resp.setStatus(Integer.parseInt(code));
		}

		Entry<String, List<String>>[] entries = getEntries();

		for (Entry<String, List<String>> entry : entries) {
			if (entry.getKey() == null) {
				continue;
			}

			List<String> valList = entry.getValue();
			for (int i = 0; i < valList.size(); i++) {
				if (i == 0) {
					resp.setHeader(entry.getKey(), valList.get(i));
				} else {
					resp.addHeader(entry.getKey(), valList.get(i));
				}
			}
		}
	}

	/**
	 * Set-Cookie, Set-Cookie2�̒l��S�Ď��o���B
	 *
	 * @return Set-Cookie, Set-Cookie2�̒l
	 */
	public String[] getResponseCookies() {
		List<String> all = new ArrayList<String>();
		List<String> cookie1 = getParameterList(HeaderNames.SET_COOKIE);
		List<String> cookie2 = getParameterList(HeaderNames.SET_COOKIE2);

		if (cookie1 != null) {
			for (String value : cookie1) {
				all.add(value);
			}
		}
		if (cookie2 != null) {
			for (String value : cookie2) {
				all.add(value);
			}
		}

		return all.toArray(new String[all.size()]);
	}

	/**
	 * Content-Encoding�Ɏw�肳�ꂽ���X�|���X�f�[�^�̈��k�������擾����B
	 *
	 * @return Content-Encoding�Ɏw�肳�ꂽ���X�|���X�f�[�^�̈��k����
	 */
	public String getContentEncoding() {
		String encoding = getParameter(HeaderNames.CONTENT_ENCODING);
		if (encoding != null) {
			return encoding.trim();
		} else {
			return null;
		}
	}

	/**
	 * �w�b�_�̕��������擾����B
	 *
	 * @return �w�b�_�̕�����
	 */
	public int size() {
		return toString().length();
	}

	/**
	 * ���N�G�X�g�w�b�_�[�̃e���v���[�g�^�C�v���擾����B
	 *
	 * @return ���N�G�X�g�w�b�_�[�̃e���v���[�g�^�C�v
	 */
	public int getTemplateType() {
		return templateType;
	}

	/**
	 * ���N�G�X�g�w�b�_�[�̃e���v���[�g�^�C�v��ݒ肷��B<br>
	 * �e���v���[�g��ݒ肷�邱�ƂŁAIE��FireFox��g�ђ[���Ȃǂ���̃��N�G�X�g�Ɍ��������邱�Ƃ��ł���B<br>
	 * �����Ɏw�肷��l��<code>RequestHeaderFactory</code>�N���X���Q�ƁB
	 *
	 */
	protected void setTemplateType(int templateType) {
		this.templateType = templateType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public HttpHeader clone() {
		HttpHeader clone = null;

		clone = new HttpHeader();
		clone.templateType = templateType;
		
		Entry<String, List<String>>[] entries = getEntries();

		for (Entry<String, List<String>> entry : entries) {
			//�V�����[�R�s�[�Ȃ̂ł������炸
			ArrayList<String> valueList = new ArrayList<String>(entry.getValue());
			clone.setParameterList(entry.getKey(), valueList);
		}

		return clone;
	}

}

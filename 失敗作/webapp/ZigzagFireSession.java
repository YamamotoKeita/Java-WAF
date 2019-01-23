package webapp;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import jp.co.altonotes.entity.DateTime;


/**
 * フレームワーク管理のセッション。
 *
 * @author Yamamoto Keita
 *
 */
@SuppressWarnings("deprecation")
public class ZigzagFireSession implements HttpSession {

	private HashMap<String, Object> mAttributes = new HashMap<String, Object>();
	private String mId = null;
	private DateTime mLastAccessTime = null;
	private DateTime mCreationTime = null;
	private int mValidSeconds = 0;
	private boolean isNew = true;
	private boolean isValid = true;
	private ServletContext mContext = null;

	/**
	 * コンストラクター。
	 *
	 * @param id
	 * @param validMinutes
	 */
	public ZigzagFireSession(ServletContext context, String id, int validSeconds) {
		mContext = context;
		mId = id;
		mValidSeconds = validSeconds;
		mLastAccessTime = DateTime.now();
		mCreationTime = DateTime.now();
	}

	/**
	 * URLにセッションIDのクエリを付与する。
	 *
	 * @param url
	 * @param session
	 * @return
	 */
	public static String addSessionQuery(String url, HttpSession session) {
		if (url.indexOf('?') != -1) {
			url += '&' + SessionContainer.ID_NAME + "=" + session.getId();
		} else {
			url += '?' + SessionContainer.ID_NAME + "=" + session.getId();
		}
		return url;
	}

	/**
	 * 最新アクセス時間を更新する。
	 */
	public void update() {
		if (isValid) {
			mLastAccessTime = DateTime.now();
			isNew = false;
		}
	}

	/**
	 * このセッションが有効期限切れか判定する。
	 *
	 * @return
	 */
	public boolean isExpire() {
		return !isValid || DateTime.now().countSecondsFrom(mLastAccessTime) > mValidSeconds;
	}

	@Override
	public Object getAttribute(String key) {
		if (isValid) {
			return mAttributes.get(key);
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public void setAttribute(String key, Object value) {
		if (isValid) {
			mAttributes.put(key, value);
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public String getId() {
		if (isValid) {
			return mId;
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public int getMaxInactiveInterval() {
		return mValidSeconds;
	}

	@Override
	public long getLastAccessedTime() {
		if (isValid) {
			return mLastAccessTime.getTimeInMillis();
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public void removeAttribute(String key) {
		if (isValid) {
			mAttributes.remove(key);
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public void setMaxInactiveInterval(int seconds) {
		mValidSeconds = seconds;
	}

	@Override
	@Deprecated
	public Object getValue(String key) {
		if (isValid) {
			return getAttribute(key);
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	@Deprecated
	public void putValue(String key, Object value) {
		if (isValid) {
			setAttribute(key, value);
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	@Deprecated
	public void removeValue(String key) {
		if (isValid) {
			removeAttribute(key);
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public long getCreationTime() {
		if (isValid) {
			return mCreationTime.getTimeInMillis();
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getAttributeNames() {
		if (isValid) {
			Set<String> keySet = mAttributes.keySet();
			return IteratorUtils.asEnumeration(keySet.iterator());
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	@Deprecated
	public String[] getValueNames() {
		if (isValid) {
			Set<String> keySet = mAttributes.keySet();
			return keySet.toArray(new String[keySet.size()]);
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public ServletContext getServletContext() {
		return mContext;
	}

	@Override
	@Deprecated
	public HttpSessionContext getSessionContext() {
		throw new IllegalStateException("このセッション機能は実装されていません。");
	}

	@Override
	public void invalidate() {
		if (isValid) {
			mAttributes = null;
			mCreationTime = null;
			mLastAccessTime = null;
			isValid = false;
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}

	@Override
	public boolean isNew() {
		if (isValid) {
			return isNew;
		} else {
			throw new IllegalStateException("このセッションは無効化されています。");
		}
	}
}

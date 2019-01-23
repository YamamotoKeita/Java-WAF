package webapp;

import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import jp.co.altonotes.util.ThreadUtils;
import jp.co.altonotes.util.UniqueIDGenerator;

/**
 * フレームワーク管理のセッションを保持する。
 *
 * @author Yamamoto Keita
 *
 */
public class SessionContainer {
	public static String ID_NAME = "zfid";

	private ConcurrentHashMap<String, ZigzagFireSession> mSessionMap = new ConcurrentHashMap<String, ZigzagFireSession>();
	private int mValidSeconds;
	private boolean mRunFlag = true;
	private ServletContext context;
	private Thread managingThread;

	/**
	 * コンストラクター。
	 * @param validMinutes
	 */
	public SessionContainer(int validMinutes, ServletContext context) {
		mValidSeconds = validMinutes * 60;
		this.context = context;

		managingThread = new Thread(){
			public void run() {
				while (mRunFlag) {
					ArrayList<String> removeList = new ArrayList<String>(100);
					Set<Entry<String, ZigzagFireSession>> entrySet = mSessionMap.entrySet();
					for (Entry<String, ZigzagFireSession> entry : entrySet) {
						if (entry.getValue().isExpire()) {
							removeList.add(entry.getKey());
						}
					}
					for (String id : removeList) {
						mSessionMap.remove(id);
					}
					ThreadUtils.sleep(1000);
				}
			}
		};

	}

	public void start() {
		managingThread.start();
	}

	/**
	 * セッション管理を停止する。
	 */
	public void stop() {
		mRunFlag = false;
	}

	/**
	 * リクエストパラメータからセッションIDを読み込み、IDに対応したセッションを取得する。
	 *
	 * @param req
	 * @return
	 */
	public ZigzagFireSession getSession(HttpServletRequest req) {
		String id = (String) req.getParameter(ID_NAME);
		if (id != null) {
			return getSession(id);
		} else {
			return null;
		}
	}

	/**
	 * 指定したIDのセッションを取得する。
	 *
	 * @param id
	 * @return
	 */
	public ZigzagFireSession getSession(String id) {
		ZigzagFireSession session = mSessionMap.get(id);
		if (session == null) {
			return null;
		}

		if (session.isExpire()) {
			mSessionMap.remove(id);
			return null;
		}

		return session;
	}

	/**
	 * 新たなセッションを作成する。
	 *
	 * @return
	 */
	public ZigzagFireSession createSession() {
		String id = UniqueIDGenerator.getID();
		ZigzagFireSession session = new ZigzagFireSession(context, id, mValidSeconds);
		mSessionMap.put(id, session);
		return session;
	}
}

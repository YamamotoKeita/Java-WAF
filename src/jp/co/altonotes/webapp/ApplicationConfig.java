package jp.co.altonotes.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import jp.co.altonotes.context.ResourceFinder;
import jp.co.altonotes.util.Checker;
import jp.co.altonotes.webapp.annotation.Gateway;
import jp.co.altonotes.webapp.annotation.GatewayUtils;
import jp.co.altonotes.webapp.exception.ComponentException;
import jp.co.altonotes.webapp.exception.ConfigException;
import jp.co.altonotes.webapp.handler.DispatchInvoker;
import jp.co.altonotes.webapp.handler.GatewayInvoker;
import jp.co.altonotes.webapp.handler.IRequestHandler;
import jp.co.altonotes.xml.XMLElement;

import org.xml.sax.SAXException;

/**
 * WEBアプリケーションのコンフィグ
 *
 * @author Yamamoto Keita
 *
 */
public final class ApplicationConfig {

	private static final String DIRECTORY = "/WEB-INF/";
	private static final String FILE_NAME = "config.xml";
	private static final String CONFIG_PATH = DIRECTORY + FILE_NAME;

	private static final String ERROR_MESSAGE_COMPONENT = "Componentが不正です：";
	private static final String ERROR_MESSAGE_CHARSET = "<charset> の記述に誤りがあります：";
	private static final String ERROR_MESSAGE_ERRORPROCESS = "<error> の記述に誤りがあります：";
	private static final String ERROR_MESSAGE_SESSION = "<url-session> の記述に誤りがあります：";
	private static final String ERROR_MESSAGE_FILTER = "<filter> の記述に誤りがあります：";
	private static final String ERROR_MESSAGE_FORWARD = "<forward> の記述に誤りがあります：";
	private static final String ERROR_MESSAGE_STARTUP = "<startup> の記述に誤りがあります：";
	private static final String ERROR_MESSAGE_HTML_TYPE = "<html-type> の記述に誤りがあります：";

	private ServletContext context;
	private XMLElement xmlElement;

	/**
	 * コンストラクター。
	 *
	 * @param context
	 */
	protected ApplicationConfig(ServletContext context) {
		this.context = context;
		loadConfigFile(CONFIG_PATH);
	}

	/**
	 * 指定したパスよりconfigファイルを読み込む。
	 *
	 * @param filePath
	 */
	private void loadConfigFile(String filePath) {

		// コンフィグファイルの読み込み
		InputStream in = context.getResourceAsStream(filePath);

		try {
			xmlElement = XMLElement.createByInputStream(in);
		} catch (IOException e) {
			throw new ConfigException(filePath + " が読み取れません：" + e);
		} catch (SAXException e) {
			throw new ConfigException(filePath + " がXMLとしてパースできません：" + e);
		}

		loadIncludeFiles(xmlElement);
	}

	/**
	 * configファイル内のincludeファイルを読み込む。
	 *
	 * @param rootElement
	 */
	private void loadIncludeFiles(XMLElement rootElement) {

		XMLElement[] includes = rootElement.getElementsByXPath("//include");

		for (XMLElement includeTag : includes) {
			String file = includeTag.getAttribute("file");
			if (file == null || file.length() == 0) {
				throw new ConfigException("<include>にfile属性の記述がありません");
			}

			InputStream in = context.getResourceAsStream(DIRECTORY + file);

			XMLElement includeXML;

			final String message = "インクルード指定されたファイルの読み取りに失敗しました。";
			try {
				includeXML = XMLElement.createByInputStream(in);
			} catch (IOException e) {
				throw new ConfigException(message + DIRECTORY + file + " が読み取れません： " + e);
			} catch (SAXException e) {
				throw new ConfigException(message + DIRECTORY + file + " がXMLとしてパースできません： " + e);
			} catch (Exception e) {
				throw new ConfigException(message + DIRECTORY + file + " が読み取れません： " + e);
			}

			loadIncludeFiles(includeXML);
			includeTag.getParent().appendBody(includeXML);
		}
	}

	/**
	 * コンフィグに指定されたbase-packageを取得する
	 *
	 * @return
	 */
	protected String[] getBasePackages() {
		XMLElement element = getElement("base-package");
		if (element == null) {
			throw new ConfigException("設定ファイルに<base-package>の記述がありません。");
		}

		String packages = element.getTextContent();
		if (packages == null || packages.length() == 0) {
			throw new ConfigException("<base-package>の記述が空です。");
		}

		String[] packageArray = packages.split(",");

		return packageArray;
	}

	/**
	 * アノテーションよりコンポーネントを読み込む
	 */
	protected RequestMap<IRequestHandler> loadGatewayMap() {
		String[] packageArray = getBasePackages();
		ResourceFinder finder = new ResourceFinder();

		RequestMap<IRequestHandler> requestMap = new RequestMap<IRequestHandler>();
		try {
			for (String packageName : packageArray) {
				packageName = packageName.trim();

				List<Class<?>> classes = finder.getClassList(packageName);

				for (Class<?> klass : classes) {
					if (klass.isAnnotationPresent(Gateway.class)) {
						addGateway(klass, requestMap);
					}
				}
			}
		} catch (Exception e) {
			throw new ComponentException(ERROR_MESSAGE_COMPONENT + e);
		}

		return requestMap;
	}

	/**
	 * GatewayコンポーネントをRequestMapに追加する。
	 *
	 * @param klass
	 */
	private void addGateway(Class<?> klass, RequestMap<IRequestHandler> map) {

		Gateway classAnnotation = klass.getAnnotation(Gateway.class);
		String rootTargetPath = classAnnotation.path();
		String rootTargetMethod = classAnnotation.method();
		Map<String, String> rootTargetParams = GatewayUtils.parseParams(classAnnotation.params());

		// メソッドの検索
		Method[] methods = klass.getDeclaredMethods();
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Gateway.class)) {
				continue;
			}

			Gateway methodAnnotation = method.getAnnotation(Gateway.class);

			// 対象 path の取得
			String targetPath = rootTargetPath  + methodAnnotation.path();
			if (!targetPath.startsWith("/")) {
				throw new ConfigException("@Gateway の path値は \"/\" で始める必要があります：" + klass.getName() + " path=" + targetPath);
			}

			// 対象 HTTP メソッドの取得
			String targetMethod = rootTargetMethod;
			String childTargetMethod = methodAnnotation.method();
			if (childTargetMethod != null && childTargetMethod.length() > 0) {
				targetMethod = childTargetMethod;
			}

			// 対象パラメーターの取得
			Map<String, String> targetParams = GatewayUtils.parseParams(methodAnnotation.params());
			targetParams.putAll(rootTargetParams);

			// Intercept 処理の取得
			Class<? extends GatewayInterceptor> interceptClass = methodAnnotation.intercept();
			GatewayInterceptor intercepter = null;
			if (!interceptClass.isInterface()) {
				intercepter = createObject(interceptClass);
			}
			
			createObject(klass); // 確認の為一度オブジェクトを作っておく
			GatewayInvoker invoker = new GatewayInvoker(klass, method, intercepter);

			if (targetMethod.length() == 0 && targetParams.size() == 0) {
				map.add(targetPath, null, invoker);
			} else {
				RequestCondition condition = new RequestCondition(targetMethod, targetParams);
				map.add(targetPath, condition, invoker);
			}
		}
	}

	/**
	 * コンフィグに指定されたアプリケーション起動処理を行う。
	 *
	 * @param config
	 * @throws ConfigException
	 */
	protected List<StartupProcess> getStartups() {
		List<StartupProcess> list = new ArrayList<StartupProcess>();

		try {
			XMLElement[] elements = getElements("startup/process");

			for (XMLElement element : elements) {
				String className = element.getAttribute("class");
				String methodName = element.getAttribute("method");
				if (methodName == null || methodName.length() == 0) {
					methodName = "init";
				}

				Object obj = createObject(className);
				Method method = createStartupMethod(obj, element);
				Method destroyMethod = createDestroyMethod(obj);
				StartupProcess startup = new StartupProcess(obj, method, destroyMethod);

				list.add(startup);
			}
		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_STARTUP + e.getMessage());
		}
		return list;
	}

	/**
	 * デフォルトサーブレットの名前を取得する
	 */
	protected String getDefaultServletName() {
		XMLElement element = getElement("default-servlet-name");
		if (element != null && Checker.isNotBlank(element.getTextContent())) {
			return element.getTextContent();
		} else {
			return "default";
		}
	}

	/**
	 * HTML TYPEのマッピングを取得する。
	 *
	 * @param maps
	 * @throws ConfigException
	 */
	protected RequestMap<String> getHTMLTypeMap() {
		RequestMap<String> pathMap = new RequestMap<String>();

		try {
			XMLElement defaultElement = getElement("html-type/default");
			if (defaultElement != null) {
				String type = defaultElement.getAttribute("type");
				pathMap.setDefault(type);
			}

			XMLElement[] mapElements = getElements("html-type/map");

			//クラスオブジェクトの取得
			for (XMLElement mapElement : mapElements) {
				String type = mapElement.getAttribute("type");
				String path = mapElement.getAttribute("path");
				pathMap.add(path, type);
			}
		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_HTML_TYPE + e.getMessage());
		}

		return pathMap;
	}

	/**
	 * Filterのmapを取得する。
	 *
	 * @return
	 */
	protected RequestMap<IRequestHandler> getFilterMap() {
		RequestMap<IRequestHandler> pathMap = new RequestMap<IRequestHandler>();

		try {
			XMLElement[] mapElements = getElements("filter/map");

			//クラスオブジェクトの取得
			for (XMLElement mapElement : mapElements) {
				String className = getAttribute(mapElement, "class");
				Class<?> klass = createClass(className);
				Method method = createMethod(klass, mapElement);
				String path = getAttribute(mapElement, "path");

				createObject(className);// 確認の為一度オブジェクトを作成しておく
				GatewayInvoker filter = new GatewayInvoker(klass, method);
				pathMap.add(path, filter);
			}
		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_FILTER + e.getMessage());
		}
		return pathMap;
	}

	/**
	 * Viewのmapを取得する
	 *
	 * @return Requestに対するViewのマップ
	 */
	public RequestMap<IRequestHandler> getViewMap() {
		RequestMap<IRequestHandler> viewMap = new RequestMap<IRequestHandler>();

		try {
			XMLElement[] mapElements = getElements("forward/map");

			//クラスオブジェクトの取得
			for (XMLElement mapElement : mapElements) {
				String path = getAttribute(mapElement, "path");
				String page = getAttribute(mapElement, "page");

				DispatchInvoker dispatcher = new DispatchInvoker(page);
				viewMap.add(path, dispatcher);
			}
		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_FORWARD + e.getMessage());
		}
		return viewMap;
	}

	/**
	 * パスと文字セットのマッピングを設定する。
	 *
	 * @param config
	 * @throws ConfigException
	 */
	protected RequestMap<String> getCharsetMap() throws ConfigException {
		RequestMap<String> charsetMap = new RequestMap<String>();

		try {
			XMLElement defaultElement = getElement("encoding/default");

			if (defaultElement != null) {
				String defaultCharset = defaultElement.getAttribute("charset");
				if (defaultCharset == null) {
					throw new ConfigException("<default>にset属性の記述がありません");
				}
				try {
					new String(new byte[0], defaultCharset);
				} catch (UnsupportedEncodingException e) {
					throw new ConfigException(defaultCharset + " はサポートされていない文字コードセットです。");
				}

				charsetMap.setDefault(defaultCharset);
			}

			XMLElement[] mapElements = getElements("encoding/map");

			//各mapの設定
			for (XMLElement mapElement : mapElements) {
				String path = getAttribute(mapElement, "path");

				String charset = mapElement.getAttribute("charset");
				if (charset == null || charset.length() == 0) {
					throw new ConfigException("path=\"" + path + "\" にcharset属性の記述がありません");
				}

				try {
					new String(new byte[0], charset);
				} catch (UnsupportedEncodingException e) {
					throw new ConfigException(charset + "はサポートされていない文字コードセットです。");
				}

				charsetMap.add(path, charset);
			}

		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_CHARSET + e.getMessage());
		}

		return charsetMap;
	}

	/**
	 * URLSessionのMapを取得する
	 *
	 * @return Requestに対するURLセッション使用有無のマップ
	 */
	public RequestMap<Boolean> getURLSessionMap() {
		RequestMap<Boolean> uelSessionMap = new RequestMap<Boolean>();

		try {
			XMLElement[] mapElements = getElements("url-session/map");

			//各mapの設定
			for (XMLElement mapElement : mapElements) {
				String path = getAttribute(mapElement, "path");
				if (path == null || path.length() == 0) {
					throw new ConfigException("pathの記述がありません");
				}
				uelSessionMap.add(path, true);
			}

		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_SESSION + e.getMessage());
		}

		return uelSessionMap;
	}

	/**
	 * パスとエラーページのマッピングを設定する。
	 * @param config
	 */
	protected RequestMap<IRequestHandler> getErrorHandlerMap() {
		RequestMap<IRequestHandler> errorMap = new RequestMap<IRequestHandler>();

		try {
			// デフォルトのエラー設定を取得
			XMLElement defaultElement = getElement("error/default");
			if (defaultElement != null) {
				String className = defaultElement.getAttribute("class");
				String pageName = defaultElement.getAttribute("page");

				if (Checker.isEmpty(className) && Checker.isEmpty(pageName)) {
					throw new ConfigException("<default> にclass属性とpage属性のどちらも記述がありません");
				} else if (!Checker.isEmpty(className) && !Checker.isEmpty(pageName)) {
					throw new ConfigException("<default> にclass属性とpage属性の両方を記述することはできません");
				}

				IRequestHandler defaultError = null;
				if (Checker.isNotEmpty(className)) {
					Class<?> klass = createClass(className);
					Method method = createMethod(klass, defaultElement);
					createObject(className); // 確認の為一度オブジェクトを作成しておく
					defaultError = new GatewayInvoker(klass, method);
				} else {
					defaultError = new DispatchInvoker(pageName);
				}

				errorMap.setDefault(defaultError);
			}

			// 特定パスに設定されたエラー設定を取得
			XMLElement[] maps = getElements("error/map");
			for (XMLElement map : maps) {
				String className = map.getAttribute("class");
				String pageName = map.getAttribute("page");
				if (Checker.isEmpty(className) && Checker.isEmpty(pageName)) {
					throw new ConfigException("<map> にclass属性とpage属性のどちらも記述がありません");
				} else if (!Checker.isEmpty(className) && !Checker.isEmpty(pageName)) {
					throw new ConfigException("<map> にclass属性とpage属性の両方を記述することはできません");
				}

				IRequestHandler error = null;
				if (Checker.isNotEmpty(className)) {
					Class<?> klass = createClass(className);
					Method method = createMethod(klass, map);
					createObject(className); // 確認の為、一度オブジェクトを作成しておく。
					error = new GatewayInvoker(klass, method);
				} else {
					error = new DispatchInvoker(pageName);
				}
				String path = getAttribute(map, "path");
				errorMap.add(path, error);
			}
		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_ERRORPROCESS + e.getMessage());
		}

		return errorMap;
	}


	/**
	 * パスに対応したconfig要素を取得する。
	 *
	 * @param xpath
	 * @return
	 */
	private XMLElement getElement(String xpath) {
		return xmlElement.getElementByXPath("/config/" + xpath);
	}

	/**
	 * パスに対応した複数のconfig要素を取得する。
	 *
	 * @param xpath
	 * @return
	 */
	private XMLElement[] getElements(String xpath) {
		return xmlElement.getElementsByXPath("/config/" + xpath);
	}

	private String getAttribute(XMLElement element, String name) {
		String attribute = element.getAttribute(name);
		if (attribute == null || attribute.length() == 0) {
			throw new ConfigException("<" + element.getTagName()+ "> に " + name + " 属性の記述がありません");
		}
		return attribute;

	}

	/**
	 * 終了処理用のメソッドを取得する。
	 *
	 * @param obj
	 * @param element
	 * @return
	 */
	private Method createDestroyMethod(Object obj) {
		try {
			return obj.getClass().getMethod("destroy");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Startup用のメソッドを取得する。
	 *
	 * @param obj
	 * @param element
	 * @return
	 */
	private Method createStartupMethod(Object obj, XMLElement element) {
		String methodName = element.getAttribute("method");
		if(methodName == null || methodName.length() == 0) {
			return null;
		}
		try {
			return obj.getClass().getMethod(methodName);
		} catch (Exception e) {
			throw (ConfigException) new ConfigException("メソッド " + obj.getClass().getName() + "#" + methodName + " が取得できませんでした：" + e).initCause(e);
		}
	}

	/**
	 * Gatewayが実行するメソッドを取得する。
	 *
	 * @param klass
	 * @param element
	 * @return
	 */
	private Method createMethod(Class<?> klass, XMLElement element) {
		String methodName = element.getAttribute("method");
		if(methodName == null || methodName.length() == 0) {
			return null;
		}
		try {
			return getMethod(klass, methodName);
		} catch (Exception e) {
			throw (ConfigException) new ConfigException("メソッド " + klass.getName() + "#" + methodName + " が取得できませんでした：" + e).initCause(e);
		}
	}

	/**
	 * メソッド名に対応したメソッドを取得する。
	 *
	 * @param klass
	 * @param name
	 * @return メソッド名に対応したメソッド
	 * @throws NoSuchMethodException
	 */
	private static Method getMethod(Class<?> klass, String name) throws NoSuchMethodException {
		if (name == null) {
			return null;
		} else {
			return klass.getMethod(name, new Class<?>[]{});
		}
	}

	/**
	 * 指定したクラス名のインスタンスを取得する。
	 *
	 * @param element
	 * @return
	 */
	private Object createObject(String className) {
		//classの作成
		Class<?> klass = createClass(className);
		return createObject(klass);
	}

	/**
	 * 指定したクラス名のクラスを取得する
	 *
	 * @param className
	 * @return
	 */
	private Class<?> createClass(String className) {
		//classの作成
		Class<?> klass = null;
		try {
			klass = getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ConfigException("クラス " + className + " が見つかりません");
		}

		return klass;
	}

	/**
	 * 指定したクラスのインスタンスを取得する。
	 *
	 * @param element
	 * @return
	 */
	private static <T> T createObject(Class<T> klass) {
		//objectの作成
		T obj = null;
		try {
			obj = klass.newInstance();
		} catch (InstantiationException e) {
			throw new ConfigException(klass.getName() + "のインスタンスが作成できません。");
		} catch (IllegalAccessException e) {
			throw new ConfigException(klass.getName() + "のコンストラクターにアクセスできません");
		} catch (Exception e) {
			throw new ConfigException(klass.getName() + "のコンストラクターでエラーが発生した可能性があります。" + e);
		}

		return obj;
	}

	/**
	 * @return クラスローダー
	 */
	private ClassLoader getClassLoader() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) { }// スレッドのコンテキストクラスローダーにアクセスできない場合は、システムクラスローダーを使う

		if (loader == null) {
			loader = ApplicationConfig.class.getClassLoader();
		}
		return loader;
	}


}

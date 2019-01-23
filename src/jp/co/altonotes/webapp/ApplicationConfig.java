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
 * WEB�A�v���P�[�V�����̃R���t�B�O
 *
 * @author Yamamoto Keita
 *
 */
public final class ApplicationConfig {

	private static final String DIRECTORY = "/WEB-INF/";
	private static final String FILE_NAME = "config.xml";
	private static final String CONFIG_PATH = DIRECTORY + FILE_NAME;

	private static final String ERROR_MESSAGE_COMPONENT = "Component���s���ł��F";
	private static final String ERROR_MESSAGE_CHARSET = "<charset> �̋L�q�Ɍ�肪����܂��F";
	private static final String ERROR_MESSAGE_ERRORPROCESS = "<error> �̋L�q�Ɍ�肪����܂��F";
	private static final String ERROR_MESSAGE_SESSION = "<url-session> �̋L�q�Ɍ�肪����܂��F";
	private static final String ERROR_MESSAGE_FILTER = "<filter> �̋L�q�Ɍ�肪����܂��F";
	private static final String ERROR_MESSAGE_FORWARD = "<forward> �̋L�q�Ɍ�肪����܂��F";
	private static final String ERROR_MESSAGE_STARTUP = "<startup> �̋L�q�Ɍ�肪����܂��F";
	private static final String ERROR_MESSAGE_HTML_TYPE = "<html-type> �̋L�q�Ɍ�肪����܂��F";

	private ServletContext context;
	private XMLElement xmlElement;

	/**
	 * �R���X�g���N�^�[�B
	 *
	 * @param context
	 */
	protected ApplicationConfig(ServletContext context) {
		this.context = context;
		loadConfigFile(CONFIG_PATH);
	}

	/**
	 * �w�肵���p�X���config�t�@�C����ǂݍ��ށB
	 *
	 * @param filePath
	 */
	private void loadConfigFile(String filePath) {

		// �R���t�B�O�t�@�C���̓ǂݍ���
		InputStream in = context.getResourceAsStream(filePath);

		try {
			xmlElement = XMLElement.createByInputStream(in);
		} catch (IOException e) {
			throw new ConfigException(filePath + " ���ǂݎ��܂���F" + e);
		} catch (SAXException e) {
			throw new ConfigException(filePath + " ��XML�Ƃ��ăp�[�X�ł��܂���F" + e);
		}

		loadIncludeFiles(xmlElement);
	}

	/**
	 * config�t�@�C������include�t�@�C����ǂݍ��ށB
	 *
	 * @param rootElement
	 */
	private void loadIncludeFiles(XMLElement rootElement) {

		XMLElement[] includes = rootElement.getElementsByXPath("//include");

		for (XMLElement includeTag : includes) {
			String file = includeTag.getAttribute("file");
			if (file == null || file.length() == 0) {
				throw new ConfigException("<include>��file�����̋L�q������܂���");
			}

			InputStream in = context.getResourceAsStream(DIRECTORY + file);

			XMLElement includeXML;

			final String message = "�C���N���[�h�w�肳�ꂽ�t�@�C���̓ǂݎ��Ɏ��s���܂����B";
			try {
				includeXML = XMLElement.createByInputStream(in);
			} catch (IOException e) {
				throw new ConfigException(message + DIRECTORY + file + " ���ǂݎ��܂���F " + e);
			} catch (SAXException e) {
				throw new ConfigException(message + DIRECTORY + file + " ��XML�Ƃ��ăp�[�X�ł��܂���F " + e);
			} catch (Exception e) {
				throw new ConfigException(message + DIRECTORY + file + " ���ǂݎ��܂���F " + e);
			}

			loadIncludeFiles(includeXML);
			includeTag.getParent().appendBody(includeXML);
		}
	}

	/**
	 * �R���t�B�O�Ɏw�肳�ꂽbase-package���擾����
	 *
	 * @return
	 */
	protected String[] getBasePackages() {
		XMLElement element = getElement("base-package");
		if (element == null) {
			throw new ConfigException("�ݒ�t�@�C����<base-package>�̋L�q������܂���B");
		}

		String packages = element.getTextContent();
		if (packages == null || packages.length() == 0) {
			throw new ConfigException("<base-package>�̋L�q����ł��B");
		}

		String[] packageArray = packages.split(",");

		return packageArray;
	}

	/**
	 * �A�m�e�[�V�������R���|�[�l���g��ǂݍ���
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
	 * Gateway�R���|�[�l���g��RequestMap�ɒǉ�����B
	 *
	 * @param klass
	 */
	private void addGateway(Class<?> klass, RequestMap<IRequestHandler> map) {

		Gateway classAnnotation = klass.getAnnotation(Gateway.class);
		String rootTargetPath = classAnnotation.path();
		String rootTargetMethod = classAnnotation.method();
		Map<String, String> rootTargetParams = GatewayUtils.parseParams(classAnnotation.params());

		// ���\�b�h�̌���
		Method[] methods = klass.getDeclaredMethods();
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Gateway.class)) {
				continue;
			}

			Gateway methodAnnotation = method.getAnnotation(Gateway.class);

			// �Ώ� path �̎擾
			String targetPath = rootTargetPath  + methodAnnotation.path();
			if (!targetPath.startsWith("/")) {
				throw new ConfigException("@Gateway �� path�l�� \"/\" �Ŏn�߂�K�v������܂��F" + klass.getName() + " path=" + targetPath);
			}

			// �Ώ� HTTP ���\�b�h�̎擾
			String targetMethod = rootTargetMethod;
			String childTargetMethod = methodAnnotation.method();
			if (childTargetMethod != null && childTargetMethod.length() > 0) {
				targetMethod = childTargetMethod;
			}

			// �Ώۃp�����[�^�[�̎擾
			Map<String, String> targetParams = GatewayUtils.parseParams(methodAnnotation.params());
			targetParams.putAll(rootTargetParams);

			// Intercept �����̎擾
			Class<? extends GatewayInterceptor> interceptClass = methodAnnotation.intercept();
			GatewayInterceptor intercepter = null;
			if (!interceptClass.isInterface()) {
				intercepter = createObject(interceptClass);
			}
			
			createObject(klass); // �m�F�̈׈�x�I�u�W�F�N�g������Ă���
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
	 * �R���t�B�O�Ɏw�肳�ꂽ�A�v���P�[�V�����N���������s���B
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
	 * �f�t�H���g�T�[�u���b�g�̖��O���擾����
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
	 * HTML TYPE�̃}�b�s���O���擾����B
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

			//�N���X�I�u�W�F�N�g�̎擾
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
	 * Filter��map���擾����B
	 *
	 * @return
	 */
	protected RequestMap<IRequestHandler> getFilterMap() {
		RequestMap<IRequestHandler> pathMap = new RequestMap<IRequestHandler>();

		try {
			XMLElement[] mapElements = getElements("filter/map");

			//�N���X�I�u�W�F�N�g�̎擾
			for (XMLElement mapElement : mapElements) {
				String className = getAttribute(mapElement, "class");
				Class<?> klass = createClass(className);
				Method method = createMethod(klass, mapElement);
				String path = getAttribute(mapElement, "path");

				createObject(className);// �m�F�̈׈�x�I�u�W�F�N�g���쐬���Ă���
				GatewayInvoker filter = new GatewayInvoker(klass, method);
				pathMap.add(path, filter);
			}
		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_FILTER + e.getMessage());
		}
		return pathMap;
	}

	/**
	 * View��map���擾����
	 *
	 * @return Request�ɑ΂���View�̃}�b�v
	 */
	public RequestMap<IRequestHandler> getViewMap() {
		RequestMap<IRequestHandler> viewMap = new RequestMap<IRequestHandler>();

		try {
			XMLElement[] mapElements = getElements("forward/map");

			//�N���X�I�u�W�F�N�g�̎擾
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
	 * �p�X�ƕ����Z�b�g�̃}�b�s���O��ݒ肷��B
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
					throw new ConfigException("<default>��set�����̋L�q������܂���");
				}
				try {
					new String(new byte[0], defaultCharset);
				} catch (UnsupportedEncodingException e) {
					throw new ConfigException(defaultCharset + " �̓T�|�[�g����Ă��Ȃ������R�[�h�Z�b�g�ł��B");
				}

				charsetMap.setDefault(defaultCharset);
			}

			XMLElement[] mapElements = getElements("encoding/map");

			//�emap�̐ݒ�
			for (XMLElement mapElement : mapElements) {
				String path = getAttribute(mapElement, "path");

				String charset = mapElement.getAttribute("charset");
				if (charset == null || charset.length() == 0) {
					throw new ConfigException("path=\"" + path + "\" ��charset�����̋L�q������܂���");
				}

				try {
					new String(new byte[0], charset);
				} catch (UnsupportedEncodingException e) {
					throw new ConfigException(charset + "�̓T�|�[�g����Ă��Ȃ������R�[�h�Z�b�g�ł��B");
				}

				charsetMap.add(path, charset);
			}

		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_CHARSET + e.getMessage());
		}

		return charsetMap;
	}

	/**
	 * URLSession��Map���擾����
	 *
	 * @return Request�ɑ΂���URL�Z�b�V�����g�p�L���̃}�b�v
	 */
	public RequestMap<Boolean> getURLSessionMap() {
		RequestMap<Boolean> uelSessionMap = new RequestMap<Boolean>();

		try {
			XMLElement[] mapElements = getElements("url-session/map");

			//�emap�̐ݒ�
			for (XMLElement mapElement : mapElements) {
				String path = getAttribute(mapElement, "path");
				if (path == null || path.length() == 0) {
					throw new ConfigException("path�̋L�q������܂���");
				}
				uelSessionMap.add(path, true);
			}

		} catch (Exception e) {
			throw new ConfigException(ERROR_MESSAGE_SESSION + e.getMessage());
		}

		return uelSessionMap;
	}

	/**
	 * �p�X�ƃG���[�y�[�W�̃}�b�s���O��ݒ肷��B
	 * @param config
	 */
	protected RequestMap<IRequestHandler> getErrorHandlerMap() {
		RequestMap<IRequestHandler> errorMap = new RequestMap<IRequestHandler>();

		try {
			// �f�t�H���g�̃G���[�ݒ���擾
			XMLElement defaultElement = getElement("error/default");
			if (defaultElement != null) {
				String className = defaultElement.getAttribute("class");
				String pageName = defaultElement.getAttribute("page");

				if (Checker.isEmpty(className) && Checker.isEmpty(pageName)) {
					throw new ConfigException("<default> ��class������page�����̂ǂ�����L�q������܂���");
				} else if (!Checker.isEmpty(className) && !Checker.isEmpty(pageName)) {
					throw new ConfigException("<default> ��class������page�����̗������L�q���邱�Ƃ͂ł��܂���");
				}

				IRequestHandler defaultError = null;
				if (Checker.isNotEmpty(className)) {
					Class<?> klass = createClass(className);
					Method method = createMethod(klass, defaultElement);
					createObject(className); // �m�F�̈׈�x�I�u�W�F�N�g���쐬���Ă���
					defaultError = new GatewayInvoker(klass, method);
				} else {
					defaultError = new DispatchInvoker(pageName);
				}

				errorMap.setDefault(defaultError);
			}

			// ����p�X�ɐݒ肳�ꂽ�G���[�ݒ���擾
			XMLElement[] maps = getElements("error/map");
			for (XMLElement map : maps) {
				String className = map.getAttribute("class");
				String pageName = map.getAttribute("page");
				if (Checker.isEmpty(className) && Checker.isEmpty(pageName)) {
					throw new ConfigException("<map> ��class������page�����̂ǂ�����L�q������܂���");
				} else if (!Checker.isEmpty(className) && !Checker.isEmpty(pageName)) {
					throw new ConfigException("<map> ��class������page�����̗������L�q���邱�Ƃ͂ł��܂���");
				}

				IRequestHandler error = null;
				if (Checker.isNotEmpty(className)) {
					Class<?> klass = createClass(className);
					Method method = createMethod(klass, map);
					createObject(className); // �m�F�ׁ̈A��x�I�u�W�F�N�g���쐬���Ă����B
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
	 * �p�X�ɑΉ�����config�v�f���擾����B
	 *
	 * @param xpath
	 * @return
	 */
	private XMLElement getElement(String xpath) {
		return xmlElement.getElementByXPath("/config/" + xpath);
	}

	/**
	 * �p�X�ɑΉ�����������config�v�f���擾����B
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
			throw new ConfigException("<" + element.getTagName()+ "> �� " + name + " �����̋L�q������܂���");
		}
		return attribute;

	}

	/**
	 * �I�������p�̃��\�b�h���擾����B
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
	 * Startup�p�̃��\�b�h���擾����B
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
			throw (ConfigException) new ConfigException("���\�b�h " + obj.getClass().getName() + "#" + methodName + " ���擾�ł��܂���ł����F" + e).initCause(e);
		}
	}

	/**
	 * Gateway�����s���郁�\�b�h���擾����B
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
			throw (ConfigException) new ConfigException("���\�b�h " + klass.getName() + "#" + methodName + " ���擾�ł��܂���ł����F" + e).initCause(e);
		}
	}

	/**
	 * ���\�b�h���ɑΉ��������\�b�h���擾����B
	 *
	 * @param klass
	 * @param name
	 * @return ���\�b�h���ɑΉ��������\�b�h
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
	 * �w�肵���N���X���̃C���X�^���X���擾����B
	 *
	 * @param element
	 * @return
	 */
	private Object createObject(String className) {
		//class�̍쐬
		Class<?> klass = createClass(className);
		return createObject(klass);
	}

	/**
	 * �w�肵���N���X���̃N���X���擾����
	 *
	 * @param className
	 * @return
	 */
	private Class<?> createClass(String className) {
		//class�̍쐬
		Class<?> klass = null;
		try {
			klass = getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ConfigException("�N���X " + className + " ��������܂���");
		}

		return klass;
	}

	/**
	 * �w�肵���N���X�̃C���X�^���X���擾����B
	 *
	 * @param element
	 * @return
	 */
	private static <T> T createObject(Class<T> klass) {
		//object�̍쐬
		T obj = null;
		try {
			obj = klass.newInstance();
		} catch (InstantiationException e) {
			throw new ConfigException(klass.getName() + "�̃C���X�^���X���쐬�ł��܂���B");
		} catch (IllegalAccessException e) {
			throw new ConfigException(klass.getName() + "�̃R���X�g���N�^�[�ɃA�N�Z�X�ł��܂���");
		} catch (Exception e) {
			throw new ConfigException(klass.getName() + "�̃R���X�g���N�^�[�ŃG���[�����������\��������܂��B" + e);
		}

		return obj;
	}

	/**
	 * @return �N���X���[�_�[
	 */
	private ClassLoader getClassLoader() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) { }// �X���b�h�̃R���e�L�X�g�N���X���[�_�[�ɃA�N�Z�X�ł��Ȃ��ꍇ�́A�V�X�e���N���X���[�_�[���g��

		if (loader == null) {
			loader = ApplicationConfig.class.getClassLoader();
		}
		return loader;
	}


}

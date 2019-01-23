package jp.co.altonotes.context;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * �w�肵���p�b�P�[�W���̃��\�[�X�A�N���X���擾����
 *
 * @author Yamamoto Keita
 *
 */
public class ResourceFinder {

	/** �t�@�C���V�X�e����URL�v���t�B�b�N�X */
	public static final String FILE_URL_PREFIX = "file:";

	/** �t�@�C���V�X�e����URL�v���g�R�� */
	public static final String URL_PROTOCOL_FILE = "file";

	/** JAR�G���g���[��URL�v���g�R�� */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** ZIP�G���g���[��URL�v���g�R�� */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** JBoss��JAR�t�@�C����URL�v���g�R�� */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/** JBoss��VFS���\�[�X��URL�v���g�R�� */
	public static final String URL_PROTOCOL_VFS = "vfs";

	/** WebSphere��JAR�t�@�C����URL�v���g�R�� */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** Oracle Containers for J2EE�iOC4J�j��JAR�t�@�C����URL�v���g�R�� */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/** JAR����JAR URL �ƃt�@�C���p�X�̃Z�p���[�^�[ */
	public static final String JAR_URL_SEPARATOR = "!/";

	/** Equinox OSGi��"bundleresource:"�A"bundleentry:" �`����URL�̃��]���o�[ */
	private static Method equinoxResolveMethod;

	static {
		// OSGi��bundle URL��resolver��T��(ex. WebSphere 6.1)
		try {
			Class<?> fileLocatorClass = ResourceFinder.class.getClassLoader().loadClass("org.eclipse.core.runtime.FileLocator");
			equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
		} catch (Throwable ex) {
			equinoxResolveMethod = null;
		}
	}

	/**
	 * �w�肵���p�b�P�[�W�ȉ��̃N���X���T�u�p�b�P�[�W���܂ߑS�Ď擾����B
	 * TODO �N���X���擾������static�̏���������A������Exception����������Ɨ�����B
	 *
	 * @param packageName
	 * @return �N���X��List
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 */
	public List<Class<?>> getClassList(String packageName) throws IOException, ClassNotFoundException {
		List<URI> resources = findResources(packageName, "class");
		String packagePath = packageToResourcePath(packageName);

		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

		int idx;
		String relativePath;
		String resourcePath;
		for (URI uri : resources) {
			resourcePath = uri.toString();
			idx = resourcePath.lastIndexOf(packagePath);
			relativePath = resourcePath.substring(idx + packagePath.length());

			if (relativePath.contains("$")) {//TODO �C���i�[�N���X�̓X�L�b�v
				continue;
			}

			relativePath = relativePath.substring(0, relativePath.length() -6);// .class���폜
			relativePath = relativePath.replace('/', '.');

			String className = packageName + "." + relativePath;
			Class<?> klass = null;
			try {
				klass = getClassLoader().loadClass(className);

				//getResurces�ɂ���ē����N���X���d�����ē���\�������邽�߃`�F�b�N
				if (!classMap.containsKey(className)) {
					classMap.put(className, klass);
				}
			} catch (Throwable e) {//TODO static�������q�̃G���[���ǂ�����H
				e.printStackTrace();
			}
		}

		List<Class<?>> classList = new ArrayList<Class<?>>();
		Collection<Class<?>> collection = classMap.values();
		for (Class<?> class1 : collection) {
			classList.add(class1);
		}

		return classList;
	}

	/**
	 * �w�肵���p�b�P�[�W���́A�w�肵���g���q�̑S�Ẵ��\�[�X��URI���擾����
	 *
	 * @param rootPackage
	 * @param extention
	 * @return �Y������URI��LIST
	 * @throws IOException
	 */
	public List<URI> findResources(String rootPackage, String extention) throws IOException {

		String path = packageToResourcePath(rootPackage);
		List<URL> rootDirResources = findResources(path);

		List<URI> result = new ArrayList<URI>(16);
		for (URL rootDirResource : rootDirResources) {
			rootDirResource = resolveBundleResource(rootDirResource);

			if (isJarURL(rootDirResource)) {
				result.addAll(findJarResources(rootDirResource, extention));
			} else if (isVFSURL(rootDirResource)) {
				result.addAll(findVFSResources(rootDirResource, extention));
			} else {
				result.addAll(findFileSystemResources(rootDirResource, extention));
			}
		}
		return result;

	}

	/**
	 * �w�肵���p�X�̃��\�[�X��S�Ď擾����B
	 *
	 * @param path
	 * @return �w�肵���p�X�̃��\�[�X��URL��List
	 * @throws IOException
	 */
	public List<URL> findResources(String path) throws IOException {
		Enumeration<URL> resourceUrls = getClassLoader().getResources(path);
		List<URL> list = new ArrayList<URL>(16);
		while (resourceUrls.hasMoreElements()) {
			URL url = resourceUrls.nextElement();
			list.add(url);
		}
		return list;
	}

	/**
	 * ���[�gURL�ȉ��̎w��g���q��URI��S�Ď擾����B
	 *
	 * @param rootURL
	 * @param extention
	 * @return
	 */
	protected List<URI> findFileSystemResources(URL rootURL, String extention) {

		List<URI> result = new ArrayList<URI>();

		File rootDir = null;
		try {
			rootDir = new File(toURI(rootURL).getSchemeSpecificPart()).getAbsoluteFile();
		} catch (URISyntaxException e) {
			return result;
		}

		if (!rootDir.exists()) {
			return result;
		}
		//���݂��邪�f�B���N�g������Ȃ��ꍇ�̓t�@�b�L���O�V�b�g�B
		if (!rootDir.isDirectory()) {
			return result;
		}
		if (!rootDir.canRead()) {
			return result;
		}

		findMatchingFiles(rootDir, extention, result);

		return result;
	}

	/**
	 * ���[�g�f�B���N�g���ȉ��̎w��g���q��URI��S�Ď擾����B
	 *
	 * @param dir
	 * @param extention
	 * @param result
	 */
	protected void findMatchingFiles(File dir, String extention, List<URI> result) {
		File[] dirContents = dir.listFiles();
		if (dirContents == null) {
			return;
		}
		for (File content : dirContents) {
			if (content.isDirectory()) {
				if (content.canRead()) {
					findMatchingFiles(content, extention, result);
				}
			} else if (content.isFile() && extentionMath(content, extention)) {
				result.add(content.toURI());
			}
		}
	}

	/**
	 * JAR�t�@�C�����̃��\�[�X���擾����B
	 *
	 * @param rootURL
	 * @param extention
	 * @return
	 * @throws IOException
	 */
	protected List<URI> findJarResources(URL rootURL, String extention) throws IOException {

		URLConnection con = rootURL.openConnection();
		JarFile jarFile;
		String jarFileUrl;
		String rootEntryPath;
		boolean newJarFile = false;

		if (con instanceof JarURLConnection) { //���ʂ�JAR�t�@�C���̏ꍇ
			JarURLConnection jarCon = (JarURLConnection) con;
			jarCon.setUseCaches(false);
			jarFile = jarCon.getJarFile();
			JarEntry jarEntry = jarCon.getJarEntry();
			rootEntryPath = jarEntry != null ? jarEntry.getName() : "";
		} else { //JarURLConnection�������ꍇ
			String urlFile = rootURL.getFile();
			int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
			if (separatorIndex != -1) { // "jar:path!/entry"�t�H�[�}�b�g�̏ꍇ
				jarFileUrl = urlFile.substring(0, separatorIndex);
				rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
				jarFile = getJarFile(jarFileUrl);
			} else { //���̑��i"file:"�v���t�B�b�N�X������ꍇ�A�����ꍇ�������܂ށj
				jarFile = new JarFile(urlFile);
				rootEntryPath = "";
			}
			newJarFile = true;
		}

		try {
			// �K�؂ȃ}�b�`���O���s�����߃��[�g�G���g���[�p�X�̓X���b�V���ŏI���K�v������B
			// BEA JRockit�̓X���b�V����t���邪�ASun��JRE�̓X���b�V����t���Ȃ����߂����ŉ�����B
			if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
				rootEntryPath = rootEntryPath + "/";
			}

			List<URI> result = new ArrayList<URI>(8);
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				String entryPath = entry.getName();
				if (entryPath.startsWith(rootEntryPath)) {
					String relativePath = entryPath.substring(rootEntryPath.length());
					if (extentionMatch(relativePath, extention)) {
						URL url = createRelative(rootURL, relativePath);
						try {
							result.add(toURI(url));
						} catch (URISyntaxException e) {
						}
					}
				}
			}
			return result;
		} finally {
			// JarURLConnection�̓t�@�C���̎Q�Ƃ��L���b�V�����邩������Ȃ��̂ŁA
			// JarURLConnection���g�킸JarFile�C���X�^���X��V�K�쐬�����ꍇ�̂݃N���[�Y����B
			if (newJarFile) {
				jarFile.close();
			}
		}
	}

	/**
	 * VFS�̃��\�[�X���擾����B
	 *
	 * @param rootURL
	 * @param extention
	 * @return
	 * @throws IOException
	 */
	private List<URI> findVFSResources(URL rootURL, String extention) throws IOException {
		Object root = VfsPatternUtils.findRoot(rootURL);
		PatternVirtualFileVisitor visitor = new PatternVirtualFileVisitor(VfsPatternUtils.getPath(root), extention);
		VfsPatternUtils.visit(root, visitor);
		return visitor.getResources();
	}


	/**
	 * @return �N���X���[�_�[
	 */
	public ClassLoader getClassLoader() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) { }// �X���b�h�̃R���e�L�X�g�N���X���[�_�[�ɃA�N�Z�X�ł��Ȃ��ꍇ�́A�V�X�e���N���X���[�_�[���g��

		if (loader == null) {
			loader = ResourceFinder.class.getClassLoader();
		}
		return loader;
	}

	/**
	 * VFS visitor for path matching purposes.
	 */
	private static class PatternVirtualFileVisitor implements InvocationHandler {

		private final String extention;

		private final String rootPath;

		private final List<URI> resources = new ArrayList<URI>();

		public PatternVirtualFileVisitor(String rootPath, String extention) {
			this.extention = extention;
			this.rootPath = (rootPath.length() == 0 || rootPath.endsWith("/") ? rootPath : rootPath + "/");
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (Object.class.equals(method.getDeclaringClass())) {
				if (methodName.equals("equals")) {
					// Only consider equal when proxies are identical.
					return (proxy == args[0]);
				} else if (methodName.equals("hashCode")) {
					return System.identityHashCode(proxy);
				}
			} else if ("getAttributes".equals(methodName)) {
				return getAttributes();
			} else if ("visit".equals(methodName)) {
				visit(args[0]);
				return null;
			} else if ("toString".equals(methodName)) {
				return toString();
			}

			throw new IllegalStateException("Unexpected method invocation: " + method);
		}

		public void visit(Object vfsResource) throws IOException {
			String path = VfsPatternUtils.getPath(vfsResource).substring(this.rootPath.length());
			if (extentionMatch(path, extention)) {
				URL url = VfsUtils.getURL(vfsResource);
				try {
					resources.add(toURI(url));
				} catch (URISyntaxException e) {
				}
			}
		}

		public Object getAttributes() {
			return VfsPatternUtils.getVisitorAttribute();
		}


		public List<URI> getResources() {
			return this.resources;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("sub-pattern: ").append(this.extention);
			sb.append(", resources: ").append(this.resources);
			return sb.toString();
		}
	}

	/**
	 * �w�肵���g���q�������Ă��邩���肷��B
	 *
	 * @param relativePath
	 * @param extention
	 * @return
	 */
	private static boolean extentionMatch(String relativePath, String extention) {
		return relativePath.endsWith(extention);
	}

	/**
	 * �w�肵���g���q�������Ă��邩���肷��B
	 *
	 * @param content
	 * @param extention
	 * @return
	 */
	private static boolean extentionMath(File content, String extention) {
		return content.isFile() && content.getName().endsWith(extention);
	}

	/**
	 * ���[�gURL�Ƒ��΃p�X���URL���쐬����
	 *
	 * @param rootURL
	 * @param relativePath
	 * @return ���΃p�X
	 * @throws MalformedURLException
	 */
	public static URL createRelative(URL rootURL, String relativePath) throws MalformedURLException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		return new URL(rootURL, relativePath);
	}

	/**
	 * Equinox OSGi��"bundleresource:"�A"bundleentry:" �`����URL��W��jar�t�@�C����URL�ɕϊ�����
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	protected static URL resolveBundleResource(URL url) {
		if (equinoxResolveMethod != null) {
			if (url.getProtocol().startsWith("bundle")) {

				try {
					Object returnValue = equinoxResolveMethod.invoke(null, url);
					return (URL) returnValue;
				} catch (InvocationTargetException e) {
					throw (IllegalStateException) new IllegalStateException().initCause(e.getCause());
				} catch (Exception e) {
					throw (IllegalStateException) new IllegalStateException().initCause(e);
				}
			}
		}
		return url;
	}

	/**
	 * �p�b�P�[�W�������\�[�X�p�X�ɕϊ�����
	 *
	 * @param packageName
	 * @return ���\�[�X�p�X
	 */
	public static String packageToResourcePath(String packageName) {
		return packageName.replace('.', '/') + "/";
	}

	/**
	 * jar�t�@�C����URL�����肷��B
	 *
	 * @param url
	 * @return ������url��jar�t�@�C�����w���ꍇ<code>true</code>
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) ||
				URL_PROTOCOL_ZIP.equals(protocol) ||
				URL_PROTOCOL_WSJAR.equals(protocol) ||
				(URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
	}

	/**
	 * VFS�t�@�C����URL�����肷��B
	 *
	 * @param url
	 * @return ������URL��VFS�t�@�C�����w���ꍇ<code>true</code>
	 */
	public static boolean isVFSURL(URL url) {
		return url.getProtocol().startsWith(URL_PROTOCOL_VFS);
	}

	/**
	 * JAR�t�@�C�����擾����B
	 *
	 * @param jarFileUrl
	 * @return
	 * @throws IOException
	 */
	protected static JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
			try {
				return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
			} catch (URISyntaxException ex) {
				// URL�������ȏꍇ�̕ی��i�قڋN����Ȃ��͂��j
				return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
			}
		} else {
			return new JarFile(jarFileUrl);
		}
	}

	/**
	 * URL��URI�ɕϊ�����
	 *
	 * @param url
	 * @return URL����쐬���ꂽURI
	 * @throws URISyntaxException
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * String��URI�ɕϊ�����
	 *
	 * @param location
	 * @return �����̕����񂩂�쐬���ꂽURI
	 * @throws URISyntaxException
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(location.replace(" ", "%20"));
	}

	/**
	 * �p�X���̃t�@�C���V�X�e���Z�p���[�^���X���b�V���ɕϊ�����B
	 *
	 * @param path
	 * @return �Z�p���[�^�[���X���b�V���ɕϊ������p�X
	 */
	public static String separatorToSlash(String path) {
		if (path == null || path.length() == 0) {
			return path;
		}
		return path.replace(File.separator, "/");
	}

}

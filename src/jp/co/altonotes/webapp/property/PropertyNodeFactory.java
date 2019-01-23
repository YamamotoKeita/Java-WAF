package jp.co.altonotes.webapp.property;

import jp.co.altonotes.util.TextUtils;

/**
 * �K���� PropertyNode ���쐬����B
 * 
 * @author Yamamoto Keita
 *
 */
public class PropertyNodeFactory {

	/**
	 * ���[�g�� PropertyNode ���쐬����
	 * @param rootObj
	 * @param propertyName
	 * @param result
	 * @return ���[�g�� PropertyNode
	 */
	public static PropertyNode createRoot(Object rootObj, String propertyName, Result result) {
		String[] keys = propertyName.split("\\.", -1);

		// �v���p�e�B���̃t�H�[�}�b�g�`�F�b�N
		for (String key : keys) {
			if (key.length() == 0) {
				result.fail("�v���p�e�B���̃t�H�[�}�b�g���s���ł��B property=" + propertyName);
				return null;
			}
		}
		
		return create(rootObj, null, keys, 0, result);
	}

	protected static PropertyNode createFromParent(Object parent, String[] keys, int depth, Result result) {
		return create(null, parent, keys, depth, result);
	}
	
	/**
	 * �v���p�e�B�m�[�h���쐬����
	 * @param obj
	 * @param parent
	 * @param keys
	 * @param depth
	 * @param result
	 * @return
	 */
	private static PropertyNode create(Object obj, Object parent, String[] keys, int depth, Result result) {
		String name = keys[depth];
		int index = TextUtils.parseArrayIndex(name);
		
		PropertyNode node = null;
		if (index == -1) {
			node = new SingleNode();
		} else {
			name = name.substring(0, name.indexOf("["));
			node = new IndexedNode(name, index);
		}

		node.obj = obj;
		node.parentObj = parent;
		node.keys = keys;
		node.depth = depth;
		
		return node;
	}
	

}

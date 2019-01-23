package jp.co.altonotes.webapp.jsptag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jp.co.altonotes.util.Checker;
import jp.co.altonotes.webapp.scope.IScope;
import jp.co.altonotes.webapp.scope.ScopeAccessor;

/**
 * �J��Ԃ��������s��
 *
 * @author Yamamoto Keita
 *
 */
public class ForEachTag extends BodyTagSupport {

	private static final long serialVersionUID = -1557386323624844733L;

	private int begin = 0;
	private int end = 0;
	private int step = 1;
	private Object items;
	private String var, varStatus;
	private String name;
	private String scopeName;

	private boolean beginSpecified;
	private boolean endSpecified;
	private boolean stepSpecified;

	private transient LoopStatus status;
	private Object item;
	private int index = 0;
	private int count = 1;
	private boolean last = false;
	private transient ForEachIterator iterator;

	/**
	 * @param begin �Z�b�g����J��Ԃ��J�n�ʒu
	 * @throws JspTagException
	 */
	public void setBegin(int begin) throws JspTagException {
		this.beginSpecified = true;
		this.begin = begin;
		if (begin < 0) {
			throw new JspTagException("'begin' �ɕ��̐����ݒ肳��Ă��܂�");
		}
	}

	/**
	 * @param end �Z�b�g����J��Ԃ��I���ʒu
	 * @throws JspTagException
	 */
	public void setEnd(int end) throws JspTagException {
		this.endSpecified = true;
		this.end = end;
		if (end < 0) {
			throw new JspTagException("'end' �ɕ��̐����ݒ肳��Ă��܂�");
		}
	}

	/**
	 * @param step �Z�b�g���郋�[�v�J�E���g�̃C���N�������g���B�f�t�H���g�͂P�B
	 * @throws JspTagException
	 */
	public void setStep(int step) throws JspTagException {
		this.stepSpecified = true;
		this.step = step;
		if (step < 1) {
			throw new JspTagException("'step' ��0�܂��͕��̐����ݒ肳��Ă��܂�");
		}
	}

	/**
	 * @param o �Z�b�g����J��Ԃ��v�f
	 * @throws JspTagException
	 */
	public void setItems(Object o) throws JspTagException {
		if (o instanceof String) {
			throw new JspTagException("<for-each> �^�O�� items �����ɕ�����͎w��ł��܂���Bitems �����ɂ͌J��Ԃ��\�ȃI�u�W�F�N�g���w�肵�܂��B\n"
									+ "[�q���g]�v���p�e�B�����w�肷��ꍇ�́Aitems �ł͂Ȃ� name �����Ɏw�肵�܂��B");
		}
		
		if (o == null) {
			items = new ArrayList<Object>();
		} else {
			items = o;
		}
	}

	/**
	 * @param var �J��Ԃ��v�f�̕ϐ���
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * @param varStatus �X�e�[�^�X���i�[����ϐ���
	 */
	public void setVarStatus(String varStatus) {
		this.varStatus = varStatus;
	}

	/**
	 * @param name �Z�b�g����name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scope �Z�b�g����scope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
    public int doStartTag() throws JspException {
		if (endSpecified && begin > end) {
			return SKIP_BODY;
		}

		if (Checker.isNotEmpty(name)) {
			IScope scope = ScopeAccessor.create(pageContext, scopeName);
			items = ScopeAccessor.extract(scope, name);
		}

        prepareIterator();

		if (iterator.hasNext()) {
			item = iterator.next();
		} else {
			return SKIP_BODY;
		}

        skipItems(step - 1);

        exposeVariables();
        setLastFlag();

        return EVAL_BODY_INCLUDE;
    }

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspException {
		index += step - 1;
		count++;

		if (iterator.hasNext() && !atEnd()) {
			index++;
			item = iterator.next();
		} else {
			return SKIP_BODY;
		}

		skipItems(step - 1);

		exposeVariables();
		setLastFlag();
		return EVAL_BODY_AGAIN;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspTagException{
		release();
		return EVAL_PAGE;
	}

	/*
	 * (�� Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
	@Override
	public void release() {
		begin = 0;
		end = 0;
		step = 1;
		items = null;
		var = null;
		varStatus = null;
		name = null;
		scopeName = null;
		beginSpecified = false;
		endSpecified = false;
		stepSpecified = false;
		status = null;
		item = null;
		index = 0;
		count = 1;
		last = false;
		iterator = null;
	}

	/**
	 * items���iterator���쐬����
	 *
	 * @throws JspTagException
	 */
	private void prepareIterator() throws JspTagException {
		if (items != null) {
			iterator = createIterator(items);
		} else {//items���Z�b�g����Ă��Ȃ��ꍇ�AInteger��Iterator���쐬
			iterator = createIntegerIterator();
		}

		// begin�̈ʒu�܂ňړ�
		int idx = 0;
		while (idx++ < begin && iterator.hasNext()) {
			iterator.next();
		}
	}

	/**
	 * �I�[�ɒB�����ꍇ�A�I�[�t���O��true�ɂ���
	 *
	 * @throws JspTagException
	 */
	private void setLastFlag() throws JspTagException {
		last = !iterator.hasNext() ||
			   atEnd() ||
			   (endSpecified && (begin + index + step > end));
	}

	/**
	 * PageContext�ɗv�f�ƃX�e�[�^�X���Z�b�g����B
	 *
	 * @throws JspTagException
	 */
	private void exposeVariables() throws JspTagException {
		if (var != null) {
			if (item == null) {
				pageContext.removeAttribute(var, PageContext.PAGE_SCOPE);
			} else {
				pageContext.setAttribute(var, item);
			}
		}
		if (varStatus != null) {
			if (getLoopStatus() == null) {
				pageContext.removeAttribute(varStatus, PageContext.PAGE_SCOPE);
			} else {
				pageContext.setAttribute(varStatus, getLoopStatus());
			}
		}
	}

	/**
	 * ���[�v�v�f���w�萔�X�L�b�v����
	 *
	 * @param n
	 * @throws JspTagException
	 */
    private void skipItems(int n) throws JspTagException {
		int oldIndex = index;
		while (n-- > 0 && !atEnd() && iterator.hasNext()) {
			index++;
			iterator.next();
		}
		index = oldIndex;
    }

	/**
	 * �ݒ肵��end�ɓ��B���������肷��B
	 *
	 * @return
	 */
	private boolean atEnd() {
		return (endSpecified && (begin + index >= end));
	}

	/**
	 * ���[�v�̃X�e�[�^�X���擾����B
	 *
	 * @return ���[�v�̃X�e�[�^�X
	 */
	public LoopStatus getLoopStatus() {
		if (status == null) {
			status = new LoopStatus();
		}
		return status;
	}

	/**
	 * ���[�v�̏�Ԃ�ێ�����
	 *
	 * @author Yamamoto Keita
	 *
	 */
    public class LoopStatus {
    	/**
    	 * @return ���[�v�ɑΉ�����v�f
    	 */
		public Object getCurrent() {
			return (ForEachTag.this.item);
		}

		/**
		 * @return �C���f�b�N�X
		 */
		public int getIndex() {
			return (index + begin);
		}

		/**
		 * @return ���[�v�J�E���g
		 */
		public int getCount() {
			return (count);
		}

		/**
		 * @return �C���f�b�N�X���[���̏ꍇ<code>true</code>
		 */
		public boolean isFirst() {
			return (index == 0);
		}

		/**
		 * @return �C���f�b�N�X�����[�ɒB���Ă���ꍇ<code>true</code>
		 */
		public boolean isLast() {
			return (last);
		}

		/**
		 * @return ���[�v�J�n�C���f�b�N�X
		 */
		public Integer getBegin() {
			if (beginSpecified) {
				return (Integer.valueOf(begin));
			} else {
				return null;
			}
		}

		/**
		 * @return ���[�v�I���C���f�b�N�X
		 */
		public Integer getEnd() {
			if (endSpecified) {
				return (Integer.valueOf(end));
			} else {
				return null;
			}
		}

		/**
		 * @return ���̃��[�v�Ői�߂�C���f�b�N�X�̐�
		 */
		public Integer getStep() {
			if (stepSpecified) {
				return (Integer.valueOf(step));
			} else {
				return null;
			}
		}

		/**
		 * @return ���݂̃J�E���g�������̏ꍇ<code>true</code>
		 */
		public boolean isEvenCount() {
			return count % 2 == 0;
		}

		/**
		 * @return ���݂̃J�E���g����̏ꍇ<code>true</code>
		 */
		public boolean isOddCount() {
			return count % 2 != 0;
		}
    }

    /**
     * Integer��ێ�����Iterator���쐬����
     *
     * @return
     */
    private ForEachIterator createIntegerIterator() {
		Object[] ia = new Integer[end + 1];
		for (int i = 0; i <= end; i++) {
			ia[i] = Integer.valueOf(i);
		}
		return new SimpleForEachIterator(Arrays.asList(ia).iterator());
    }

    /**
     * �I�u�W�F�N�g�̌^�ɉ�����Iterator���쐬����B
     *
     * @param o
     * @return
     * @throws JspTagException
     */
	@SuppressWarnings("unchecked")
	protected ForEachIterator createIterator(Object o) throws JspTagException {
		ForEachIterator items;
		if (o instanceof Object[]) {
			items = toForEachIterator((Object[]) o);
		} else if (o instanceof boolean[]) {
			items = toForEachIterator((boolean[]) o);
		} else if (o instanceof byte[]) {
			items = toForEachIterator((byte[]) o);
		} else if (o instanceof char[]) {
			items = toForEachIterator((char[]) o);
		} else if (o instanceof short[]) {
			items = toForEachIterator((short[]) o);
		} else if (o instanceof int[]) {
			items = toForEachIterator((int[]) o);
		} else if (o instanceof long[]) {
			items = toForEachIterator((long[]) o);
		} else if (o instanceof float[]) {
			items = toForEachIterator((float[]) o);
		} else if (o instanceof double[]) {
			items = toForEachIterator((double[]) o);
		} else if (o instanceof Collection) {
			items = toForEachIterator((Collection<Object>) o);
		} else if (o instanceof Iterator) {
			items = toForEachIterator((Iterator<Object>) o);
		} else if (o instanceof Enumeration) {
			items = toForEachIterator((Enumeration<?>) o);
		} else if (o instanceof Map) {
			items = toForEachIterator((Map<?,?>) o);
		} else {
			throw new JspTagException("<forEach> �^�O items �����ɐݒ肳�ꂽ " + o.getClass().getName() + " �̃I�u�W�F�N�g�̓��[�v�����ł��܂���B");
		}

		return (items);
	}

	/**
	 * ForEach�^�O�̃��[�v�v�f
	 *
	 * @author Yamamoto Keita
	 *
	 */
	protected static interface ForEachIterator {
		public boolean hasNext() throws JspTagException;
		public Object next() throws JspTagException;
	}

	/**
	 * Iterator�I�u�W�F�N�g���g�p����ForEachIterator
	 *
	 * @author Yamamoto Keita
	 *
	 */
	protected static class SimpleForEachIterator implements ForEachIterator {
		private Iterator<Object> i;
		public SimpleForEachIterator(Iterator<Object> i) {
			this.i = i;
		}
		public boolean hasNext() {
			return i.hasNext();
		}
		public Object next() {
			return i.next();
		}
	}

	// �e�I�u�W�F�N�g�^�C�v�ɑΉ�����ForEachIterator�쐬���\�b�h

	protected ForEachIterator toForEachIterator(Object[] a) {
		return new SimpleForEachIterator(Arrays.asList(a).iterator());
	}

	protected ForEachIterator toForEachIterator(boolean[] a) {
		Object[] wrapped = new Boolean[a.length];
		for (int i = 0; i < a.length; i++) {
		    wrapped[i] = Boolean.valueOf(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
	}

    protected ForEachIterator toForEachIterator(byte[] a) {
		Object[] wrapped = new Byte[a.length];
		for (int i = 0; i < a.length; i++) {
			wrapped[i] = Byte.valueOf(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

	protected ForEachIterator toForEachIterator(char[] a) {
		Object[] wrapped = new Character[a.length];
		for (int i = 0; i < a.length; i++) {
			wrapped[i] = Character.valueOf(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
	}

	protected ForEachIterator toForEachIterator(short[] a) {
		Object[] wrapped = new Short[a.length];
		for (int i = 0; i < a.length; i++) {
			wrapped[i] = Short.valueOf(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
	}

	protected ForEachIterator toForEachIterator(int[] a) {
		Object[] wrapped = new Integer[a.length];
		for (int i = 0; i < a.length; i++) {
			wrapped[i] = Integer.valueOf(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
	}

	protected ForEachIterator toForEachIterator(long[] a) {
		Object[] wrapped = new Long[a.length];
		for (int i = 0; i < a.length; i++) {
			wrapped[i] = Long.valueOf(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
	}

	protected ForEachIterator toForEachIterator(float[] a) {
		Object[] wrapped = new Float[a.length];
		for (int i = 0; i < a.length; i++) {
			wrapped[i] = new Float(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
	}

	protected ForEachIterator toForEachIterator(double[] a) {
		Object[] wrapped = new Double[a.length];
		for (int i = 0; i < a.length; i++) {
			wrapped[i] = new Double(a[i]);
		}
		return new SimpleForEachIterator(Arrays.asList(wrapped).iterator());
	}

	protected ForEachIterator toForEachIterator(Collection<Object> c) {
		return new SimpleForEachIterator(c.iterator());
	}

	protected ForEachIterator toForEachIterator(Iterator<Object> i) {
		return new SimpleForEachIterator(i);
	}

	protected ForEachIterator toForEachIterator(Enumeration<?> e) {
		class EnumerationAdapter implements ForEachIterator {
			private Enumeration<?> e;
			public EnumerationAdapter(Enumeration<?> e) {
				this.e = e;
			}
			public boolean hasNext() {
				return e.hasMoreElements();
			}
			public Object next() {
				return e.nextElement();
			}
		}

		return new EnumerationAdapter(e);
	}

	protected ForEachIterator toForEachIterator(Map m) {
		return new SimpleForEachIterator(m.entrySet().iterator());
	}

	protected ForEachIterator toForEachIterator(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		return toForEachIterator(st);
	}
}

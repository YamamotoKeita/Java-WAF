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
 * 繰り返し処理を行う
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
	 * @param begin セットする繰り返し開始位置
	 * @throws JspTagException
	 */
	public void setBegin(int begin) throws JspTagException {
		this.beginSpecified = true;
		this.begin = begin;
		if (begin < 0) {
			throw new JspTagException("'begin' に負の数が設定されています");
		}
	}

	/**
	 * @param end セットする繰り返し終了位置
	 * @throws JspTagException
	 */
	public void setEnd(int end) throws JspTagException {
		this.endSpecified = true;
		this.end = end;
		if (end < 0) {
			throw new JspTagException("'end' に負の数が設定されています");
		}
	}

	/**
	 * @param step セットするループカウントのインクリメント数。デフォルトは１。
	 * @throws JspTagException
	 */
	public void setStep(int step) throws JspTagException {
		this.stepSpecified = true;
		this.step = step;
		if (step < 1) {
			throw new JspTagException("'step' に0または負の数が設定されています");
		}
	}

	/**
	 * @param o セットする繰り返し要素
	 * @throws JspTagException
	 */
	public void setItems(Object o) throws JspTagException {
		if (o instanceof String) {
			throw new JspTagException("<for-each> タグの items 属性に文字列は指定できません。items 属性には繰り返し可能なオブジェクトを指定します。\n"
									+ "[ヒント]プロパティ名を指定する場合は、items ではなく name 属性に指定します。");
		}
		
		if (o == null) {
			items = new ArrayList<Object>();
		} else {
			items = o;
		}
	}

	/**
	 * @param var 繰り返し要素の変数名
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * @param varStatus ステータスを格納する変数名
	 */
	public void setVarStatus(String varStatus) {
		this.varStatus = varStatus;
	}

	/**
	 * @param name セットするname
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param scope セットするscope
	 */
	public void setScope(String scope) {
		this.scopeName = scope;
	}

	/*
	 * (非 Javadoc)
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
	 * (非 Javadoc)
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
	 * (非 Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspTagException{
		release();
		return EVAL_PAGE;
	}

	/*
	 * (非 Javadoc)
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
	 * itemsよりiteratorを作成する
	 *
	 * @throws JspTagException
	 */
	private void prepareIterator() throws JspTagException {
		if (items != null) {
			iterator = createIterator(items);
		} else {//itemsがセットされていない場合、IntegerのIteratorを作成
			iterator = createIntegerIterator();
		}

		// beginの位置まで移動
		int idx = 0;
		while (idx++ < begin && iterator.hasNext()) {
			iterator.next();
		}
	}

	/**
	 * 終端に達した場合、終端フラグをtrueにする
	 *
	 * @throws JspTagException
	 */
	private void setLastFlag() throws JspTagException {
		last = !iterator.hasNext() ||
			   atEnd() ||
			   (endSpecified && (begin + index + step > end));
	}

	/**
	 * PageContextに要素とステータスをセットする。
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
	 * ループ要素を指定数スキップする
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
	 * 設定したendに到達したか判定する。
	 *
	 * @return
	 */
	private boolean atEnd() {
		return (endSpecified && (begin + index >= end));
	}

	/**
	 * ループのステータスを取得する。
	 *
	 * @return ループのステータス
	 */
	public LoopStatus getLoopStatus() {
		if (status == null) {
			status = new LoopStatus();
		}
		return status;
	}

	/**
	 * ループの状態を保持する
	 *
	 * @author Yamamoto Keita
	 *
	 */
    public class LoopStatus {
    	/**
    	 * @return ループに対応する要素
    	 */
		public Object getCurrent() {
			return (ForEachTag.this.item);
		}

		/**
		 * @return インデックス
		 */
		public int getIndex() {
			return (index + begin);
		}

		/**
		 * @return ループカウント
		 */
		public int getCount() {
			return (count);
		}

		/**
		 * @return インデックスがゼロの場合<code>true</code>
		 */
		public boolean isFirst() {
			return (index == 0);
		}

		/**
		 * @return インデックスが末端に達している場合<code>true</code>
		 */
		public boolean isLast() {
			return (last);
		}

		/**
		 * @return ループ開始インデックス
		 */
		public Integer getBegin() {
			if (beginSpecified) {
				return (Integer.valueOf(begin));
			} else {
				return null;
			}
		}

		/**
		 * @return ループ終了インデックス
		 */
		public Integer getEnd() {
			if (endSpecified) {
				return (Integer.valueOf(end));
			} else {
				return null;
			}
		}

		/**
		 * @return 一回のループで進めるインデックスの数
		 */
		public Integer getStep() {
			if (stepSpecified) {
				return (Integer.valueOf(step));
			} else {
				return null;
			}
		}

		/**
		 * @return 現在のカウントが偶数の場合<code>true</code>
		 */
		public boolean isEvenCount() {
			return count % 2 == 0;
		}

		/**
		 * @return 現在のカウントが奇数の場合<code>true</code>
		 */
		public boolean isOddCount() {
			return count % 2 != 0;
		}
    }

    /**
     * Integerを保持するIteratorを作成する
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
     * オブジェクトの型に応じたIteratorを作成する。
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
			throw new JspTagException("<forEach> タグ items 属性に設定された " + o.getClass().getName() + " のオブジェクトはループ処理できません。");
		}

		return (items);
	}

	/**
	 * ForEachタグのループ要素
	 *
	 * @author Yamamoto Keita
	 *
	 */
	protected static interface ForEachIterator {
		public boolean hasNext() throws JspTagException;
		public Object next() throws JspTagException;
	}

	/**
	 * Iteratorオブジェクトを使用したForEachIterator
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

	// 各オブジェクトタイプに対応したForEachIterator作成メソッド

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

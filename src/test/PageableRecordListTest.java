package test;

import static org.junit.Assert.assertEquals;
import jp.co.altonotes.util.PageableRecordList;

import org.junit.Test;

public class PageableRecordListTest {
	
	@Test
	public void test0Records() {
		PageableRecordList<String> obj = new PageableRecordList<String>(3, 5, new String[]{});
		assertEquals(0, obj.getCurrentRecords().size());
		assertEquals(false, obj.hasMoreLeftPage());
		assertEquals(false, obj.hasMoreRightPage());
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(1, obj.getLastAnchorNumber());
		assertEquals(false, obj.nextPage());
		assertEquals(false, obj.previousPage());
		assertEquals(1, obj.getCurrentPageNumber());
		assertEquals(true, obj.movePage(1));
		assertEquals(1, obj.getCurrentPageNumber());
		assertEquals(false, obj.movePage(2));
		assertEquals(1, obj.getCurrentPageNumber());
	}

	@Test
	public void test3Records() {
		PageableRecordList<String> obj = new PageableRecordList<String>(3, 5, new String[]{"1", "2", "3"});
		//1�y�[�W
		assertEquals(3, obj.getCurrentRecords().size());
		assertEquals(false, obj.hasMoreLeftPage());
		assertEquals(false, obj.hasMoreRightPage());
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(1, obj.getLastAnchorNumber());
		assertEquals(false, obj.nextPage());
		assertEquals(1, obj.getCurrentPageNumber());
		assertEquals(false, obj.previousPage());
		assertEquals(1, obj.getCurrentPageNumber());
		assertEquals(false, obj.movePage(2));
		assertEquals(1, obj.getCurrentPageNumber());
		assertEquals(3, obj.getCurrentRecords().size());
	}

	@Test
	public void test4Records() {
		PageableRecordList<String> obj = new PageableRecordList<String>(3, 5, new String[]{"1", "2", "3", "4"});
		//1�y�[�W
		assertEquals(3, obj.getCurrentRecords().size());
		assertEquals(false, obj.hasMoreLeftPage());
		assertEquals(true, obj.hasMoreRightPage());
		assertEquals(true, obj.nextPage());
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(2, obj.getLastAnchorNumber());
		//2�y�[�W
		assertEquals(2, obj.getCurrentPageNumber());
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(2, obj.getLastAnchorNumber());
		assertEquals(1, obj.getCurrentRecords().size());
		assertEquals(false, obj.hasMoreRightPage());
		assertEquals(true, obj.hasMoreLeftPage());
		assertEquals(false, obj.nextPage());
		assertEquals(true, obj.previousPage());
		//1�y�[�W
		assertEquals(1, obj.getCurrentPageNumber());
		assertEquals(1, obj.getCurrentPageNumber());
		assertEquals(3, obj.getCurrentRecords().size());
		assertEquals(true, obj.movePage(2));
		//2�y�[�W
		assertEquals(2, obj.getCurrentPageNumber());
		assertEquals(1, obj.getCurrentRecords().size());
		assertEquals("4", obj.getCurrentRecords().get(0));
	}
	
	@Test
	public void testPaging() {
		//3���R�[�h�~5�y�[�W�i�A���J�[�T�j
		PageableRecordList<String> obj = new PageableRecordList<String>(3, 5, new String[15]);
		//1�y�[�W
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(5, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//2�y�[�W
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(5, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//3�y�[�W
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(5, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//4�y�[�W
		assertEquals(4, obj.getCurrentPageNumber());
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(5, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//5�y�[�W
		assertEquals(5, obj.getCurrentPageNumber());
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(5, obj.getLastAnchorNumber());
		assertEquals(false, obj.nextPage());
	}

	@Test
	public void testPaging2() {
		//3���R�[�h�~5�y�[�W + 1�i�A���J�[4�j
		PageableRecordList<String> obj = new PageableRecordList<String>(3, 4, new String[16]);
		//1�y�[�W
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(4, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//2�y�[�W
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(4, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//3�y�[�W
		assertEquals(1, obj.getFirstAnchorNumber());
		assertEquals(4, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//4�y�[�W
		assertEquals(4, obj.getCurrentPageNumber());
		assertEquals(2, obj.getFirstAnchorNumber());
		assertEquals(5, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//5�y�[�W
		assertEquals(5, obj.getCurrentPageNumber());
		assertEquals(3, obj.getFirstAnchorNumber());
		assertEquals(6, obj.getLastAnchorNumber());
		assertEquals(true, obj.nextPage());
		//6�y�[�W
		assertEquals(6, obj.getCurrentPageNumber());
		assertEquals(3, obj.getFirstAnchorNumber());
		assertEquals(6, obj.getLastAnchorNumber());
		assertEquals(false, obj.nextPage());
		assertEquals(1, obj.getCurrentRecords().size());
		assertEquals(false, obj.nextPage());
	}

}

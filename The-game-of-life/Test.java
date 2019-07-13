import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;

class Test {

	@org.junit.jupiter.api.Test
	void test() {
		//fail("Not yet implemented");
	}

	@org.junit.jupiter.api.Test
	public void testCreate() {
		int width = 10;
		int height = 10;
		Board board = new Board(width, height);
		Assert.assertNotNull(board);
		
		for(int i=0; i< width; i++) {
			for(int j=0; j<height; j++) {
				Assert.assertEquals(false, board.getCellValue(i, j));
			}
		}
	}
	
	@org.junit.jupiter.api.Test
	public void testSetValue() {
		Board board = new Board(10, 10);
		
		board.setCellValue(5, 5, true);
		Assert.assertEquals(true, board.getCellValue(5, 5));
		
		board.setCellValue(0, 0, true);
		Assert.assertEquals(true, board.getCellValue(0, 0));
		
		board.setCellValue(0, 1, true);
		Assert.assertEquals(true, board.getCellValue(0, 1));
		
		board.setCellValue(1, 0, true);
		Assert.assertEquals(true, board.getCellValue(1, 0));
	}
	
	@org.junit.jupiter.api.Test
	public void testDieLonely() {
		Board board = new Board(5, 5);
		board.setCellValue(0, 0, true);
		Assert.assertEquals(0, board.countAliveNeightbours(0, 0));
		board.nextCycle();
		Assert.assertEquals(false, board.getCellValue(0, 0));
		
		//jeden sasiad
		Board board2 = new Board(5, 5);
		board2.setCellValue(0, 0, true);
		board2.setCellValue(0, 1, true);
		Assert.assertEquals(1, board2.countAliveNeightbours(0, 0));
		board2.nextCycle();
		Assert.assertEquals(false, board2.getCellValue(0, 0));
	}
	
	@org.junit.jupiter.api.Test
	public void testSurvive() {
		//2 sasiadow
		Board board = new Board(5,5);
		board.setCellValue(0, 0, true);
		board.setCellValue(0, 1, true);
		board.setCellValue(1, 0, true);
		Assert.assertEquals(2, board.countAliveNeightbours(0, 0));
		board.nextCycle();
		Assert.assertEquals(true, board.getCellValue(0, 0));
		
		//3 sasiadow
		Board board2 = new Board(5,5);
		board2.setCellValue(0, 0, true);
		board2.setCellValue(0, 1, true);
		board2.setCellValue(1, 0, true);
		board2.setCellValue(1, 1, true);
		Assert.assertEquals(3, board.countAliveNeightbours(0, 0));
		board2.nextCycle();
		Assert.assertEquals(true, board.getCellValue(0, 0));
	}
	
	@org.junit.jupiter.api.Test
	public void testDieOverpopulation() {
		//czterech sasiadow
		Board board = new Board(5,5);
		board.setCellValue(2, 2, true);
		board.setCellValue(2, 1, true);
		board.setCellValue(2, 3, true);
		board.setCellValue(1, 2, true);
		board.setCellValue(3, 2, true);
		Assert.assertEquals(4, board.countAliveNeightbours(2, 2));
		board.nextCycle();
		Assert.assertEquals(false, board.getCellValue(2, 2));
	}
	
	@org.junit.jupiter.api.Test
	public void testRessurection() {
		
		Board board = new Board(5,5);
		board.setCellValue(2, 2, false);
		board.setCellValue(2, 1, true);
		board.setCellValue(1, 2, true);
		board.setCellValue(3, 2, true);
		Assert.assertEquals(3, board.countAliveNeightbours(2, 2));
		board.nextCycle();
		Assert.assertEquals(true, board.getCellValue(2, 2));
		
	}
}

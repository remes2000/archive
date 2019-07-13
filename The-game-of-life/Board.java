import java.util.Scanner;

public class Board {
	
	private Cell[][] cells;
	private int width;
	private int height;
	
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		this.cells = new Cell[width][height];
		resetAll();
	}
	
	private void resetAll() {
		for(int i=0; i < width; i++) {
			
			for(int j=0; j < height; j++) {
				cells[i][j] = new Cell(false);
			}
			
		}
	}
	
	public void setCellValue(int x, int y, boolean isAlive) {
		if(x >= 0 && y >= 0 && x < width && y < height) {
			cells[x][y].setAlive(isAlive);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	public boolean getCellValue(int x, int y) {
		if(x >= 0 && y >= 0 && x < width && y < height) {
			return cells[x][y].isAlive();
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	public void nextCycle() {
		
		Cell[][] newBoard = new Cell[width][height];
		for(int i=0; i< width; i++) {
			for(int j=0; j<height; j++) {
				newBoard[i][j] = cells[i][j].clone();
			}
		}
		
		//wykonanie akcji dla danej komorki
		for(int i=0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				int neightboursCount = countAliveNeightbours(i, j);
				newBoard[i][j].changeState(neightboursCount);
			}
		}
		
		cells = newBoard;
	}
	
	public int countAliveNeightbours(int i, int j) {
		int startX = Math.max(i - 1, 0);
		int startY = Math.max(j - 1, 0);
		int endX = Math.min(i + 1, width - 1);
		int endY = Math.min(j + 1, height - 1);
		
		int aliveNeightbours = 0;
		for(int x=startX; x<=endX; x++) {
			for(int y=startY; y<=endY; y++) {
				
				if(cells[x][y].isAlive()) {
					aliveNeightbours++;
				}
				
			}
		}
		
		if(cells[i][j].isAlive()) {
			aliveNeightbours--;
		}
		
		return aliveNeightbours;
	}
	
	public void printBoard() {
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				System.out.print((cells[j][i].isAlive()?'1':'0') + " ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Board board = new Board(10, 10);
		board.setCellValue(5, 5, true);
		board.setCellValue(6, 5, true);
		board.setCellValue(7, 5, true);
		while(true) {
			board.printBoard();
		    System.out.print("Wykonaj nastêpny cykl . . . ");
		    scanner.nextLine();
			board.nextCycle();
		}
	}
}

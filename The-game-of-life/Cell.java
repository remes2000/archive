
public class Cell implements Cloneable{
	private boolean alive;
	
	public Cell(boolean alive) {
		this.setAlive(alive);
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	@Override
	public Cell clone() {
		return new Cell(alive);
	}
	
	public void changeState(int neightboursCount) {
		
		if(alive) {
			
			//mniej ni¿ dwóch s¹siadów - œmieræ z samotnoœci
			if(neightboursCount < 2) {
				alive = false;
			}
			//wiecej niz 3 sasiadow ginie z przeludnienia
			else if(neightboursCount > 3) {
				alive = false;
			}
			
		} else {
			
			//Jezeli ma 3 sasiadow o¿ywa
			if(neightboursCount == 3) {
				alive = true;
			}
			
		}
		
	}
}

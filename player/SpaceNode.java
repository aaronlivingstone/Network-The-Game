package player;
import player.list.*;

public class SpaceNode{
	int item = Board.EMPTY;
	SList coordList = new SList();
	boolean visited;

	
	
	public static void main(String[] args){
		SpaceNode a = new SpaceNode();
		System.out.println(a.item);
		System.out.println(a.visited);
		
	}
}



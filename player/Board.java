/* Board.java  */

package player;
import player.list.*;

public class Board {
	static final int EMPTY = 2;
	static final int WHITE = 1;
	static final int BLACK = 0;

	static final int SIZE = 10;
	static final int CHIPS = 10;

	static final int CONTINUE_SEARCH = 0;
	
	static final int NORTH = 1;
	static final int NORTHEAST = 2;
	static final int EAST = 3;
	static final int SOUTHEAST = 4;
	static final int SOUTH = 5;
	static final int SOUTHWEST = 6;
	static final int WEST = 7;
	static final int NORTHWEST = 8;
	
	static boolean GAMEOVER = false;

//we will adjust security as needed. private seems fitting at the moment. 
	private int blackChips;
	private int whiteChips;
	private SpaceNode[][] myBoard;
	
	Board() {
		myBoard = new SpaceNode[SIZE][SIZE];
		for(int j = 0; j < SIZE; j++){
			for(int i = 0; i < SIZE; i++){
				myBoard[i][j] = new SpaceNode();
			}
		}
		blackChips = CHIPS;
		whiteChips = CHIPS;
	}

	Board(Board b) {
		myBoard = b.myBoard;
		blackChips = b.blackChips;
		whiteChips = b.whiteChips;
	}
		
	Move[] validMoves(int player) {
		// how many chips are left in the players chips
		int playerChips;
		if (player == BLACK) {
			playerChips = blackChips;
		} else {
			playerChips = whiteChips;
		}
		// now if playerChips < 0 then move is STEP MOVE
		
		// create appropriate size array for either add moves or step moves
		Move[] moveArr;
		if (playerChips > 0) {
			moveArr = new Move[48 - (CHIPS - playerChips)];
		} else {
			moveArr = new Move[38 * CHIPS];
		}
			
		// fill moveArr
		int counter = 0; 
		Move m;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((myBoard[i + 1][j + 1].item == player) && (playerChips < 0)) {
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							if (myBoard[l + 1][l + 1].item == EMPTY) {
								m = new Move(k, l, i, j);
								if (isValid(m, player)) {
									moveArr[counter] = m;
									counter++;
								}
							}
						}
					}
				} else if (myBoard[i + 1][j + 1].item == EMPTY) {
					m = new Move(i, j);
					if (isValid(m, player)) {
						moveArr[counter] = m;
						counter++;
					}
				}
			}
		}
		return moveArr;
	}

	void addBlack(int x, int y) {
		myBoard[x + 1][y + 1].item = BLACK;
		blackChips--;
	}

	void addWhite(int x, int y) {
		myBoard[x + 1][y + 1].item = WHITE;
		whiteChips--;
	}

	void removeChip(int x, int y, int player) {
		myBoard[x + 1][y + 1].item = EMPTY;
		myBoard[x + 1][y + 1].coordList = new SList();
		if (player == WHITE) {
			whiteChips++;
		} else {
			blackChips++;
		}
	}
	
	boolean isValid(Move m, int player) { 
		// no longer have valid moves if GAMEOVER
		boolean debug = false;
		if (GAMEOVER) {
			return false;
		}
		
		// if movekind is quit then return false evermore. 
		if(m.moveKind == Move.QUIT){
			GAMEOVER = true;
			return false;
		}
		
		// no longer add chips if number of chips is 0
		if (m.moveKind==Move.ADD){
		    if (player==WHITE){
			if (whiteChips == 0){
			    return false;
			}
		    }else{
			if (blackChips == 0){
			    return false;
			}
		    }
		}
		
		// if steping from a different color then false
		if(m.moveKind == Move.STEP){
			if(myBoard[m.x2 + 1][m.y2 + 1].item != player){
				return false;
			}
		}
		
		// reassigning x,y values for our larger array
		int x = m.x1 + 1;
		int y = m.y1 + 1;

		// 3) No chip may be placed in a square that is already occupied
		if (myBoard[x][y].item != EMPTY) {
			return false;
		}

		// 1) no chip may be placed in any of the four corners
		if ((x == 1 && y == 1) || (x == 1 && y == 8) ||
				(y == 1 && x==8) || (x == 8 && y == 8)) {
			return false;
		}

		// 2) No chip may be placed in a goal of the opposite color.
		if (player == BLACK) {
			if (x < 2 || x > 7 || y < 1 || y > 8) {
				return false;
			}
		} else {
			if (x < 1 || x > 8 || y < 2 || y > 7) {
				return false;
			}
		}
		
		// 4) A player may not have more than two chips in a connected group
		int countMain = 0;
		int countSecondary = 0;
		
		// if step we need to remove from square to check move to position.
		if (m.moveKind == Move.STEP) {
			removeChip(m.x2, m.y2, player);
		}

		for (int i = (x - 1); i <= (x + 1); i++) {
			for (int j = (y - 1); j <= (y + 1); j++) {
				if (debug) {
					System.out.println("Main: (" + (i - 1) + "," + (j - 1) + ") -- Item: " + myBoard[i][j].item);
				}
				if (myBoard[i][j].item == player) {
					countMain++;
					for (int k = (i - 1); k <= (i + 1); k++) {
						for (int l = (j - 1); l <= (j + 1); l++) {
							if (debug) {
								System.out.println("Secondary: (" + (k - 1) + "," + (l - 1) + ") -- Item: " + myBoard[k][l].item);
							}
							if (myBoard[k][l].item == player) {
								countSecondary++;
							}
						}
					}
					if (countSecondary >= 2) {
						// replacing the chip we removed for the step test
						if (m.moveKind == Move.STEP) {
							if (player == WHITE) {
								addWhite(m.x2, m.y2);
							} else {
								addBlack(m.x2, m.y2);
							}
						}
						return false;
					}
						countSecondary = 0;
				}
			}
		}
		if (countMain >= 2) {
			// replacing the chip we removed for the step test
			if (m.moveKind == Move.STEP) {
				if (player == WHITE) {
					addWhite(m.x2, m.y2);
				} else {
					addBlack(m.x2, m.y2);
				}
			}
			return false;
		}	
			
		// replacing the chip we removed for the step test
		if (m.moveKind == Move.STEP) {
			if (player == WHITE) {
				addWhite(m.x2, m.y2);
			} else {
				addBlack(m.x2, m.y2);
			}
		}
		return true;
	}
	
	boolean isNetwork(int player) {
		// check if enough chips have been played
		if ((player == BLACK && blackChips > 4)
				|| (player == WHITE && whiteChips > 4)) {
			return false;
		} 

		// check left white goal for chips then right goal for networks
		if (player == WHITE) {
			int whiteGoal = 0;
			for (int j = 2; j < SIZE - 2; j++) {
				if (this.myBoard[1][j].item == WHITE) {
					whiteGoal++;
				}
			}
			if (whiteGoal < 1) {
				return false;
			}
			for (int j = 2; j < SIZE - 2; j++) {
				if (this.myBoard[8][j].item == WHITE) {
					if (findNetwork(8, j, player, 1, SOUTH)) {
						return true;
					}
				}
			}
		}
		
		// check top black goal for chips then bottom goal for networks
		if (player == BLACK) {
			int blackGoal = 0;
			for (int i = 2; i < SIZE - 2; i++) {
				if (this.myBoard[i][1].item == BLACK) {
					blackGoal++;
				}
			}
			if (blackGoal < 1) {
				return false;
			}
			for (int i = 2; i < SIZE - 2; i++) {
				if (this.myBoard[i][8].item == BLACK) {
					if (findNetwork(i, 8, player, 1, WEST)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean findNetwork(int x, int y, int player, int counter, int lastDirection) {
		if (counter > 10) {
			return false;
			}
			
		// base cases for white
		if (player == WHITE) {
			// don't look into same goal that we started in
			if (counter < 6 && x == 1) {
				return false;
			}
			if (counter > 1 && x > 7) {
				return false;
			} 
			if (counter < 6 && x == 1) {
				return false;
			}
			if (counter == 10 && x != 1) {
				return false;
			}
			if (counter >= 6 && x == 1) {
				System.out.println(" N: (" + (x - 1) + "," + (y - 1) + ")"); // for debugging
				return true;
			}
		}
		
		// base case for black
		if (player == BLACK) {
			// don't look into same goal that we started in
			if (counter < 6 && y == 1) {
				return false;
			}
			if (counter > 1 && y > 7) {
				return false;
			}
			if (counter < 6 && y == 1) {
				return false;
			}
			if (counter == 10 && y!= 1) {
				return false;
			}
			if (counter >= 6 && y == 1) {
				System.out.println(" N: (" + (x - 1) + "," + (y - 1) + ")"); // for debugging
				return true;
			}
		}
		
		// What chips can this chip see?
		basicSeerFcn(this, x, y, player, true);
			
		// return false if coordList is empty
		if (myBoard[x][y].coordList.isEmpty()) {
			return false;
		}
 
		// set space to visited (need to unset at bottom)
		myBoard[x][y].visited = true;

		// make list of possible moves (shrink SList)
		SList tempList = myBoard[x][y].coordList;
		SListNode tempNode = (SListNode)tempList.front();
		SListNode nextNode;
		try {
		  while (tempNode.isValidNode()) {
				nextNode = (SListNode)tempNode.next();
				if (myBoard[tempNode.valX()][tempNode.valY()].visited == true) {
					tempNode.remove();
				} else if (tempNode.item() == lastDirection){
					tempNode.remove();
				}
				tempNode = nextNode;
			}
		} catch (InvalidNodeException ine) {
			System.err.println("InvalidNodeException 1");
		}
		
		// base case, if tempList empty return false
		if (tempList.length()  == 0) {
			return false;
		}

		tempNode = (SListNode)tempList.front();
		try {
			while (tempNode.isValidNode()) {
				if (findNetwork(tempNode.valX(), tempNode.valY(), player, counter + 1, tempNode.item())) {
					System.out.println(" N: (" + (x - 1) + "," + (y - 1) + ")");
					return true;
				}
				tempNode = (SListNode)tempNode.next();
			}
		} catch (InvalidNodeException ine) {
			System.err.println("InvalidNodeException 2");
		}

		// unset space to visited
		myBoard[x][y].visited = false;

		return false;
	}
	
	public void seerFcn(Board b, int x, int y, int player, boolean firstLevel) {
		int opponent = (player + 1) % 2;
		boolean foundOpp = false;
		int j;
		
		b.myBoard[x][y].coordList = new SList();
		
		// going W
		for (int i = (x - 1); i > 0; i--) {
			if (b.myBoard[i][y].item == opponent || b.myBoard[i][y].visited == true) {	
				foundOpp = true;
			}
			if (b.myBoard[i][y].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(WEST, i, y);
			}
		}
		
		// going E
		foundOpp = false;
		for (int i = (x + 1); i < (SIZE - 1); i++) {
			if (b.myBoard[i][y].item == opponent || b.myBoard[i][y].visited == true) {
				foundOpp = true;	
			}
			if (b.myBoard[i][y].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(EAST, i, y);
			}
		}
		
		// going N
		foundOpp = false;
		for (j = (y - 1); j > 0; j--) {
			if (b.myBoard[x][j].item == opponent || b.myBoard[x][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[x][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(NORTH, x, j);
			}
		}
		
		// going S
		foundOpp = false;
		for (j = (y + 1); j < SIZE - 1; j++) {
			if (b.myBoard[x][j].item == opponent || b.myBoard[x][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[x][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(SOUTH, x, j);
			}
		}	

		// going NW
		j = (y - 1);
		foundOpp = false;
		for (int i = (x - 1); i > 0; i--) {
			if (j < 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(NORTHWEST, i, j);
			}
			j--;
		}
		
		// going NE
		j = (y - 1);
		foundOpp = false;
		for (int i = (x + 1); i < SIZE - 1; i++) {
			if (j < 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(NORTHEAST, i, j);
			}
			j--;
		}
		
		// going SW
		j = (y + 1);
		foundOpp = false;
		for (int i = (x - 1); i > 0; i--) {
			if (j > SIZE - 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(SOUTHWEST, i, j);
			}
			j++;
		}
		
		// going SE
		j = (y + 1);
		foundOpp = false;
		for (int i = (x + 1); i < SIZE - 1; i++) {
			if (j > SIZE - 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(SOUTHEAST, i, j);
			}
			j++;
		}
	}
	
	public void basicSeerFcn(Board b, int x, int y, int player, boolean firstLevel) {
		int opponent = (player + 1) % 2;
		boolean foundOpp = false;
		int j;
		
		b.myBoard[x][y].coordList = new SList();
		
		// going W
		for (int i = (x - 1); i > 0; i--) {
			if (b.myBoard[i][y].item == opponent || b.myBoard[i][y].visited == true) {	
				foundOpp = true;
			}
			if (b.myBoard[i][y].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(WEST, i, y);
				break;
			}
		}
		
		// going E
		foundOpp = false;
		for (int i = (x + 1); i < (SIZE - 1); i++) {
			if (b.myBoard[i][y].item == opponent || b.myBoard[i][y].visited == true) {
				foundOpp = true;	
			}
			if (b.myBoard[i][y].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(EAST, i, y);
				break;
			}
		}
		
		// going N
		foundOpp = false;
		for (j = (y - 1); j > 0; j--) {
			if (b.myBoard[x][j].item == opponent || b.myBoard[x][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[x][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(NORTH, x, j);
				break;
			}
		}
		
		// going S
		foundOpp = false;
		for (j = (y + 1); j < SIZE - 1; j++) {
			if (b.myBoard[x][j].item == opponent || b.myBoard[x][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[x][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(SOUTH, x, j);
				break;
			}
		}	

		// going NW
		j = (y - 1);
		foundOpp = false;
		for (int i = (x - 1); i > 0; i--) {
			if (j < 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(NORTHWEST, i, j);
				break;
			}
			j--;
		}
		
		// going NE
		j = (y - 1);
		foundOpp = false;
		for (int i = (x + 1); i < SIZE - 1; i++) {
			if (j < 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(NORTHEAST, i, j);
				break;
			}
			j--;
		}
		
		// going SW
		j = (y + 1);
		foundOpp = false;
		for (int i = (x - 1); i > 0; i--) {
			if (j > SIZE - 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(SOUTHWEST, i, j);
				break;
			}
			j++;
		}
		
		// going SE
		j = (y + 1);
		foundOpp = false;
		for (int i = (x + 1); i < SIZE - 1; i++) {
			if (j > SIZE - 1) {
				break;
			}
			if (b.myBoard[i][j].item == opponent || b.myBoard[i][j].visited == true) {
				foundOpp = true;
			}
			if (b.myBoard[i][j].item == player && foundOpp == false) {
				b.myBoard[x][y].coordList.insertFront(SOUTHEAST, i, j);
				break;
			}
			j++;
		}
	}

	public int findWinner (int player) {
		int opponent = (player + 1) % 2;
		
		if((this.isNetwork(player) && this.isNetwork(opponent))
			 || (!this.isNetwork(player) && this.isNetwork(opponent))){
			//opponent won 
			return opponent;
		}
		else if(this.isNetwork(player) && !this.isNetwork(opponent)){
			//player won
			return player;
		}
		else{
			//no definite winner yet
			return CONTINUE_SEARCH;
		}
	}
	
	public double evaluatorFcn(int player){
		double result;
		//int maxFriend=0;
		//int maxFoe=0;
		double enemyCount = 0.0;
		double heroCount = 0.0;
		double friendBorder = borderCount(player);
		int opponent = (player+1)%2;
		double foeBorder = borderCount(opponent);
		for (int i = 2; i < SIZE - 2; i++){
			for (int j = 2; j < SIZE - 2; j++){
				if (this.myBoard[i][j].item == player){
					seerFcn(this,i,j,player,true);
					heroCount += myBoard[i][j].coordList.length();
					//SList visited = new SList();
					//visited.insertBack(5,i,j);
					//int temp = maxLength(myBoard[i][j].coordList,visited);
					//if (temp>maxFriend){
						//  maxFriend = temp;
						//}
					}else if (this.myBoard[i][j].item == opponent){
						seerFcn(this,i,j,opponent,true);
						enemyCount += myBoard[i][j].coordList.length();
						//SList visited = new SList();
						//visited.insertBack(5,i,j);
						//int temp = maxLength(myBoard[i][j].coordList,visited);
						//if (temp>maxFoe){
							//    maxFoe = temp;
							//}
						}
					}
				}
				heroCount = heroCount * friendBorder;
				enemyCount = enemyCount * foeBorder;
				result = (heroCount-enemyCount)/(heroCount+enemyCount);
				return result;
			}

			private double borderCount(int player){
				double count = 0.0;
				if (player==WHITE){
					for (int i=2; i<SIZE-2;i++){
						if (this.myBoard[1][i].item==player){
							count++;
						}
						if (this.myBoard[8][i].item==player){
							count++;
						}
					}
				}else{
					for (int j=2; j<SIZE-2;j++){
						if (this.myBoard[j][1].item==player){
							count++;
						}
						if (this.myBoard[j][8].item==player){
							count++;
						}
					}
				}
				if (count<=2.0){
					return 1.0;
				}else{
					return 2.0/count;
				}
			}



			/*    private int maxLength(SList coordList,SList visited){
			int max = 0;
			try{
			ListNode temp = coordList.front();
			ListNode temper = visited.front();
			while (temp.isValidNode()){
			SList map = (SList) myBoard[((SListNode) temp).valX()][((SListNode) temp).valY()].coordList;
			while (temper.isValidNode()){
			ListNode another = map.front();
			while  (another.isValidNode()){
			if (((SListNode) temper).valX()==((SListNode) another).valX()&&((SListNode) temper).valY()==((SListNode) another).valY()){
			ListNode sub = another;
			another.remove();
			another = sub.next();
			}else{
			another = another.next();
			}    
			}
			temper = temper.next();
			}
			if (map.isEmpty()){
			return 1;
			}
			visited.insertBack(5,((SListNode) temp).valX(),((SListNode) temp).valY());
			int tempest = maxLength(map,visited)+1;
			if (tempest>max){
			max = tempest;
			}
			temp = (SListNode) temp.next();
			}
			}catch (InvalidNodeException e){}
			return max;
			} */

	private String border(){
		System.out.print('\n');
		for(int i = 0; i < SIZE + 7; i++){
			System.out.print("- ");
		}
		System.out.print('\n');
		return "";
	}
	
	public String toString(){
		border();
		for(int j = 0; j < SIZE - 2; j++){
			System.out.print("| ");
			for(int i = 0; i < SIZE - 2; i++){
				if(myBoard[i+1][j+1].item == WHITE){
					System.out.print("W" + " | ");
				}
				else if(myBoard[i+1][j+1].item == BLACK){
					System.out.print("B" + " | ");
				}
				else{
					System.out.print(" " + " | ");
				}
			}
			border();
		}		
		return "";
	}
	
	private static void testNetworks(){
		Board a = new Board();
		System.out.println("Printing Empty myBoard:");
		System.out.println(a);
			
		a.addWhite(0, 1);
		a.addWhite(1, 1);
		a.addWhite(1, 3);
		a.addWhite(2, 3);
		a.addWhite(4, 3);
		a.addWhite(6, 1);
		a.addWhite(7, 3);	
		//a.addBlack(2, 1);
	  a.addWhite(7, 0);
	  a.addWhite(6, 5);
		System.out.println(a);	
		System.out.println("testing WHITE: " + a.isNetwork(WHITE));
		
		/*		
		a.addBlack(1, 3);		
		a.addBlack(2, 0);
		a.addBlack(2, 5);
		a.addBlack(2, 7);
		a.addBlack(3, 5);
		a.addBlack(4, 2);
		a.addBlack(5, 7);		
		a.addBlack(6, 0);
		a.addBlack(6, 5);
		System.out.println(a);	
		System.out.println("testing BLACK: " + a.isNetwork(BLACK));
		System.out.println("testing WHITE: " + a.isNetwork(WHITE));
		*/
	}
	
	//Debugging Code, reader may skip :)
	public static void main(String[] args){
		/*	Board b = new Board();
				System.out.println("Printing Empty myBoard:");
				System.out.println(b);
		
				System.out.println("Adding 5 Black Chips:");
				b.addBlack(0, 0);
				b.addBlack(3, 4);
				b.addBlack(5, 6);
				b.addBlack(6, 1);
				b.addBlack(7, 7);
				System.out.println(b);
				
				System.out.println("Adding 5 White Chips:");
				b.addWhite(0, 1);
				b.addWhite(2, 5);
				b.addWhite(5, 7);
				b.addWhite(5, 6);
				b.addWhite(7, 6);
				System.out.println(b);
				
				Board c = new Board(b);
				System.out.println("Testing single parameter constructor which creates new board c" +
				" which copies all contents of b to it");
				System.out.println(c);
				
				System.out.println("Creating new empty board to test isValid()");
				Board a = new Board();
				System.out.println(a);
		
				System.out.println("Creating Move objects:");
				
				//moveKind == QUIT
				Move m1 = new Move();
				
				//Corner positions 
				Move c1 = new Move(0, 0);
				Move c2 = new Move(0, 7);
				Move c3 = new Move(7, 0);
				Move c4 = new Move(7, 7);
				
				//Goals
				Move bg1 = new Move(6, 7);
				Move bg2 = new Move(1, 7);
				Move bg3 = new Move(1, 0);
				Move bg4 = new Move(6, 0);
				
				Move wg1 = new Move(0, 1);
				Move wg2 = new Move(0, 6);
				Move wg3 = new Move(7, 1);
				Move wg4 = new Move(7, 6);
				
				//moveKind == STEP
				//Move s1 = new Move(0,0,1,0);
				
				System.out.println("moveKind == QUIT shoud return false for BLACK: " + a.isValid(m1, BLACK));
				System.out.println("moveKind == QUIT shoud return false for WHITE: " + a.isValid(m1, WHITE));
				System.out.println('\n');
				
				System.out.println("moveKind == ADD in corner1 should return false for BLACK: " + a.isValid(c1, BLACK));
				System.out.println("moveKind == ADD in corner1 should return false for WHITE: " + a.isValid(c1, WHITE));
				System.out.println("moveKind == ADD in corner2 should return false for BLACK: " + a.isValid(c2, BLACK));
				System.out.println("moveKind == ADD in corner2 should return false for WHITE: " + a.isValid(c2, WHITE));
				System.out.println("moveKind == ADD in corner3 should return false for BLACK: " + a.isValid(c3, BLACK));
				System.out.println("moveKind == ADD in corner3 should return false for WHITE: " + a.isValid(c3, WHITE));
				System.out.println("moveKind == ADD in corner4 should return false for BLACK: " + a.isValid(c4, BLACK));
				System.out.println("moveKind == ADD in corner4 should return false for WHITE: " + a.isValid(c4, WHITE));
				System.out.println('\n');
				
				System.out.println("moveKind == ADD in opposite goal for BLACK should return false: " + a.isValid(wg1, BLACK));
				System.out.println("moveKind == ADD in opposite goal for BLACK should return false: " + a.isValid(wg2, BLACK));
				System.out.println("moveKind == ADD in opposite goal for BLACK should return false: " + a.isValid(wg3, BLACK));
				System.out.println("moveKind == ADD in opposite goal for BLACK should return false: " + a.isValid(wg4, BLACK));
				System.out.println('\n');
				
				System.out.println("moveKind == ADD in opposite goal for WHITE should return false: " + a.isValid(bg1, WHITE));
				System.out.println("moveKind == ADD in opposite goal for WHITE should return false: " + a.isValid(bg2, WHITE));
				System.out.println("moveKind == ADD in opposite goal for WHITE should return false: " + a.isValid(bg3, WHITE));
				System.out.println("moveKind == ADD in opposite goal for WHITE should return false: " + a.isValid(bg4, WHITE));
				System.out.println('\n');
				
				System.out.println("moveKind == ADD in goal for WHITE should return true: " + a.isValid(wg1, WHITE));
				System.out.println("moveKind == ADD in goal for WHITE should return true: " + a.isValid(wg2, WHITE));
				System.out.println("moveKind == ADD in goal for WHITE should return true: " + a.isValid(wg3, WHITE));
				System.out.println("moveKind == ADD in goal for WHITE should return true: " + a.isValid(wg4, WHITE));
				System.out.println('\n');
				
				System.out.println("moveKind == ADD in goal for BLACK should return true: " + a.isValid(bg1, BLACK));
				System.out.println("moveKind == ADD in goal for BLACK should return true: " + a.isValid(bg2, BLACK));
				System.out.println("moveKind == ADD in goal for BLACK should return true: " + a.isValid(bg3, BLACK));
				System.out.println("moveKind == ADD in goal for BLACK should return true: " + a.isValid(bg4, BLACK));		
				System.out.println('\n');
		
				//TEST moveKind == STEP
				//System.out.println("moveKind == STEP in opposite goal for Black should return false: " + a.isValid(s1, BLACK));
				//System.out.println("moveKind == STEP in goal for White should return true: " + a.isValid(s1, WHITE));

				//Test seerFcn
				
				Board s = new Board();
				System.out.println("Empty board to test seerFcn: ");
				System.out.println(s);
				System.out.println("adding a few chips to board:");
				s.addBlack(1, 1);
				s.addBlack(1, 4);
				s.addBlack(1, 7);
				s.addBlack(2, 6);
				s.addBlack(4, 0);
				s.addBlack(4, 4);
				s.addBlack(4, 6);
				s.addBlack(5, 5);		
				s.addBlack(6, 2);
				s.addBlack(7, 1);
				s.addBlack(7, 4);
				s.addWhite(3, 5);
				s.addWhite(2, 4);
				s.addWhite(6, 5);
				System.out.println(s);
				
				System.out.println("called seerFcn on BLACK chip at location (4, 4). \n" + 
				"Now printing chip (4, 4) 's coordList, chips it can 'see': ");
				System.out.println(s.myBoard[5][5].coordList);
				
				System.out.println("removing WHITE @ (3, 5)");
				s.removeChip(3, 5, WHITE);
				System.out.println(s);
				System.out.println("updated coordList for BLACK @ (4, 4): ");
				System.out.println(s.myBoard[5][5].coordList);
		*/
		testNetworks();
	}
}

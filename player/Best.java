package player;

public class Best {
	public Move move;
	public double score;
	
	public Best(){
		this.move = null;
		this.score = 0;
	}
	
	public Best(double score){
		this.move = null;
		this.score = score;
	}

	public Best(Move move, double score){
		this.move = move;
		this.score = score;
	}
}
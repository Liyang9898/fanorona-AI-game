package AI_datastructure;

import game.object.Board;

import java.util.ArrayList;

import datastructure.Move;

public class State {
	//instance
	public int v;
	public int alpha;
	public int beta;
	public int maxmin;//1=max 2=min
	public int utility;
	public int dot[][];
	public ArrayList<Move> action;
	
	public int depth;
	public int x_bound;
	public int y_bound;
	public int blackleft;
	public int whiteleft;
	//construct
	public State(Board board,int turn){
		this.maxmin=turn;
		this.action=board.possible_move;
		this.dot=board.dot;
		this.x_bound=board.x_bound;
		this.y_bound=board.y_bound;
		
		//calculate
		this.blackleft=0;
		this.whiteleft=0;
		for(int i=0;i<board.y_bound;i++){
			for(int j=0;j<board.x_bound;j++){
				if(board.dot[j][i]==1){
					this.blackleft++;
					
				}else if(board.dot[j][i]==2){
					this.whiteleft++;
					
				}else{
					
				}
			}
		}
		this.utility=blackleft-whiteleft;
		String tag2="check";
		
	}
	//method
	
	
	
	
	
}

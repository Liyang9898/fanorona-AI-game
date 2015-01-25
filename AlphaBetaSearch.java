package AI;

import game.object.Board;
import AI_datastructure.State;
import android.os.SystemClock;
import datastructure.Move;

public class AlphaBetaSearch {
	//instance
	public int depth;
	public int NodeCount[];
	public long TimeInMax=0;
	public long TimeInMin=0;
	//construct
	public Move Alpha_Beta_Search(State state){
		state.depth=0;
		
		NodeCount=new int[depth+1];
		for(int i=0;i<=depth;i++){NodeCount[i]=0;}
		NodeCount[0]=1;
		Move temp=new Move();
		
		if(state.maxmin==1){
			state.v=MaxValue(state,Integer.MIN_VALUE,Integer.MAX_VALUE);
			
			for(int i=0;i<state.action.size();i++){
				if(state.action.get(i).utility==state.v){
					temp=state.action.get(i);
				}
			}
		}else if(state.maxmin==2){
			state.v=MinValue(state,Integer.MIN_VALUE,Integer.MAX_VALUE);
		
			for(int i=0;i<state.action.size();i++){
				if(state.action.get(i).utility==state.v){
					temp=state.action.get(i);
				}
			}
		}else{
			
		}
		return temp;
		
	}
	
	//method
	public int MaxValue(State state,int alpha,int beta){
		long StartTime=SystemClock.uptimeMillis();
		if(TerminalTest(state)==true){
			return state.utility;
		}

		state.v=Integer.MIN_VALUE;
		for(int i=0;i<state.action.size();i++){
			
			int child_v=MinValue(Result(state,state.action.get(i)),alpha,beta);
			state.action.get(i).utility=child_v;
			
			state.v=Math.max(state.v,child_v);
			
			if(state.v>=beta){
				return state.v;
			}
			alpha=Math.max(alpha, state.v);
			
		}
		long EndTime=SystemClock.uptimeMillis();
		TimeInMax=TimeInMax+EndTime-StartTime;
		return state.v;
	}
	
	public int MinValue(State state,int alpha,int beta){
		long StartTime=SystemClock.uptimeMillis();
		if(TerminalTest(state)==true){
			return state.utility;
		}
		
		state.v=Integer.MAX_VALUE;
		for(int i=0;i<state.action.size();i++){
			int child_v=MaxValue(Result(state,state.action.get(i)),alpha,beta);
			state.action.get(i).utility=child_v;
			state.v=Math.min(state.v, child_v);
			
			if(state.v<=alpha){
				return state.v;
			}
			beta=Math.min(beta, state.v);
		}
		long EndTime=SystemClock.uptimeMillis();
		TimeInMin=TimeInMax+EndTime-StartTime;
		return state.v;
		
	}
	
	public State Result(State state,Move move){
		//copy state to game board
		
		Board board2=new Board();
		board2.x_bound=state.x_bound;
		board2.y_bound=state.y_bound;
		board2.dot=new int[state.x_bound][state.y_bound];
		
		for(int i=0;i<state.y_bound;i++){
			for(int j=0;j<state.x_bound;j++){
				board2.dot[j][i]=state.dot[j][i];
			}
		}
		

		
		board2.Turn=state.maxmin;
		//make your move

		if(move.type==1){
			board2.approach(move,board2.Turn);
		}else if(move.type==2){
			board2.withdraw(move, board2.Turn);
		}else{
			board2.paika(move, board2.Turn);
		}
		
		//copy from game board to state node
		int StateTurn=1;
		if(board2.Turn==1){
			StateTurn=2;
		}else if(board2.Turn==2){
			StateTurn=1;
		}else{
		}
		
		State result=new State(board2,StateTurn);
		result.depth=state.depth+1;
		
		//node count
		NodeCount[result.depth]++;
		return result;
	}
	
	
	public boolean TerminalTest(State state){
		//to be done
		if(state.depth==this.depth){
			return true;
		}else{
			return false;
		}
	}
	
	
}

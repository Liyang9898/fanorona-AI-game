package game.object;

import java.util.ArrayList;

import datastructure.Move;

public class Board {
	public int x_bound;
	public int y_bound;
	public int x_boundmax;
	public int y_boundmax;
	
	public int dot[][];//1 for player1, 2 for player2, 3 for empty
	public int blackleft;
	public int whiteleft;
	public int Turn;
	public boolean MustPaika; 
	public ArrayList<Move> possible_move;
	
	//public ArrayList possible_move_type;
	public Board(){
		
	}
	public Board(int a,int b){
		x_bound=a;
		y_bound=b;
		dot=new int[x_bound][y_bound];
	}
	
	//check approach or withdraw
	public int AWcheck(Move move,int turn){
		int oppo_color=1;
		boolean approachable=false;
		boolean withdrawable=false;
		
		
		//set current opponent color
		if (turn==1) oppo_color=2;
		if (turn==2) oppo_color=1;
		
		//check approach
		if((2*move.x2-move.x1)>=0 && (2*move.x2-move.x1)<=(x_bound-1) && (2*move.y2-move.y1)>=0 && (2*move.y2-move.y1)<=(y_bound-1)){
			if(dot[2*move.x2-move.x1][2*move.y2-move.y1]==oppo_color){
				approachable=true;//can approach
			}else{
				approachable=false;//can't approach
			}
		}
		
		//check withdraw
		if((2*move.x1-move.x2)>=0 && (2*move.x1-move.x2)<=(x_bound-1) && (2*move.y1-move.y2)>=0 && (2*move.y1-move.y2)<=(y_bound-1)){
			if(dot[2*move.x1-move.x2][2*move.y1-move.y2]==oppo_color){
				withdrawable=true;//can withdraw
			}else{
				withdrawable=false;//can't withdraw
			}
		}
		
		//return
		if(approachable==true && withdrawable==false){
			return 1;//approach
		}else if(approachable==false && withdrawable==true){
			return 2;//withdraw
		}else if(approachable==true && withdrawable==true){
			return 3;//both
		}else{
			return 4;//pika
		}

	}
	
	
	
	//approach
	public void approach(Move move,int turn){
		int x_step=move.x2-move.x1;
		int y_step=move.y2-move.y1;
		int x=move.x2+x_step;
		int y=move.y2+y_step;
		
		//set opposite color 
		int oppo_color=1;
		if (turn==1) oppo_color=2;
		if (turn==2) oppo_color=1;
		
		//kill dot
		do{ 
			
			if(x>=0 && y>=0 && x<x_bound && y<y_bound){
				dot[x][y]=3;
				
				if(turn==2){
					blackleft--;
				}else if(turn==1){
					whiteleft--;
				}else{}
			}
			x=x+x_step;
			y=y+y_step;
		}while(x>=0 && y>=0 && x<x_bound && y<y_bound && dot[x][y]==oppo_color);
		
		//move
		dot[move.x1][move.y1]=3;
		dot[move.x2][move.y2]=turn;
		
		//getpossiblemove
		getpossiblemove(oppo_color);
	}
	
	//withdraw
	public void withdraw(Move move,int turn){
		int x_step=move.x1-move.x2;
		int y_step=move.y1-move.y2;
		int x=move.x1+x_step;
		int y=move.y1+y_step;
		
		//set opposite color 
		int oppo_color=1;
		if (turn==1) oppo_color=2;
		if (turn==2) oppo_color=1;
		
		//kill dot
		do{
			if(x>=0 && y>=0 && x<x_bound && y<y_bound){
				dot[x][y]=3;
				if(turn==2){
					blackleft--;
				}else if(turn==1){
					whiteleft--;
				}else{}
			}
			x=x+x_step;
			y=y+y_step;
		}while(x>=0 && y>=0 && x<x_bound && y<y_bound && dot[x][y]==oppo_color);
		
		//move
		dot[move.x1][move.y1]=3;
		dot[move.x2][move.y2]=turn;
		
		//getpossiblemove
		getpossiblemove(oppo_color);
	}	
	
	//paika
	public void paika(Move move,int turn){
		//set opposite color 
		int oppo_color=1;
		if (turn==1) oppo_color=2;
		if (turn==2) oppo_color=1;
		
		//move
		dot[move.x1][move.y1]=3;
		dot[move.x2][move.y2]=turn;
		
		//getpossiblemove
		getpossiblemove(oppo_color);
	}
	

	
	//get possible move
	public void getpossiblemove(int turn){
		MustPaika=false;
		int oppo_color=1;
		//Move temp_move=new Move();
		possible_move=new ArrayList<Move>();
		//possible_move_type=new ArrayList();
		
		if (turn==1) oppo_color=2;
		if (turn==2) oppo_color=1;
		
		//non paika check
		for(int i=0;i<y_bound;i++){
			for(int j=0;j<x_bound;j++){
				if(dot[j][i]==turn){//check every dot,if it's the color, then
					
					//check for withdraw
					for(int step_y=-1;step_y<=1;step_y++){
						for(int step_x=-1;step_x<=1;step_x++){
							if(step_x==0 && step_y==0){
								continue;
							}
							//check bound
							if(j+step_x<0 || j+step_x>=x_bound || i+step_y<0 || i+step_y>=y_bound || j-step_x<0 || j-step_x>=x_bound || i-step_y<0 || i-step_y>=y_bound){
								continue;
							}
							
							if(((i-j)%2)!=0 && (step_x*step_y)!=0){
								continue;
							}
							
							if(dot[j+step_x][i+step_y]==oppo_color && dot[j-step_x][i-step_y]==3){
								Move temp_move=new Move();
								temp_move.x1=j;temp_move.y1=i;
								temp_move.x2=j-step_x;temp_move.y2=i-step_y;
								temp_move.type=2;
								possible_move.add(temp_move);
								
								
							}
							
							
						}
					}
					
					//check for approach
					for(int step_y=-1;step_y<=1;step_y++){
						for(int step_x=-1;step_x<=1;step_x++){
							if(step_x==0 && step_y==0){
								continue;
							}
							//check bound
							if(j+step_x<0 || j+step_x>=x_bound || i+step_y<0 || i+step_y>=y_bound || j+2*step_x<0 || j+2*step_x>=x_bound || i+2*step_y<0 || i+2*step_y>=y_bound){
								continue;
							}
							
							if(((i-j)%2)!=0 && (step_x*step_y)!=0){
								continue;
							}
							
							if(dot[j+2*step_x][i+2*step_y]==oppo_color && dot[j+step_x][i+step_y]==3){
								Move temp_move=new Move();
								temp_move.x1=j;temp_move.y1=i;
								temp_move.x2=j+step_x;temp_move.y2=i+step_y;
								temp_move.type=1;
								possible_move.add(temp_move);
								
							}
							
							
						}
					}//check for approach
				
				}
			}
		}//non paika check done
		
		
		if(possible_move.size()==0){//paika move
			MustPaika=true;
			getpaikamove(turn);
		}
		
	}
	
	public void getpaikamove(int turn){
		int oppo_color=1;
		possible_move=new ArrayList<Move>();
		
		if (turn==1) oppo_color=2;
		if (turn==2) oppo_color=1;
		
		for(int i=0;i<y_bound;i++){
			for(int j=0;j<x_bound;j++){
				if(dot[j][i]==turn){//match color
					for(int step_y=-1;step_y<=1;step_y++){//8 direction
						for(int step_x=-1;step_x<=1;step_x++){
							if(step_x==0 && step_y==0){//cancel itself
								continue;								
							}
							//out of bound
							if(j+step_x<0 || j+step_x>=x_bound || i+step_y<0 || i+step_y>=y_bound){
								continue;
							}
							//no way to go
							if(((i-j)%2)!=0 && (step_x*step_y)!=0){
								continue;
							}
							if(dot[j+step_x][i+step_y]==3){
								Move temp_move=new Move();
								temp_move.x1=j;temp_move.y1=i;
								temp_move.x2=j+step_x;temp_move.y2=i+step_y;
								temp_move.type=4;
								possible_move.add(temp_move);	
							}
							
							
						}
					}
				}
			}
		}
		
		
	}
	
	
}

package activity;


import game.object.Board;

import java.util.Timer;
import java.util.TimerTask;

import AI.AlphaBetaSearch;
import AI_datastructure.Info;
import AI_datastructure.State;
import activity.fanorona.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import datastructure.Move;

public class MainActivity extends ActionBarActivity {
	//common
	int turn;
	Move move=new Move();
	Move select=new Move();
	int boardsize;
	Board board;
	Timer timer;
	MyTimerTask1 callPlayer1;
	MyTimerTask2 callPlayer2;
	boolean wait;
	boolean ingame;
	int humanposition;
	int AITreeDepth;
	int player1;//1=human,2=AI
	int player2;
	
	//AI-Info
	Info AIInfo=new Info();
	
	
	
	
	class MyTimerTask1 extends TimerTask {
		  @Override
		  public void run() {
		  	   runOnUiThread(new Runnable(){
		  	   @Override
			   public void run() {
			    	player1();
			   }});
		  }
	 }
	
	class MyTimerTask2 extends TimerTask {
		  @Override
		  public void run() {
		  	   runOnUiThread(new Runnable(){
		  	   @Override
			   public void run() {
			    	player2();
			   }});
		  }
	 }
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//in here, you set view, start game, and arrange button
		setContentView(R.layout.fragment_main);
		//GET PLAYER SETTING
		Intent intent = getIntent();
		boardsize=intent.getIntExtra("board", 0);
		player1=intent.getIntExtra("player1", 0);
		player2=intent.getIntExtra("player2", 0);
		AITreeDepth=intent.getIntExtra("Difficulty", 0);
		
		board=new Board(boardsize,boardsize);
		board.x_boundmax=5;
		board.y_boundmax=5;
		ImageView boardP=(ImageView)findViewById(R.id.imageView1);
		if(boardsize==5){
			boardP.setImageResource(R.drawable.board5x5);
		}else if(boardsize==3){
			boardP.setImageResource(R.drawable.board3x3);
		}else{
			
		}
		
		//arrange button
		dotoncreate();
		
		Button b_b = (Button) findViewById(R.id.buttonback);//restart button
		b_b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				finish();
			}
		});
		
		Button b_reset = (Button) findViewById(R.id.buttonrestart);//restart button
		b_reset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				reset();
			}
		});
		
		Button b_start = (Button) findViewById(R.id.startgame);//start button
		b_start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				startgame();
			}
		});
		 
		
		
		Button b_possiblemove = (Button) findViewById(R.id.button1);//hint button
		b_possiblemove.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {	
				Move possi;
				TextView textpossi = (TextView) findViewById(R.id.textView1);
				String text="Hint:You have "+Integer.toString(board.possible_move.size())+" possible moves\n";
				if(board.possible_move.size()!=0){
					for(int i=0;i<board.possible_move.size();i++){
						possi=board.possible_move.get(i);
						String type="";
						if(possi.type==1){
							type="Approach";
						}else if(possi.type==2){
							type="Withdraw";
						}else if(possi.type==4){
							type="Paika";
						}else{
							
						}
						text=text+"From ("+Integer.toString(possi.x1)+","+Integer.toString(possi.y1)+") To("+Integer.toString(possi.x2)+","+Integer.toString(possi.y2)+") "+type+"\n";
					}
					textpossi.setText(text);				
				}else{
					textpossi.setText("No capture available");
				}
			}
		});
		
		
		Button b_AI = (Button) findViewById(R.id.buttonAI);//start button
		b_AI.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				ShowAI(v);
			}
		});
				
		//reset game
		reset();	
	}

	//start game
	public void startgame(){
		ingame=true;
		//call player
		if(turn==1){
			player1();
		}else if(turn==2){
			player2();
		}else{
			
		}
		
	}
	
	//main game control for human
	public void game(){
		int result=board.AWcheck(move, turn);
		
		if(result==1){
			approach();
		}else if(result==2){
			withdraw();
		}else if(result==3){
			open();
		}else{
			paika();
		}
	}
	
	//main game control for AI
	public void GameAI(){
		if(move.type==1){
			approach();
		}else if(move.type==2){
			withdraw();
		}else if(move.type==4){
			paika();
		}else{
		}
	}
	
	
	//apply move-approach
	public void approach(){
		board.approach(move, turn);
		cleanup();
	}
	
	//apply move-withdraw
	public void withdraw(){
		board.withdraw(move, turn);
		cleanup();
	}
	
	//apply move-paika
	public void paika(){
		board.paika(move, turn);
		cleanup();
	}
	
	//clean up
	public void cleanup(){
		//render
		render();
		
  	
		//change turn
		if(turn==1){
			turn=2;
		}else{
			turn=1;
		}
		
		//show whose turn
		TextView textturn = (TextView) findViewById(R.id.textturn);
		if (turn==1){
			textturn.setText("Black's turn");
		}else{
			textturn.setText("White's turn");
		}
		
		//win condition check
		showscore();
		
		if(board.blackleft==0){
			textturn.setText("White wins!");
			ingame=false;
		}
		if(board.whiteleft==0){
			textturn.setText("Black wins!");
			ingame=false;
		}		
		
		//clear move
		move.x1=-1;move.y1=-1;move.x2=-1;move.y2=-1;
		select.x1=-1;select.y1=-1;select.x2=-1;select.y2=-1;
		
		
		//wait and call player
		timer=new Timer();
		if(turn==1){
			callPlayer1=new MyTimerTask1();
			timer.schedule(callPlayer1, 1000);
		}else if(turn==2){
			callPlayer2=new MyTimerTask2();
			timer.schedule(callPlayer2, 1000);
		}else{
			
		}
		
	}
	
	//player 1
	public void player1(){
		if(player1==2){
			AIClickEvent();
		}else{
			
		}
	}
	
	//player 2
	public void player2(){
		if(player2==2){
			AIClickEvent();
		}else{
			
		}
	}
	
	//start game
	public void reset(){
		//set player
		//player1=1;//1=human,2=AI
		//player2=2;
		
		
		
		//new game
		ingame=false;
		turn=2;
		
		//AITreeDepth=5;
		if(boardsize==5){
			board.dot[0][0]=1;board.dot[1][0]=1;board.dot[2][0]=1;board.dot[3][0]=1;board.dot[4][0]=1;
			board.dot[0][1]=1;board.dot[1][1]=1;board.dot[2][1]=1;board.dot[3][1]=1;board.dot[4][1]=1;
			board.dot[0][2]=1;board.dot[1][2]=2;board.dot[2][2]=3;board.dot[3][2]=1;board.dot[4][2]=2;
			board.dot[0][3]=2;board.dot[1][3]=2;board.dot[2][3]=2;board.dot[3][3]=2;board.dot[4][3]=2;
			board.dot[0][4]=2;board.dot[1][4]=2;board.dot[2][4]=2;board.dot[3][4]=2;board.dot[4][4]=2;
		}else if(boardsize==3){
			board.dot[0][0]=2;board.dot[1][0]=2;board.dot[2][0]=2;
			board.dot[0][1]=2;board.dot[1][1]=3;board.dot[2][1]=1;
			board.dot[0][2]=1;board.dot[1][2]=1;board.dot[2][2]=1;
		}else{}

		
		//test
		/*
		board.dot[0][0]=3;board.dot[1][0]=3;board.dot[2][0]=3;board.dot[3][0]=3;board.dot[4][0]=3;
		board.dot[0][1]=3;board.dot[1][1]=3;board.dot[2][1]=3;board.dot[3][1]=3;board.dot[4][1]=3;
		board.dot[0][2]=2;board.dot[1][2]=3;board.dot[2][2]=3;board.dot[3][2]=1;board.dot[4][2]=3;
		board.dot[0][3]=3;board.dot[1][3]=3;board.dot[2][3]=3;board.dot[3][3]=3;board.dot[4][3]=3;
		board.dot[0][4]=3;board.dot[1][4]=3;board.dot[2][4]=3;board.dot[3][4]=3;board.dot[4][4]=3;		
		*/
		
		
		//count dots
		int blackleft2=0;
		int whiteleft2=0;
		for(int i=0;i<board.x_bound;i++){
			for(int j=0;j<board.y_bound;j++){
				if(board.dot[j][i]==1){
					blackleft2++;
				}else if(board.dot[j][i]==2){
					whiteleft2++;
				}else{	
				}
			}
		}
		board.blackleft=blackleft2;
		board.whiteleft=whiteleft2;
		showscore();
		
		move.x1=-1;move.y1=-1;move.x2=-1;move.y2=-1;         
		select.x1=-1;select.y1=-1;select.x2=-1;select.y2=-1;
		
		//show whose turn
		TextView textturn = (TextView) findViewById(R.id.textturn);
		if (turn==1){
			textturn.setText("Black's turn");
		}else{
			textturn.setText("White's turn");
		}
		
		//render
		render();
		
		//getpossiblemove
		board.getpossiblemove(turn);
		
		//AI-info help
		AIInfo.nodecount=new int[AITreeDepth+1];
		AIInfo.nodecount[0]=1;
		for(int i=1;i<=AITreeDepth;i++){
			AIInfo.nodecount[i]=0;
		}
		AIInfo.TimeInMax=0;
		AIInfo.TimeInMin=0;
	}
	
	//AI click event
	public void AIClickEvent(){
		
		AlphaBetaSearch search=new AlphaBetaSearch();
		search.depth=AITreeDepth;
		State state=new State(board,turn);
		state.depth =0;
		move=search.Alpha_Beta_Search(state);//AI find next move!!!!--most important part of this game
		AIInfo.nodecount=search.NodeCount;
		AIInfo.TimeInMax=AIInfo.TimeInMax+search.TimeInMax;
		AIInfo.TimeInMin=AIInfo.TimeInMin+search.TimeInMin;
		GameAI();
	}
	
	//click event
	public void copy_coordinate(int click_x,int click_y){
		if(ingame==true && wait==false){
			//TextView text1 = (TextView) findViewById(R.id.textView1);
			//text1.setText("COPY");
			//click the same color dot
			if(move.x2==-1 && board.dot[click_x][click_y]==turn){
				//text1.setText("COPY1");
				//copy move	
				move.x1=click_x;
				move.y1=click_y;
				//select-view-aid
				select.x2=click_x;
				select.y2=click_y;
				selectrender(select);
				select.x1=select.x2;
				select.y1=select.y2;
			}
			//click the empty dot
			if(board.dot[click_x][click_y]==3 && move.x1!=-1 && move.x2==-1){//make sure it's an empty slot and only the dot is selected, not the empty slot
				if(Math.abs(click_x-move.x1)<=1 && Math.abs(click_y-move.y1)<=1){//make sure you only move one step
					if((move.x1-move.y1)%2==0 || ((move.x1-move.y1)%2!=0 && (move.x1==click_x || move.y1==click_y))){//make sure dots move on lines
						//check if paika / check if capturable
						Move temp_move2=new Move();
						temp_move2.x1=move.x1;temp_move2.y1=move.y1;
						temp_move2.x2=click_x;temp_move2.y2=click_y;
						
						
						if(board.MustPaika=false && board.AWcheck(temp_move2, turn)==4){//if is paika && capture able
							//message
							Toast.makeText(getBaseContext(),"Capture is mandatory!",
							Toast.LENGTH_SHORT).show();
							TextView textpossi = (TextView) findViewById(R.id.textView1);
							Move possi=board.possible_move.get(0);
							textpossi.setText("From"+Integer.toString(possi.x1)+" "+Integer.toString(possi.y1)+"To"+Integer.toString(possi.x2)+" "+Integer.toString(possi.y2)+" "+Integer.toString(board.possible_move.size()));
							// possi=board.possible_move.get(1);
							
						}else{
							//apply the move
							move.x2=click_x;
							move.y2=click_y;
							game();
						}
					// 
					}
				}
			}
		}
	}
	
	//Dialog A/W
	public void open(){
	      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	      alertDialogBuilder.setMessage("Approach or Withdraw?");
	      alertDialogBuilder.setPositiveButton("Approach", 
	      new DialogInterface.OnClickListener() {
			
	         @Override
	         public void onClick(DialogInterface arg0, int arg1) {
	        	 approach();
	         }
	      });
	      alertDialogBuilder.setNegativeButton("Withdraw", 
	      new DialogInterface.OnClickListener() {
				
	         @Override
	         public void onClick(DialogInterface dialog, int which) {
	        	 withdraw();
			 }
	      });
		    
	      AlertDialog alertDialog = alertDialogBuilder.create();
	      alertDialog.show();
		    
 }
	
	//score
	public void showscore(){
		TextView textscore = (TextView) findViewById(R.id.textscore);
		textscore.setText("Black vs White="+Integer.toString(board.blackleft)+":"+Integer.toString(board.whiteleft));
	}
	
	//select render
	public void selectrender(Move select){
		//set control
		ImageButton b[][]=new ImageButton[board.x_boundmax][board.x_boundmax];
		b[0][0] = (ImageButton) findViewById(R.id.ImageButton00);
		b[1][0] = (ImageButton) findViewById(R.id.ImageButton10);
		b[2][0] = (ImageButton) findViewById(R.id.ImageButton20);
		b[3][0] = (ImageButton) findViewById(R.id.ImageButton30);
		b[4][0] = (ImageButton) findViewById(R.id.ImageButton40);
		
		b[0][1] = (ImageButton) findViewById(R.id.ImageButton01);
		b[1][1] = (ImageButton) findViewById(R.id.ImageButton11);
		b[2][1] = (ImageButton) findViewById(R.id.ImageButton21);
		b[3][1] = (ImageButton) findViewById(R.id.ImageButton31);
		b[4][1] = (ImageButton) findViewById(R.id.ImageButton41);
		
		b[0][2] = (ImageButton) findViewById(R.id.ImageButton02);
		b[1][2] = (ImageButton) findViewById(R.id.ImageButton12);
		b[2][2] = (ImageButton) findViewById(R.id.ImageButton22);
		b[3][2] = (ImageButton) findViewById(R.id.ImageButton32);
		b[4][2] = (ImageButton) findViewById(R.id.ImageButton42);
		
		b[0][3] = (ImageButton) findViewById(R.id.ImageButton03);
		b[1][3] = (ImageButton) findViewById(R.id.ImageButton13);
		b[2][3] = (ImageButton) findViewById(R.id.ImageButton23);
		b[3][3] = (ImageButton) findViewById(R.id.ImageButton33);
		b[4][3] = (ImageButton) findViewById(R.id.ImageButton43);
		
		b[0][4] = (ImageButton) findViewById(R.id.ImageButton04);
		b[1][4] = (ImageButton) findViewById(R.id.ImageButton14);
		b[2][4] = (ImageButton) findViewById(R.id.ImageButton24);
		b[3][4] = (ImageButton) findViewById(R.id.ImageButton34);
		b[4][4] = (ImageButton) findViewById(R.id.ImageButton44);
			
		if(turn==1){
			if (select.x1!=-1){
				b[select.x1][select.y1].setImageResource(R.drawable.black); 	
			}
			b[select.x2][select.y2].setImageResource(R.drawable.blackselect);
		}else{
			if (select.x1!=-1){
				b[select.x1][select.y1].setImageResource(R.drawable.white); 
			}
			b[select.x2][select.y2].setImageResource(R.drawable.whiteselect);
		}
	}
	
	//render
	public void render(){
		//set control
		ImageButton b[][]=new ImageButton[board.x_boundmax][board.y_boundmax];
		b[0][0] = (ImageButton) findViewById(R.id.ImageButton00);
		b[1][0] = (ImageButton) findViewById(R.id.ImageButton10);
		b[2][0] = (ImageButton) findViewById(R.id.ImageButton20);
		b[3][0] = (ImageButton) findViewById(R.id.ImageButton30);
		b[4][0] = (ImageButton) findViewById(R.id.ImageButton40);
		
		b[0][1] = (ImageButton) findViewById(R.id.ImageButton01);
		b[1][1] = (ImageButton) findViewById(R.id.ImageButton11);
		b[2][1] = (ImageButton) findViewById(R.id.ImageButton21);
		b[3][1] = (ImageButton) findViewById(R.id.ImageButton31);
		b[4][1] = (ImageButton) findViewById(R.id.ImageButton41);
		
		b[0][2] = (ImageButton) findViewById(R.id.ImageButton02);
		b[1][2] = (ImageButton) findViewById(R.id.ImageButton12);
		b[2][2] = (ImageButton) findViewById(R.id.ImageButton22);
		b[3][2] = (ImageButton) findViewById(R.id.ImageButton32);
		b[4][2] = (ImageButton) findViewById(R.id.ImageButton42);
		
		b[0][3] = (ImageButton) findViewById(R.id.ImageButton03);
		b[1][3] = (ImageButton) findViewById(R.id.ImageButton13);
		b[2][3] = (ImageButton) findViewById(R.id.ImageButton23);
		b[3][3] = (ImageButton) findViewById(R.id.ImageButton33);
		b[4][3] = (ImageButton) findViewById(R.id.ImageButton43);
		
		b[0][4] = (ImageButton) findViewById(R.id.ImageButton04);
		b[1][4] = (ImageButton) findViewById(R.id.ImageButton14);
		b[2][4] = (ImageButton) findViewById(R.id.ImageButton24);
		b[3][4] = (ImageButton) findViewById(R.id.ImageButton34);
		b[4][4] = (ImageButton) findViewById(R.id.ImageButton44);
		
		//wipe empty
		for(int i=0;i<board.x_boundmax;i++){
			for(int j=0;j<board.y_boundmax;j++){
				
					b[j][i].setImageResource(R.drawable.empty);
				
			}
		}
		
		
		//render
		for(int i=0;i<board.x_bound;i++){
			for(int j=0;j<board.y_bound;j++){
				if(board.dot[j][i]==1){
					b[j][i].setImageResource(R.drawable.black);
				}else if(board.dot[j][i]==2){
					b[j][i].setImageResource(R.drawable.white);
				}else{
					b[j][i].setImageResource(R.drawable.empty);
				}
			}
		}

	}
	
	//arrange button
	public void dotoncreate(){
		//dot 00
		ImageButton b00 = (ImageButton) findViewById(R.id.ImageButton00);
		b00.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=0;int y=0;
				copy_coordinate(x,y);
			}
		});

		//dot 01
		ImageButton b01 = (ImageButton) findViewById(R.id.ImageButton01);
		b01.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=0;int y=1;
				copy_coordinate(x,y);
			}
		});
		
		//dot 02
		ImageButton b02 = (ImageButton) findViewById(R.id.ImageButton02);
		b02.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=0;int y=2;
				copy_coordinate(x,y);
			}
		});
		
		//dot 03
		ImageButton b03 = (ImageButton) findViewById(R.id.ImageButton03);
		b03.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=0;int y=3;
				copy_coordinate(x,y);
			}
		});
		
		//dot 04
		ImageButton b04 = (ImageButton) findViewById(R.id.ImageButton04);
		b04.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=0;int y=4;
				copy_coordinate(x,y);
			}
		});
		
		////////////////////////////////////
		//dot 10
		ImageButton b10 = (ImageButton) findViewById(R.id.ImageButton10);
		b10.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=1;int y=0;
				copy_coordinate(x,y);
			}
		});

		//dot 11
		ImageButton b11 = (ImageButton) findViewById(R.id.ImageButton11);
		b11.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=1;int y=1;
				copy_coordinate(x,y);
			}
		});
		
		//dot 12
		ImageButton b12 = (ImageButton) findViewById(R.id.ImageButton12);
		b12.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=1;int y=2;
				copy_coordinate(x,y);
			}
		});
		
		//dot 13
		ImageButton b13 = (ImageButton) findViewById(R.id.ImageButton13);
		b13.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=1;int y=3;
				copy_coordinate(x,y);
			}
		});
		
		//dot 14
		ImageButton b14 = (ImageButton) findViewById(R.id.ImageButton14);
		b14.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=1;int y=4;
				copy_coordinate(x,y);
			}
		});
		
		////////////////////////////////////
		//dot 20
		ImageButton b20 = (ImageButton) findViewById(R.id.ImageButton20);
		b20.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=2;int y=0;
				copy_coordinate(x,y);
			}
		});

		//dot 21
		ImageButton b21 = (ImageButton) findViewById(R.id.ImageButton21);
		b21.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=2;int y=1;
				copy_coordinate(x,y);
			}
		});
		
		//dot 22
		ImageButton b22 = (ImageButton) findViewById(R.id.ImageButton22);
		b22.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=2;int y=2;
				copy_coordinate(x,y);
			}
		});
		
		//dot 23
		ImageButton b23 = (ImageButton) findViewById(R.id.ImageButton23);
		b23.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=2;int y=3;
				copy_coordinate(x,y);
			}
		});
		
		//dot 24
		ImageButton b24 = (ImageButton) findViewById(R.id.ImageButton24);
		b24.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=2;int y=4;
				copy_coordinate(x,y);
			}
		});
		
		////////////////////////////////////
		//dot 30
		ImageButton b30 = (ImageButton) findViewById(R.id.ImageButton30);
		b30.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=3;int y=0;
				copy_coordinate(x,y);
			}
		});

		//dot 31
		ImageButton b31 = (ImageButton) findViewById(R.id.ImageButton31);
		b31.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=3;int y=1;
				copy_coordinate(x,y);
			}
		});
		
		//dot 32
		ImageButton b32 = (ImageButton) findViewById(R.id.ImageButton32);
		b32.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=3;int y=2;
				copy_coordinate(x,y);
			}
		});
		
		//dot 33
		ImageButton b33 = (ImageButton) findViewById(R.id.ImageButton33);
		b33.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=3;int y=3;
				copy_coordinate(x,y);
			}
		});
		
		//dot 34
		ImageButton b34 = (ImageButton) findViewById(R.id.ImageButton34);
		b34.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=3;int y=4;
				copy_coordinate(x,y);
			}
		});
		
		////////////////////////////////////

		//dot 40
		ImageButton b40 = (ImageButton) findViewById(R.id.ImageButton40);
		b40.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=4;int y=0;
				copy_coordinate(x,y);
			}
		});

		//dot 41
		ImageButton b41 = (ImageButton) findViewById(R.id.ImageButton41);
		b41.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=4;int y=1;
				copy_coordinate(x,y);
			}
		});
		
		//dot 42
		ImageButton b42 = (ImageButton) findViewById(R.id.ImageButton42);
		b42.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=4;int y=2;
				copy_coordinate(x,y);
			}
		});
		
		//dot 43
		ImageButton b43 = (ImageButton) findViewById(R.id.ImageButton43);
		b43.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=4;int y=3;
				copy_coordinate(x,y);
			}
		});
		
		//dot 44
		ImageButton b44 = (ImageButton) findViewById(R.id.ImageButton44);
		b44.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				int x=4;int y=4;
				copy_coordinate(x,y);
			}
		});
		
		////////////////////////////////////

	}
	
	//dot on create
	public void ShowAI(View v){
		Intent intent = new Intent(this, AIInfo.class);
		intent.putExtra("depth", AITreeDepth);
		intent.putExtra("nodecount", AIInfo.nodecount);
		intent.putExtra("timeinmax", AIInfo.TimeInMax);
		intent.putExtra("timeinmin", AIInfo.TimeInMin);
	    startActivity(intent);
	}
	

	
	

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/


	/*
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
*/
}

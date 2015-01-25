package activity;

import java.util.ArrayList;
import java.util.List;

import activity.fanorona.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class StartSetting extends ActionBarActivity {
	int player1c=0;
	int player2c=0;
	int boardsize=0;
	int DiffiLevel=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_start_setting);

    	//builder spinner
  	    Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
  	    List list=new ArrayList();
  	    for(int i=1;i<=5;i++){
  		    list.add(i);
  	    }
  	    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
  	    spinner1.setAdapter(dataAdapter);
  	    		
		
		Button b_start = (Button) findViewById(R.id.button1);//start button
		b_start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				
				RadioGroup player1 = (RadioGroup) findViewById(R.id.radioGroup1); 
				switch(player1.getCheckedRadioButtonId()){
					case R.id.radio0:player1c=1;
					break;
					case R.id.radio1:player1c=2;
					break;
				}
				
				RadioGroup player2 = (RadioGroup) findViewById(R.id.radioGroup2); 
				switch(player2.getCheckedRadioButtonId()){
				case R.id.radio0:player2c=1;
				break;
				case R.id.radio1:player2c=2;
				break;
				}
				
				RadioGroup board = (RadioGroup) findViewById(R.id.radioGroup3); 
				switch(board.getCheckedRadioButtonId()){
				case R.id.radio0:boardsize=3;
				break;
				case R.id.radio1:boardsize=5;
				break;
				}
				
				
				
		    	//GET DIFFICULT
		    	Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
		    	DiffiLevel=spinner1.getSelectedItemPosition()+1;
		    	
		    	
	   
		    	
				play(v);
			}
		});
		 
		
	}

	
	public void play(View v){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("player1", player1c);
		intent.putExtra("player2", player2c);
		intent.putExtra("Difficulty", DiffiLevel);
		intent.putExtra("board", boardsize);
	    startActivity(intent);
	}
	
}

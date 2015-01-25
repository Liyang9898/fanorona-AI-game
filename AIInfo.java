package activity;

import activity.fanorona.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AIInfo extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		int depth;
		depth=intent.getIntExtra("depth",1);
		int a[]=intent.getIntArrayExtra("nodecount");		
		
		setContentView(R.layout.fragment_aiinfo);
		String textCutoff="Cutoff: Yes\n\n";
		String textDepth="Tree depth: "+Integer.toString(depth)+"\n\n";
		
		String textNodeCount="Nodes count:\n"+"Level 0: "+Integer.toString(a[0])+" nodes\n";
		int totalnode=1;
		for(int i=1;i<=depth;i++){
			textNodeCount=textNodeCount+"Level "+i+": "+Integer.toString(a[i])+" nodes\n";
			totalnode=totalnode+a[i];
		}
		textNodeCount=textNodeCount+"Total nodes: "+Integer.toString(totalnode)+"\n\n";		
		
		long timeinmax=intent.getLongExtra("timeinmax", 1);
		long timeinmin=intent.getLongExtra("timeinmin", 1);
		String TimeInfo="Time in max nodes: "+timeinmax+"milliseconds\n"+"Time in min nodes: "+timeinmin+"milliseconds\n";
		
		
		
		
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setText(textCutoff+textDepth+textNodeCount+TimeInfo);

		
		Button b_back = (Button) findViewById(R.id.button1);//restart button
		b_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		    	
				finish();
			}
		});	
		
		
	}

	

}

package com.hangman;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
//import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;



public class GameActivity extends Activity implements OnClickListener {

	private String[] words;
	private String[] hints;
	
	private static final String TAG = "hangman";
	private int Rindex,hScore;
	TextView scorex;
	//random for word selection
	private Random rand;
	//store the current word
	private String curntWrd, scorekey;
	//the layout holding the answer
	private LinearLayout wordLayout;
	//text views for each letter in the answer
	private TextView[] charViews;
	//letter button grid
	private GridView letters;
	//letter button adapter
	private LtrAdp ltrAdapt;
	//body part images
	private ImageView[] bodyParts;
	//total parts //current part //num of chars in word //num of correct words
	private int numParts=6, currPart, numChars, numCorr;
	
	//help
	private AlertDialog helpAlert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Log.v(TAG, "inside oncreate method");
		
		 //read answer words in TO-DO MAKE this DATABASE
		Resources gres = getResources();
		words = gres.getStringArray(R.array.words);
		hints = gres.getStringArray(R.array.hints);
		//create a new random
		rand = new Random();
		//initialize word
		curntWrd="";
		
		//get answer area
		wordLayout = (LinearLayout)findViewById(R.id.word);

		//get letter button grid
		letters = (GridView)findViewById(R.id.letters);

		//get body part images
		bodyParts = new ImageView[numParts];
		bodyParts[0] = (ImageView)findViewById(R.id.head);
		bodyParts[1] = (ImageView)findViewById(R.id.body);
		bodyParts[2] = (ImageView)findViewById(R.id.arm1);
		bodyParts[3] = (ImageView)findViewById(R.id.arm2);
		bodyParts[4] = (ImageView)findViewById(R.id.leg1);
		bodyParts[5] = (ImageView)findViewById(R.id.leg2);

		//set home as up
		getActionBar().setDisplayHomeAsUpEnabled(true);

		//start gameplay
		playGame();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		case R.id.action_help:
			showHelp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//play a new game method
	private void playGame(){
	
		Log.v(TAG, "inside playgame method");
		SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		hScore = sp.getInt("scorekey",hScore);
		
		Typeface chalkfnt = Typeface.createFromAsset(getAssets(),"fonts/crayon.ttf");
		//Grabs a new word from the Strings listing 
		
		Rindex = rand.nextInt(words.length);  //used to match hints and Word together
		String HintTxt = hints[Rindex];
		String selWord = words[Rindex];
		
		//make sure not same word as last time
		while(selWord.equals(curntWrd)){
			
			Rindex = rand.nextInt(words.length);
			selWord = words[Rindex];
			HintTxt = hints[Rindex];
			 
		}

		//update current word
		curntWrd = selWord;
		
		Log.v(TAG, curntWrd+" was the selected word");
		
		//Hints Score and custom text
		TextView hintview  = (TextView)findViewById(R.id.hintview);
		hintview.setText(HintTxt);
		hintview.setTypeface(chalkfnt);
		
		TextView chardisplay = (TextView)findViewById(R.id.chardisplay);
		chardisplay.setText("The Word is "+curntWrd.length()+" characters in length");
		chardisplay.setTypeface(chalkfnt);
		
		Button gethint = (Button)findViewById(R.id.hintbtn);
		gethint.setOnClickListener(this);
		gethint.setTypeface(chalkfnt);
		
		scorex = (TextView)findViewById(R.id.scorepts);
		scorex.setTypeface(chalkfnt);
		
		
		//create new array for character text views
		charViews = new TextView[curntWrd.length()];

		//remove any existing letters
		wordLayout.removeAllViews();

		//loop through characters separate and display using XML
		for(int c=0; c<curntWrd.length(); c++)
		{
			charViews[c] = new TextView(this);
			//set the current letter in position
			charViews[c].setText(""+curntWrd.charAt(c));
			//set layout
			charViews[c].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT));
			charViews[c].setGravity(Gravity.CENTER);
			charViews[c].setTextColor(Color.BLACK); //Masks the Word BLACK
			charViews[c].setBackgroundResource(R.drawable.letter_bg); //Draws the _
			
			//add to display
			wordLayout.addView(charViews[c]);
			
		}

		//reset adapter
		ltrAdapt=new LtrAdp(this); 
		letters.setAdapter(ltrAdapt);

		//start part at zero
		currPart=0;
		//set word length and correct choices
		numChars=curntWrd.length();
		numCorr=0;

		//hide all parts
		for(int p=0; p<numParts; p++){
			bodyParts[p].setVisibility(View.INVISIBLE);
		}
		

	}

	//letter pressed method
	public void letterPressed(View view){
		//find out which letter was pressed
		String ltr=((TextView)view).getText().toString();
		char letterChar = ltr.charAt(0);
		
		//disable view
		view.setEnabled(false);
		view.setBackgroundResource(R.drawable.letter_down);  //XML for DownPressed
		
		//check if correct
		boolean correct=false;
		
		for(int k=0; k<curntWrd.length(); k++){ 
			if(curntWrd.charAt(k)==letterChar){
				correct=true;
				numCorr++;
				charViews[k].setTextColor(Color.WHITE);
				//Add the Character, in string position
			}
			

		}
		
		//check winning Case
		if(correct){
			if(numCorr==numChars) //If the correct number of correct guesses is the length of the word (GAME ENDS)
			{
				//disable all buttons
				disableBtns();
				
				//Output winning Screen to user - VIA- Custom Window BUILDER
				
				AlertDialog.Builder winnerWindow = new AlertDialog.Builder(this);
				winnerWindow.setTitle("YAY");
				winnerWindow.setMessage("You win!\n\nThe answer was:\n\n"+curntWrd);
				winnerWindow.setPositiveButton("Play Again", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int id) {
						
						SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putInt("scorekey", hScore=hScore+10);
						editor.commit();
						
						scorex = (TextView)findViewById(R.id.scorepts);
						hScore = sp.getInt("scorekey",hScore);
						
						TextView hintview  = (TextView)findViewById(R.id.hintview);
						hintview.setTextColor(Color.BLACK);						
						scorex.setText("Your Score is "+hScore);
						GameActivity.this.playGame();

					}});
				winnerWindow.setNegativeButton("Exit", 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						GameActivity.this.finish();
					}});
				winnerWindow.show();
			}
		}
		
		//check if user still has guesses
		else if(currPart<numParts){
			//show next part
			bodyParts[currPart].setVisibility(View.VISIBLE);
			currPart++;
		}
		else
		{
			
			disableBtns();
			//User has Lost the Game
			AlertDialog.Builder LoserWindow = new AlertDialog.Builder(this);
			LoserWindow.setTitle("WELL-HUNG-M8");
			LoserWindow.setMessage("You lose!\n\nThe answer was:\n\n"+curntWrd);
			LoserWindow.setPositiveButton("Play Again", new DialogInterface.OnClickListener() 
			
			{
				public void onClick(DialogInterface dialog, int id) {
					GameActivity.this.playGame();										
					TextView hintview  = (TextView)findViewById(R.id.hintview);
					hintview.setTextColor(Color.BLACK);															
				}});
			
			LoserWindow.setNegativeButton("Exit", 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					GameActivity.this.finish();
				}});
			LoserWindow.show();

		}
	}

	//disable letter buttons
	public void disableBtns()
	{	
		int numLetters = letters.getChildCount(); //gets number of replicated 
		for(int l=0; l<numLetters; l++){
			letters.getChildAt(l).setEnabled(false); //increment and Disable
		}
	}

	//Help custom window -- similar to the MAIN - HELP -
	
	public void showHelp()
	{
		AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);
		helpBuild.setTitle("Help");
		helpBuild.setMessage("Guess word - try selecting letters.\n\n"
				+ "You have 6 wrong moves then it's over!");
		helpBuild.setPositiveButton("OK", 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				helpAlert.dismiss();
			}});
		helpAlert = helpBuild.create();
		helpBuild.show();
	}
	
	@Override
	public void onClick(View v) 
	{
		
		
		if (v.getId() == R.id.hintbtn)
		{
			TextView hintview  = (TextView)findViewById(R.id.hintview);
			hintview.setTextColor(Color.WHITE);
		
		}
	}

}

package com.hangman;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;




public class MainActivity extends Activity  implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Typeface chalkfnt = Typeface.createFromAsset(getAssets(),"fonts/crayon.ttf");
		
		Button playBtn = (Button)findViewById(R.id.playBtn);
		playBtn.setTypeface(chalkfnt);
		playBtn.setOnClickListener(this);

        Button scoreBtn = (Button)findViewById(R.id.howwhy);
        scoreBtn.setTypeface(chalkfnt);
        scoreBtn.setOnClickListener(this);
		
		Button ExitBtn = (Button)findViewById(R.id.ExitBtn);
		ExitBtn.setTypeface(chalkfnt);
        ExitBtn.setOnClickListener(this);
		

	}

    @Override
    public void onClick(View view) {
        //handle clicks
        if (view.getId() == R.id.playBtn) {
            Intent playIntent = new Intent(this, GameActivity.class);
            this.startActivity(playIntent);
        } else if (view.getId() == R.id.howwhy) {
            Intent howIntent = new Intent(this, HowToActivity.class);
            this.startActivity(howIntent);
        } else if (view.getId() == R.id.ExitBtn) {
            finish();
            System.exit(0);}
    }
}

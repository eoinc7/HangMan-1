package com.hangman;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HowToActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view from activity_how_to.xml
        setContentView(R.layout.activity_how_to);
        
        Typeface chalkfnt = Typeface.createFromAsset(getAssets(),"fonts/crayon.ttf");

        Button returnToMain = (Button)findViewById(R.id.returnToMain);
        returnToMain.setTypeface(chalkfnt);
        returnToMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //handle clicks
       if (view.getId()==R.id.returnToMain){
           onBackPressed();
        }
}

}
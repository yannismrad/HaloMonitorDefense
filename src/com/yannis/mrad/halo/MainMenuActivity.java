package com.yannis.mrad.halo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	private Button play, help,intro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		this.play = (Button)findViewById(R.id.playButton);
		this.help= (Button)findViewById(R.id.helpButton);
		this.intro= (Button)findViewById(R.id.helpButton);

		/*
		 * Listeners de clic sur un bouton pour lancer une activité
		 */

		this.play.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainMenuActivity.this, GameActivity.class);
				startActivity(i);	
			}

		});

		this.help.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Intent i = new Intent(MainMenuActivity.this, HelpActivity.class);
				//startActivity(i);	
			}

		});

		this.intro.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Intent i = new Intent(MainMenuActivity.this, IntroActivity.class);
				//startActivity(i);	
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

}

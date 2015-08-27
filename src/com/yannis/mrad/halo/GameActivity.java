package com.yannis.mrad.halo;

import java.util.ArrayList;
import java.util.Timer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.yannis.mrad.halo.interfaces.LoaderListener;
import com.yannis.mrad.halo.interfaces.SelectionListener;
import com.yannis.mrad.halo.tools.GameEngine;
import com.yannis.mrad.halo.tools.GameGLSurfaceView;
import com.yannis.mrad.halo.tools.GameRenderer;
import com.yannis.mrad.halo.tools.StaticVars;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity {
	private GameGLSurfaceView mGLView;
	private GameRenderer renderer;
	private GameEngine engine;
	private FrameLayout topMenu;
	private RelativeLayout waveMessageLayout;
	private Animation animationUp, animationDown, animationFadeIn, animationFadeOut;
	private boolean loadingScreenDisplayed;
	private Dialog loadingDialog;
	private ArrayList<Timer> timers;
	private ArrayList<CountDownTimer> countDowntimers;
	private TextView lifePointsText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timers = new ArrayList<Timer>();
		countDowntimers = new ArrayList<CountDownTimer>();
		setContentView(R.layout.activity_game);
		initGlSurfaceView();
		this.engine = new GameEngine(this, renderer);
		initUIElements();
		createTopMenu();
		createWaveMessage();
		displayLoadingScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}
	
	/**
	 * Méthode d'initialisation de certains éléments de l'interface utilisateur
	 */
	public void initUIElements()
	{
		lifePointsText = (TextView) findViewById(R.id.lifePoints);
		lifePointsText.setText(""+engine.getLifePoints());
	}
	
	public void updateLifePointsCounter()
	{
		
	}

	/**
	 * Initialiser le GLSurfaceView
	 */
	public void initGlSurfaceView()
	{
		mGLView = new GameGLSurfaceView(getApplication());
		FrameLayout fl = (FrameLayout) findViewById(R.id.frameLayout);
		mGLView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		fl.addView(mGLView);

		LinearLayout topBar = (LinearLayout)findViewById(R.id.topBar);
		topBar.bringToFront();

		topBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("TAG", "top bar touched");
			}
		});

		mGLView.setEGLContextClientVersion(2); //OPEN GL ES 2.0

		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});

		SelectionListener selecListener = setupSelectionListener();

		renderer = new GameRenderer(this);
		renderer.setSelectionListener(selecListener);
		renderer.setLoaderListener(new LoaderListener(){

			@Override
			public void onLoadingFinished() {
				Log.d("TAG","Loading is finished");
				removeLoadingScreen();
				engine.startWaveTimer();

			}

		});
		mGLView.setRenderer(renderer);
		mGLView.enableTouchEvents();
	}
	
	/**
	 * Méthode de mise à jour du compteur de points de vie à l'écran
	 * @param count
	 */
	public void updateLifePointsCounter(int count)
	{
		getLifePointsText().setText(""+count);
	}

	/**
	 * Méthode d'affichage de l'écran de chargement sur le decorView
	 */
	public void displayLoadingScreen()
	{
		if(!loadingScreenDisplayed)
		{
			loadingDialog = new Dialog(this);
			loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
			loadingDialog.setContentView(R.layout.loading_screen);
			loadingDialog.show();
			loadingDialog.setCanceledOnTouchOutside(false);
			Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
			ImageView logo = (ImageView)loadingDialog.findViewById(R.id.loadingIcon);
			logo.startAnimation(rotate);
			loadingScreenDisplayed = true;
		}

	}

	/**
	 * Méthode de suppression de l'écran de chargement
	 */
	public void removeLoadingScreen()
	{
		if(loadingScreenDisplayed)
		{
			loadingDialog.dismiss();
			loadingScreenDisplayed = false;
		}
	}

	/**
	 * Méthode de création d'un message indiquant le numéro des vagues d'ennemis
	 */
	public void createWaveMessage()
	{
		//Ajouy du waveMessage dans le decorView
		LayoutInflater inflater = LayoutInflater.from(this);
		View topView = inflater.inflate(R.layout.wave_message, null, false);
		ViewGroup decorViewGroup = (ViewGroup) getWindow().getDecorView();
		decorViewGroup.addView(topView);

		waveMessageLayout = (RelativeLayout) findViewById(R.id.waveMessageLayout);
		RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
				android.view.ViewGroup.LayoutParams.MATCH_PARENT)); 

		relParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT; 
		relParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

		//Message caché par défaut
		waveMessageLayout.setVisibility(View.INVISIBLE);

		//Animation du message de vague
		animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
		animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

		//Listener d'animation du message
		animationFadeIn.setAnimationListener(new Animation.AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				//Fin de l'animation d'apparition => faire disparaitre
				animationFadeOut.setStartOffset(2000); //Laisser le message affiché 2 secondes avant disparition
				waveMessageLayout.startAnimation(animationFadeOut);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				if (waveMessageLayout.getVisibility() == View.INVISIBLE) 
				{
					waveMessageLayout.setVisibility(View.VISIBLE);
				}

			}

		});
		
		//Listener pour l'animation de disparition du message
		animationFadeOut.setAnimationListener(new Animation.AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				if (waveMessageLayout.getVisibility() == View.VISIBLE) 
				{
					waveMessageLayout.setVisibility(View.INVISIBLE);
				}
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	/**
	 * Méthode qui affiche le message de vague actuelle
	 * @param number
	 */
	public void displayWaveMessage(int number)
	{
			TextView msg = (TextView) findViewById(R.id.waveMessageText);
			msg.setText(msg.getText()+" "+number);
			waveMessageLayout.startAnimation(animationFadeIn);

	}




	/**
	 * Méthode de création du menu de choix des tours en haut de l'écran
	 */
	public void createTopMenu()
	{
		// Récupération du layout du top menu
		LayoutInflater inflater = LayoutInflater.from(this);
		View topView = inflater.inflate(R.layout.top_menu_setup, null, false);
		ViewGroup decorViewGroup = (ViewGroup) getWindow().getDecorView();
		decorViewGroup.addView(topView); //affichage du top menu au dessus des autres vues

		topMenu = (FrameLayout) findViewById(R.id.topMenu);
		FrameLayout.LayoutParams relParams = new FrameLayout.LayoutParams(new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
				android.view.ViewGroup.LayoutParams.MATCH_PARENT)); 

		relParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT; 
		relParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

		//Menu caché par défaut
		topMenu.setVisibility(View.INVISIBLE);

		//Animation du top menu
		animationDown = AnimationUtils.loadAnimation(this, R.anim.slidedown);
		animationUp = AnimationUtils.loadAnimation(this, R.anim.slideup);

		setImageListeners();
	}

	/**
	 * Méthode de création d'un listener qui réagit à une sélection depuis le renderer
	 * @return
	 */
	public SelectionListener setupSelectionListener()
	{
		SelectionListener listener = new SelectionListener(){

			@Override
			public void onTowerBlockSelected() {
				if (topMenu.getVisibility() == View.INVISIBLE) 
				{
					topMenu.setVisibility(View.VISIBLE);
					topMenu.startAnimation(animationDown);
					Log.d("TAG","Top menu displayed");
				}
			}

			@Override
			public void onTowerBlockUnselected() {
				if (topMenu.getVisibility() == View.VISIBLE) 
				{
					topMenu.startAnimation(animationUp);
					topMenu.setVisibility(View.INVISIBLE);
					Log.d("TAG","Top menu hidden");
				}
			}

		};

		return listener;
	}

	/**
	 * Méthode de création de listeners pour les boutons du menu du haut
	 */
	public void setImageListeners()
	{
		ImageView iBlue = (ImageView)findViewById(R.id.towerBlue);
		ImageView iRed = (ImageView)findViewById(R.id.towerRed);
		ImageView iYellow = (ImageView)findViewById(R.id.towerYellow);
		ImageView iGreen = (ImageView)findViewById(R.id.towerGreen);

		iBlue.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				renderer.requestBuildTower(StaticVars.COLOR_BLUE);

			}

		});

		iRed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				renderer.requestBuildTower(StaticVars.COLOR_RED);

			}

		});

		iGreen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				renderer.requestBuildTower(StaticVars.COLOR_GREEN);

			}

		});

		iYellow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				renderer.requestBuildTower(StaticVars.COLOR_YELLOW);

			}

		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();

		//Arrêt des timers lancés
		for(Timer t :timers)
		{
			t.cancel();
		}

		for(CountDownTimer t :countDowntimers)
		{
			t.cancel();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(loadingScreenDisplayed)
		{
			loadingDialog.dismiss();
			loadingScreenDisplayed = false;
		}

		//Arrêt des timers lancés
		for(Timer t :timers)
		{
			t.cancel();
		}

		for(CountDownTimer t :countDowntimers)
		{
			t.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		//Arrêt des timers lancés
		for(Timer t :timers)
		{
			t.cancel();
		}

		for(CountDownTimer t :countDowntimers)
		{
			t.cancel();
		}
	}

	public GameGLSurfaceView getmGLView() {
		return mGLView;
	}

	public void setmGLView(GameGLSurfaceView mGLView) {
		this.mGLView = mGLView;
	}

	public GameRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}

	public GameEngine getEngine() {
		return engine;
	}

	public void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public FrameLayout getTopMenu() {
		return topMenu;
	}

	public void setTopMenu(FrameLayout topMenu) {
		this.topMenu = topMenu;
	}

	public Animation getAnimationUp() {
		return animationUp;
	}

	public void setAnimationUp(Animation animationUp) {
		this.animationUp = animationUp;
	}

	public Animation getAnimationDown() {
		return animationDown;
	}

	public void setAnimationDown(Animation animationDown) {
		this.animationDown = animationDown;
	}

	public boolean loadingScreenIsDisplayed() {
		return loadingScreenDisplayed;
	}

	public void setLoadingScreenDisplayed(boolean loadingScreenDisplayed) {
		this.loadingScreenDisplayed = loadingScreenDisplayed;
	}

	public Dialog getLoadingDialog() {
		return loadingDialog;
	}

	public void setLoadingDialog(Dialog loadingDialog) {
		this.loadingDialog = loadingDialog;
	}

	public ArrayList<Timer> getTimers() {
		return timers;
	}

	public void setTimers(ArrayList<Timer> timers) {
		this.timers = timers;
	}

	public ArrayList<CountDownTimer> getCountDowntimers() {
		return countDowntimers;
	}

	public void setCountDowntimers(ArrayList<CountDownTimer> countDowntimers) {
		this.countDowntimers = countDowntimers;
	}

	public TextView getLifePointsText() {
		return lifePointsText;
	}

	public void setLifePointsText(TextView lifePointsText) {
		this.lifePointsText = lifePointsText;
	}

	public boolean isLoadingScreenDisplayed() {
		return loadingScreenDisplayed;
	}

	






}

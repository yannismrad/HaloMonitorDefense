package com.yannis.mrad.halo.tools;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public class GameGLSurfaceView extends GLSurfaceView{
	private GameRenderer renderer;
	private SimpleOnGestureListener simpleOnGestureListener;
	private GestureDetector gestureDetector;

	public GameGLSurfaceView(Context context) {
		super(context);
	}

	/**
	 * Méthode d'activation des évènement de toucher
	 */
	public void enableTouchEvents()
	{
		gestureDetector = new GestureDetector(getContext(),new SimpleOnGestureListener()
		{
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e)
			{
				Log.d("TAG","Touché !");
				renderer.processTouchEvent(e.getX(), e.getY());

				return super.onSingleTapConfirmed(e);

			}

			@Override
			public boolean onDoubleTap(MotionEvent e)
			{
				Log.d("TAG","Double Touché !");
				renderer.getCameraListener().onCameraZoomed();
				return super.onDoubleTap(e);

			}

			@Override
			public boolean  onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
			{
				Log.d("TAG","Scroll from "+e1.getX()+","+e1.getY()+" to "+e2.getX()+","+e2.getY());
				Log.d("TAG","Scroll distance x : "+Math.round((e2.getX() - e1.getX()))+ " y : "+Math.round((e2.getY() - e1.getY())));
				//renderer.updateCameraPosition(e1.getX(),e1.getY(),e2.getX(), e2.getY());
				renderer.getCameraListener().onCameraChanged(e1.getX(),e2.getX(),e1.getY(), e2.getY());
				return super.onScroll(e1, e2, distanceX, distanceY);
			}



			@Override
			public boolean onDown(MotionEvent e)
			{
				return true;
			}
		});

	}

	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
		gestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public void setRenderer(Renderer renderer)
	{
		super.setRenderer(renderer);
		this.renderer = (GameRenderer) renderer;
	}


}

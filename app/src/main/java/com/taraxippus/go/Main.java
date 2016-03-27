package com.taraxippus.go;

import android.app.*;
import android.content.*;
import android.opengl.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.taraxippus.go.game.*;
import com.taraxippus.go.game.gameobject.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;
import java.io.*;
import java.nio.*;

public class Main extends Activity implements View.OnTouchListener
{
	static
	{
		if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof UncaughtExceptionHandler))
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler("/go-crashed/"));
	}
	
	public static final float FIXED_DELTA = 1 / 60F;
	public float timeFactor = 1;
	
	public final ResourceHelper resourceHelper = new ResourceHelper(this);
	public final Renderer renderer = new Renderer(this);
	public final Game game = new Game(this);
	public final World world = new World(this);
	public final Camera camera = new Camera(this);
	
	public GLSurfaceView view;
	public TextView fpsView;
	
	public TextView blackView;
	public TextView whiteView;
	
	private ScaleGestureDetector scaleDetector;
	private GestureDetector gestureDetector;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
		gestureDetector = new GestureDetector(this, new GestureListener());
		
		view = new GLSurfaceView(this);
		view.setOnTouchListener(this);
		
		view.setPreserveEGLContextOnPause(true);
		view.setEGLContextClientVersion(2);
		view.setEGLConfigChooser(new ConfigChooser(this));
		
		view.setRenderer(renderer);
		view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
        setContentView(view);
		
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		
		fpsView = new TextView(this);
		fpsView.setTextColor(0xFFFFFFFF);
		fpsView.setShadowLayer(padding, -1, -1, 0xFF000000);
		fpsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		fpsView.setPadding(padding, padding / 2, padding, padding / 2);
		fpsView.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		addContentView(fpsView, params);
		
		blackView = new TextView(this);
		blackView.setText("Black");
		blackView.setTextColor(0xFFFFFFFF);
		blackView.setShadowLayer(padding, -1, -1, 0xFF000000);
		blackView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		blackView.setPadding(padding, padding / 2, padding, padding / 2);
		blackView.setGravity(Gravity.RIGHT | Gravity.TOP);
		addContentView(blackView, params);
		
		whiteView = new TextView(this);
		whiteView.setText("White");
		whiteView.setTextColor(0x88FFFFFF);
		whiteView.setShadowLayer(padding, -1, -1, 0xFF000000);
		whiteView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		whiteView.setPadding(padding, padding / 2, padding, padding / 2);
		whiteView.setGravity(Gravity.LEFT | Gravity.TOP);
		addContentView(whiteView, params);
		
		view.setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_FULLSCREEN
			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) 
	{
        super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus)
		{
			view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		renderer.lastTime = 0;
	}
	
	@Override
	protected void onDestroy()
	{
		renderer.delete();
		
		super.onDestroy();
	}
	
	private static final int INVALID_POINTER_ID = -1;
	
	private int activePointerId = INVALID_POINTER_ID;
	private float lastTouchX, lastTouchY;

	@Override
	public boolean onTouch(View view, MotionEvent ev)
	{
		scaleDetector.onTouchEvent(ev);
		gestureDetector.onTouchEvent(ev);

//		final int action = MotionEventCompat.getActionMasked(ev); 
//
//		switch (action)
//		{ 
//			case MotionEvent.ACTION_DOWN:
//				{
//					final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
//					final float x = MotionEventCompat.getX(ev, pointerIndex); 
//					final float y = MotionEventCompat.getY(ev, pointerIndex); 
//
//					lastTouchX = x;
//					lastTouchY = y;
//					
//					activePointerId = MotionEventCompat.getPointerId(ev, 0);
//					break;
//				}
//
//			case MotionEvent.ACTION_MOVE:
//				{
//					final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);  
//
//					final float x = MotionEventCompat.getX(ev, pointerIndex);
//					final float y = MotionEventCompat.getY(ev, pointerIndex);
//
//			
//					final float dx = x - lastTouchX;
//					final float dy = y - lastTouchY;
//
//					camera.rotation.y -= dx * 0.1F;
//					camera.rotation.x -= dy * 0.1F;
//
//					camera.rotation.x = Math.min(180 - 1F, Math.max(camera.rotation.x, 1F));
//					
//					lastTouchX = x;
//					lastTouchY = y;
//
//					break;
//				}
//
//			case MotionEvent.ACTION_UP: 
//				{
//					activePointerId = INVALID_POINTER_ID;
//					break;
//				}
//
//			case MotionEvent.ACTION_CANCEL:
//				{
//					activePointerId = INVALID_POINTER_ID;
//					break;
//				}
//
//			case MotionEvent.ACTION_POINTER_UP: 
//				{
//
//					final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
//					final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex); 
//
//					if (pointerId == activePointerId)
//					{
//						final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//						lastTouchX = MotionEventCompat.getX(ev, newPointerIndex); 
//						lastTouchY = MotionEventCompat.getY(ev, newPointerIndex); 
//						activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
//					}
//					break;
//				}
//		}       
		return true;
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener 
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector) 
		{
			camera.zoom /= detector.getScaleFactor();
			camera.zoom = Math.max(Camera.ZOOM_MIN, Math.min(camera.zoom * Camera.ZOOM_SPEED, Camera.ZOOM_MAX));

			return true;
		}
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());
			
			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);
			
			if (touched != null)
				touched.onTouch(viewRay.intersection, viewRay.normal);

			else if (viewRay.intersects(game.boardObject.modelMatrix, game.boardObject.invModelMatrix, false))
			{
				game.onWallTouched(viewRay.intersection, viewRay.normal);
			}
				
			
			
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());

			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);

			if (touched != null)
				touched.onLongTouch(viewRay.intersection, viewRay.normal);
		}

		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());
		
			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);

			if (touched != null)
				touched.onSingleTouch(viewRay.intersection, viewRay.normal);
				
			return touched != null;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());
			
			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);

			if (touched != null)
				touched.onDoubleTouch(viewRay.intersection, viewRay.normal);

			return touched != null;
		}

		
	}
}

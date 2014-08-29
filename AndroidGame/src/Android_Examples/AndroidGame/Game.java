package Android_Examples.AndroidGame;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Game extends Activity {
	private int						screenWidth, screenHeight;				// �ù����e
	private int						level		= 10;						// �r����
	private int						speed		= 5;						// ���U�t��
	private int						score		= 0;						// ����
	private int						A=0,B=0,MUSIC=0;
	
	private Context tempcontext;
	private Canvas					canvas;
	private ArrayList<Bill>			bills		= new ArrayList<Bill>();
	private MyCountDown				cdTimer;								// �˼ƭp�ɾ�
	private String					remainingTime;							// �Ѿl�ɶ�
	Thread							mainLoop;
	BillMove						billMove;								// ���渨�U�ʧ@
	private boolean					terminate	= false,temp1=false,temp2=true;					// �B�zThread����

	/* �e��ø�X�PĲ�I�ƥ� */
	class MySurfaceView extends SurfaceView implements Runnable {
		BitmapDrawable	background;
		Paint			paint	= new Paint();

		public MySurfaceView(Context context) {
			super(context);
			tempcontext=context;
			/* ���o�ù����e */
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;

			/* �]�w�I���� */
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.grid);
			background = new BitmapDrawable(context.getResources(), bitmap);
			background.setBounds(0, 0, screenWidth, screenHeight);

			//paint.setColor(Color.BLACK);
			paint.setTextSize(35);

			initilizeGame(context); // �H�����m�Ϥ���m

			
			

			mainLoop = new Thread(this);
			mainLoop.start();
		}

		/* �e��ø�X */
		void doDraw() {
			canvas = getHolder().lockCanvas();
			if (canvas != null) {
				background.draw(canvas);

				for (int i = 0; i < bills.size(); i++) {
					Bitmap bitmap = bills.get(i).bitmap;
					int left = bills.get(i).left;
					int top = bills.get(i).top;

					canvas.drawBitmap(bitmap, left, top, null);
				}
				
				String s = "Score�G" + String.valueOf(score);
				
				paint.setColor(0xffffffff);
				canvas.drawLine(0, screenHeight*15/100, screenWidth, screenHeight*15/100, paint);
				canvas.drawLine(0, screenHeight*45/100, screenWidth, screenHeight*45/100, paint);
				canvas.drawLine(0, screenHeight*75/100, screenWidth, screenHeight*75/100, paint);
				canvas.drawText(s, 5, 30, paint); // �e�X����
				canvas.drawText(remainingTime, screenWidth*3/4, 30, paint); // �e�X�Ѿl���
				
				getHolder().unlockCanvasAndPost(canvas);
			}
		}

		public void run() {
			while (!terminate) {
				if(temp1)doDraw();
				if(temp2){
					canvas = getHolder().lockCanvas();
					if (canvas != null) {
					background.draw(canvas);
					if(A<screenWidth){
					    A+=10;
					    paint.setColor(0xffffffff);
					    canvas.drawText("Touch Every Where To Start",A, screenHeight/2, paint);
					}
					else{
						A=0;
						paint.setColor(0xffffffff);
						canvas.drawText("Touch Every Where To Start",A, screenHeight/2, paint);
					}
					getHolder().unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		/* Ĳ�I�ƥ� */
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			

			final int action = ev.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_UP: {
					final float x = ev.getX();
					final float y = ev.getY();
                    if(temp1){
					  for (int i = 0; i < bills.size(); i++) {
						Bitmap bitmap = bills.get(i).bitmap;
						

						int dollar = bills.get(i).dollar;
						int left = bills.get(i).left;
						int top = bills.get(i).top ;
						int right = bills.get(i).left + bitmap.getWidth();
						int bottum = bills.get(i).top + bitmap.getHeight();

						// �ˬd�I�O�_�b�r���W
						if (x >= left && x <= right && y >= top && y <= bottum) {
							if( (screenHeight*15/100) >= top && (screenHeight*15/100) <= bottum){
							   score += dollar; // �[��
							   bills.set(i, ResetBill());
							}
							else if( (screenHeight*45/100) >= top && (screenHeight*45/100) <= bottum){
							   score += dollar/2; // �[��
							   bills.set(i, ResetBill());
							}
							else if( (screenHeight*75/100) >= top && (screenHeight*75/100) <= bottum){
							   score += dollar/4; // �[��
							   bills.set(i, ResetBill());
							}
							else if( (screenHeight)>=top && (screenHeight*2/3)+100 <=top){
								   score -= dollar; // �[��
								   bills.set(i, ResetBill());
							}
							break;
						}
					  }
                    }
                    if(temp2){
                    	
                    			temp2=false;
                    			temp1=true;
                    			
                    			if(MUSIC==1){
                    			  cdTimer = new MyCountDown(175000, 500, tempcontext); // �˼ƭp�ɾ�
                    			  cdTimer.start();
                    			  Music.play(Game.this, R.drawable.one);
                    			}
                    			if(MUSIC==2){
                      			  cdTimer = new MyCountDown(105000, 500, tempcontext); // �˼ƭp�ɾ�
                      			  cdTimer.start();
                      			  Music.play(Game.this, R.drawable.two);
                      			}
                    			if(MUSIC==3){
                      			  cdTimer = new MyCountDown(104000, 500, tempcontext); // �˼ƭp�ɾ�
                      			  cdTimer.start();
                      			  Music.play(Game.this, R.drawable.there);
                      			}
                        
                    }
									
					break;
				}
			}
			return true;
		}

		
		
	}

	/* ����r���U���t�� */
	class BillMove extends Thread {
		void doMove() {
			for (int i = 0; i < bills.size(); i++) {
				bills.get(i).top += speed; // �U���T��
				// ���m�]�X�e�����r��
				if (bills.get(i).top > screenHeight) {
					bills.set(i, ResetBill());
				}
			}

			try {
				if(B<=79)
				  Thread.sleep(20); // �����m��s�t�v
				else
				  Thread.sleep(40);
			} catch (Exception ex) {
			}
		}

		public void run() {
			while (!terminate) {
				
				if(temp1)
				  doMove();
			}
		}
	}

	/* �˼ƭp�ɾ� */
	public class MyCountDown extends CountDownTimer {
		Context	context;

		public MyCountDown(long millisInFuture, long countDownInterval, Context context) {
			super(millisInFuture, countDownInterval);
			this.context = context;
		}

		@Override
		public void onFinish() {
			remainingTime = "Time�G0";
			Music.stop(Game.this);
			Button button = new Button(context);
			button.setText("Sure");
			final Dialog dialog = new Dialog(context);
			dialog.setTitle("You Get " + score + " Score�I");
			dialog.setContentView(button);
			dialog.show();
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					Intent replyIntent =new Intent();
		    		Bundle bundle =new Bundle();
		    		replyIntent.putExtras(bundle);
		    		setResult(RESULT_OK,replyIntent);
					Game.this.finish();
				}
			});
			
			terminate = true;
			mainLoop.interrupt();
			billMove.interrupt();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			remainingTime = "Time�G" + (millisUntilFinished / 1000 + 1);
			B=(int) (millisUntilFinished / 1000 + 1);
		}
	}

	/* �N�ϭ��s��b�e���W���H����m */
	private void initilizeGame(Context context) {
		bills.clear();

		for (int i = 0; i < level; i++) {
			Bill bill = ResetBill();
			bills.add(bill);
		}

		score = 0; // ���m����

		

		billMove = new BillMove(); // �B�z�r�����ʪ�Thread
		billMove.start();
	}

	/* �H�����ͤ@�i�r�� */
	private Bill ResetBill() {
		Random r = new Random();

		Bitmap bitmap;
		int dollar = 100;
		int j = r.nextInt(100);
		if (j < 30) {
			// 100��
			bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bill100);
		} else if (j < 60) {
			// 500��
			dollar = 500;
			bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bill500);
		} else {
			// 1000��
			dollar = 1000;
			bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bill1000);
		}

		int left = r.nextInt(screenWidth - bitmap.getWidth());
		int top = 0 - bitmap.getHeight() - r.nextInt(screenHeight) * 2;
		Bill bill = new Bill(bitmap, dollar, left, top);
		Log.v("left",String.valueOf(left));
		Log.v("top",String.valueOf(top));

		return bill;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(new MySurfaceView(this));
		
		new AlertDialog.Builder(this)
	    .setTitle("Title")//.setView(view)
	    .setMessage("Select Music")
	    
	    .setNegativeButton("Music 1",
	        new DialogInterface.OnClickListener(){
	            public void onClick(
	                DialogInterface dialoginterface, int i){
	            	MUSIC=1;
	                }
	            })
	    .setNeutralButton("Music 2",
	        new DialogInterface.OnClickListener(){
	            public void onClick(
	                DialogInterface dialoginterface, int i){
	            	MUSIC=2;
	                }
	            })
	    .setPositiveButton("Music 3",
	        new DialogInterface.OnClickListener(){
	            public void onClick(
	                DialogInterface dialoginterface, int i){
	            	MUSIC=3;
	                }
	            })
	    .show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		terminate = true;
		mainLoop.interrupt();
		billMove.interrupt();
		cdTimer.cancel();
		Music.stop(Game.this);
	}
	public static class Music {
		  private static MediaPlayer mp = null;

		  /** Stop old song and start new one */
		  public static void play(Context context, int resource) {
		    stop(context);
		    mp = MediaPlayer.create(context, resource);
		    mp.setLooping(true);
		    mp.start();
		  }

		  /** Stop the music */
		  public static void stop(Context context) {
		    if (mp != null) {
		      mp.stop();
		      mp.release();
		      mp = null;
		    }
		  }
		}
	
}

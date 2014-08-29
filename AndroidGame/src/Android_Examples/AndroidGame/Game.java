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
	private int						screenWidth, screenHeight;				// 螢幕長寬
	private int						level		= 10;						// 鈔票數
	private int						speed		= 5;						// 落下速度
	private int						score		= 0;						// 分數
	private int						A=0,B=0,MUSIC=0;
	
	private Context tempcontext;
	private Canvas					canvas;
	private ArrayList<Bill>			bills		= new ArrayList<Bill>();
	private MyCountDown				cdTimer;								// 倒數計時器
	private String					remainingTime;							// 剩餘時間
	Thread							mainLoop;
	BillMove						billMove;								// 執行落下動作
	private boolean					terminate	= false,temp1=false,temp2=true;					// 處理Thread結束

	/* 畫面繪出與觸碰事件 */
	class MySurfaceView extends SurfaceView implements Runnable {
		BitmapDrawable	background;
		Paint			paint	= new Paint();

		public MySurfaceView(Context context) {
			super(context);
			tempcontext=context;
			/* 取得螢幕長寬 */
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;

			/* 設定背景圖 */
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.grid);
			background = new BitmapDrawable(context.getResources(), bitmap);
			background.setBounds(0, 0, screenWidth, screenHeight);

			//paint.setColor(Color.BLACK);
			paint.setTextSize(35);

			initilizeGame(context); // 隨機重置圖片位置

			
			

			mainLoop = new Thread(this);
			mainLoop.start();
		}

		/* 畫面繪出 */
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
				
				String s = "Score：" + String.valueOf(score);
				
				paint.setColor(0xffffffff);
				canvas.drawLine(0, screenHeight*15/100, screenWidth, screenHeight*15/100, paint);
				canvas.drawLine(0, screenHeight*45/100, screenWidth, screenHeight*45/100, paint);
				canvas.drawLine(0, screenHeight*75/100, screenWidth, screenHeight*75/100, paint);
				canvas.drawText(s, 5, 30, paint); // 畫出分數
				canvas.drawText(remainingTime, screenWidth*3/4, 30, paint); // 畫出剩餘秒數
				
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

		/* 觸碰事件 */
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

						// 檢查點是否在鈔票上
						if (x >= left && x <= right && y >= top && y <= bottum) {
							if( (screenHeight*15/100) >= top && (screenHeight*15/100) <= bottum){
							   score += dollar; // 加分
							   bills.set(i, ResetBill());
							}
							else if( (screenHeight*45/100) >= top && (screenHeight*45/100) <= bottum){
							   score += dollar/2; // 加分
							   bills.set(i, ResetBill());
							}
							else if( (screenHeight*75/100) >= top && (screenHeight*75/100) <= bottum){
							   score += dollar/4; // 加分
							   bills.set(i, ResetBill());
							}
							else if( (screenHeight)>=top && (screenHeight*2/3)+100 <=top){
								   score -= dollar; // 加分
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
                    			  cdTimer = new MyCountDown(175000, 500, tempcontext); // 倒數計時器
                    			  cdTimer.start();
                    			  Music.play(Game.this, R.drawable.one);
                    			}
                    			if(MUSIC==2){
                      			  cdTimer = new MyCountDown(105000, 500, tempcontext); // 倒數計時器
                      			  cdTimer.start();
                      			  Music.play(Game.this, R.drawable.two);
                      			}
                    			if(MUSIC==3){
                      			  cdTimer = new MyCountDown(104000, 500, tempcontext); // 倒數計時器
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

	/* 控制鈔票下降速度 */
	class BillMove extends Thread {
		void doMove() {
			for (int i = 0; i < bills.size(); i++) {
				bills.get(i).top += speed; // 下降幅度
				// 重置跑出畫面的鈔票
				if (bills.get(i).top > screenHeight) {
					bills.set(i, ResetBill());
				}
			}

			try {
				if(B<=79)
				  Thread.sleep(20); // 限制位置更新速率
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

	/* 倒數計時器 */
	public class MyCountDown extends CountDownTimer {
		Context	context;

		public MyCountDown(long millisInFuture, long countDownInterval, Context context) {
			super(millisInFuture, countDownInterval);
			this.context = context;
		}

		@Override
		public void onFinish() {
			remainingTime = "Time：0";
			Music.stop(Game.this);
			Button button = new Button(context);
			button.setText("Sure");
			final Dialog dialog = new Dialog(context);
			dialog.setTitle("You Get " + score + " Score！");
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
			remainingTime = "Time：" + (millisUntilFinished / 1000 + 1);
			B=(int) (millisUntilFinished / 1000 + 1);
		}
	}

	/* 將圖重新放在畫面上方隨機位置 */
	private void initilizeGame(Context context) {
		bills.clear();

		for (int i = 0; i < level; i++) {
			Bill bill = ResetBill();
			bills.add(bill);
		}

		score = 0; // 重置分數

		

		billMove = new BillMove(); // 處理鈔票移動的Thread
		billMove.start();
	}

	/* 隨機產生一張鈔票 */
	private Bill ResetBill() {
		Random r = new Random();

		Bitmap bitmap;
		int dollar = 100;
		int j = r.nextInt(100);
		if (j < 30) {
			// 100元
			bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bill100);
		} else if (j < 60) {
			// 500元
			dollar = 500;
			bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bill500);
		} else {
			// 1000元
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

package ca.paulshin.yunatube.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Utils;

import com.google.analytics.tracking.android.EasyTracker;

public class Splash extends Activity {
	private ImageView splash;

	private static final int SPLASH_DURATION = YunaTubeApplication.debuggable ? 100 : 1400;
	private static final int FADE_OUT_DURATION = YunaTubeApplication.debuggable ? 100 : 1600;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);
		splash = (ImageView) findViewById(R.id.splash_image);
		
		//april fools
		if (Utils.isAprilFools()) {
			splash.setImageResource(R.drawable.fool_splash);
		}

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation arg0) {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Intent main = new Intent(Splash.this, MainActivity.class);
						startActivity(main);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

						finish();
					}
				}, FADE_OUT_DURATION);
			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationStart(Animation animation) {

			}
		});
		animation.setDuration(SPLASH_DURATION);
		splash.startAnimation(animation);
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
}

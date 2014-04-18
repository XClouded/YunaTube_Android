package ca.paulshin.yunatube.youtube;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.http.YouTubeDataLoader;

import com.squareup.picasso.Picasso;

public class RandomDialog extends Dialog implements OnClickListener {

	private TextView title;
	private ImageView thumbnail;
	private Picasso picasso;
	private Activity activity;
	
	private String ytid;

	public RandomDialog(final Activity activity) {
		super(activity, R.style.Theme_YunaTubeDialog);
		this.setContentView(R.layout.dialog_random);
		this.setCancelable(true);
		this.activity = activity;
		this.picasso = Picasso.with(activity);

		Button ui_yes = (Button) findViewById(R.id.random_yes);
		Button ui_no = (Button) findViewById(R.id.random_no);
		title = (TextView) findViewById(R.id.random_title);
		thumbnail = (ImageView) findViewById(R.id.random_thumb);
		TextView ui_close = (TextView) findViewById(R.id.random_close);

		try {
			getRandomYouTube();
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			dismiss();
		}

		ui_yes.setOnClickListener(this);
		ui_no.setOnClickListener(this);
		ui_close.setOnClickListener(this);
	}

	private void getRandomYouTube() {
		new AsyncTask<String, Void, Clip>() {
			@Override
			protected Clip doInBackground(String... params) {
				return YouTubeDataLoader.getLoaderInstance().getRandom();
			}

			protected void onPostExecute(Clip clip) {
				if (clip != null) {
					ytid = clip.getYtid();
					title.setText(clip.getYtitle());
					picasso.load(String.format(Constants.CLIP_HQ_THUMBNAIL_URL, clip.getYtid())).placeholder(R.drawable.random_stub).resize(480, 360).into(thumbnail);
				}
			};
		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.random_yes:
			RandomDialog.this.dismiss();
			Bundle bundle = new Bundle();
			bundle.putString(YouTubeActivity.EXTRA_YOUTUBE_YTID, ytid);
			Intent intent = new Intent(activity, YouTubeActivity.class);
			intent.putExtras(bundle);
			activity.startActivity(intent);
			break;

		case R.id.random_no:
			getRandomYouTube();
			break;

		case R.id.random_close:
			dismiss();
			break;
		}
	}
}
package com.aidufei.mirror.control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.aidufei.protocol.remote.callback.GetPlayInfoCallback;
import com.aidufei.protocol.remote.handle.RemoteMedia;
import com.aidufei.protocol.remote.handle.mediaInfo;
import com.coship.ott.activity.R;
import com.coship.ott.utils.LogUtils;

public class MediaActivity extends Activity {

	private Context mContext;
	private Button pushUrl;
	private Button getinfo;
	private EditText urltext;
	private TextView director;
	private TextView language;
	private RemoteMedia media;
	private RadioGroup mediatype;

	// @Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.trace(Log.ERROR, "Main", "MainActivity oncreate");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.media);
		urltext = (EditText) findViewById(R.id.url_input);
		pushUrl = (Button) findViewById(R.id.push_btn);
		getinfo = (Button) findViewById(R.id.getinfo_btn);
		director = (TextView) findViewById(R.id.info_director);
		language = (TextView) findViewById(R.id.info_language);
		mediatype = (RadioGroup) findViewById(R.id.radioGroup1);
		// media = MainActivity.center.getRemoteMedia();

		pushUrl.setOnClickListener(new OnClickListener() {

			// @Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = urltext.getText().toString();
				if (url.length() > 0) {
					media.remotePlayMediaUrl(url, getMediaType());
				}
			}
		});

		getinfo.setOnClickListener(new OnClickListener() {

			// @Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = urltext.getText().toString();
				media.getMediaInfo(url, callback);
			}
		});

		mediatype.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			// @Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.video_type:
					// urltext.setText("http://10.161.179.88:9000/disk/DLNA-PNMPEG_TS_HD_KO-OP11-FLAGS01700000/O0$3$27I784.mpg");
					break;

				case R.id.audio_type:
					// urltext.setText("http://10.161.179.88:9000/disk/DLNA-PNWMAPRO-OP01-FLAGS01700000/O0$1$7I3345.wma");
					break;

				case R.id.image_type:
					// urltext.setText("http://10.161.179.88:9000/disk/DLNA-PNJPEG_LRG-OP01-FLAGS00f00000/O0$2$19I35343.jpg");
					break;

				default:
					break;
				}
			}
		});
		String ip = MainActivity.center.getLocalHostIPaddress();
		urltext.setText("http://" + ip + ":8077/mnt/sdcard/82301.mp4");
	}

	private mediaInfo info = new mediaInfo();
	private Handler mHandlr = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 61:
				/* parse xml */
				director.setText(info.getDirector());
				language.setText(info.getLanguage());
				break;

			default:
				break;
			}

		}
	};

	/* this callback may called in another thread so do not update UI directorly */
	GetPlayInfoCallback callback = new GetPlayInfoCallback() {

		// @Override
		public void returnPlayingInfo(boolean arg0, mediaInfo arg1) {
			// TODO Auto-generated method stub
			if (arg0) {
				info = arg1;
				mHandlr.sendEmptyMessage(61);
			}
		}

	};

	public final static int MEDIA_TYPE_VEDIO = 1;
	public final static int MEDIA_TYPE_AUDIO = 2;
	public final static int MEDIA_TYPE_IMAGE = 3;

	protected int getMediaType() {
		int mediaType = 0;

		switch (mediatype.getCheckedRadioButtonId()) {
		case R.id.video_type:
			mediaType = MEDIA_TYPE_VEDIO;
			break;
		case R.id.audio_type:
			mediaType = MEDIA_TYPE_AUDIO;
			break;

		case R.id.image_type:
			mediaType = MEDIA_TYPE_IMAGE;
			break;
		default:
			break;
		}
		return mediaType;
	}

	protected void onDestroy() {
		super.onDestroy();
	}
}

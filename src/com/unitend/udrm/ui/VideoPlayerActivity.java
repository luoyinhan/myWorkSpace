package com.unitend.udrm.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aidufei.protocol.core.Client;
import com.aidufei.protocol.core.Device;
import com.aidufei.protocol.devicelist.DeviceDialog;
import com.aidufei.protocol.oto.Global;
import com.coship.ott.activity.R;
import com.coship.ott.constant.Constant;
import com.coship.ott.transport.InterfaceUrls;
import com.coship.ott.transport.action.BookMarkAction;
import com.coship.ott.transport.action.FavoriteAction;
import com.coship.ott.transport.action.LiveAction;
import com.coship.ott.transport.action.SystemAction;
import com.coship.ott.transport.action.VodAction;
import com.coship.ott.transport.dto.BaseJsonBean;
import com.coship.ott.transport.dto.bookmark.BookMark;
import com.coship.ott.transport.dto.bookmark.BookMarksJson;
import com.coship.ott.transport.dto.live.ProgramInfo;
import com.coship.ott.transport.dto.live.ProgramInfosJson;
import com.coship.ott.transport.dto.system.SystemTimeJson;
import com.coship.ott.transport.dto.vod.PlayURLJson;
import com.coship.ott.utils.AppManager;
import com.coship.ott.utils.DbHelper;
import com.coship.ott.utils.LogUtils;
import com.coship.ott.utils.Session;
import com.coship.ott.utils.UIUtility;
import com.coship.ott.utils.Utility;
import com.coship.ott.view.CommonAdapter;
import com.coship.ott.view.VerticalSeekBar;
import com.coship.ott.view.VerticalSeekBar.OnVolumeChangedListener;
import com.unitend.udrm.ui.ShareWindow.ShareWindowListener;
import com.unitend.udrm.util.GL2JNIView;
import com.unitend.udrm.util.Util;

public class VideoPlayerActivity extends Activity implements OnClickListener {
	public final static String TAG = "UDRM/VideoPlayerActivity";
	private Context mContext = null;
	private LayoutInflater mLayoutInflater;
	// 数字太和播放器播放状态常量
	private static final int UDRM_STATE_NOTHINGSPECIAL = 1;
	private static final int UDRM_STATE_LOADING = 2;
	private static final int UDRM_STATE_PLAYING = 3;
	private static final int UDRM_STATE_PAUSE = 4;
	private static final int UDRM_STATE_STOP = 5;
	private static final int UDRM_STATE_FINAISH = 6;
	private static final int UDRM_STATE_ERROR = 7;
	// 每秒钟更新一次播放时间和进度条
	private static final int MSG_UPDATE_PHONETV_STATE = 12;
	// 每3秒同步一次电视播放进度和音量等信息
	private static final int MESSAGE_GET_STATUS = 13;
	// 播放新片源，数据从收到的message里获取
	public static final int MSG_CHANGE_PLAY = 14;

	private static final int SEND_MESSAGE_STOP = 0x12233;
	// PAD播放器最大音量
	public static final int PAD_MAX_VOLUME = 15;
	// TV最大音量
	public static final int TV_MAX_VOLUME = 31;
	// 处理Surfaceview的大小
	private MediaControler mediaControler;
	private AudioManager audiomanage;
	private GL2JNIView mSurfaceView;
	private GestureDetector mGestureDetector;
	private VerticalSeekBar mVoiceSeekBar;
	private Dialog dialog;
	public static boolean isv7 = false;
	public static boolean isneon = false;
	// 是否静音，0有音量，1为静音
	public int isMute = 0;
	public static final String sdPath = Environment
			.getExternalStorageDirectory().getPath();
	// 获取本地播放器播放状态
	private static final int MSG_GET_PLAYER_STATE = 20000;
	private int state;
	private static final int MSG_HIDE_PROGRESSBAR = 20001;
	// 播控栏,声音控制栏
	private View playerContralRoot;
	// 控制条是否显示
	private boolean isPrograssBarShowing = false;
	// 触屏后，操作控件默认显示时间长度
	private int defaultShowTime = 5000;
	// 触屏计时器，当大于defaultShowTime时，组件隐藏
	private int showTime = 0;
	// 获取播放串用到的元素
	private String resourceCode;
	private String productCode;
	private String programID;
	// 播放类型。1为点播,2为直播,3为绝对时间时移，4为相对秒数时移
	public int playType;
	private long delay = 0;
	private long shifttime;
	private long tempShiftTime;
	private long shiftend;
	// 码率标识 1：400（流畅） 2：800 （清晰） 3： 800（高清）
	private int fmt = 2;
	// 高标清标识 0为标清，1为高清
	private int videoType;
	private long timecode;
	// 进入播放器时的timeCode,用来纠正当timeCode不为0时，获取到播放串后播放器获取总时长为实际总时长减去timeCode的错误。
	private long rawTimeCode;
	private int playTime;
	private String assetName;
	private String assetID;
	private String providerID;
	private String posterUrl;
	private String subID;
	private boolean isFromLive = false;
	// 播放串
	private String mPath;
	// EPG弹出窗口
	private PopupWindow mPopupWindow;
	// 推荐弹出窗口
	private CommonPopupWindow recommandWindow;
	// 历史记录弹出窗口
	private CommonPopupWindow bookMarkWindow;
	// 评论弹出窗口
	private CommentPopupWindow commentWindow;

	// popWindow的listView
	private ListView popList;
	private ProgressBar mPopProgress;

	private RelativeLayout rootView;
	// EPG数据适配器
	private EpgAdapter epgAdapter = null;
	// 分享窗口
	private ShareWindow shareWindow;
	// EPG信息
	private String nowDay = Utility.getDay();
	// EPG信息每页显示多少条
	protected static final int EPG_PAGE_SIZE = 500;
	// 当前频道今天的EPG节目列表
	private ArrayList<ProgramInfo> channelPrograms;
	// 当前正在播放节目
	public ProgramInfo curPlayprogram;
	// 当前播放节目在channelPrograms中的位置
	public int curPlayprogramIndex = 0;
	// 时间进度条是否已初始化
	private boolean isTimeBarInited = false;
	private RelativeLayout playerTopBar;
	private TextView mTotalTimeBar;
	private TextView mCurTimeBar;
	private SeekBar mPlaySeekBar;
	// 影片名称显示组件
	private TextView assetNameText;
	// 左下角tv和手机的切换
	private Button tvButton, phoneButton;
	// 声音按钮 码率焦点
	private Button voiceBtn, playerML;
	// 收藏按钮
	private Button playerCollectBtn;
	// 新接口
	private Device mRemote = null;
	private DeviceDialog mDeviceDialog = null;
	private boolean isControlTVState = false;
	private View mPauseButton;
	private View mPlayButton;
	// 是否正在拖动
	private boolean isDraging = false;
	// 是否已经正常播放完毕
	private boolean IsPlayEnd = false;
	// 加载中
	private ProgressBar loadingBar;

	private boolean mPowerPressed = false;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 开锁
			if ("android.intent.action.USER_PRESENT".equals(action)) {
				mSurfaceView.onResume();
				getPlayUrl();
				mPlayButton.setVisibility(View.GONE);
				mPauseButton.setVisibility(View.VISIBLE);
				mPowerPressed = false;
			} else if ("android.intent.action.SCREEN_OFF".equals(action)) {// 锁屏键广播action
				mPowerPressed = true;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.video_player);
		mContext = this;
		AppManager.getAppManager().addActivity(this);
		mLayoutInflater = LayoutInflater.from(mContext);
		// 获取系统声音服务
		audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 获得服务器时间与系统当前时间对比
		checkTime();
		// 获取传递进来的变量
		initIntentdata();
		setupView();
		// 初始化播控栏状态
		showController();
		// 初始化播放组件
		initPlayer();
		if (2 == playType) {// 直播
			// 取数据
			gotoPlay();
			getChannelProgram(1);
			isFromLive = true;
		} else {
			if (1 == playType) { // 点播是否有历史播放记录
				checkIfPlayBookMark();
				getUserFavourite();
			} else {
				gotoPlay();
			}
			isFromLive = false;
		}
		registerReceiver();
	}

	private void gotoPlay() {
		if (!TextUtils.isEmpty(mPath)) {
			play();
		} else {
			getPlayUrl();
		}
	}

	private void initIntentdata() {
		Intent intent = getIntent();
		// 获取传递进来的信息，获取播放串
		mPath = intent.getStringExtra("playUrl");
		resourceCode = intent.getStringExtra("resourceCode");
		playType = intent.getIntExtra("playType", 0);
		delay = intent.getLongExtra("delay", 0l);
		shifttime = intent.getLongExtra("shifttime", 0l);
		tempShiftTime = shifttime;
		shiftend = intent.getLongExtra("shiftend", 0l);
		timecode = intent.getLongExtra("timecode", 0);
		videoType = intent.getIntExtra("videoType", 0);
		fmt = intent.getIntExtra("fmt", 2);
		playTime = intent.getIntExtra("playTime", 0);
		assetName = intent.getStringExtra("assetName");
		assetID = intent.getStringExtra("assetID");
		providerID = intent.getStringExtra("providerID");
		posterUrl = intent.getStringExtra("posterUrl");
		subID = intent.getStringExtra("subID");
		productCode = intent.getStringExtra("productCode");
		programID = intent.getStringExtra("programID");
	}

	// 获得服务器时间与系统当前时间对比
	private void checkTime() {
		new AsyncTask<Void, Void, SystemTimeJson>() {
			@Override
			protected SystemTimeJson doInBackground(Void... params) {
				return new SystemAction()
						.getSystemTimeInfo(InterfaceUrls.GET_SYSTEM_TIME);
			}

			@Override
			protected void onPostExecute(SystemTimeJson result) {
				if (result == null) {
					Toast.makeText(mContext, "获取系统时间失败", Toast.LENGTH_SHORT)
							.show();
				} else {
					long timeValue = Utility.dealTimeToSeconds(result
							.getDateTime());
					long nowTime = System.currentTimeMillis() / 1000;
					System.out.println("systemtime=" + timeValue
							+ ",phone time=" + nowTime);
					if (Math.abs(timeValue - nowTime) > 60 * 5) {
						Toast.makeText(mContext, " 您当前Pad的时间不正确，请更改！  ",
								Toast.LENGTH_LONG).show();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								finish();
								startActivity(new Intent(
										Settings.ACTION_DATE_SETTINGS));
							}
						}, 2000);
					}
				}
			}
		}.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSurfaceView.onResume();
		if (mediaControler != null
				&& mediaControler.getPlayState() == UDRM_STATE_STOP
				&& !mPowerPressed) {
			mediaControler.resume();
			play();
		}
		// 获取播放状态、监控播控栏、信息栏显示
		if (!handler.hasMessages(MSG_GET_PLAYER_STATE)) {
			handler.sendEmptyMessage(MSG_GET_PLAYER_STATE);
		}
		mPlayButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSurfaceView.onPause();
		if (mediaControler != null
				&& (UDRM_STATE_PLAYING == mediaControler.getPlayState())) {
			mediaControler.pause();
			mPlayButton.setVisibility(View.VISIBLE);
			mPauseButton.setVisibility(View.GONE);
		}
	}

	/**
	 * 退出程序,停止播放
	 * */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mediaControler.stop();
		mediaControler.Destroy();
		mediaControler = null;
		audiomanage = null;
		// 停止获取本地播放器状态
		handler.removeMessages(MSG_GET_PLAYER_STATE);
		// 停止获取电视播放状态
		handler.removeMessages(MESSAGE_GET_STATUS);
		// 如果是点播，添加书签
		LogUtils.trace(Log.DEBUG, TAG, "==playType==>>" + playType);
		if (1 == playType) {
			if (IsPlayEnd) {
				timecode = Constant.PLAY_OVER;// 播放结束存入值为999999
			}
			addBookMark();
		}
		unregisterReceiver(mBroadcastReceiver);// 注销广播监听器
	}

	/**
	 * 装载页面并添加监听器
	 * */
	private void setupView() {
		rootView = (RelativeLayout) this.findViewById(R.id.videoPlayerRoot);
		// 回退按钮
		this.findViewById(R.id.playerBackBtn).setOnClickListener(this);
		// 播控条
		playerContralRoot = this.findViewById(R.id.playerContralRoot);
		// 顶部信息栏
		playerTopBar = (RelativeLayout) this.findViewById(R.id.playerTopBar);
		// 切屏按钮
		tvButton = (Button) findViewById(R.id.tv_button);
		tvButton.setOnClickListener(this);
		phoneButton = (Button) findViewById(R.id.phone_button);
		phoneButton.setOnClickListener(this);
		// 播放按钮
		mPlayButton = this.findViewById(R.id.mpPlay);
		mPlayButton.setOnClickListener(this);
		// 暂停按钮
		mPauseButton = this.findViewById(R.id.mpPause);
		mPauseButton.setOnClickListener(this);
		// 暂时用来显示播放状态
		this.assetNameText = (TextView) findViewById(R.id.playerMoiveName);
		this.assetNameText.setText(assetName);
		// 声音按钮事件
		mVoiceSeekBar = (VerticalSeekBar) this.findViewById(R.id.voiceSeekBar);
		mVoiceSeekBar.setOnVolumeChangedListener(mSeekListener);
		// 静音按钮
		voiceBtn = (Button) this.findViewById(R.id.voiceBtn);
		voiceBtn.setOnClickListener(this);

		playerCollectBtn = (Button) this.findViewById(R.id.playerCollectBtn);
		// 码率选择开关
		playerML = (Button) this.findViewById(R.id.playerML);
		playerML.setOnClickListener(this);
		// 码率选择开关
		malvLayout = (LinearLayout) this.findViewById(R.id.malvLayout);
		if (0 == videoType) {// 如果是标清，可以改码率播高清或标清，如果是高清，只能播高清
			playerML.setText("流畅");
			// 流畅
			this.findViewById(R.id.maLvLower).setVisibility(View.VISIBLE);
			this.findViewById(R.id.maLvLower).setOnClickListener(this);
			// 清晰
			this.findViewById(R.id.maLvHigher).setVisibility(View.VISIBLE);
			this.findViewById(R.id.maLvHigher).setOnClickListener(this);
			// 高清
			this.findViewById(R.id.maLvDefinition).setVisibility(View.VISIBLE);
			this.findViewById(R.id.maLvDefinition).setOnClickListener(this);
		} else {
			playerML.setText("高清");
			// 流畅
			this.findViewById(R.id.maLvLower).setVisibility(View.GONE);
			// 清晰
			this.findViewById(R.id.maLvHigher).setVisibility(View.VISIBLE);
			this.findViewById(R.id.maLvHigher).setOnClickListener(this);
			// 高清
			this.findViewById(R.id.maLvDefinition).setVisibility(View.VISIBLE);
			this.findViewById(R.id.maLvDefinition).setOnClickListener(this);
		}
		// 获取surfaceView
		this.mSurfaceView = (GL2JNIView) this.findViewById(R.id.playerView);
		mSurfaceView.setBackgroundColor(0);
		this.mSurfaceView.setOnTouchListener(new SurfaceViewOnTouchListener());
		// 手势监听器
		mGestureDetector = new GestureDetector(mContext,
				new PanelOnGestureListener());
		mGestureDetector.setIsLongpressEnabled(false);
		// 播放时间显示
		mTotalTimeBar = (TextView) this.findViewById(R.id.totalTime);
		mCurTimeBar = (TextView) this.findViewById(R.id.mpCurrentTime);

		// 播放进度控制条
		mPlaySeekBar = (SeekBar) this.findViewById(R.id.playSeekBar);
		if (0 < mPlaySeekBar.getMax()) {
			mPlaySeekBar
					.setOnSeekBarChangeListener(new SeekBarChangeListener());
		}

		loadingBar = (ProgressBar) this.findViewById(R.id.loadingBar);
		// 初始化EPG显示pop
		initEpgPopWindow();
		// 分享窗口
		createShareWindow();
		// 控制按钮显示和隐藏
		initBtnsByPlayType();
	}

	private void initBtnsByPlayType() {
		switch (playType) {
		case 1: // 点播界面按钮处理
			this.findViewById(R.id.playLive).setVisibility(View.INVISIBLE);
			this.findViewById(R.id.showEpgBtn).setVisibility(View.GONE);
			this.findViewById(R.id.playerDiscussBtn).setVisibility(View.GONE);
			this.findViewById(R.id.playerHistoryBtn)
					.setVisibility(View.VISIBLE);
			this.findViewById(R.id.playerRecommandBtn).setVisibility(
					View.VISIBLE);
			playerCollectBtn.setVisibility(View.VISIBLE);
			break;
		case 3:
			if (isFromLive) {// 如果在直播播放器里进入回看，按时移和直播来处理界面按钮
				this.findViewById(R.id.playLive).setVisibility(View.VISIBLE);
				this.findViewById(R.id.showEpgBtn).setVisibility(View.VISIBLE);
				this.findViewById(R.id.playerDiscussBtn).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.playerHistoryBtn).setVisibility(
						View.GONE);
				this.findViewById(R.id.playerRecommandBtn).setVisibility(
						View.GONE);
			} else { // 否则按点播来处理界面按钮
				this.findViewById(R.id.playLive).setVisibility(View.INVISIBLE);
				this.findViewById(R.id.showEpgBtn).setVisibility(View.GONE);
				this.findViewById(R.id.playerDiscussBtn).setVisibility(
						View.GONE);
				this.findViewById(R.id.playerHistoryBtn).setVisibility(
						View.VISIBLE);
				this.findViewById(R.id.playerRecommandBtn).setVisibility(
						View.VISIBLE);
			}
			playerCollectBtn.setVisibility(View.GONE);
			break;
		case 4:
		case 2:
			// 直播、时移界面按钮处理
			this.findViewById(R.id.playLive).setVisibility(View.VISIBLE);
			this.findViewById(R.id.showEpgBtn).setVisibility(View.VISIBLE);
			this.findViewById(R.id.playerDiscussBtn)
					.setVisibility(View.VISIBLE);
			this.findViewById(R.id.playerHistoryBtn).setVisibility(View.GONE);
			this.findViewById(R.id.playerRecommandBtn).setVisibility(View.GONE);
			playerCollectBtn.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		this.findViewById(R.id.playerShareBtn).setVisibility(View.VISIBLE);
		this.findViewById(R.id.playLive).setOnClickListener(this);
		this.findViewById(R.id.showEpgBtn).setOnClickListener(this);
		this.findViewById(R.id.playerShareBtn).setOnClickListener(this);
		this.findViewById(R.id.playerDiscussBtn).setOnClickListener(this);
		this.findViewById(R.id.playerHistoryBtn).setOnClickListener(this);
		this.findViewById(R.id.playerRecommandBtn).setOnClickListener(this);
		playerCollectBtn.setOnClickListener(this);
	}

	// 初始化播放组件
	private void initPlayer() {
		mediaControler = new MediaControler(this);
		if (!handler.hasMessages(MSG_GET_PLAYER_STATE)) {
			handler.sendEmptyMessage(MSG_GET_PLAYER_STATE);
		}
	}

	/**
	 * 获取播放串
	 * */
	private void getPlayUrl() {
		new AsyncTask<Void, Void, PlayURLJson>() {
			protected void onPreExecute() {
				if (null != loadingBar
						&& View.GONE == loadingBar.getVisibility()) {
					loadingBar.setVisibility(View.VISIBLE);
				}
			};

			@Override
			protected PlayURLJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new VodAction().getPlayURL(InterfaceUrls.GET_PLAYURL,
						resourceCode, productCode, subID,
						session.getUserCode(), playType, delay, shifttime,
						shiftend, timecode, fmt);
			}

			@Override
			protected void onPostExecute(PlayURLJson result) {
				if (null == result) {
					return;
				}
				if (0 == result.getRet()) {
					mPath = result.getPalyURL();
					if (TextUtils.isEmpty(mPath)) {
						Toast.makeText(mContext, "无法获取播放地址！", Toast.LENGTH_LONG)
								.show();
						return;
					}
					play();
				} else {
					Toast.makeText(mContext, "此视频播放出错！", Toast.LENGTH_LONG)
							.show();
				}
			}
		}.execute();
	}

	private void play() {
		// 控制按钮显示和隐藏
		initBtnsByPlayType();
		// 播放
		mediaControler.play(mPath);
	}

	// 改变音量大小
	private final OnVolumeChangedListener mSeekListener = new OnVolumeChangedListener() {
		@Override
		public void setYourVolume(int index) {
			// 清除控制条显示时间
			showTime = 0;
			if (isControlTVState) {
				if (mRemote != null) {
					mRemote.adapter().setVolume(mRemote,
							index * TV_MAX_VOLUME / PAD_MAX_VOLUME);
				}
			} else {
				isMute = 0;
				voiceBtn.setBackgroundResource(R.drawable.mp_volume);
				audiomanage.setStreamMute(AudioManager.STREAM_MUSIC, false);
				audiomanage
						.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
			}
		}
	};
	private LinearLayout malvLayout;
	private int count;

	// 音量键改变音量
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			audiomanage.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
			mVoiceSeekBar.setIndex(audiomanage
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			audiomanage.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
			mVoiceSeekBar.setIndex(audiomanage
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			break;
		}
		int volume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
		System.out.println(count++ + " ======== " + volume);

		return super.onKeyDown(keyCode, event);
	}

	class SurfaceViewOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return mGestureDetector.onTouchEvent(event);
		}
	}

	// 手势监听器
	class PanelOnGestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			if (isPrograssBarShowing) {
				closeController();
			} else {
				showController();
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return true;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_PLAYER_STATE:
				state = mediaControler.getPlayState();
				switch (state) {
				case UDRM_STATE_NOTHINGSPECIAL: {
					if (null != loadingBar
							&& View.GONE == loadingBar.getVisibility()) {
						loadingBar.setVisibility(View.VISIBLE);
					}
					LogUtils.trace(Log.DEBUG, TAG, "正在打开码流");
					break;
				}
				case UDRM_STATE_LOADING: {
					if (null != loadingBar
							&& View.GONE == loadingBar.getVisibility()) {
						loadingBar.setVisibility(View.VISIBLE);
					}
					delay++;
					LogUtils.trace(Log.DEBUG, TAG, "正在缓冲");
					break;
				}
				case UDRM_STATE_PLAYING: {
					// 关闭加载中
					if (dialog != null) {
						dialog.dismiss();
					}
					if (null != loadingBar
							&& View.VISIBLE == loadingBar.getVisibility()) {
						loadingBar.setVisibility(View.GONE);
					}
					LogUtils.trace(Log.DEBUG, TAG, "正在播放");
					if (isDraging) {
						return;
					}
					// 计时，记录当前播放时长
					timecode++;
					if (isTimeBarInited) {
						upDataProgressSeekBar();
					} else {
						initProgressSeekBar();
					}
					// 控制条是否为显示状态
					if (isPrograssBarShowing) {
						if (null != mPopupWindow && mPopupWindow.isShowing()) {
							break;
						}
						if (null != shareWindow && shareWindow.isShow()) {
							break;
						}
						if (null != recommandWindow
								&& recommandWindow.isShowing()) {
							break;
						}
						if (null != bookMarkWindow
								&& bookMarkWindow.isShowing()) {
							break;
						}
						if (null != commentWindow && commentWindow.isShowing()) {
							break;
						}
						if (null != malvLayout
								&& View.VISIBLE == malvLayout.getVisibility()) {
							break;
						}
						showTime += 1000;
						if (showTime > defaultShowTime) {
							// 隐藏控制条
							handler.sendEmptyMessage(MSG_HIDE_PROGRESSBAR);
						}
					}
					break;
				}
				case UDRM_STATE_PAUSE: {
					delay++;
					// 如果直播状态暂停切换为时移
					if (playType == 2) {
						playType = 4;
					}
					LogUtils.trace(Log.DEBUG, TAG, "播放暂停");
					break;
				}
				case UDRM_STATE_STOP: {
					LogUtils.trace(Log.DEBUG, TAG, "播放停止");
					break;
				}
				case UDRM_STATE_FINAISH: {
					LogUtils.trace(Log.DEBUG, TAG, "播放结束");
					if (1 == playType) {
						Toast.makeText(mContext,
								getString(R.string.play_finish),
								Toast.LENGTH_SHORT).show();
						finish();
					}
					break;
				}
				case UDRM_STATE_ERROR: {
					LogUtils.trace(Log.DEBUG, TAG, "出错了");
					Toast.makeText(mContext, "出错了！", Toast.LENGTH_SHORT).show();
					break;
				}
				default: {
					break;
				}
				}
				handler.sendEmptyMessageDelayed(MSG_GET_PLAYER_STATE, 1000);
				break;
			case MSG_UPDATE_PHONETV_STATE: // 更新电视、手机按钮状态
				assetNameText.setText(assetName);
				if (isControlTVState) {
					tvButton.setBackgroundResource(R.drawable.video_change);
					phoneButton
							.setBackgroundResource(R.drawable.video_change_null);
					mPauseButton.setVisibility(View.GONE);
					mPlayButton.setVisibility(View.VISIBLE);
				} else {
					tvButton.setBackgroundResource(R.drawable.video_change_null);
					phoneButton.setBackgroundResource(R.drawable.video_change);
					mPauseButton.setVisibility(View.VISIBLE);
					mPlayButton.setVisibility(View.GONE);
				}
				break;
			case SEND_MESSAGE_STOP: { // 106 发送停止指令给stb
				if (mRemote != null)
					mRemote.adapter().playControl(mRemote, Global.MEDIA_STOP,
							null);
				break;
			}
			case MESSAGE_GET_STATUS: { // 103 发送获取状态指令给stb
				if (mRemote != null) {
					mRemote.adapter().getVolume(mRemote);
					mRemote.adapter().playStatusSync(mRemote);
				}
				upDataProgressSeekBar();
				break;
			}
			case MSG_HIDE_PROGRESSBAR: { // 隐藏控制栏消息
				closeController();
				break;
			}
			case MSG_CHANGE_PLAY: { // 播放其它影片消息，用于CommonPopupWindow的点击事件处理
				mediaControler.stop();
				Bundle data = msg.getData();
				resourceCode = data.getString("resourceCode");
				shifttime = data.getLong("shifttime");
				shiftend = data.getLong("shiftend");
				timecode = data.getLong("timecode");
				playTime = data.getInt("playTime");
				playType = data.getInt("playType");
				assetName = data.getString("assetName");
				posterUrl = data.getString("posterUrl");
				assetID = data.getString("assetID");
				providerID = data.getString("providerID");
				rawTimeCode = playTime;
				assetNameText.setText(assetName);
				// 更新进度条信息
				initProgressSeekBar();
				upDataProgressSeekBar();
				// 获取播放地址并开始播放
				getPlayUrl();
				break;
			}
			default:
				break;
			}
		}
	};

	private void initProgressSeekBar() {
		if (isControlTVState) {
			if (1 == playType) {
				if (mediaControler == null) {
					return;
				}
				// 获取初始播放时间
				mPlaySeekBar.setMax(playTime);
				mPlaySeekBar.setProgress((int) timecode);
				mTotalTimeBar.setText(Util.millisToString(playTime * 1000));
			}
		} else {
			if (1 == playType) {// 点播
				if (mediaControler == null) {
					return;
				}
				// 获取初始播放时间
				long time = timecode * 1000;// 获取当前播放时间
				long length = mediaControler.getDuration() + rawTimeCode * 1000;// 获取视频时长
				mPlaySeekBar.setMax((int) length);
				mPlaySeekBar.setProgress((int) time);
				mTotalTimeBar.setText(Util.millisToString(length));
			} else if (2 == playType || 4 == playType) {
				if (null == curPlayprogram) {
					mPlaySeekBar.setEnabled(false);// 禁用
					return;
				}
				long startTime = Utility.dealTimeToSeconds(curPlayprogram
						.getBeginTime());
				long nowTime = System.currentTimeMillis() / 1000;
				long endTime = Utility.dealTimeToSeconds(curPlayprogram
						.getEndTime());
				mPlaySeekBar.setMax((int) (endTime - startTime));
				mPlaySeekBar.setProgress((int) (nowTime - delay - startTime));
				Log.e("SeekBar startTime---", nowTime - delay - startTime + "");
				mTotalTimeBar.setText(Utility.dateStrForTime(endTime)
						.substring(11));
			} else if (3 == playType) {// 回看
				if (timecode < shiftend - tempShiftTime) {// 没有播放到结尾
					if (isFromLive) {
						long startTime = Utility
								.dealTimeToSeconds(curPlayprogram
										.getBeginTime());
						long endTime = Utility.dealTimeToSeconds(curPlayprogram
								.getEndTime());
						mPlaySeekBar.setMax((int) (endTime - startTime));
						mPlaySeekBar.setProgress((int) (timecode));
						mTotalTimeBar.setText(Utility.dateStrForTime(endTime)
								.substring(11));
					} else {
						mPlaySeekBar.setMax((int) (shiftend - tempShiftTime));
						mPlaySeekBar.setProgress((int) timecode);
						mTotalTimeBar
								.setText(Util
										.millisToString((shiftend - tempShiftTime) * 1000));
					}
				}
			}
		}

		isTimeBarInited = true;
	}

	// 更新播放进度条进度
	private void upDataProgressSeekBar() {
		if (isControlTVState) {
			if (1 == playType) {
				if (mediaControler == null) {
					return;
				}
				mCurTimeBar.setText(Util.millisToString(timecode * 1000));
			}
		} else {
			if (1 == playType) {// 点播
				if (mediaControler == null) {
					return;
				}
				// 获取初始播放时间
				long time = timecode * 1000;// 获取当前播放时间
				mCurTimeBar.setText(Util.millisToString(time));
				mPlaySeekBar.setProgress((int) time);
				if (mPlaySeekBar.getMax() == time) {
					IsPlayEnd = true;
					// 退出播放
					Toast.makeText(mContext, getString(R.string.play_finish),
							Toast.LENGTH_SHORT).show();
					finish();
				}
			} else if (2 == playType || 4 == playType) {// 直播，时移
				if (null == curPlayprogram) {
					return;
				}
				long startTime = Utility.dealTimeToSeconds(curPlayprogram
						.getBeginTime());
				long nowTime = System.currentTimeMillis() / 1000;
				long endTime = Utility.dealTimeToSeconds(curPlayprogram
						.getEndTime());
				if (nowTime - delay >= endTime) { // 播放到结尾播放下一个节目
					getCurrentProgram();
				} else {
					mCurTimeBar.setText(Utility.dateStrForTime(nowTime - delay)
							.substring(11));
					mPlaySeekBar
							.setProgress((int) (nowTime - delay - startTime));
				}
			} else if (3 == playType) {// 回看
				if (timecode < shiftend - tempShiftTime) {// 没有播放到结尾
					if (isFromLive) {
						long nowTime = System.currentTimeMillis() / 1000;
						long startTime = Utility
								.dealTimeToSeconds(curPlayprogram
										.getBeginTime());
						mCurTimeBar.setText(Utility.dateStrForTime(
								startTime + timecode).substring(11));
					} else {
						mCurTimeBar.setText(Util
								.millisToString(timecode * 1000));
					}
					mPlaySeekBar.setProgress((int) timecode);
				} else {// 播放到结尾
					if (isFromLive) {
						// 播放下一个节目
						curPlayprogramIndex++;
						curPlayprogram = channelPrograms
								.get(curPlayprogramIndex);
						goToSeeBack();
					} else {
						// 退出播放
						Toast.makeText(mContext,
								getString(R.string.play_finish),
								Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			}
		}
	}

	/**
	 * 进度条监听器
	 * */
	class SeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			long seekTime = (long) seekBar.getProgress();
			switch (playType) {
			case 1:
				timecode = seekTime / 1000;
				break;
			case 2:
			case 4:
				if (curPlayprogram != null) {
					long startTime = Utility.dealTimeToSeconds(curPlayprogram
							.getBeginTime());
					long nowTime = System.currentTimeMillis() / 1000;
					timecode = 0;
					delay = nowTime - seekTime - startTime;// 时移的时间
				}
				break;
			case 3:
				timecode = seekTime;
				shifttime = tempShiftTime + seekTime;
				break;
			default:
				break;
			}
			upDataProgressSeekBar();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			findViewById(R.id.mpPlay).setVisibility(View.GONE);
			findViewById(R.id.mpPause).setVisibility(View.VISIBLE);
			isDraging = false;
			long seekTime = (long) seekBar.getProgress();
			LogUtils.trace(Log.VERBOSE, TAG, "===seekTime=====>>" + seekTime);
			switch (playType) {
			case 1:
				VodSeekBarChange(seekTime);
				break;
			case 2:
			case 4:
				LiveSeekBarChange(seekBar, seekTime);
				break;
			case 3:
				playBackSeekBarChange(seekTime);
				break;
			default:
				break;
			}
			upDataProgressSeekBar();
			if (isControlTVState) {
				// 暂停同步电视端的状态
				handler.removeMessages(MESSAGE_GET_STATUS);
				// 重新启动同步电视端的状态
				handler.sendEmptyMessageDelayed(MESSAGE_GET_STATUS, 5000);
			} else {
				// 获取播放状态、监控播控栏、信息栏显示
				handler.removeMessages(MSG_GET_PLAYER_STATE);
				handler.sendEmptyMessage(MSG_GET_PLAYER_STATE);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// 清除控制条显示时间
			showTime = 0;
			isDraging = true;
		}
	}

	private void VodSeekBarChange(long seekTime) {
		if (isControlTVState) { // 遥控器状态，发暂停命令给电视
			if (mRemote != null) {
				mRemote.adapter().playControl(mRemote, Global.MEDIA_PROGRESS,
						(int) (seekTime * 1000 / mediaControler.getDuration()));
			}
		} else {
			timecode = seekTime / 1000;
			rawTimeCode = timecode;
			// 获取当前时长
			if (UDRM_STATE_PAUSE == mediaControler.getPlayState()) {
				mediaControler.pause();
			}
			getPlayUrl();
		}
	}

	private void LiveSeekBarChange(SeekBar seekBar, long seekTime) {
		if (curPlayprogram == null) {
			goToLive();
		} else {
			long startTime = Utility.dealTimeToSeconds(curPlayprogram
					.getBeginTime());
			long nowTime = System.currentTimeMillis() / 1000;
			if (seekTime < nowTime - startTime) {
				mediaControler.stop();
				timecode = 0;
				delay = nowTime - seekTime - startTime;
				playType = 4;
				getPlayUrl();
			} else {
				if (4 == playType) {
					goToLive();
				}
				seekBar.setProgress((int) (nowTime - startTime));
			}
		}
	}

	private void playBackSeekBarChange(long seekTime) {
		if (isControlTVState) { // 遥控器状态，发暂停命令给电视
			if (mRemote != null) {
				mRemote.adapter().playControl(mRemote, Global.MEDIA_PROGRESS,
						(int) (seekTime * 1000 / mediaControler.getDuration()));
			}
		} else {
			if (seekTime > System.currentTimeMillis() / 1000 - tempShiftTime) {
				goToLive();
			} else {
				timecode = seekTime;
				shifttime = tempShiftTime + seekTime;
				getPlayUrl();
			}
		}
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		// 回退按钮
		case R.id.playerBackBtn:
			onBackPressed();
			break;
		// 播放按钮
		case R.id.mpPlay:
			if (isControlTVState) { // 遥控器状态，发播放命令给电视
				if (mRemote != null) {
					mRemote.adapter().playControl(mRemote, Global.MEDIA_PLAY,
							null);
				}
			} else {
				if (UDRM_STATE_PAUSE == mediaControler.getPlayState()) {
					mediaControler.resume();
					findViewById(R.id.mpPlay).setVisibility(View.GONE);
					findViewById(R.id.mpPause).setVisibility(View.VISIBLE);
				}
			}
			break;
		// 暂停按钮
		case R.id.mpPause:
			if (isControlTVState) { // 遥控器状态，发暂停命令给电视
				if (mRemote != null) {
					mRemote.adapter().playControl(mRemote, Global.MEDIA_PAUSE,
							null);
				}
			} else {
				if (UDRM_STATE_PLAYING == mediaControler.getPlayState()) {
					mediaControler.pause();
					findViewById(R.id.mpPlay).setVisibility(View.VISIBLE);
					findViewById(R.id.mpPause).setVisibility(View.GONE);
				}
			}
			break;
		// 一键回直播
		case R.id.playLive:
			if (playType != 2) {
				mediaControler.stop();
				goToLive();
				long startTime = Utility.dealTimeToSeconds(curPlayprogram
						.getBeginTime());
				long nowTime = System.currentTimeMillis() / 1000;
				mPlaySeekBar.setProgress((int) (nowTime - startTime));// 同时更新进步按钮的位置
			} else {
				Toast.makeText(mContext, "已是直播状态", Toast.LENGTH_SHORT).show();
			}
			break;
		// 码率选择开关
		case R.id.playerML:
			if (View.VISIBLE == malvLayout.getVisibility()) {
				malvLayout.setVisibility(View.GONE);
			} else {
				malvLayout.setVisibility(View.VISIBLE);
			}
			break;
		// 流畅
		case R.id.maLvLower:
			playerML.setText("流畅");
			fmt = 1;
			resetPlayer();
			break;
		// 清晰
		case R.id.maLvHigher:
			playerML.setText("标清");
			fmt = 2;
			resetPlayer();
			break;
		// 高清
		case R.id.maLvDefinition:
			playerML.setText("高清");
			fmt = 3;
			resetPlayer();
			break;
		// 静音按钮
		case R.id.voiceBtn:
			if (isMute == 0) {
				isMute = 1;
				voiceBtn.setBackgroundResource(R.drawable.no_volume);
				audiomanage.setStreamMute(AudioManager.STREAM_MUSIC, true);
				mVoiceSeekBar.setIndex(0);
			} else if (isMute == 1) {
				isMute = 0;
				voiceBtn.setBackgroundResource(R.drawable.mp_volume);
				audiomanage.setStreamMute(AudioManager.STREAM_MUSIC, false);
				int volume = audiomanage
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				mVoiceSeekBar.setIndex(volume);
			}
			break;
		case R.id.tv_button: { // 切换到tv
			// sendToTV();
			break;
		}
		case R.id.phone_button: { // 拉回到手机
			getTVPlay();
			break;
		}
		case R.id.showEpgBtn: { // 显示EPG信息
			mPopupWindow.showAtLocation(findViewById(R.id.playerShareBtn),
					Gravity.NO_GRAVITY, 960, 45);
			break;
		}
		case R.id.playerShareBtn: { // 分享按钮
			int objType = -1;
			switch (playType) {
			case 1:
				objType = 2;
				shareWindow.show(assetName, posterUrl, objType, resourceCode);
				break;
			case 2:
				objType = 1;
				if (curPlayprogram != null) {
					shareWindow.show(assetName, posterUrl, objType,
							curPlayprogram.getProgramId());
				} else {
					Toast.makeText(mContext, "当前节目信息为空", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case 3:
				objType = 1;
				shareWindow.show(assetName, posterUrl, objType, programID);
				break;
			case 4:
				objType = 1;
				break;
			default:
				break;
			}
			if (curPlayprogram != null) {
				shareWindow.show(assetName, posterUrl, objType,
						curPlayprogram.getProgramId());
			} else {
				Toast.makeText(mContext, "当前节目信息为空", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case R.id.playerDiscussBtn: { // 评论按钮
			View contentView = mLayoutInflater.inflate(R.layout.pop_discuss,
					null);
			if (curPlayprogram != null) {
				commentWindow = new CommentPopupWindow(mContext, contentView,
						curPlayprogram.getProgramId());
				commentWindow.showAtLocation(findViewById(R.id.playerShareBtn),
						Gravity.NO_GRAVITY, 960, 45);
			} else {
				Toast.makeText(mContext, "当前节目信息为空", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case R.id.playerHistoryBtn: { // 历史按钮
			View contentView = mLayoutInflater.inflate(R.layout.popup, null);
			bookMarkWindow = new CommonPopupWindow(mContext, contentView, 1,
					handler, MSG_CHANGE_PLAY);
			bookMarkWindow.showAtLocation(findViewById(R.id.playerShareBtn),
					Gravity.NO_GRAVITY, 960, 45);
			break;
		}
		case R.id.playerRecommandBtn: { // 推荐按钮
			View contentView = mLayoutInflater.inflate(R.layout.pop_recommand,
					null);
			recommandWindow = new CommonPopupWindow(mContext, contentView, 0,
					handler, MSG_CHANGE_PLAY);
			recommandWindow.showAtLocation(findViewById(R.id.playerShareBtn),
					Gravity.NO_GRAVITY, 960, 45);
			break;
		}
		case R.id.playerCollectBtn: { // 收藏按钮
			Session session = Session.getInstance();
			// 同时存入本地数据库中
			DbHelper dbhelper = new DbHelper(mContext);
			boolean isSuccess = dbhelper.queryData(session.getUserCode(),
					resourceCode);
			if (!isSuccess) {
				if (dbhelper.insertData(session.getUserCode(), resourceCode)) {
					addFavourite(resourceCode);
				}
			} else {
				Toast.makeText(mContext, getString(R.string.collected),
						Toast.LENGTH_SHORT).show();
			}
			dbhelper.closeConn();
			break;
		}
		}
	}

	// 切换码率之后重置播放器
	private void resetPlayer() {
		mediaControler.stop();
		getPlayUrl();
		findViewById(R.id.mpPlay).setVisibility(View.GONE);
		findViewById(R.id.mpPause).setVisibility(View.VISIBLE);
		closeController();// 关闭控制栏
	}

	private void initEpgPopWindow() {
		// 创建popupWindow
		View popupWindwow = mLayoutInflater.inflate(R.layout.popup, null);
		mPopupWindow = new PopupWindow(popupWindwow, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.pop_bg));
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		// 初始化popupWindow上的listView
		popList = (ListView) popupWindwow.findViewById(R.id.popList);
		mPopProgress = (ProgressBar) popupWindwow
				.findViewById(R.id.popLoadingBar);

		epgAdapter = new EpgAdapter();
		popList.setAdapter(epgAdapter);
		popList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ProgramInfo programInfo = (ProgramInfo) epgAdapter
						.getItemData(arg2);
				curPlayprogram = programInfo;
				curPlayprogramIndex = arg2;
				goToSeeBack();
			}
		});
	}

	private void getTVPlay() {
		if (!isControlTVState) {
			return;
		}
		mRemote = Client.create().current();
		if (mRemote == null) {
			mDeviceDialog.show();
			return;
		}
		mRemote.adapter().playSync(mRemote);
		isControlTVState = false;
		tvButton.setBackgroundResource(R.drawable.video_change_null);
		phoneButton.setBackgroundResource(R.drawable.video_change);
	}

	/**
	 * 回直播
	 * */
	private void goToLive() {
		getCurrentProgram();
		if (null != curPlayprogram) {
			playType = 2;
			delay = 0;
			shifttime = 0;
			shiftend = 0;
			timecode = 0;
			getPlayUrl();
			assetNameText.setText(curPlayprogram.getEventName());
			// 设置播放时间
			long startTime = Utility.dealTimeToSeconds(curPlayprogram
					.getBeginTime());
			long endTime = Utility.dealTimeToSeconds(curPlayprogram
					.getEndTime());
			playTime = (int) (endTime - startTime);
			mPlaySeekBar.setMax(playTime);
		}
	}

	/**
	 * 回看
	 * */
	private void goToSeeBack() {
		long startTime = Utility.dealTimeToSeconds(curPlayprogram
				.getBeginTime());
		long endTime = Utility.dealTimeToSeconds(curPlayprogram.getEndTime());
		long nowTime = System.currentTimeMillis() / 1000;
		shifttime = startTime;
		tempShiftTime = startTime;
		shiftend = endTime;
		playTime = (int) (shiftend - shifttime);
		timecode = 0;
		// 设置播控信息
		if (nowTime < endTime && nowTime > startTime) {
			playType = 4;// 时移
		} else {
			playType = 3;// 回看
		}
		mPlaySeekBar.setMax((int) (endTime - startTime));
		mPlaySeekBar.setProgress(0);
		mTotalTimeBar.setText(Utility.dateStrForTime(endTime).substring(11));
		mediaControler.stop();
		assetName = curPlayprogram.getEventName();
		assetNameText.setText(assetName);
		// 获取播放地址并开始播放
		getPlayUrl();
	}

	private void getCurrentProgram() {
		ProgramInfo program = null;
		if (channelPrograms == null) {
			return;
		}
		for (int index = 0, len = channelPrograms.size(); index < len; index++) {
			program = channelPrograms.get(index);
			long startTime = Utility.dealTimeToSeconds(program.getBeginTime());
			long endTime = Utility.dealTimeToSeconds(program.getEndTime());
			long nowTime = System.currentTimeMillis() / 1000;
			if (startTime <= nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，为当前正在播放的节目
				curPlayprogramIndex = index;
				curPlayprogram = program;
				assetName = curPlayprogram.getEventName();
				assetNameText.setText(assetName);// 获得节目名称
				mPlaySeekBar.setMax((int) (endTime - startTime));
				mPlaySeekBar.setProgress((int) (nowTime - startTime));
				mTotalTimeBar.setText(Utility.dateStrForTime(endTime)
						.substring(11));
				break;
			}
		}
	}

	/**
	 * 显示控制栏
	 * */
	private void showController() {
		showTime = 0;
		// 弹出进度控制栏
		playerContralRoot.setVisibility(View.VISIBLE);
		// 声音控制栏
		mVoiceSeekBar.setVisibility(View.VISIBLE);
		// 顶部信息栏
		playerTopBar.setVisibility(View.VISIBLE);
		isPrograssBarShowing = true;
	}

	/**
	 * 关闭控制栏
	 * */
	private void closeController() {
		malvLayout.setVisibility(View.GONE);
		mPopupWindow.dismiss();
		mVoiceSeekBar.setVisibility(View.GONE);
		playerContralRoot.setVisibility(View.GONE);
		playerTopBar.setVisibility(View.GONE);
		isPrograssBarShowing = false;
		showTime = 0;
	}

	/**
	 * 获取EPG列表数据
	 */
	private void getChannelProgram(final int pageNo) {
		new AsyncTask<Void, Void, ProgramInfosJson>() {
			@Override
			protected ProgramInfosJson doInBackground(Void... params) {
				Session session = Session.getInstance();
				return new LiveAction().getChannelProgram(
						InterfaceUrls.GET_CHANNEL_PROGRAM,
						session.getUserCode(), resourceCode, EPG_PAGE_SIZE,
						pageNo, nowDay + " 00:00:00", nowDay + " 23:59:59");
			}

			@Override
			protected void onPostExecute(ProgramInfosJson result) {
				mPopProgress.setVisibility(View.GONE);
				if (null != result && 0 == result.getRet()) {
					channelPrograms = result.getProgram();
					for (ProgramInfo program : channelPrograms) {
						long startTime = Utility.dealTimeToSeconds(program
								.getBeginTime());
						long endTime = Utility.dealTimeToSeconds(program
								.getEndTime());
						long nowTime = System.currentTimeMillis() / 1000;

						if (startTime < nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，显示正在播放
							curPlayprogram = program;
							if (2 == playType) {
								playTime = (int) (endTime - startTime);
								mPlaySeekBar.setMax(playTime);
								mPlaySeekBar
										.setProgress((int) (nowTime - startTime));
								assetName = program.getEventName();
								assetNameText.setText(program.getEventName());
								break;
							}
						}
						curPlayprogramIndex++;
					}
					// 初始化EPG列表及星期按钮列表
					filterChannelPrograms(channelPrograms);
				}
			};
		}.execute();
	}

	// 过滤未播放节目
	private void filterChannelPrograms(ArrayList<ProgramInfo> programs) {
		ArrayList<ProgramInfo> delayPrograms = new ArrayList<ProgramInfo>();
		for (ProgramInfo program : channelPrograms) {
			long startTime = Utility.dealTimeToSeconds(program.getBeginTime());
			long nowTime = System.currentTimeMillis() / 1000;
			if (startTime < nowTime) {
				delayPrograms.add(program);
			}
		}
		epgAdapter.addNewDatas(delayPrograms);
	}

	/**
	 * EPG结果列表项
	 */
	public final class ViewEpgHolder {
		public TextView epgStartTime;
		public TextView epgProgrameName;
		public ImageView playButton;
	}

	/**
	 * EPG数据注入
	 */
	public class EpgAdapter extends CommonAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewEpgHolder holder = null;
			if (convertView == null) {
				holder = new ViewEpgHolder();
				convertView = mLayoutInflater.inflate(R.layout.player_epg_list,
						null);
				holder.epgStartTime = (TextView) convertView
						.findViewById(R.id.epgStartTime);
				holder.epgProgrameName = (TextView) convertView
						.findViewById(R.id.epgProgrameName);
				holder.playButton = (ImageView) convertView
						.findViewById(R.id.play_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewEpgHolder) convertView.getTag();
			}

			ProgramInfo programInfo = (ProgramInfo) datas.get(position);
			if (programInfo == null) {
				return convertView;
			}
			long startTime = Utility.dealTimeToSeconds(programInfo
					.getBeginTime());
			long endTime = Utility.dealTimeToSeconds(programInfo.getEndTime());
			long nowTime = System.currentTimeMillis() / 1000;
			if (nowTime > endTime) {// 如果大于结束时间，显示回看
				holder.playButton.setVisibility(View.VISIBLE);
			} else if (startTime < nowTime && nowTime < endTime) { // 如果大于开始时间且小于结束时间，显示正在播放
				holder.playButton.setVisibility(View.VISIBLE);
			} else {
				holder.playButton.setVisibility(View.INVISIBLE);
			}
			String beginTime = programInfo.getBeginTime();
			try {
				beginTime = beginTime.substring(11, 16);
			} catch (Exception e) {
			}
			holder.epgStartTime.setText(beginTime);
			holder.epgProgrameName.setText(programInfo.getEventName());
			return convertView;
		}
	}

	/**
	 * 获取用户历史记录（书签）
	 * */
	private void checkIfPlayBookMark() {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<String, Void, BookMarksJson>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (null != loadingBar
						&& View.GONE == loadingBar.getVisibility()) {
					loadingBar.setVisibility(View.VISIBLE);
				}
			}

			@Override
			protected BookMarksJson doInBackground(String... params) {
				return new BookMarkAction().getBookMark(
						InterfaceUrls.GET_BOOKMARK, session.getUserCode(),
						session.getUserName());
			};

			@Override
			protected void onPostExecute(BookMarksJson result) {
				if (null != loadingBar
						&& View.VISIBLE == loadingBar.getVisibility()) {
					loadingBar.setVisibility(View.GONE);
				}
				Log.e("getBooKMark",
						result.getRet() + "------" + result.getRetInfo());
				if (null != result && 0 == result.getRet()
						&& result.getBookMark() != null) {
					for (BookMark bookMark : result.getBookMark()) {
						if (!TextUtils.isEmpty(bookMark
								.getCurrentResourceCode())) {
							if (resourceCode.equals(bookMark
									.getCurrentResourceCode())
									|| resourceCode.equals(bookMark
											.getResourceCode())) {
								if (bookMark.getBookMark() > 0
										&& bookMark.getBookMark() != Constant.PLAY_OVER) {
									// 如果没有选择，默认播放历史记录
									createDialog(bookMark.getBookMark()).show();
								}
								break;
							}
						}
					}
				} else {
					if (TextUtils.isEmpty(mPath)) {
						getPlayUrl();
					} else {
						play();
					}
				}
			}
		}.execute();
	}

	/**
	 * 查询当前用户的所有的收藏记录
	 * */
	private void getUserFavourite() {
		final Session session = Session.getInstance();
		if (!TextUtils.isEmpty(session.getUserCode())) {
			DbHelper dbhelper = new DbHelper(mContext);
			boolean result = dbhelper.queryData(session.getUserCode(),
					resourceCode);
			if (result) {
				playerCollectBtn.setBackgroundResource(R.drawable.collected);
			}
			dbhelper.closeConn();
		}
	}

	private Dialog createDialog(final long bookmark) {
		dialog = new AlertDialog.Builder(mContext).setTitle("提示")
				.setMessage("是否从上次观看记录继续观看？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						timecode = bookmark;
						rawTimeCode = timecode;
						getPlayUrl();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						timecode = 0;
						rawTimeCode = timecode;
						if (TextUtils.isEmpty(mPath)) {
							getPlayUrl();
						} else {
							play();
						}
					}
				}).create();
		return dialog;
	}

	/**
	 * 添加书签
	 * */
	private void addBookMark() {
		if (0 >= timecode) {
			return;
		}
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				return new BookMarkAction().addBookMark(
						InterfaceUrls.ADD_BOOKMARK, session.getUserCode(),
						session.getUserName(), resourceCode, timecode);
			}

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != result && 0 == result.getRet()) {
					LogUtils.trace(Log.DEBUG, TAG, assetName + "已经加入历史记录！");
				} else {
					LogUtils.trace(Log.DEBUG, TAG, assetName + "加入历史记录时发生错误");
				}
			}
		}.execute();
	}

	/**
	 * 加入收藏夹
	 * */
	private void addFavourite(final String resourceCode) {
		final Session session = Session.getInstance();
		if (!session.isLogined()) {
			UIUtility.showDialog(mContext);
			return;
		}
		new AsyncTask<Void, Void, BaseJsonBean>() {
			@Override
			protected BaseJsonBean doInBackground(Void... params) {
				return new FavoriteAction().addFavorite(
						InterfaceUrls.ADD_FAVORITE, session.getUserCode(),
						session.getUserName(), resourceCode);
			};

			@Override
			protected void onPostExecute(BaseJsonBean result) {
				if (null != result && 0 == result.getRet()) {
					playerCollectBtn
							.setBackgroundResource(R.drawable.collected);
				} else {
					Toast.makeText(mContext,
							getString(R.string.collect_failed),
							Toast.LENGTH_LONG).show();
				}
			};
		}.execute();
	}

	private void createShareWindow() {
		try {
			shareWindow = new ShareWindow(rootView, mContext,
					new ShareWindowListener() {
						@Override
						public void show() {
							if (UDRM_STATE_PLAYING == mediaControler
									.getPlayState()) {
								mediaControler.pause();
							}
							mPlayButton.setVisibility(View.VISIBLE);
							mPauseButton.setVisibility(View.GONE);
						}

						@Override
						public void dismiss() {
							if (UDRM_STATE_PAUSE == mediaControler
									.getPlayState()) {
								mediaControler.resume();
							}
							mPlayButton.setVisibility(View.GONE);
							mPauseButton.setVisibility(View.VISIBLE);
						}
					});
		} catch (Throwable e) {
		}
	}

	// 注册监听器
	private void registerReceiver() {
		// 注册BroadcastReceiver
		IntentFilter ifilter = new IntentFilter();
		// 滑动解锁过滤器
		ifilter.addAction("android.intent.action.USER_PRESENT");
		ifilter.addAction("android.intent.action.SCREEN_OFF");
		registerReceiver(mBroadcastReceiver, ifilter);
	}
}
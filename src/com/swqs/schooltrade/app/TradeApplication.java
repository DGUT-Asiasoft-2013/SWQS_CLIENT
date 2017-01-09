package com.swqs.schooltrade.app;

import android.app.Application;
import android.os.Environment;
import cn.jmessage.android.uikit.chatting.utils.NotificationClickEventReceiver;
import cn.jmessage.android.uikit.chatting.utils.SharePreferenceManager;
import cn.jpush.im.android.api.JMessageClient;

public class TradeApplication extends Application {

	public static final int REQUEST_CODE_TAKE_PHOTO = 4;
	public static final int REQUEST_CODE_SELECT_PICTURE = 6;
	public static final int RESULT_CODE_SELECT_PICTURE = 8;
	public static final int REQUEST_CODE_SELECT_ALBUM = 10;
	public static final int RESULT_CODE_SELECT_ALBUM = 11;
	public static final int REQUEST_CODE_BROWSER_PICTURE = 12;
	public static final int RESULT_CODE_BROWSER_PICTURE = 13;
	public static final int REQUEST_CODE_CHAT_DETAIL = 14;
	public static final int RESULT_CODE_CHAT_DETAIL = 15;
	public static final int REQUEST_CODE_FRIEND_INFO = 16;
	public static final int RESULT_CODE_FRIEND_INFO = 17;
	public static final int REQUEST_CODE_CROP_PICTURE = 18;
	public static final int REQUEST_CODE_ME_INFO = 19;
	public static final int RESULT_CODE_ME_INFO = 20;
	public static final int REQUEST_CODE_ALL_MEMBER = 21;
	public static final int RESULT_CODE_ALL_MEMBER = 22;
	public static final int RESULT_CODE_SELECT_FRIEND = 23;
	public static final int REQUEST_CODE_SEND_LOCATION = 24;
	public static final int RESULT_CODE_SEND_LOCATION = 25;
	public static final int REQUEST_CODE_SEND_FILE = 26;
	public static final int RESULT_CODE_SEND_FILE = 27;
	public static final int REQUEST_CODE_EDIT_NOTENAME = 28;
	public static final int RESULT_CODE_EDIT_NOTENAME = 29;
	public static final int ON_GROUP_EVENT = 3004;

	public static final String SCHOOLTRADE_CONFIGS = "SchoolTrade_Configs";
	public static final String CONV_TITLE = "convTitle";
	public static final String TARGET_APP_KEY = "targetAppKey";
	public static final String TARGET_ID = "targetId";
	public static final String AVATAR = "avatar";
	public static final String NAME = "name";
	public static final String NICKNAME = "nickname";
	public static final String NOTENAME = "notename";
	public static final String GENDER = "gender";
	public static final String REGION = "region";
	public static final String SIGNATURE = "signature";
	public static final String STATUS = "status";
	public static final String POSITION = "position";
	public static final String MsgIDs = "msgIDs";
	public static final String DRAFT = "draft";
	public static final String DELETE_MODE = "deleteMode";
	public static final String ACCOUNT = "account";
	public static final String PASSWORD = "password";
	public static final String IS_AUTO_LOGIN = "is_auto_login";
	public static String PICTURE_DIR = Environment.getExternalStorageDirectory()+"/SchoolTrade/pictures/";
	public static String FILE_DIR = Environment.getExternalStorageDirectory()+"/SchoolTrade/recvFiles/";

	public static String uid="";
	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化JMessage-sdk
		JMessageClient.init(getApplicationContext());
		SharePreferenceManager.init(getApplicationContext(), SCHOOLTRADE_CONFIGS);
		// 设置Notification的模式
		JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
		// 注册Notification点击的接收器
		new NotificationClickEventReceiver(getApplicationContext());
	}
}

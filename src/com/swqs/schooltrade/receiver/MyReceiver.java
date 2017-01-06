package com.swqs.schooltrade.receiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqs.schooltrade.activity.BuyOrderDetailsActivity;
import com.swqs.schooltrade.activity.SellOrderDetailsActivity;
import com.swqs.schooltrade.entity.Goods;
import com.swqs.schooltrade.util.Server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MyReceiver extends BroadcastReceiver {

	private static final String TAG = "OK";
	private static Map<String, Goods> goodsMap = new HashMap<String, Goods>();

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.e(TAG, "onReceive - " + intent.getAction());

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// 自定义消息不会展示在通知栏，完全要开发者写代码去处理
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			if (bundle != null) {
				String jsonString = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Log.e(TAG, jsonString);
				try {
					JSONObject obj = new JSONObject(jsonString);
					String goodsId = obj.getString("goods_id");
					getGoods(goodsId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			// 在这里可以做些统计，或者做些其他工作
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			if (bundle != null) {
				String jsonString = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Log.e(TAG, "点击：" + jsonString);
				System.out.println("用户点击打开了通知");
				try {
					JSONObject obj = new JSONObject(jsonString);
					String goodsId = obj.getString("goods_id");
					String pushType=obj.getString("pushType");
					if("1".equals(pushType)){
						Intent i = new Intent(context, SellOrderDetailsActivity.class); // 自定义打开的界面
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("data", goodsMap.get(goodsId));
						context.startActivity(i);
					}else if("2".equals(pushType)){
						Intent i = new Intent(context, BuyOrderDetailsActivity.class); // 自定义打开的界面
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("data", goodsMap.get(goodsId));
						context.startActivity(i);
					}else if("3".equals(pushType)){
						Intent i = new Intent(context, SellOrderDetailsActivity.class); // 自定义打开的界面
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("data", goodsMap.get(goodsId));
						context.startActivity(i);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 在这里可以自己写代码去定义用户点击后的行为
				
			}

		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());
		}
	}

	private void getGoods(final String goodsId) {
		Request request = Server.requestBuilderWithApi("getGoods?goods_id=" + goodsId).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					Goods goods = new ObjectMapper().readValue(arg1.body().string(), Goods.class);
					goodsMap.put(goodsId, goods);
				} catch (final Exception e) {
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException e) {
			}
		});
	}

}

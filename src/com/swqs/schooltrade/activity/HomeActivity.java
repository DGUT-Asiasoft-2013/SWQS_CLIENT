package com.swqs.schooltrade.activity;

import java.util.Set;

import com.swqs.schooltrade.R;
import com.swqs.schooltrade.page.GoodsListFragment;
import com.swqs.schooltrade.page.MyprofileFragment;
import com.swqs.schooltrade.page.SearchFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.jmessage.android.uikit.chatting.ConversationListFragment;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

public class HomeActivity extends Activity implements OnClickListener {

	private LinearLayout ll_home;
	private LinearLayout ll_search;
	private LinearLayout ll_message;
	private LinearLayout ll_me;
	private Button btnAddGoods;
	private ImageView image_home;
	private ImageView image_search;
	private ImageView image_message;
	private ImageView image_me;
	// Fragment������
	private FragmentManager fm = this.getFragmentManager();
	private FragmentTransaction ft;
	private GoodsListFragment fragmentPageGoods;
	private MyprofileFragment fragmentPageMe;
	private SearchFragment fragmentPageSearch;
	private ConversationListFragment fragmentconversation;
	private static final String TAG = "HomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initView();
		// ��ʼ����ÿ�θı�Fragment������֮��Ҫ�ύ��
		ft = fm.beginTransaction();
		home();
		// �ύ����
		ft.commit();
		UserInfo myInfo = JMessageClient.getMyInfo();
		if (myInfo != null) {
			JPushInterface.setAlias(HomeActivity.this, myInfo.getUserName(), new TagAliasCallback() {

				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					Log.e(TAG, arg0 + "");
					Log.e(TAG, arg1);
				}
			});
		}
	}

	private void initView() {

		ll_home = (LinearLayout) findViewById(R.id.ll_home);
		ll_search = (LinearLayout) findViewById(R.id.ll_search);
		ll_message = (LinearLayout) findViewById(R.id.ll_message);
		ll_me = (LinearLayout) findViewById(R.id.ll_me);
		btnAddGoods = (Button) findViewById(R.id.btnAdd);

		image_home = (ImageView) findViewById(R.id.image_home);
		image_search = (ImageView) findViewById(R.id.image_search);
		image_message = (ImageView) findViewById(R.id.image_message);
		image_me = (ImageView) findViewById(R.id.image_me);

		ll_home.setOnClickListener(this);
		ll_search.setOnClickListener(this);
		ll_message.setOnClickListener(this);
		ll_me.setOnClickListener(this);
		btnAddGoods.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, NewGoodsActivity.class);
				startActivity(intent);
			}
		});
		ll_home.setSelected(true);
		image_home.setSelected(true);

	}

	@Override
	public void onClick(View v) {
		// ÿ�ε��ʱ����Ҫ���¿�ʼ����
		ft = fm.beginTransaction();
		// ����ʾ��Fragment����
		setSelected();
		switch (v.getId()) {
		case R.id.ll_home:
			ll_home.setSelected(true);
			image_home.setSelected(true);
			home();
			break;
		case R.id.ll_search:
			ll_search.setSelected(true);
			image_search.setSelected(true);
			search();

			break;

		case R.id.ll_message:
			ll_message.setSelected(true);
			image_message.setSelected(true);
			message();
			break;

		case R.id.ll_me:
			ll_me.setSelected(true);
			image_me.setSelected(true);
			me();
			break;
		}
		ft.commit();

	}

	private void setSelected() {
		ll_home.setSelected(false);
		ll_search.setSelected(false);
		ll_message.setSelected(false);
		ll_me.setSelected(false);
		image_home.setSelected(false);
		image_search.setSelected(false);
		image_message.setSelected(false);
		image_me.setSelected(false);
		if (fragmentPageGoods != null) {
			// ����Fragment
			ft.hide(fragmentPageGoods);
		}
		if (fragmentPageMe != null) {
			ft.hide(fragmentPageMe);
		}
		if (fragmentPageSearch != null) {
			ft.hide(fragmentPageSearch);
		}
		if (fragmentconversation != null) {
			ft.hide(fragmentconversation);
		}
	}

	private void home() {
		if (fragmentPageGoods == null) {
			fragmentPageGoods = new GoodsListFragment();
			ft.add(R.id.fl_content, fragmentPageGoods);
		} else {
			// ��ʾFragment
			ft.show(fragmentPageGoods);
		}
	}

	private void me() {
		if (fragmentPageMe == null) {
			fragmentPageMe = new MyprofileFragment();
			ft.add(R.id.fl_content, fragmentPageMe);
		} else {
			ft.show(fragmentPageMe);
		}

	}

	private void search() {
		if (fragmentPageSearch == null) {
			fragmentPageSearch = new SearchFragment();
			ft.add(R.id.fl_content, fragmentPageSearch);
		} else {
			ft.show(fragmentPageSearch);
		}

	}

	private void message() {
		if (fragmentconversation == null) {
			fragmentconversation = new ConversationListFragment();
			ft.add(R.id.fl_content, fragmentconversation);
		} else {
			ft.show(fragmentconversation);
		}

	} /*
		 * ��ӵ�Fragment�������� ���������replace�� ��ÿ�ε���ʱ�����ǰһ��Fragment���ɵ���
		 * �����͵�����ÿһ�ζ�Ҫ���������٣� ���ݾͺ��ѱ��棬��add�Ͳ����������������ˣ� ��Fragment����ʱ���������ʾ��������ʱ�ʹ�����
		 * �����Ļ����ݾͲ���Ҫ�Լ������ˣ� ��Ϊ��һ�δ�����ʱ����Ѿ������ˣ� ֻҪ������һֱ��������
		 */
}

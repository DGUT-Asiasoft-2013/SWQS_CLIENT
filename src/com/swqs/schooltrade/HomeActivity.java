package com.swqs.schooltrade;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import page.FeedListFragment;
import page.GoodsListFragment;
import page.MyprofileFragment;
import page.NoteListFragment;
import page.PublishFragment;
import page.SearchFragment;


public class HomeActivity extends Activity implements OnClickListener{  
	private LinearLayout ll_home;  
	private LinearLayout ll_friends;
	private LinearLayout ll_publish;
	private LinearLayout ll_message;  
	private LinearLayout ll_more;  
	private ImageView image_home;  
	private ImageView image_friends;
	private ImageView image_publish;
	private ImageView image_message;  
	private ImageView image_more;  
	//Fragment������  
	private FragmentManager fm = this.getFragmentManager();  
	private FragmentTransaction ft;  
	private GoodsListFragment fragmentPageGoods;  
	private MyprofileFragment fragmentPageMe;
	private SearchFragment fragmentPageSearch;
	private NoteListFragment fragmentPageNote;
	private PublishFragment  fragmentPagePublish;

	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_home);  

		initView();  
		//��ʼ����ÿ�θı�Fragment������֮��Ҫ�ύ��  
		ft = fm.beginTransaction();  
		home();  
		//�ύ����  
		ft.commit();
	    
		//��ӷ�����Ʒ��+����ť�ĵ���¼�
		findViewById(R.id.text_publish).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				publishGoods();
			}
		});
	}
	
	//������Ʒ����
	void publishGoods(){
		Intent itnt = new Intent(this, NewGoodsActivity.class);
		startActivity(itnt);
	}

	private void initView(){

		ll_home = (LinearLayout)findViewById(R.id.ll_home);  
		ll_friends = (LinearLayout)findViewById(R.id.ll_search);  
		ll_message = (LinearLayout)findViewById(R.id.ll_message);  
		ll_more = (LinearLayout)findViewById(R.id.ll_me);
		ll_publish = (LinearLayout)findViewById(R.id.ll_publish);

		image_home = (ImageView)findViewById(R.id.image_home);  
		image_friends = (ImageView)findViewById(R.id.image_search);  
		image_message = (ImageView)findViewById(R.id.image_message);  
		image_more = (ImageView)findViewById(R.id.image_me);
		image_publish = (ImageView)findViewById(R.id.image_publish);

		ll_home.setOnClickListener(this);  
		ll_friends.setOnClickListener(this);  
		ll_message.setOnClickListener(this);  
		ll_more.setOnClickListener(this);
		ll_publish.setOnClickListener(this);
		ll_home.setSelected(true);  
		image_home.setSelected(true);  

	}  

	@Override  
	public void onClick(View v) {  
		//ÿ�ε��ʱ����Ҫ���¿�ʼ����  
		ft = fm.beginTransaction();  
		//����ʾ��Fragment����  
		setSelected();  
		switch (v.getId()) {  
		case R.id.ll_home:  
			ll_home.setSelected(true);  
			image_home.setSelected(true);  
			home();  
			break;  
		case R.id.ll_search:  
			ll_friends.setSelected(true);  
			image_friends.setSelected(true);  
			friend();  

			break;  
		case R.id.ll_message:  
			ll_message.setSelected(true);  
			image_message.setSelected(true);  
			message();  
			break;  
		case R.id.ll_me:  
			ll_more.setSelected(true);  
			image_more.setSelected(true);  
			more();  
			break;  
		}  
		ft.commit();  

	}  

	private void setSelected(){  
		ll_home.setSelected(false);  
		ll_friends.setSelected(false);  
		ll_message.setSelected(false);  
		ll_more.setSelected(false);  
		image_home.setSelected(false);  
		image_friends.setSelected(false);  
		image_message.setSelected(false);  
		image_more.setSelected(false);  
		if(fragmentPageGoods != null){  
			//����Fragment  
			ft.hide(fragmentPageGoods);  
		}  
		if(fragmentPageMe != null){  
			ft.hide(fragmentPageMe);  
		}  
		if(fragmentPageSearch != null){  
			ft.hide(fragmentPageSearch);  
		}  
		if(fragmentPageNote != null){  
			ft.hide(fragmentPageNote);  
		}  
	}  

	private void home(){  
		if(fragmentPageGoods == null){  
			fragmentPageGoods = new GoodsListFragment();  
			/*��ӵ�Fragment�������� 
            ���������replace�� 
            ��ÿ�ε���ʱ�����ǰһ��Fragment���ɵ��� 
            �����͵�����ÿһ�ζ�Ҫ���������٣� 
            ���ݾͺ��ѱ��棬��add�Ͳ����������������ˣ� 
            ��Fragment����ʱ���������ʾ��������ʱ�ʹ����� 
            �����Ļ����ݾͲ���Ҫ�Լ������ˣ� 
            ��Ϊ��һ�δ�����ʱ����Ѿ������ˣ� 
            ֻҪ������һֱ��������*/  
			ft.add(R.id.fl_content, fragmentPageGoods);  
		}else{  
			//��ʾFragment  
			ft.show(fragmentPageGoods);  
		}  
	}  
	private void friend(){  
		if(fragmentPageMe == null){  
			fragmentPageMe = new MyprofileFragment();  
			ft.add(R.id.fl_content, fragmentPageMe);  
		}else{  
			ft.show(fragmentPageMe);  
		}  

	}  
	private void message(){  
		if(fragmentPageSearch == null){  
			fragmentPageSearch = new SearchFragment();  
			ft.add(R.id.fl_content, fragmentPageSearch);  
		}else{  
			ft.show(fragmentPageSearch);  
		}  

	}  
	private void more(){  
		if(fragmentPageNote == null){  
			fragmentPageNote = new NoteListFragment();  
			ft.add(R.id.fl_content, fragmentPageNote);  
		}else{  
			ft.show(fragmentPageNote);  
		}  

	}  
}

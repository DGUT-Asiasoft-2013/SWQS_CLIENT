package com.swqs.schooltrade;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import page.FeedListFragment;
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
    //Fragment管理器  
    private FragmentManager fm = this.getFragmentManager();  
    private FragmentTransaction ft;  
    private FeedListFragment fragmentPage1;  
    private MyprofileFragment fragmentPage2;
    private SearchFragment fragmentPage3;
    private NoteListFragment fragmentPage4;
    private PublishFragment  fragmentPage5;
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_home);  
          
        initView();  
        //开始事务（每次改变Fragment管理器之后都要提交）  
        ft = fm.beginTransaction();  
        home();  
        //提交事务  
        ft.commit();  
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
        //每次点击时都需要重新开始事务  
        ft = fm.beginTransaction();  
        //把显示的Fragment隐藏  
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
        if(fragmentPage1 != null){  
            //隐藏Fragment  
            ft.hide(fragmentPage1);  
        }  
        if(fragmentPage2 != null){  
            ft.hide(fragmentPage2);  
        }  
        if(fragmentPage3 != null){  
            ft.hide(fragmentPage3);  
        }  
        if(fragmentPage4 != null){  
            ft.hide(fragmentPage4);  
        }  
    }  
  
    private void home(){  
        if(fragmentPage1 == null){  
            fragmentPage1 = new FeedListFragment();  
            /*添加到Fragment管理器中 
            这里如果用replace， 
            当每次调用时都会把前一个Fragment给干掉， 
            这样就导致了每一次都要创建、销毁， 
            数据就很难保存，用add就不存在这样的问题了， 
            当Fragment存在时候就让它显示，不存在时就创建， 
            这样的话数据就不需要自己保存了， 
            因为第一次创建的时候就已经保存了， 
            只要不销毁一直都将存在*/  
            ft.add(R.id.fl_content, fragmentPage1);  
        }else{  
            //显示Fragment  
            ft.show(fragmentPage1);  
        }  
    }  
    private void friend(){  
        if(fragmentPage2 == null){  
            fragmentPage2 = new MyprofileFragment();  
            ft.add(R.id.fl_content, fragmentPage2);  
        }else{  
            ft.show(fragmentPage2);  
        }  
          
    }  
    private void message(){  
        if(fragmentPage3 == null){  
            fragmentPage3 = new SearchFragment();  
            ft.add(R.id.fl_content, fragmentPage3);  
        }else{  
            ft.show(fragmentPage3);  
        }  
          
    }  
    private void more(){  
        if(fragmentPage4 == null){  
            fragmentPage4 = new NoteListFragment();  
            ft.add(R.id.fl_content, fragmentPage4);  
        }else{  
            ft.show(fragmentPage4);  
        }  
          
    }  
}

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
import page.ConversationListFragment;
import page.PublishFragment;
import page.SearchFragment;

public class HomeActivity extends Activity implements OnClickListener{  
 
    private LinearLayout ll_home;  
    private LinearLayout ll_search;
    private LinearLayout ll_publish;
    private LinearLayout ll_message;  
    private LinearLayout ll_me;  
    private ImageView image_home;  
    private ImageView image_search;
    private ImageView image_publish;
   private ImageView image_message;  
    private ImageView image_me;  
    //Fragment管理器  
    private FragmentManager fm = this.getFragmentManager();  
    private FragmentTransaction ft;  
    private GoodsListFragment fragmentPageGoods;  
    private MyprofileFragment fragmentPageMe;
    private SearchFragment fragmentPageSearch;
    private ConversationListFragment fragmentconversation;
    private PublishFragment  fragmentPagePublish;
    
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
        ll_search = (LinearLayout)findViewById(R.id.ll_search);  
        ll_message = (LinearLayout)findViewById(R.id.ll_message);  
        ll_me = (LinearLayout)findViewById(R.id.ll_me);
        ll_publish = (LinearLayout)findViewById(R.id.ll_publish);
          
       image_home = (ImageView)findViewById(R.id.image_home);  
        image_search = (ImageView)findViewById(R.id.image_search);  
        image_message = (ImageView)findViewById(R.id.image_message);  
        image_me = (ImageView)findViewById(R.id.image_me);
        image_publish = (ImageView)findViewById(R.id.image_publish);
          
        ll_home.setOnClickListener(this);  
        ll_search.setOnClickListener(this);  
        ll_message.setOnClickListener(this);  
        ll_me.setOnClickListener(this);
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
            ll_search.setSelected(true);  
            image_search.setSelected(true);  
            search();  
              
           break;
           
        case R.id.ll_publish:
//        	ll_publish.setSelected(true);
//        	image_publish.setSelected(true);
//        	publish();
        	Intent intent=new Intent(this,NewGoodsActivity.class);
        	startActivity(intent);
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
      
    private void setSelected(){  
        ll_home.setSelected(false);  
        ll_search.setSelected(false);  
        ll_message.setSelected(false);  
        ll_me.setSelected(false);  
        image_home.setSelected(false);  
        image_search.setSelected(false);  
        image_message.setSelected(false);  
        image_me.setSelected(false);  
        if(fragmentPageGoods != null){  
            //隐藏Fragment  
            ft.hide(fragmentPageGoods);  
        }  
        if(fragmentPageMe != null){  
            ft.hide(fragmentPageMe);  
        }  
        if(fragmentPageSearch != null){  
            ft.hide(fragmentPageSearch);  
        }  
        if(fragmentconversation != null){  
            ft.hide(fragmentconversation);  
        }  
    }  
  

    private void home(){  
        if(fragmentPageGoods == null){  
            fragmentPageGoods = new GoodsListFragment();            
            ft.add(R.id.fl_content, fragmentPageGoods);  
        }else{  
            //显示Fragment  
            ft.show(fragmentPageGoods);  
        }  
    }  
    private void me(){  
        if(fragmentPageMe == null){  
            fragmentPageMe = new MyprofileFragment();  
            ft.add(R.id.fl_content, fragmentPageMe);  
        }else{  
            ft.show(fragmentPageMe);  
        }  
          
    }  
    private void publish(){
    	if(fragmentPagePublish == null){
    		fragmentPagePublish = new PublishFragment();
    		ft.add(R.id.fl_content, fragmentPagePublish);
    	}else{
    		ft.show(fragmentPagePublish);
    	}
    }
    
    private void search(){  
        if(fragmentPageSearch == null){  
            fragmentPageSearch = new SearchFragment();  
            ft.add(R.id.fl_content, fragmentPageSearch);  
        }else{  
            ft.show(fragmentPageSearch);  
        }  
          
    }  
    private void message(){  
        if(fragmentconversation == null){  
        	fragmentconversation = new ConversationListFragment();  
            ft.add(R.id.fl_content, fragmentconversation);  
        }else{  
            ft.show(fragmentconversation);  
        }  
          
    }  /*添加到Fragment管理器中 
    这里如果用replace， 
    当每次调用时都会把前一个Fragment给干掉， 
    这样就导致了每一次都要创建、销毁， 
    数据就很难保存，用add就不存在这样的问题了， 
    当Fragment存在时候就让它显示，不存在时就创建， 
    这样的话数据就不需要自己保存了， 
    因为第一次创建的时候就已经保存了， 
    只要不销毁一直都将存在*/    
}

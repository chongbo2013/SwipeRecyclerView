package com.ferris.holeswipeview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.ferris.holeswipeview.CusomSwipeView.RemoveDirection;
import com.ferris.holeswipeview.CusomSwipeView.RemoveListener;

public class CustomScrollViewActivity extends Activity {
	private CusomSwipeView sl;
	private List<ApplicationBeam> applicationBeams=new ArrayList<ApplicationBeam>();
	
	private ApplicationAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customscrollview);
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		sl=(CusomSwipeView)findViewById(R.id.sl);
		
		for(int i=0;i<=10;i++){
			ApplicationBeam applicationBeam=new ApplicationBeam();
			applicationBeam.setName("标题"+i);
			applicationBeams.add(applicationBeam);
		}
		adapter=new ApplicationAdapter(getApplicationContext(), applicationBeams);
		//设置布局管理器  
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this); 
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);  
        sl.setLayoutManager(linearLayoutManager);  
		sl.setAdapter(adapter);
		sl.setRemoveListener(new RemoveListener() {
			
			@Override
			public void removeItem(RemoveDirection direction, int position) {
				// TODO Auto-generated method stub
				Toast.makeText(CustomScrollViewActivity.this, "移除"+position, Toast.LENGTH_SHORT).show();
			}
		});
	}
}

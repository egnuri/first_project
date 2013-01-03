package your.jong.namespace;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/***************
 * 
 * 테이블의 위치를 수정하는 액티비티
 * 서버로부터 테이블의 위치를 바꿀수있지만, 어플상에서도 가능
 *
 ***********/
public class EditTable extends Activity{
	static final int MAX_TABLE = 9;
	TextView[] tablePosition;
	AlertDialog myDialog;
	CanDB DB;
	
	TextView tempView;
	 @Override
	    public void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.edit_table);
	        // 해당 테이블 포지션 인플레이팅
	        tablePosition = new TextView[]{
	        		(TextView)findViewById(R.id.position0),
	        		(TextView)findViewById(R.id.position1),
	        		(TextView)findViewById(R.id.position2),
	        		(TextView)findViewById(R.id.position3),
	        		(TextView)findViewById(R.id.position4),
	        		(TextView)findViewById(R.id.position5),
	        		(TextView)findViewById(R.id.position6),
	        		(TextView)findViewById(R.id.position7),
	        		(TextView)findViewById(R.id.position8),
	        };
	        
	        DB = new CanDB(this);
	       
	       
	        OnClickListener onClick = new OnClickListener(){
				public void onClick(View v) {
					tempView= (TextView)v;
					myDialog = creatDialog();
	                myDialog.show();
				}
	        	
	        };
	        for(int i=0; i<MAX_TABLE; i++){
	        	tablePosition[i].setOnClickListener(onClick);
	        }
	    }
	// 첫번쨰 테이블 포지션을 기준으로 나머지 위치들을 계산함
	public int calculatePosition(TextView v){
		int pivot = tablePosition[0].getId();
		return v.getId()-pivot;
	}
	 @Override
	protected void onStart() {
		//액티비티 시작시,존재하는 테이블들을 다른 이미지로 표시 
		 for(int i=0; i<MAX_TABLE; i++){
			 try{
			 	if(DB.ifTableExist(i)){
			 		tablePosition[i].setBackgroundResource(R.drawable.table);
			 		
			 	}
			 }catch(Exception e){
				 Log.d("OnStart","ERROR");
			 }
		 }
		super.onStart();
	}
	 //테이블 생성, 삭제하는 다이얼로그
	public AlertDialog creatDialog(){
		 AlertDialog myDialog= new AlertDialog.Builder(EditTable.this)
		 
		 .setTitle("테이블") 
		 .setPositiveButton("테이블 생성", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				if(DB.ifTableExist(calculatePosition(tempView))){
					Toast.makeText(getApplicationContext(), "이미 존재하는 테이블 입니다.", 2000).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "태이블 생성", 2000).show();
					tempView.setBackgroundResource(0);
					tempView.setBackgroundResource(R.drawable.table);
					DB.insertTable(calculatePosition(tempView));
				}
				
			}
		})
		 .setNegativeButton("테이블 삭제", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(DB.ifTableExist(calculatePosition(tempView))){
					DB.deleteTable(calculatePosition(tempView));
					tempView.setBackgroundResource(0);
					tempView.setBackgroundResource(R.drawable.empty_table);
					Toast.makeText(getApplicationContext(), "테이블을 삭제하였습니다.", 2000).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "빈 공간입니다!", 2000).show();
					
				}
			}
		})
		 .setNeutralButton("취소", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.create();	
			
		 return myDialog;
	 }
	 
	 
	 
	 
}

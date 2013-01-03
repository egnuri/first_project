package your.jong.namespace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;



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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/****************************
 * 
 *  �ъ슜�먮뒗 二쇰Ц諛쏆쓣 �뚯씠釉붿쓣 �뚮윭 硫붾돱瑜��좏깮�⑥쑝濡쒖꽌, 二쇰Ц�꾨컺怨�洹몃궡�⑹쓣 �쒕쾭���꾩넚諛쏆쓣���덈떎.
 *  �ㅻⅨ �⑤쭚湲곗궗�⑹옄�ㅺ낵���숆린�붾� �꾪븯�� refresh 踰꾪듉�쇰줈 �ы깭源뚯� �낅젰���뺣낫媛믪쓣 諛쏆쓣 ���덈떎.
 *  硫붾돱 二쇰Ц�� �ㅼ씠�쇰줈洹몃� �앹꽦�섏뿬, 硫붾돱瑜�二쇰Ц諛쏅뒗��
 *
 ******************/
public class orderMenu extends Activity implements OnClickListener{
	static final int MAX_TABLE = 9;
	TextView[] tablePosition;
	AlertDialog myDialog;
	Button refreshBtn;
	CanDB DB;
	MenuDialog orderDialog;
	//�좏깮���꾩떆 view
	TextView tempView;
	//�ъ슜�먭� �곗튂濡��좏깮���뚯씠釉��몃뜳��
	int selectedTable;
	/// 踰꾪띁
	private BufferedReader buffReader;
    private BufferedWriter buffWriter;
    String SERVER_IP = "192.168.43.75";
	int SERVER_PORT=5423;
    Socket tSocket;
	 @Override
	    public void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.order_menu);
	        // �뚯씠釉��ъ����명뵆�덉씠��
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
	        refreshBtn = (Button)findViewById(R.id.refresh);
	        refreshBtn.setOnClickListener(this);
	        DB = new CanDB(this);
	       
	       
	       
	    }
	//泥ル쾲夷��뚯씠釉��ъ��섏쓣 湲곗��쇰줈 �섎㉧吏��꾩튂�ㅼ쓣 怨꾩궛��
	public int calculatePosition(TextView v){
		int pivot = tablePosition[0].getId();
		return v.getId()-pivot;
	}

	//�대┃�먮���濡쒖쭅 泥섎━
	public void onClick(View v) {
		// 媛깆떊踰꾪듉���뚯씠釉��ъ��섎뱾怨��덉쓽 硫붾돱�댁슜媛믩뱾��媛깆떊諛쏅뒗��
		if(v.getId()==R.id.refresh){
			String tempInfo=" ";
			NetworkClass nc = new NetworkClass();
			tempInfo=nc.refreshAll().toString();
			DB.updateFromServer(tempInfo);
			for(int i=0; i<MAX_TABLE; i++){
				 try{
				 	if(DB.ifTableExist(i)){
				 		tablePosition[i].setBackgroundResource(R.drawable.table);
				 		tablePosition[i].setVisibility(View.VISIBLE);
				 		tablePosition[i].setOnClickListener(this);
				 	}
				 	else{
				 		tablePosition[i].setVisibility(View.INVISIBLE);
				 	}
				 }catch(Exception e){
					 Log.d("OnStart","ERROR");
				 }
			 }
			Toast.makeText(getApplicationContext(), "�쒕쾭濡쒕����곗씠�곕� 媛깆떊諛쏆븯�듬땲��", 2000).show();
			
		}else{
		tempView= (TextView)v;
		myDialog = creatDialog();
        myDialog.show();
		}
		
	}
	//�쒖옉���뚯씠釉��꾩튂�ㅼ쓣 �쒖떆�쒕떎
	 @Override
	protected void onStart() {
		 for(int i=0; i<MAX_TABLE; i++){
			 try{
			 	if(DB.ifTableExist(i)){
			 		tablePosition[i].setBackgroundResource(R.drawable.table);
			 		tablePosition[i].setOnClickListener(this);
			 	}
			 	else{
			 		tablePosition[i].setVisibility(View.INVISIBLE);
			 	}
			 }catch(Exception e){
				 Log.d("OnStart","ERROR");
			 }
		 }
		super.onStart();
	}
	 // �ㅼ씠�쇰줈洹��몄텧
	public AlertDialog creatDialog(){
		selectedTable=calculatePosition(tempView);
		 AlertDialog myDialog= new AlertDialog.Builder(orderMenu.this)
		 
		 .setTitle("�뚯씠釉�"+(selectedTable+1))
		 .setPositiveButton("二쇰Ц/�섏젙", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				orderDialog =new MenuDialog(orderMenu.this); 
				orderDialog.show();
				 
				
			}
		})
		 .setNegativeButton("移댁슫���꾩넚", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				NetworkClass nc = new NetworkClass();
				nc.releaseOrder(DB.getOrderedInfo(selectedTable).toString());
				nc = new NetworkClass();
				nc.releasePrice(DB.calculateTotal(selectedTable).toString());
				
			}
		})
		 .setNeutralButton("痍⑥냼", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.create();	
			
		 return myDialog;
	 }
	 // �먮떂�쇰줈遺�꽣 二쇰Ц諛쏅뒗 �ㅼ씠�쇰줈洹명샇異�
	// Spinner瑜��쒖슜�섏뿬 硫붾돱瑜��좏깮, +, -濡�硫붾돱��媛�닔瑜�異붽�/��젣
	 public class MenuDialog extends Dialog implements OnItemSelectedListener,OnClickListener{
		 Spinner menuSpinner;
		 ArrayList<String> menuArray;
		 ArrayAdapter<String> menuAdapter;
		 String tempOrder;
		 TextView orderInfo,total;
		 Button addMenu, deleteMenu;
		public MenuDialog(Context context) {
			super(context);
			setContentView(R.layout.order_dialog);
			setTitle("二쇰Ц&�섏젙");
			
			//�ㅽ뵾���명똿, �붾퉬�먯꽌 �곗씠�곕�諛쏆븘 �ㅽ뵾�덉뿉 �ｋ뒗��
			menuArray=DB.getMenu();
			menuSpinner = (Spinner)findViewById(R.id.menu_spinner);
			menuSpinner.setOnItemSelectedListener(this);
			menuAdapter = new ArrayAdapter<String>(orderMenu.this,android.R.layout.simple_spinner_item,menuArray);
			menuAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
			menuSpinner.setAdapter(menuAdapter);
			//�명뵆�덉씠��
			total=(TextView)findViewById(R.id.total);
			orderInfo=(TextView)findViewById(R.id.order_info);
			addMenu  =(Button)findViewById(R.id.add_menu);
			deleteMenu  =(Button)findViewById(R.id.delete_menu);
			
			orderInfo.setText(DB.getOrderedInformation(selectedTable).toString()  +"\n珥앷퀎 : "+DB.calculateTotal(selectedTable)  );
			addMenu.setOnClickListener(this);
			deleteMenu.setOnClickListener(this);
		}
		/// �낅젰諛쏆� 留뚰겮 媛�怨꾩궛
		public void onClick(View arg0) {
			if(arg0.getId()==R.id.add_menu){
				DB.orderMenu(selectedTable,tempOrder);
				orderInfo.setText(DB.getOrderedInformation(selectedTable).toString());
				total.setText("\n珥앷퀎 : "+DB.calculateTotal(selectedTable));
			}
			if(arg0.getId()==R.id.delete_menu){
				DB.cancelMenu(selectedTable,tempOrder);
				orderInfo.setText(DB.getOrderedInformation(selectedTable).toString());
				total.setText("\n珥앷퀎 : "+DB.calculateTotal(selectedTable));
			}
		}
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			tempOrder=(String) parent.getItemAtPosition(position);
			
		}
		public void onNothingSelected(AdapterView<?> arg0) {
			tempOrder=null;
		}
		
		 
	 }

	 
	 
}
package your.jong.namespace;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
/***********
 * SQLITE를 활용한 클라이언트 데이터베이스, 
 * 및 정보를 꺼내고, 받고,parsing하는 메서드 들
 * 디비 테이블은 총 3개로 음식이름과 가격을 포함하고있는 MENU 테이블,
 * 현재 존재하는 테이블의 인덱스들을 갖고있는 INDEX_TABLE,
 * 테이블별로 입력받은 음식과 음식의 갯수를 갖고있는 DETAIL_TABLE, 3개로 이루어져있다.
 *******************/
public class CanDB extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="db3";
	   
	static final String MENU = "MENU";
	
	static final String CATE = "title";
	SQLiteDatabase db;
	int TOTAL_TABLE = 0;
	public CanDB(Context context) {
		super(context, DATABASE_NAME, null, 1);
		db = this.getWritableDatabase();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {//디비 생성
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE MENU (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ "food TEXT, " 
				+ "price INTEGER);" );
		db.execSQL("CREATE TABLE DETAIL_TABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ "table_index INTEGER, "
				+ "order_food TEXT, "
				+ "number_food INTEGER);");
		db.execSQL("CREATE TABLE INDEX_TABLE  (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ "table_index INTEGER, " 
				+ "total_price INTEGER);");
	}
	//디비를 모두 삭제하고 새로생성
	public void deleteAndMake(){
		db.execSQL("drop TABLE MENU");
		db.execSQL("drop TABLE DETAIL_TABLE");
		db.execSQL("drop TABLE INDEX_TABLE");
		db.execSQL("CREATE TABLE MENU (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ "food TEXT, " 
				+ "price INTEGER);" );
		db.execSQL("CREATE TABLE DETAIL_TABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ "table_index INTEGER, "
				+ "order_food TEXT, "
				+ "number_food INTEGER);");
		db.execSQL("CREATE TABLE INDEX_TABLE  (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ "table_index INTEGER, " 
				+ "total_price INTEGER);");
		 
	} 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	// 테이블(여기서 말하는 테이블은 식당의 테이블)의 인덱스를 생성
	public void insertTable(int x)
	{
		ContentValues cv = new ContentValues();
		cv.put("table_index", x);
		db.insert("INDEX_TABLE", null, cv);;
	}
	// 존재하는 테이블(여기서 말하는 테이블은 식당의 테이블) 삭제
	public void deleteTable(int x)
	{
		db.delete("INDEX_TABLE", "table_index ='"+x+ "'", null);
		db.delete("DETAIL_TABLE", "table_index ='"+x+ "'", null);
	}
	//테이블이 존재하면 true, 아니면 false리턴
	public boolean ifTableExist(int index){
		try{		
		Cursor c = db.rawQuery("SELECT table_index FROM INDEX_TABLE WHERE table_index = "+index, null);
		if(c.moveToFirst()==false)
			return false;
		}catch(Exception e){
			Log.d("TABLEEXIST","SDFASDF");
		}
		return true;
	}	
//MENU테이블에 입력받은 string과 일치하는 메뉴가 있는지 확인
	public boolean ifMenuExist(String m){
		
		
		try{
		Cursor c = db.rawQuery("SELECT food FROM MENU WHERE food = "+"'"+m+"'", null);
		
		if(c.moveToFirst()==false)
			return false;
		}catch(Exception e){
			Log.d("MENUEXIST",m);
		}
		return true;
	}
	//메뉴 insert
	public boolean insertMenu(String m, int p)
	{
		if(this.ifMenuExist(m)){
			return false;
		}
		else{
			ContentValues cv = new ContentValues();
			cv.put("food", m);
			cv.put("price", p);
			db.insert(MENU, null, cv);
			return true;
		}
	}
	//서버로부터 받은 모든 데이터들을 파싱
	public String updateFromServer(String data){
		db.execSQL("drop TABLE DETAIL_TABLE");
		db.execSQL("CREATE TABLE DETAIL_TABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ "table_index INTEGER, "
				+ "order_food TEXT, "
				+ "number_food INTEGER);");
		//토크나이저 사용
		StringTokenizer st=new StringTokenizer(data,"-");
		String token;
		while(st.hasMoreElements()){
			token = st.nextToken();
			int index= Integer.parseInt( new String().valueOf(token.charAt(0)));
			token = token.substring(1, token.length());
			StringTokenizer stt=new StringTokenizer(token,"/");
			
			while(stt.hasMoreTokens()){
				//파싱한 값들을 빈 디비에 넣는다
				String mToken = stt.nextToken();
				String pToken = stt.nextToken();
				String sql = "SELECT * FROM DETAIL_TABLE";
				Cursor c = db.rawQuery(sql, null);
				ContentValues cv = new ContentValues();
				cv.put("table_index",index);
				cv.put("order_food", mToken);
				cv.put("number_food", pToken);
				db.insert("DETAIL_TABLE", null, cv);
			}			
		}
		return "";
	}
	//서버로부터 받은 테이블과 메뉴값을 파싱
	public boolean parseMenuTable(String data){
		
		String tempTable, tempMenu;
		
		int numbOfTable = Integer.parseInt( new String().valueOf(data.charAt(0)));
		tempTable = data.substring(1, numbOfTable+1);
		tempMenu = data.substring(numbOfTable+1, data.length());
		
		char[] tableChar = tempTable.toCharArray();
		//테이블 인덱스들을 파싱하여 디비에 넣는다
		for(int i=0; i<tempTable.length();i++){
			int index= Integer.parseInt( new String().valueOf(tableChar[i]))-1;
			if(ifTableExist(index)){				
			}else{
				insertTable(index);
			}
			
		}
		//메뉴를 파싱할 차례
		StringTokenizer st=new StringTokenizer(tempMenu,"/");
		String tokenMenu;
		String tokenPrice;
		while(st.hasMoreElements()){
			tokenMenu = st.nextToken();
			tokenPrice = st.nextToken();
			insertMenu(tokenMenu, Integer.parseInt(tokenPrice));
		}
		
		return true;
	}
	//존재하는 메뉴들값을 디비로부터 요청해 ArrayList로 반환받는 메서드
	public ArrayList<String> getMenu()
	{
		String sql = "SELECT * FROM MENU";
		Cursor c = db.rawQuery(sql, null);
		ArrayList al = new ArrayList<String>();
		if(c.moveToFirst()){
			do{
				al.add(c.getString(1));
			}while(c.moveToNext());
		}
		return al;
	}
	//입력받은 메뉴의 갯수를 줄이는 메서드
	public boolean cancelMenu(int index, String menu){
		
		String sql = "SELECT * FROM DETAIL_TABLE WHERE table_index = "+"'"+index+"'";
		Cursor c = db.rawQuery(sql, null);
		
		if(c.moveToFirst()){
			do{// 메뉴가 일치하면
				if(c.getString(2).equals(menu)){
					if(Integer.parseInt((c.getString(3)))!=0){		 //0이아니면 하나 줄임
					db.execSQL("UPDATE DETAIL_TABLE SET number_food = "+"'"
				+(Integer.parseInt((c.getString(3)))-1)+"'"+" WHERE order_food = "+"'"+menu+"'"+" AND table_index = "+"'"+index+"'" );       
					return false;
					}
				}
			}while(c.moveToNext());
		}
		return false;
	}
	//위 메서드와는 반대로 입력받은 메뉴를 +1시킴
	public boolean orderMenu(int index, String menu){
		
		String sql = "SELECT * FROM DETAIL_TABLE WHERE table_index = "+"'"+index+"'";
		Cursor c = db.rawQuery(sql, null);
		
		if(c.moveToFirst()){
			do{
				if(c.getString(2).equals(menu)){
					db.execSQL("UPDATE DETAIL_TABLE SET number_food = "+"'"
				+(Integer.parseInt((c.getString(3)))+1)+"'"+" WHERE order_food = "+"'"+menu+"'"+" AND table_index = "+"'"+index+"'" );       
					return false;
				}
			}while(c.moveToNext());
		}
		ContentValues cv = new ContentValues();
		cv.put("table_index",index);
		cv.put("order_food", menu);
		cv.put("number_food", 1);
		
		db.insert("DETAIL_TABLE", null, cv);
		return false;
	}
	
	
	//해당 index테이블이 먹은 메뉴들의 총 합계값을 구함
	public String calculateTotal(int index){
		int TOTAL=0;
		String tableSQL = "SELECT * FROM DETAIL_TABLE WHERE table_index = "+"'"+index+"'";
		Cursor tableC = db.rawQuery(tableSQL, null);
		String menuSQL;
		Cursor menuC;// = db.rawQuery(menuSQL, null);
		
		if(tableC.moveToFirst()){
			do{ 
				String selectedMenu = tableC.getString(2);
				menuSQL = "SELECT * FROM MENU WHERE food = " +"'"+ selectedMenu+"'";
				menuC = db.rawQuery(menuSQL, null);
				menuC.moveToFirst();
				TOTAL=TOTAL+(  Integer.parseInt(tableC.getString(3))*Integer.parseInt(menuC.getString(2))  );
				
			}while(tableC.moveToNext());
		}
		return ""+TOTAL+"원";
	}
	//테이블의 주문한 정보들을 스트링 값으로 받음(서버에 전송용)
	public String getOrderedInfo(int index){
		String sql = "SELECT * FROM DETAIL_TABLE WHERE table_index = "+"'"+index+"'";
		Cursor c = db.rawQuery(sql, null);
		String result=index+"/";
		try{
			if(c.moveToFirst()){
				do{
					result=result+ c.getString(2) +"/"+c.getString(3)+"/";
				}while(c.moveToNext());
			} 
		}catch(Exception e){
			Log.d("D", "DDD");
		}
		return result;
	}
	//주문받은 메뉴를 ArrayList로 리턴받음
	public ArrayList<String> getOrderedInformation(int index)
	{
		String sql = "SELECT * FROM DETAIL_TABLE WHERE table_index = "+"'"+index+"'";
		
		Cursor c = db.rawQuery(sql, null);
		ArrayList al = new ArrayList<String>();
		try{
			if(c.moveToFirst()){
				do{
					al.add( " "+c.getString(2) +" "+c.getString(3)+"개 " +"\n" );
				}while(c.moveToNext());
			}
		}catch(Exception e){
			Log.d("D", "DDD");
		}
		return al;
	}
}

package your.jong.namespace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;
/******************************
 *  서버 <-----> 클라이언트 간 프로토콜을 담당하는 클래스
 *  서버에 데이터를 요청할때, 모두 String or Char[]값을 이용,
 *  첫번째 Char[0]에는 요청하는 인덱스 0/1/2/를 붙여보낸다(서버쪽에 설명되어있는) *
 *******************************/
public class NetworkClass {
	///변수선언
		private BufferedReader buffReader;
	    private BufferedWriter buffWriter;
	    //서버의 아이피와 포트
	    String SERVER_IP = "192.168.43.75";
		int SERVER_PORT=5423;
	    Socket tSocket;
	    NetworkClass(){
	    	try {
				tSocket=new Socket(SERVER_IP,SERVER_PORT);
			} catch (Exception e) {
				Log.d("ERROR", "WHILE CONNECTING");			
			} 
			try {
				//버퍼쓰는/읽는 변수 미리 정의
				buffReader =
						new BufferedReader(new InputStreamReader(tSocket.getInputStream()));

				buffWriter = 
						new BufferedWriter(new OutputStreamWriter(tSocket.getOutputStream()));
				
			} catch (IOException e1) {
				Log.d("ER", "ERROR WHILE BUFF");
			}

			
	    }
	    /**************************************************
	     * 서버와 클라이언트간의 프로토콜( char[0]값에 붙이는것들)
	     *  0 - 클라이언트가 메인메뉴에서 서버에게 테이블 인덱스값과 메뉴정보 요청
	     *  1 - 클라이언트가 입력받은 메뉴의 정보를 서버에 전송
	     *  2 - 서버가 클라이언트에게 갖고있는 모든정보를 전송
	     ***************************************************/	
	    public String getMenuTable(){
	    	PrintWriter out = new PrintWriter(buffWriter,true);
	        String WWW= "0";// 0�Ͻ�
	        out.println(WWW);
			String temp = "";
			
			
			try {
				temp = buffReader.readLine();
			} catch (IOException e) {
				Log.d("ER", "While reading");
			}finally{
				try {
						tSocket.close();
					}catch (IOException e) {
				}
			}
			return temp;
	    }
	    // 받은 메뉴값을 서버에 전송
	    public void releaseOrder(String order){
	    	PrintWriter out = new PrintWriter(buffWriter,true);
	        String WWW= "1"+order;// 0�Ͻ�
	        out.println(WWW);
	    }
	    // 총 가격값을 서버에 전송
	    public void releasePrice(String order){
	    	PrintWriter out = new PrintWriter(buffWriter,true);
	        String WWW= "3"+order;// 0�Ͻ�
	        out.println(WWW);
	    }
	    // 모든 테이블 및 메뉴전송정보 refresh
	    public String refreshAll(){
	    	PrintWriter out = new PrintWriter(buffWriter,true);
	        String WWW= "2";// we want whole data
	        out.println(WWW);//what we want
	        
			String temp = "";
			try {
				temp = buffReader.readLine();
			} catch (IOException e) {
				Log.d("ER", "while reading");
			}finally{
				try {
						tSocket.close();
					}catch (IOException e) {
				}
			}
			return temp;
	    }
}

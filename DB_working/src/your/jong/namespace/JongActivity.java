package your.jong.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
/***********************
 *  어플시작시 나오는 액티비티
 *  각각의 메뉴선택값에 맞는 액티비티/요청을 호출
 ***********************/

public class JongActivity extends Activity {
    /** Called when the activity is first created. */
	Button editTable, orderMenu , addMenu;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);    
        orderMenu=(Button)findViewById(R.id.order_menu);
        editTable=(Button)findViewById(R.id.edit_table);
        addMenu=(Button)findViewById(R.id.add_menu);
       
        final CanDB DB=new CanDB(this);
        
        editTable.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent a = new Intent(getBaseContext(),EditTable.class);
				startActivity(a);
			}	    
        });
        orderMenu.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent a = new Intent(getBaseContext(),orderMenu.class);
				startActivity(a);
			}	
        });
        // add메뉴의 경우 서버로부터 값을 요청받아 업데이트한다
        // NetworkClass를 이용, 메뉴/테이블 인덱스들을 전송받는다
        addMenu.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DB.deleteAndMake();
				NetworkClass nc = new NetworkClass();
				String tempParse=nc.getMenuTable();
				
				DB.parseMenuTable(tempParse);
				Toast.makeText(getApplicationContext(),"서버로부터 메뉴와 테이블 정보를 받았습니다.", 3000).show();
				
			}	
        });
       
        
    }
}
/****
 * ������ Ŭ���̾�Ʈ�� ��������
 *  0 - ���θ޴����� ���̺� �ε������ �޴����� �޾ƿ�
 *  1 - Ŭ�󿡼� ������ ��� ���̺��� ������ ���ſ�û
 *  2 - Ŭ���̾�Ʈ�� ������� �ش� ���̺� ��ġ�� �ֹ��������� ����
 ****/
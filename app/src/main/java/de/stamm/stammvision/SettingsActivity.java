package de.stamm.stammvision;

import de.stamm.clouddisplay.R;
import de.stamm.stammvision.data.MainModel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	
	MainModel mainModel;
	Thread thread;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mainModel = ((MainModel) getApplicationContext());        
        
        TextView contentTextView = (TextView) findViewById(R.id.settings_textview);
        contentTextView.setText("Ihre Device ID:\r\n " + mainModel.getSettings().getDevice_id()+"\r\n"+mainModel.getVersion());
		
		findViewById(R.id.settings_close_button).setOnTouchListener(onCloseButtonListener);
		
		thread = new Thread( new Runnable() {
	        @Override
	        public void run() {
	            try {
	            	Thread.sleep(30000);
	            }
	            catch (Exception e) {
	                e.printStackTrace();
	            }
	            finally {
		            //Intent intent = new Intent(SettingsActivity.this, FullscreenActivity.class);
	            	//intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
	            	//startActivity(intent);
	            	finish();
	            }
	        }
	    });

	    thread.start();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	View.OnTouchListener onCloseButtonListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

            //Intent intent = new Intent(SettingsActivity.this, FullscreenActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			//startActivity(intent);
			finish();
			thread.interrupt();
			return false;
		}
	};
	
}

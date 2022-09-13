package de.stamm.stammvision;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.stamm.core.DownloadTask;
import de.stamm.core.FileDownloader;
import de.stamm.stammvision.data.MainModel;
import de.stamm.stammvision.rootaccess.RootAccess;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundTask extends Service {

	protected MainModel mainModel;
			    
    public void runServerTask() {
		if (!MainModel.START_BACKGROUND) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			java.util.Date currentDate = new Date(calendar.getTimeInMillis());
			
			MainModel.START_BACKGROUND = true;
			if (MainModel.PAUSE_COUNT > 2 && MainModel.ROOTED) {
				mainModel.log(MainModel.LOGLEVEL_WARN, "REBOOT from Pause");
				if (mainModel.isKeepAlive && mainModel.isOnline()) RootAccess.rebootSU();
			}
			
			if (MainModel.PAUSE) {
				mainModel.log(MainModel.LOGLEVEL_WARN, "DEVICE ON PAUSE "+MainModel.PAUSE_COUNT);
				MainModel.PAUSE_COUNT++;
			}
			

			if (mainModel.isOnline()) {	
				mainModel.serverCommuniction();
							
				mainModel.log(MainModel.LOGLEVEL_INFO,"Loading Playlists - " + format.format(currentDate));
				mainModel.getPlaylists();
				
				mainModel.setStartDone(true);
			}
			
			if (isNewVersionAvailable()) {
				mainModel.log(MainModel.LOGLEVEL_INFO,"new Version available");
				String filename = getUpdateFile();
				String loadFilename = MainModel.Server_UPDATE+filename;
				if (!loadFilename.equals("")) {
					mainModel.log(MainModel.LOGLEVEL_INFO,"new filename: "+loadFilename);
					mainModel.installInfoNewVersion();
		        	String targetFile = MainModel.PATH_APP+filename;
		        	RootAccess root = new RootAccess();
		        	
		        	final DownloadTask downloadTask = new DownloadTask(BackgroundTask.this);
		        	String[] param = {loadFilename, targetFile};
		        	downloadTask.execute(param);
		        	
					//FileDownloader.downloadFile(loadFilename, targetFile);
		        	
					mainModel.log(MainModel.LOGLEVEL_INFO,"start install filename: "+targetFile);
		        	root.installNewApk(targetFile);
				}
			}
			

			if (mainModel.isReboot()) {
				mainModel.log(MainModel.LOGLEVEL_INFO, "reboot from Web");
				RootAccess.rebootSU();
			}
			
			MainModel.START_BACKGROUND = false;
		}
    	
    }
    
	private String getUpdateFile() {
		if (mainModel.isOnline()) {
			try {
				return mainModel.getApp_file();
			} catch (Exception e) {}
		}
		return "";
	}

	
	public boolean isNewVersionAvailable() {
		if (mainModel.isOnline() && MainModel.ROOTED) {
			try {
				if (Double.valueOf(mainModel.getApp_version()) > 
					Double.valueOf(mainModel.getVersion())) return true;
			} catch (Exception e) {}
		}
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {    
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
        mainModel = ((MainModel) getApplicationContext()); 
        mainModel.loadPlaylists();    
                
        new java.util.Timer().schedule( 
    	    new java.util.TimerTask() {
    	        @Override
    	        public void run() {
					runServerTask();
    	        }
    	    }, 
    	    5*1000, 1*60*1000
    	);
	}

 
	@Override
	public void onDestroy() {
		if (mainModel.isDebug || mainModel.isKeepAlive) RootAccess.rebootSU();
	}
}

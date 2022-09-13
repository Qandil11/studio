package de.stamm.stammvision.update;

import de.stamm.stammvision.rootaccess.RootAccess;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnUpgradeReceiver extends BroadcastReceiver {
	
	public void onReceive(Context context, Intent intent) {
	  if(intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)){
		  RootAccess.rebootSU();
	  }
	}
}
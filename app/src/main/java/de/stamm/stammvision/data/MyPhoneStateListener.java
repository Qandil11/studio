package de.stamm.stammvision.data;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

public class MyPhoneStateListener extends PhoneStateListener {
	
	MainModel mainModel;
	
	public MyPhoneStateListener(MainModel mainModel) {
		this.mainModel = mainModel;
	}
	
    /* Get the Signal strength from the provider, each tiome there is an update */
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
       super.onSignalStrengthsChanged(signalStrength);
       mainModel.setGsmSignalStrength(signalStrength.getGsmSignalStrength());
    }
}

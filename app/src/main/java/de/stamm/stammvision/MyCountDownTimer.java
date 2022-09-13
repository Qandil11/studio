package de.stamm.stammvision;

import android.os.CountDownTimer;

public abstract class MyCountDownTimer {

    private CountDownTimer cdt;
    private long millisInFuture;
    private long countDownInterval;

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;

        recreateCounter(millisInFuture, countDownInterval);
    }

    public abstract void onFinish();

    public abstract void onTick(long millisUntilFinished);

    public void start(){
        cdt.start();
    }

    protected void setMillisInFuture(long millisInFuture){
        this.millisInFuture = millisInFuture;
    }

    public void onIncrement(long millis){
        millisInFuture += millis;
        recreateCounter(millisInFuture, countDownInterval);
    }

    protected void recreateCounter(long millisInFuture, long countDownInterval){
    	if (millisInFuture < 0) millisInFuture = 0;
        if(cdt != null){
            try {
                cdt.cancel();
            } catch (Exception e) {
            }
        }

        cdt = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                setMillisInFuture(millisUntilFinished);
                MyCountDownTimer.this.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                MyCountDownTimer.this.onFinish();
            }
        };
    }
}

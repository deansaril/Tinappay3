package com.mobdeve.s13.group12.tinappay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mobdeve.s13.group12.tinappay.objects.Keys;

/**
 * This runnable handles the thread for the progress bars
 */
public class ProgressBarRunnable implements Runnable {

    /* Class variables */
    private Handler handler;
    private int progress;
    private int delay;

    /**
     * Instantiates a ProgressBarRunnable
     * @param handler Handler - handler which sends the message
     * @param progress int - "filled" value of progressbar
     */
    public ProgressBarRunnable(Handler handler, int progress) {
        this.handler = handler;
        this.progress = progress;
        this.delay = 100;
    }

    /**
     * Instantiates a ProgressBarRunnable
     * @param handler Handler - handler which sends the message
     * @param progress int - "filled" value of progressbar
     * @param delay int - time (in ms) to delay increments of progressbar movement
     */
    public ProgressBarRunnable(Handler handler, int progress, int delay) {
        this.handler = handler;
        this.progress = progress;
        this.delay = delay;
    }



    /* Function overrides */
    @Override
    public void run() {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();

        bundle.putInt(Keys.KEY_LOAD.name(), this.progress);
        message.setData(bundle);

        this.handler.sendMessage(message);

        try {
            Thread.sleep(this.delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.mobdeve.s13.group12.tinappay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mobdeve.s13.group12.tinappay.objects.Keys;

public class ProgressBarRunnable implements Runnable {

    private Handler handler;
    private int progress;
    private int delay;

    public ProgressBarRunnable(Handler handler, int progress) {
        this.handler = handler;
        this.progress = progress;
        this.delay = 500;
    }

    public ProgressBarRunnable(Handler handler, int progress, int delay) {
        this.handler = handler;
        this.progress = progress;
        this.delay = delay;
    }

    @Override
    public void run() {
        Message message = Message.obtain();

        Bundle bundle = new Bundle();

        bundle.putInt(Keys.KEY_PROGRESS, this.progress);

        message.setData(bundle);

        this.handler.sendMessage(message);

        try {
            Thread.sleep(this.delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

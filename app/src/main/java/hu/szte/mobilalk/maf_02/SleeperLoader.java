package hu.szte.mobilalk.maf_02;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.Random;

public class SleeperLoader extends AsyncTaskLoader<String> {

    SleeperLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        Random r = new Random();
        int n = r.nextInt(10) + 1;
        int msec = n * 1000;

        try {
            Thread.sleep(msec);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("SLEEPER", String.valueOf(n));
        return "I have slept for " + n + " seconds";
    }
}

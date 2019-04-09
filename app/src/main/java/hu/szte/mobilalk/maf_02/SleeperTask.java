package hu.szte.mobilalk.maf_02;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class SleeperTask extends AsyncTask<Void, Void, String> {

    private WeakReference<TextView> textViewWeakReference;


    SleeperTask(TextView tv) {
        textViewWeakReference = new WeakReference<TextView>(tv);
    }

    @Override
    protected String doInBackground(Void... voids) {
        Random r = new Random();
        int n = r.nextInt(10) + 1;
        int msec = n * 1000;

        try {
            Thread.sleep(msec);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        return "I have slept for " + n + " seconds";
    }

    @Override
    protected void onPostExecute(String result) {
        this.textViewWeakReference.get().setText(result);
    }
}

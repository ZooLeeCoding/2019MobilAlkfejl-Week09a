package hu.szte.mobilalk.maf_02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, intent, context);
        asyncTask.execute();
    }

    private static class Task extends AsyncTask {
        private final PendingResult pendingResult;
        private final Intent intent;

        private Task(PendingResult pResult, Intent myInt, Context ctx) {
            this.pendingResult = pResult;
            this.intent = myInt;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            StringBuilder stb = new StringBuilder();
            stb.append("Action: " + intent.getAction());
            String log = stb.toString();
            Log.i("BRECEIVER", log);
            return log;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pendingResult.finish();
        }
    }
}

package hu.szte.mobilalk.maf_02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MySyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder stb = new StringBuilder();
        stb.append("Action: " + intent.getAction());
        String log = stb.toString();
        Toast.makeText(context, log, Toast.LENGTH_SHORT).show();
        Log.i("BRECEIVER", log);
    }
}

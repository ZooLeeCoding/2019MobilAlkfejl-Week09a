package hu.szte.mobilalk.maf_02;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<String> {

    private int counter;
    private TextView counterView;
    private TextView helloView;

    private BroadcastReceiver br;
    private AlarmManager mAlarmManager;
    private PendingIntent alarmPendingIntent;

    public static final String EXTRA_MESSAGE = "hu.szte.mobilalk.maf_02.MESSAGE";
    public static final int TEXT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.counterView = findViewById(R.id.countView);
        this.helloView = findViewById(R.id.helloView);


        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
            this.counter = savedInstanceState.getInt("counter");
            this.helloView.setText(savedInstanceState.getCharSequence("helloView"));
            this.counterView.setText(String.valueOf(this.counter));
        } else {
            this.counter = 0;
        }

        if(getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null,
                    this);
        }

        this.br = new MySyncReceiver();
        //IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        IntentFilter filter = new IntentFilter("hu.szte.mobilalkfejl.CUSTOM_BROADCAST");
        this.registerReceiver(this.br, filter);

        this.mAlarmManager =
                (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(this.br);
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.item_async:
                getSupportLoaderManager().restartLoader(0, null,
                        this);
                break;
            case R.id.item_book:
                launchBookSearch();
                break;
            case R.id.item_broadcast:
                startBroadcasting();
                break;
            case R.id.item_notification:
                notifyMe();
                break;
            case R.id.item_alarm:
                setAlarm();
                break;
            case R.id.item_cancel_alarm:
                cancelAlarms();
                break;
            default:
                Toast.makeText(this, "invalid selection", Toast.LENGTH_SHORT)
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAlarm() {
        Intent intent = new Intent("hu.szte.mobilalkfejl.CUSTOM_BROADCAST");
        this.alarmPendingIntent =
                PendingIntent.getBroadcast(this, 0,intent, 0);

        // 10 masodperccel inditas utan fusson le
        /*this.mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 10 * 1000, pendingIntent);*/

        // 30 masodperccel inditas utan es aztan 30 masodpercenkent ismetles
        /*this.mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 30 * 1000,
                30 * 1000, alarmPendingIntent);*/

        // 13:09 perckor hivodjon meg, aztan pedig 10 masodpercenkent ismetlodjon
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 9);
        this.mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), 10 * 1000,
                this.alarmPendingIntent);

    }

    public void cancelAlarms() {
        if(this.mAlarmManager != null && this.alarmPendingIntent != null) {
            this.mAlarmManager.cancel(this.alarmPendingIntent);
        }
    }

    public void notifyMe() {
        CharSequence name = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            name = "custom channel";
            String description = "ez egy pelda broadcast csatorna";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(name.toString(), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, (name != null ? name.toString(): null))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Értesítés a Mobil kurzusról")
                        .setContentText(this.helloView.getText())
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, builder.build());
    }

    public void startBroadcasting() {
        Intent intent = new Intent();
        intent.setAction("hu.szte.mobilalkfejl.CUSTOM_BROADCAST");
        sendBroadcast(intent);
        //sendOrderedBroadcast(intent);
        //LocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", this.counter);
        outState.putCharSequence("helloView", this.helloView.getText());
    }

    public void toastMe(View view) {
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(R.string.toast_message) +
                this.counter;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void countMe(View view) {
        this.counter++;
        counterView.setText(String.valueOf(this.counter));
    }

    public void launchOther(View view) {
        /*Intent intent = new Intent(this, MessageActivity.class);
        String message = "Counter was: " + this.counter;
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivityForResult(intent, TEXT_REQUEST);*/

        String textMessage = "The counter is " + this.counter;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        sendIntent.setType("text/plain");

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    public void launchBookSearch() {
        Intent intent = new Intent(this, BookActivity.class);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(MessageActivity.EXTRA_REPLY);
                helloView.setText(reply);
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return  new SleeperLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        helloView.setText(s);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}

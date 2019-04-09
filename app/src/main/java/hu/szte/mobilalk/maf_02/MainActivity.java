package hu.szte.mobilalk.maf_02;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Message;
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

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<String> {

    private int counter;
    private TextView counterView;
    private TextView helloView;

    private BroadcastReceiver br;

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
        /*Toast toast = Toast.makeText(getApplicationContext(), item.getTitle(),
                Toast.LENGTH_SHORT);
        toast.show();*/

        if(id == R.id.item_async) {
            //new SleeperTask(this.helloView).execute();
            getSupportLoaderManager().restartLoader(0, null,
                    this);
        } else if(id == R.id.item_book) {
            launchBookSearch();
        } else if(id == R.id.item_broadcast) {
            startBroadcasting();
        } else if(id == R.id.item_notification) {
            notifyMe();
        }
        return super.onOptionsItemSelected(item);
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


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, (name != null ? name.toString(): null))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Értesítés a Mobil kurzusról")
                        .setContentText(this.helloView.getText())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

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

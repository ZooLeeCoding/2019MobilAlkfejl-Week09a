package hu.szte.mobilalk.maf_02;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_REPLY = "hu.szte.mobilalk.maf_02.REPLY";

    private TextView displayView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent starterIntent = getIntent();
        String message = starterIntent.getStringExtra(Intent.EXTRA_TEXT);
        this.displayView = findViewById(R.id.displayView);
        this.displayView.setText(message);
        this.editText = findViewById(R.id.editText);

        Spinner spinner = findViewById(R.id.spinner);
        if(spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.spinner_values,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        if(spinner != null) {
            spinner.setAdapter(adapter);
        }
    }

    public void back(View view) {
        String reply = "I'm back!";
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, reply);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void imageClick(View view) {
        createToast(this.editText.getText());
    }

    public void onRadioPress(View view) {
        String chosen = "";
        switch(view.getId()) {
            case R.id.radioButton:
                chosen = "elso";
                break;
            case R.id.radioButton2:
                chosen = "masodik";
                break;
            case R.id.radioButton3:
                chosen = "harmadik";
                break;
            default:
                break;
        }
        createToast(chosen + " volt kiválasztva");
    }

    public void createToast(CharSequence msg) {
        Toast toast = Toast.makeText(getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String spinnerLabel = parent.getItemAtPosition(position).toString();
        createToast(spinnerLabel);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void alertClick(View view) {
        AlertDialog.Builder myAlertBuilder =
                new AlertDialog.Builder(MessageActivity.this);
        myAlertBuilder.setTitle(R.string.alert_button);
        myAlertBuilder.setMessage(R.string.alert_text);

        myAlertBuilder.setPositiveButton(R.string.ok_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createToast("OK button");
                    }
                });

        myAlertBuilder.setNegativeButton(R.string.cancel_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createToast("Cancel button");
                    }
                });

        myAlertBuilder.show();
    }

    public void fragmentClick(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(),
                getString(R.string.fragment_button));
    }

    public void processTimeResult(int hour, int min) {
        createToast(hour + ":" + min);
    }
}

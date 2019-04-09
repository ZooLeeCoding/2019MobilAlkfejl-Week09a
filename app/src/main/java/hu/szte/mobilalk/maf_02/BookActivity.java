package hu.szte.mobilalk.maf_02;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>{

    private EditText mBookInput;
    private TextView mAuthorText;
    private TextView mTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        this.mBookInput = findViewById(R.id.bookInput);
        this.mAuthorText = findViewById(R.id.authorText);
        this.mTitleText = findViewById(R.id.titleText);
    }

    public void startSearch(View view) {
        String queryString = this.mBookInput.getText().toString();

        InputMethodManager inputManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if(networkInfo != null && networkInfo.isConnected()) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            mAuthorText.setText("");
            mTitleText.setText("");
        } else {
            Toast.makeText(this, "no connection", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendBack(View view) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(MessageActivity.EXTRA_REPLY, mTitleText.getText());
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        String queryString = "";
        if(bundle != null) {
            queryString = bundle.getString("queryString");
        }
        return new BookAsyncLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null;
            String authors = null;

            while(i < itemArray.length() && (authors == null && title ==null)) {
                JSONObject book = itemArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch(Exception e) {
                    e.printStackTrace();
                }
                i++;
            }

            if(title != null && authors != null) {
                mTitleText.setText(title);
                mAuthorText.setText(authors);
            } else {
                mTitleText.setText("No result");
                mAuthorText.setText("No result");
            }

        } catch(JSONException e) {
            e.printStackTrace();
            mTitleText.setText("No result");
            mAuthorText.setText("No result");
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}

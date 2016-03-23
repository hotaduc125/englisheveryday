package com.me.englisheveryday.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.me.englisheveryday.R;
import com.me.englisheveryday.utils.SessionManager;
import com.me.englisheveryday.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DictionaryActivity extends AppCompatActivity {

    @Bind(R.id.tvMeaning)
    TextView tvMeaning;

    @Bind(R.id.tvDicWord)
    TextView tvDicWord;

    @Bind(R.id.tvDicSpelling)
    TextView tvDicSpelling;

    @Bind(R.id.tvDicWordType)
    TextView tvDicWordType;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        displayWordDefinition();
    }

    private void displayWordDefinition(){
        ArrayList<String> content = null;
        try {
            content = getNewWord(this, sessionManager.getIndex());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvMeaning.setText(Html.fromHtml(content.get(2)));
        tvDicWord.setText(content.get(0));
        tvDicSpelling.setText(content.get(1));
        tvDicWordType.setText(content.get(3));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayWordDefinition();
    }

    /**
     * get new word from json file by index
     * @param context
     * @param index
     * @return
     * @throws JSONException
     */
    private ArrayList<String> getNewWord(Context context, int index) throws JSONException {
        ArrayList<String> newWord = new ArrayList<>();
        String content = Utilities.getInstance().readJSONFromAsset(context);
        JSONArray jsonArray = new JSONArray(content);
        newWord.add(0, jsonArray.getJSONObject(index).getJSONObject("newword").getString("word"));
        newWord.add(1, jsonArray.getJSONObject(index).getJSONObject("newword").getString("pronounce"));
        newWord.add(2, jsonArray.getJSONObject(index).getJSONObject("newword").getString("meaning"));
        newWord.add(3, jsonArray.getJSONObject(index).getJSONObject("newword").getString("type"));
        return newWord;
    }
}

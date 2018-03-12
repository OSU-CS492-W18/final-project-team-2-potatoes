package com.cogwerks.potato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText mPotatoSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPotatoSearchText = (EditText) findViewById(R.id.et_search_box);

        Button tellmeButton = (Button)findViewById(R.id.btn_tell_me);
        //Called when user hits tell me button
        tellmeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = mPotatoSearchText.getText().toString();
                if(!TextUtils.isEmpty(searchText)){
                    Intent intent = new Intent(getApplicationContext(), ResultDetailActivity.class);
                    intent.putExtra("searchString", searchText);
                    startActivity(intent);
                }
            }
        });
    }

    //Called when user hits tell me button
   /* public void sendMessage(View view) {
        Intent intent = new Intent(this, ResultDetailActivity.class);
        startActivity(intent);
    }*/

}

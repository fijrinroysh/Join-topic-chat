package com.example.app.ourapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

/**
 * Created by sarumugam on 10/12/16.
 */
public class LocationActivity extends AppCompatActivity {

    private TextInputLayout mCityHolder,mLocationHolder;
    private MultiAutoCompleteTextView mCityBox,mLocationBox;
    private Button mLocateButton;
    private ImageView mNavigateBackButton;

    private ArrayAdapter<String> mLocationAdapter;
    private ArrayAdapter<String> mCitiesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initializeViews();
        initializeListeners();
        setUpSuggestion();
    }

    private void initializeViews() {
        mCityHolder = (TextInputLayout) findViewById(R.id.city_box_holder);
        mLocationHolder = (TextInputLayout) findViewById(R.id.location_box_holder);
        mCityBox = (MultiAutoCompleteTextView) findViewById(R.id.city_box);
        mLocationBox = (MultiAutoCompleteTextView) findViewById(R.id.location_box);
        mLocateButton = (Button) findViewById(R.id.locate_button);
        mNavigateBackButton = (ImageView) findViewById(R.id.navigate_back);
    }

    private void initializeListeners(){
        mLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mCityHolder.setError("Whats up?");
//                Intent intent = new Intent(LocationActivity.this,RestaurantsActivity.class);
//                startActivity(intent);
            }
        });

        mNavigateBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUpSuggestion(){
        mCitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.cities));
        mCityBox.setAdapter(mCitiesAdapter);
        mCityBox.setThreshold(1);
//        mCityBox.setFilters(new InputFilter[]);
        mCityBox.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mCityBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(LocationActivity.this,"selected is : "+adapterView.getItemAtPosition(i),Toast.LENGTH_LONG).show();
//                displayLocation((String) adapterView.getItemAtPosition(i));
            }
        });

        mLocationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mLocationBox.setAdapter(mLocationAdapter);
        mLocationBox.setThreshold(1);
        mLocationBox.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mLocationBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(LocationActivity.this,"selected is : "+i+1,Toast.LENGTH_LONG).show();
            }
        });
    }
}
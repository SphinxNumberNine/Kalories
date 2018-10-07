package com.example.krishna.kalories;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {

    // Yhurd

    private static final int CAM_REQUEST = 1313;
    Bitmap bitmap;
    ArrayList<String> imagefoodSearch;
    ArrayList<Food> finalFood, wordSearchList;
    Food baseMacros;
    ListView foodList;
    RelativeLayout relativeLayout;
    TextView baseCalories, baseProtein, baseCarbs, baseFat,nutrition;
    int cal, pro, carb, fat;
    Typeface font;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.foodList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.add:
                Log.d("XD","ADD");
                finalFood.get(info.position).setCalories(finalFood.get(info.position).getCalories()+(finalFood.get(info.position).getCalories()/finalFood.get(info.position).getQ()));
                finalFood.get(info.position).setProtein(finalFood.get(info.position).getProtein()+(finalFood.get(info.position).getProtein()/finalFood.get(info.position).getQ()));
                finalFood.get(info.position).setCarbs(finalFood.get(info.position).getCarbs()+(finalFood.get(info.position).getCarbs()/finalFood.get(info.position).getQ()));
                finalFood.get(info.position).setFat(finalFood.get(info.position).getFat()+(finalFood.get(info.position).getFat()/finalFood.get(info.position).getQ()));
                finalFood.get(info.position).increaseQuantity();
                updateList();
                CustomAdapter myRealAdapter = new CustomAdapter(this, R.layout.food_layout, finalFood);
                foodList.setAdapter(myRealAdapter);
                return true;
            case R.id.delete:
                finalFood.remove(info.position);
                CustomAdapter adapter = new CustomAdapter(this,R.layout.food_layout, finalFood);
                foodList.setAdapter(adapter);
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        font = Typeface.createFromAsset(getAssets(), "fonts/default.ttf");

        cal = 0;
        pro = 0;
        carb = 0;
        fat = 0;

        finalFood = new ArrayList<>();
        foodList = (ListView) findViewById(R.id.foodList);
        registerForContextMenu(foodList);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        try {
            cal = sharedPreferences.getInt("Calories", 0);
            pro = sharedPreferences.getInt("Protein", 0);
            carb = sharedPreferences.getInt("Carbs", 0);
            fat = sharedPreferences.getInt("Fat", 0);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("Foods", "");
            ArrayList<Food> lstArrayList = gson.fromJson(json, new TypeToken<List<Food>>() {
            }.getType());
            if (lstArrayList != null) {
                finalFood = lstArrayList;
                CustomAdapter myRealAdapter = new CustomAdapter(this, R.layout.food_layout, finalFood);
                foodList.setAdapter(myRealAdapter);
            }

            Log.d("FinalFood", "" + finalFood);
        } catch (Exception e) {
            e.printStackTrace();
        }

        baseCalories = (TextView) findViewById(R.id.caloriesTrack);
        baseProtein = (TextView) findViewById(R.id.protienTrack);
        baseCarbs = (TextView) findViewById(R.id.carbsTrack);
        baseFat = (TextView) findViewById(R.id.fatsTrack);
        nutrition = (TextView) findViewById(R.id.nutrition);

        baseCalories.setText(cal + " Calories");
        baseCalories.setTypeface(font);
        baseProtein.setText(pro + "g Protien");
        baseProtein.setTypeface(font);
        baseCarbs.setText(carb + "g Carbs");
        baseCarbs.setTypeface(font);
        baseFat.setText(fat + "g Fat");
        baseFat.setTypeface(font);
        nutrition.setTypeface(font);

        relativeLayout = (RelativeLayout) findViewById(R.id.backgroundlayout);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_add && isNetworkAvailable()) {
                    relativeLayout.setVisibility(View.INVISIBLE);
                    Fragment fragment = new AddFood();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragContainer, fragment);
                    ft.commit();
                } else if (item.getItemId() == R.id.action_menu ) {
                    Fragment f = getFragmentManager().findFragmentById(R.id.fragContainer);
                    if (f != null) {
                        Log.d("XDDD", "XDDD");
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.fragContainer)).commit();
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                } else if (item.getItemId() == R.id.image_add && isNetworkAvailable()) {
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAM_REQUEST);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getItemId() == R.id.new_day) {
                    Fragment f = getFragmentManager().findFragmentById(R.id.fragContainer);
                    if (f != null) {
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.fragContainer)).commit();
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    finalFood = new ArrayList<>();
                    CustomAdapter myRealAdapter = new CustomAdapter(MainActivity.this, R.layout.food_layout, finalFood);
                    foodList.setAdapter(myRealAdapter);
                    cal = 0;
                    pro = 0;
                    carb = 0;
                    fat = 0;
                    Typeface font = Typeface.createFromAsset(getAssets(), "fonts/default.ttf");
                    baseCalories.setText(cal + " Calories");
                    baseCalories.setTypeface(font);
                    baseProtein.setText(pro + "g Protien");
                    baseProtein.setTypeface(font);
                    baseCarbs.setText(carb + "g Carbs");
                    baseCarbs.setTypeface(font);
                    baseFat.setText(fat + "g Fat");
                    baseFat.setTypeface(font);
                }
                else{
                    Toast.makeText(MainActivity.this, "No Interent Connection", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        baseMacros = new Food();
        imagefoodSearch = new ArrayList<>();
        wordSearchList = new ArrayList<>();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAM_REQUEST) {
                bitmap = (Bitmap) data.getExtras().get("data");
                ResultsThread thread = new ResultsThread();
                try {
                    imagefoodSearch = new ArrayList<>(thread.execute().get());
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("Words", imagefoodSearch);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    Fragment fragment = new AddImage();
                    fragment.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragContainer, fragment);
                    ft.commit();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ResultsThread extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            //Initialize client
            ClarifaiClient client = new ClarifaiBuilder("GZCf7zRJZ71RHtwCsCCGLrlg1m2u1qUu4IXW-o-4", "WMmb3mztn89VyYirN97CogjqXfuxhpaXpZS9SecL").buildSync();

            //Converting bitmap to Byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            final byte[] image = stream.toByteArray();

            //Get list of predictions
            try {
                final List<ClarifaiOutput<Concept>> results =
                        client.getDefaultModels().foodModel()
                                .predict()
                                .withInputs(
                                        ClarifaiInput.forImage(ClarifaiImage.of(image))
                                )
                                .executeSync()
                                .get();

                Gson gson = new Gson();
                String json = gson.toJson(results);
                System.out.println("json = " + json);
                JSONArray list = new JSONArray(json); //Something wrong here
                ArrayList<String> returnValue = new ArrayList<>();
                for (int x = 0; x < 15; x++) {
                    returnValue.add(list.getJSONObject(0).getJSONArray("data").getJSONObject(x).getString("name"));
                }

                return returnValue;

            } catch (Exception e) {
            }

            return null;
        }

    }


    public void addFood(Food f) {
        finalFood.add(f);
        CustomAdapter myRealAdapter = new CustomAdapter(this, R.layout.food_layout, finalFood);
        foodList.setAdapter(myRealAdapter);
        relativeLayout.setVisibility(View.VISIBLE);
        updateList();
    }

    public void updateList(){
        cal=0;
        pro=0;
        carb=0;
        fat=0;

        for (int x = 0; x < finalFood.size(); x++) {
            cal += finalFood.get(x).getCalories();
            pro += finalFood.get(x).getProtein();
            carb += finalFood.get(x).getCarbs();
            fat += finalFood.get(x).getFat();
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/default.ttf");
        baseCalories.setText(cal + " Calories");
        baseCalories.setTypeface(font);
        baseProtein.setText(pro + "g Protien");
        baseProtein.setTypeface(font);
        baseCarbs.setText(carb + "g Carbs");
        baseCarbs.setTypeface(font);
        baseFat.setText(fat + "g Fat");
        baseFat.setTypeface(font);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(finalFood);
        editor.putString("Foods", json);
        editor.putInt("Calories", cal);
        editor.putInt("Protein", pro);
        editor.putInt("Carbs", carb);
        editor.putInt("Fat", fat);
        editor.commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

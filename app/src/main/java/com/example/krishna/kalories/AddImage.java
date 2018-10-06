package com.example.krishna.kalories;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by krishna on 5/13/17.
 */

public class AddImage extends Fragment {

    ArrayList<String> imagefoodSearch;
    ListView imageList;
    CustomAdapterImage myAdapter;
    int returnfood;
    ArrayList<Food> results;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (results != null) {
            ((MainActivity) getActivity()).addFood(results.get(returnfood));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.add_image_layout, container, false);

        imageList = (ListView) v.findViewById(R.id.imageList);
        results = new ArrayList<>();

        if (this.getArguments() != null) {
            imagefoodSearch = this.getArguments().getStringArrayList("Words");
            myAdapter = new CustomAdapterImage(getActivity(), R.layout.imagefood_layout, imagefoodSearch);
            imageList.setAdapter(myAdapter);
            imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WordSearchThread getId = new WordSearchThread();
                    getId.execute(imagefoodSearch.get(position));
                }
            });
        }

        return v;
    }

    public class WordSearchThread extends AsyncTask<String, Void, ArrayList<Food>> {
        @Override
        protected void onPostExecute(ArrayList<Food> foods) {
            super.onPostExecute(foods);
            results = foods;
            Log.d("XDDDDDD", "" + foods);

            try {
                CustomAdapter myRealAdapter = new CustomAdapter(getActivity(), R.layout.food_layout, results);
                imageList.setAdapter(myRealAdapter);
                imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        returnfood = position;
                        getActivity().getFragmentManager().beginTransaction().remove(AddImage.this).commit();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getContext(), "No Interent Connection or No Items Found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected ArrayList<Food> doInBackground(String... params) {
            String words = params[0];
            URL url;
            URLConnection urlConnection;
            InputStream inputStream;
            BufferedReader bufferedReader;
            try {
                url = new URL("https://api.nutritionix.com/v1_1/search/" + URLEncoder.encode(words, "UTF-8") + "?results=0%3A20&cal_min=0&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2Cbrand_id&appId=b3e09840&appKey=0987ed980daaa58cba3daffbc34e8c7d");
                urlConnection = url.openConnection();
                inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String JSONstr = bufferedReader.readLine();
                bufferedReader.close();
                Log.d("TEST", JSONstr);
                JSONObject list = new JSONObject(JSONstr);
                ArrayList<String> returnValue = new ArrayList<>();
                for (int x = 0; x < 3; x++) {
                    returnValue.add(list.getJSONArray("hits").getJSONObject(x).getJSONObject("fields").getString("item_id"));
                }
                ArrayList<Food> foods = new ArrayList<>();
                for (int y = 0; y < returnValue.size(); y++) {
                    URL url2;
                    URLConnection urlConnection2;
                    InputStream inputStream2;
                    BufferedReader bufferedReader2;
                    try {
                        url2 = new URL("https://api.nutritionix.com/v1_1/item?id=" + returnValue.get(y) + "&appId=b3e09840&appKey=0987ed980daaa58cba3daffbc34e8c7d");
                        urlConnection2 = url2.openConnection();
                        inputStream2 = urlConnection2.getInputStream();
                        bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
                        String JSONstr2 = bufferedReader2.readLine();
                        bufferedReader2.close();
                        Log.d("Initializing", JSONstr2);
                        JSONObject list2 = new JSONObject(JSONstr2);
                        foods.add(y, new Food());
                        foods.get(y).setCalories(list2.getInt("nf_calories"));
                        foods.get(y).setName(list2.getString("item_name"));
                        foods.get(y).setFatcalories(list2.getInt("nf_calories_from_fat"));
                        foods.get(y).setFat(list2.getInt("nf_total_fat"));
                        foods.get(y).setSodium(list2.getInt("nf_sodium"));
                        foods.get(y).setCarbs(list2.getInt("nf_total_carbohydrate"));
                        foods.get(y).setFiber(list2.getInt("nf_dietary_fiber"));
                        foods.get(y).setSugar(list2.getInt("nf_sugars"));
                        foods.get(y).setProtein(list2.getInt("nf_protein"));

                    } catch (Exception e) {
                        Log.d("ERROR", e.toString());
                    }

                }
                return foods;
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }

            return null;
        }
    }

}

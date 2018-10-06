package com.example.krishna.kalories;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by krishna on 5/6/17.
 */

public class Food implements Parcelable {

    private String name;

    private int calories,fatcalories,fat,carbs,fiber,sodium,sugar,protein;

    private int quantity;

    public Food(){
        quantity=1;
    }

    protected Food(Parcel in) {
        name = in.readString();
        calories = in.readInt();
        fatcalories = in.readInt();
        fat = in.readInt();
        carbs = in.readInt();
        fiber = in.readInt();
        sodium = in.readInt();
        sugar = in.readInt();
        protein = in.readInt();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(calories);
        dest.writeInt(fatcalories);
        dest.writeInt(fat);
        dest.writeInt(carbs);
        dest.writeInt(fiber);
        dest.writeInt(sodium);
        dest.writeInt(sugar);
        dest.writeInt(protein);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public int getFatcalories() {
        return fatcalories;
    }

    public int getFat() {
        return fat;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFiber() {
        return fiber;
    }

    public int getSodium() {
        return sodium;
    }

    public int getSugar() {
        return sugar;
    }

    public int getProtein() {
        return protein;
    }

    public String toString(){
        return name+" - "+quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFatcalories(int fatcalories) {
        this.fatcalories = fatcalories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setFiber(int fiber) {
        this.fiber = fiber;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public void setSugar(int sugar) {
        this.sugar = sugar;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void increaseQuantity(){
        quantity++;
    }

    public int getQ(){
        return quantity;
    }





}

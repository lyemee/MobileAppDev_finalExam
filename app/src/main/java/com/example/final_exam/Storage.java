package com.example.final_exam;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Storage {
    private static final String PREF = "workouts_pref";
    private static final String KEY_LIST = "workouts";
    private static final String KEY_STEP_BASELINE = "step_baseline_";
    private static final String KEY_STEP_DATE = "step_date";

    public static void saveWorkout(Context ctx, Workout w) {
        ArrayList<Workout> list = loadWorkouts(ctx);
        list.add(0, w); // add newest first
        saveList(ctx, list);
    }

    public static ArrayList<Workout> loadWorkouts(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = sp.getString(KEY_LIST, "[]");
        ArrayList<Workout> result = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Workout w = new Workout(
                        o.optString("type",""),
                        o.optInt("durationMinutes",0),
                        o.optString("imageUrl",""),
                        o.optLong("timestamp",0)
                );
                result.add(w);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void saveList(Context ctx, ArrayList<Workout> list) {
        JSONArray arr = new JSONArray();
        try {
            for (Workout w : list) {
                JSONObject o = new JSONObject();
                o.put("type", w.type);
                o.put("durationMinutes", w.durationMinutes);
                o.put("imageUrl", w.imageUrl);
                o.put("timestamp", w.timestamp);
                arr.put(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putString(KEY_LIST, arr.toString()).apply();
    }

    /** Step counter baseline (cumulative since boot → convert to today's steps) */
    public static void setStepBaseline(Context ctx, String dateKey, float baseline) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_STEP_DATE, dateKey)
                .putFloat(KEY_STEP_BASELINE + dateKey, baseline)
                .apply();
    }

    public static float getStepBaseline(Context ctx, String dateKey) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getFloat(KEY_STEP_BASELINE + dateKey, Float.NaN);
    }

    public static String getSavedDate(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(KEY_STEP_DATE, "");
    }
}


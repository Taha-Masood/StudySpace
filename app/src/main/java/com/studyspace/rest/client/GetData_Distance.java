package com.studyspace.rest.client;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.studyspace.studyspace.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class GetData_Distance {

    private double [] origin, dest;
    private final String uri = "https://maps.googleapis.com/maps/api/distancematrix";
    private RequestParams params;
    private Context ctx;
    private String building;

    public GetData_Distance(Context ctx, double [] origin,
                            double [] dest, String building, ArrayList<String> used_build){//origin and dest are lat,long coords
        this.building = building;
        this.ctx = ctx;
        this.origin = origin;
        this.dest = dest;
        this.params = new RequestParams();
        this.params.put("key", "AIzaSyDfluUsxpHauw41nPXWETJw7WOC1K_uNOw");//Google API Key
//        double dist = MainActivity.db.get_dist(building);
//        if(dist != -1.0){
//            Log.d("Found Dist: ", String.valueOf(dist));
//            MainActivity.db.update_dist(dist, building);
//            return;
//        } //Work in Progress (Efficent API Calls)
        this.get_dist();
    }

    JsonHttpResponseHandler dist_handler = new JsonHttpResponseHandler(){
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
        {
            Log.d("API CALL FAILED: ", errorResponse.toString());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try{
                JSONArray rows = response.getJSONArray("rows");
                JSONObject obj = rows.getJSONObject(0);
                JSONArray elem = obj.getJSONArray("elements");
                JSONObject obj_2 = elem.getJSONObject(0);
                JSONObject dist = obj_2.getJSONObject("distance");
                double dist_val = (double) dist.getInt("value");
                Log.d("Distance: ", String.valueOf(dist_val));
                MainActivity.db.update_dist(dist_val, building);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };

    public void get_dist(){
        String endpoint = "/json";
        params.put("origins",origin[0]+","+origin[1]);
        params.put("destinations",dest[0]+","+dest[1]);
        RestClientUsage dist_matrix = new RestClientUsage(ctx, uri, endpoint);
        dist_matrix.api_call(params, dist_handler);
    }


}

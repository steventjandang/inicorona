package com.example.inicorona;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CountryFragment extends Fragment {
    private TextView totalCasesTextView;
    private TextView totalRecoveredTextView;
    private TextView totalDeathsTextView;
    private TextView dailyCasesTextView;
    private TextView dailyRecoveredTextView;
    private TextView dailyDeathsTextView;

    private RequestQueue requestQueue;

    public CountryFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country, container, false);

        totalCasesTextView = view.findViewById(R.id.total_cases);
        totalRecoveredTextView = view.findViewById(R.id.total_recovered);
        totalDeathsTextView = view.findViewById(R.id.total_deaths);
        dailyCasesTextView = view.findViewById(R.id.daily_cases);
        dailyRecoveredTextView = view.findViewById(R.id.daily_recovered);
        dailyDeathsTextView = view.findViewById(R.id.daily_deaths);

        requestQueue = Volley.newRequestQueue(view.getContext());
        load_data();

        return view;
    }

    public void load_data() {
        String url = "https://data.covid19.go.id/public/api/update.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject results = response.getJSONObject("update");
                    JSONObject total = results.getJSONObject("total");
                    int total_cases = total.getInt("jumlah_positif");
                    int total_recovered = total.getInt("jumlah_sembuh");
                    int total_deaths = total.getInt("jumlah_meninggal");

                    JSONObject daily = results.getJSONObject("penambahan");
                    int daily_cases = daily.getInt("jumlah_positif");
                    int daily_recovered = daily.getInt("jumlah_sembuh");
                    int daily_deaths = daily.getInt("jumlah_meninggal");

                    display(total_cases, total_recovered, total_deaths, daily_cases, daily_recovered, daily_deaths);
                } catch (JSONException e) {
                    Log.e("country", "Failed to retrieve country data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("country", "Failed to retrieve country api", error);
            }
        });

        requestQueue.add(request);
    }

    public void display(int total_cases, int total_recovered, int total_deaths, int daily_cases, int daily_recovered, int daily_deaths) {
        totalCasesTextView.setText(formatBigNumber(total_cases));
        totalRecoveredTextView.setText(formatBigNumber(total_recovered));
        totalDeathsTextView.setText(formatBigNumber(total_deaths));
        dailyCasesTextView.setText(formatBigNumber(daily_cases));
        dailyRecoveredTextView.setText(formatBigNumber(daily_recovered));
        dailyDeathsTextView.setText(formatBigNumber(daily_deaths));
    }

    public String formatBigNumber(int bigNumber) {
        return String.format("%,d", bigNumber);
    }
}

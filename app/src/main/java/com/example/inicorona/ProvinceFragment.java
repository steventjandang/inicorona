package com.example.inicorona;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProvinceFragment extends Fragment {
    private Spinner spinner;
    private List<String> provinceList = new ArrayList<>();

    private TextView totalCasesTextView;
    private TextView totalRecoveredTextView;
    private TextView totalDeathsTextView;
    private TextView dailyCasesTextView;
    private TextView dailyRecoveredTextView;
    private TextView dailyDeathsTextView;

    private RequestQueue requestQueue;

    public ProvinceFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_province, container, false);

        spinner = view.findViewById(R.id.province_spinner);

        totalCasesTextView = view.findViewById(R.id.total_cases);
        totalRecoveredTextView = view.findViewById(R.id.total_recovered);
        totalDeathsTextView = view.findViewById(R.id.total_deaths);
        dailyCasesTextView = view.findViewById(R.id.daily_cases);
        dailyRecoveredTextView = view.findViewById(R.id.daily_recovered);
        dailyDeathsTextView = view.findViewById(R.id.daily_deaths);

        requestQueue = Volley.newRequestQueue(view.getContext());

//        load_spinner();
        provinceList.add("Aceh");
        provinceList.add("Bali");
        provinceList.add("Banten");
        provinceList.add("Bengkulu");
        provinceList.add("Daerah Istimewa Yogyakarta");
        provinceList.add("DKI Jakarta");
        provinceList.add("Gorontalo");
        provinceList.add("Jambi");
        provinceList.add("Jawa Barat");
        provinceList.add("Jawa Tengah");
        provinceList.add("Jawa Timur");
        provinceList.add("Kalimantan Barat");
        provinceList.add("Kalimantan Selatan");
        provinceList.add("Kalimantan Tengah");
        provinceList.add("Kalimantan Timur");
        provinceList.add("Kalimantan Utara");
        provinceList.add("Kepulauan Bangka Belitung");
        provinceList.add("Kepulauan Riau");
        provinceList.add("Lampung");
        provinceList.add("Maluku");
        provinceList.add("Maluku Utara");
        provinceList.add("Nusa Tenggara Barat");
        provinceList.add("Nusa Tenggara Timur");
        provinceList.add("Papua");
        provinceList.add("Papua Barat");
        provinceList.add("Riau");
        provinceList.add("Sulawesi Barat");
        provinceList.add("Sulawesi Selatan");
        provinceList.add("Sulawesi Tengah");
        provinceList.add("Sulawesi Timur");
        provinceList.add("Sulawesi Utara");
        provinceList.add("Sumatera Barat");
        provinceList.add("Sumatera Utara");
        provinceList.add("Sumatera Selatan");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, provinceList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                load_data(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                load_data("DKI JAKARTA");
            }
        });

        return view;
    }

    public void load_spinner() {
        String url = "https://data.covid19.go.id/public/api/prov.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("list_data");
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        provinceList.add(result.getString("key"));
                    }
                } catch (JSONException e) {
                    Log.e("spinner", "Failed to retrieve province data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("spinner", "Failed to retrieve province api", error);
            }
        });

        requestQueue.add(request);
    }

    public void load_data(final String province) {
        String url = "https://data.covid19.go.id/public/api/prov.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("list_data");
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        if(result.getString("key").toLowerCase().equals(province.toLowerCase())) {
                            int total_cases = result.getInt("jumlah_kasus");
                            int total_recovered = result.getInt("jumlah_sembuh");
                            int total_deaths = result.getInt("jumlah_meninggal");

                            JSONObject daily = result.getJSONObject("penambahan");
                            int daily_cases = daily.getInt("positif");
                            int daily_recovered = daily.getInt("sembuh");
                            int daily_deaths = daily.getInt("meninggal");

                            display(total_cases, total_recovered, total_deaths, daily_cases, daily_recovered, daily_deaths);

                            break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("province", "Failed to retrieve province data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("province", "Failed to retrieve province api", error);
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

package com.example.inicorona;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> implements Filterable {
    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerView;
        private TextView nameTextView;
        private TextView addressTextView;
        private TextView phoneTextView;
        private TextView regionTextView;

        HospitalViewHolder(View view) {
            super(view);

            containerView = view.findViewById(R.id.hospital_row);
            nameTextView = view.findViewById(R.id.hospital_name);
            addressTextView = view.findViewById(R.id.hospital_address);
            phoneTextView = view.findViewById(R.id.hospital_phone);
            regionTextView = view.findViewById(R.id.hospital_region);
        }
    }

    private List<Hospital> hospitals = new ArrayList<>();
    private RequestQueue requestQueue;

    HospitalAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        load_hospital();
    }

    public void load_hospital() {
        String url = "https://dekontaminasi.com/api/id/covid19/hospitals";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject result = response.getJSONObject(i);
                        String name = result.getString("name");
                        String address = result.getString("address");
                        String phone = result.getString("phone");
                        String region = result.getString("region");

                        hospitals.add(new Hospital(name, address, phone, region));

                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("hospital", "Failed to retrieve hospital data");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("hospital", "Failed to retrieve hospital api", error);
            }
        });

        requestQueue.add(request);
    }

    private List<Hospital> filteredHospitals = hospitals;

    public class HospitalFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String search = constraint.toString().toLowerCase().trim();

            if(search.isEmpty()) {
                filteredHospitals = hospitals;
            } else {
                List<Hospital> filteredList = new ArrayList<>();
                for(Hospital row : hospitals) {
                    if(row.getAddress().toLowerCase().contains(search) || row.getRegion().toLowerCase().contains(search)) {
                        filteredList.add(row);
                    }
                }
                filteredHospitals = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredHospitals;
            filterResults.count = filteredHospitals.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredHospitals = (List<Hospital>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        return new HospitalFilter();
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_row, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalAdapter.HospitalViewHolder holder, int position) {
        Hospital current = filteredHospitals.get(position);
        holder.nameTextView.setText(current.getName());
        holder.addressTextView.setText(current.getAddress());
        holder.phoneTextView.setText(current.getPhone());
        holder.regionTextView.setText(current.getRegion());
    }

    @Override
    public int getItemCount() {
        return filteredHospitals.size();
    }
}

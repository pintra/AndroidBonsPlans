package com.example.mohameddhiahachem.bonplanapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mohameddhiahachem.bonplanapp.Adapter.EventAdapter;
import com.example.mohameddhiahachem.bonplanapp.Adapter.ProductAdapter;
import com.example.mohameddhiahachem.bonplanapp.Entity.EventPlan;
import com.example.mohameddhiahachem.bonplanapp.Entity.Product;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class FragmentPlanProduct extends Fragment {

    private View view;
    private String id ;
    ArrayList<Product> lists = new ArrayList<>();
    Product[] products;
    ProductAdapter adapter;
    private ListView listEvent;
    private int page = 0;
    private boolean isLoad = true;

    @SuppressLint("ValidFragment")
    public FragmentPlanProduct(String id){
        this.id = id;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plan_product, container, false);

        listEvent = (ListView) view.findViewById(R.id.fragment_plan_products);
        try {
            loadData();
        }catch (Exception e){ }


        listEvent.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listEvent.getLastVisiblePosition() - listEvent.getHeaderViewsCount() -
                        listEvent.getFooterViewsCount()) >= (listEvent.getCount() - 1)) {

                    page+= 5;
                    loadData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });




        return view;
    }


    public void loadData(){
        String URL_REGIST = "http://10.0.3.2:8000/api/" + id + "/products";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("code");
                    if(success){
                        Gson gson = new Gson();
                        products = gson.fromJson(jsonObject.getJSONArray("products").toString(), Product[].class);
                        //Log.d("Plans", plans.toString());
                        for( Product e : products){
                            lists.add(new Product(e.getId(), e.getName(),e.getImage(),e.getPrice(),e.getAddress(),e.getCity(),e.getGovernorate()));
                        }
                        Toast.makeText(getContext(), "set Products Plan", Toast.LENGTH_SHORT).show();
                        if(isLoad){
                            adapter = new ProductAdapter(getContext(), lists);
                            listEvent.setAdapter(adapter);
                            isLoad = false;

                        }else{
                            adapter.notifyDataSetChanged();
                        }


                    }else{
                        Toast.makeText(getContext(), "not good", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(getContext(), "Erorr Eroor !!!!!!!!!!!!!!!!!!" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Erorr Eroor !!!!!!!!!!!!!!!!!!" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("page", String.valueOf(page));
                params.put("id", id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                2,
                1f));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}

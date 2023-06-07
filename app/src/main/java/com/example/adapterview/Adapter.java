package com.example.adapterview;

import android.app.Activity;
import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class Adapter extends BaseAdapter implements Filterable {
    private ArrayList<Contact> dataBackUp;
    // Nguồn dữ liệu cho Adapter
    private ArrayList<Contact> data;
    // Ngữ cảnh của ứng dụng
    private Activity context;
    //Đối tượng phân tích layout
    private LayoutInflater inflater;
    public Adapter(){

    }
    public Adapter(ArrayList<Contact> data, Activity activity){
        this.data = data;
        this.context=context;
        this.inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if(v==null){
            v = inflater.inflate(R.layout.layoutadapter,null);
        }
        ImageView imgprofile = v.findViewById(R.id.imageView);
        TextView tvname = v.findViewById(R.id.tvName);
        tvname.setText(data.get(i).getName());
        TextView tvPhone = v.findViewById(R.id.tvPhone);
        tvPhone.setText(data.get(i).getPhone());

        return v;
    }

    @Override
    public Filter getFilter() {
        Filter f = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults fr = new FilterResults();
                if(dataBackUp==null){
                    dataBackUp=new ArrayList<>(data);
                }
                if(charSequence==null||charSequence.length()==0){
                    fr.count=dataBackUp.size();
                    fr.values=dataBackUp;
                }
                else {
                    ArrayList<Contact> newData=new ArrayList<>();
                    for(Contact c:dataBackUp){
                        if(c.getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                            newData.add(c);
                        }
                    }
                    fr.count=newData.size();
                    fr.values=newData;
                }
                return fr;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                data=new ArrayList<Contact>();
                ArrayList<Contact> tmp = (ArrayList<Contact>) filterResults.values;
                for(Contact c: tmp)
                    data.add(c);
                notifyDataSetChanged();
            }
        };
        return f;
    }
}

package com.example.autoamigos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private Context context;
    private List<Car> carList;

    public CarAdapter(Context context, List<Car> carList) {
        this.context = context;
        this.carList = carList;
    }

    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarAdapter.ViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.textViewModel.setText(car.getModel());
        holder.textViewCategory.setText(car.getCategory_name());
        holder.textViewPrice.setText('$'+(String.valueOf(car.getPrice())));
        Glide.with(context).load(car.getImage_url()).into(holder.imageViewCar);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewModel;
        public TextView textViewCategory;
        public TextView textViewPrice;

        public ImageView imageViewCar;
        // Other views

        public ViewHolder(View itemView) {
            super(itemView);
            textViewModel = itemView.findViewById(R.id.textViewModel);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageViewCar = itemView.findViewById(R.id.imageViewCar);
        }
    }
}

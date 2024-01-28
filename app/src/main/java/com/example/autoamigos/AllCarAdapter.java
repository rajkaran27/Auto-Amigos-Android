package com.example.autoamigos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AllCarAdapter extends RecyclerView.Adapter<AllCarAdapter.ViewHolder>{
    private Context context;
    private List<Car> carList;
    private AllCarAdapter.OnCarClickListener listener;

    public AllCarAdapter(Context context, List<Car> carList, AllCarAdapter.OnCarClickListener listener) {
        this.context = context;
        this.carList = carList;
        this.listener = listener;
    }

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    @Override
    public AllCarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_car_item, parent, false);
        return new AllCarAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllCarAdapter.ViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.txtModelM.setText(car.getModel());
        holder.txtCatC.setText(car.getCategory_name());
        holder.txtPriceP.setText('$'+(String.valueOf(car.getPrice())));
        Glide.with(context).load(car.getImage_url()).into(holder.imgCarC);

        holder.btnSee.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCarClick(car);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtModelM;
        public TextView txtCatC;
        public TextView txtPriceP;

        public ImageView imgCarC;

        public Button btnSee;

        public ViewHolder(View itemView) {
            super(itemView);
            txtModelM = itemView.findViewById(R.id.txtModelM);
            txtCatC = itemView.findViewById(R.id.txtCatC);
            txtPriceP = itemView.findViewById(R.id.txtPriceP);
            imgCarC = itemView.findViewById(R.id.imgCarC);
            btnSee = itemView.findViewById(R.id.btnSee);
        }
    }
}

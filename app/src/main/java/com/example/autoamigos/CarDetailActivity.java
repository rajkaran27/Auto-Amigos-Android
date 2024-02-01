package com.example.autoamigos;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class CarDetailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        Button btnBuy = findViewById(R.id.btnBuy);
        if (currentUser == null) {
            btnBuy.setVisibility(View.GONE);
        }

        ImageButton imageButton = findViewById(R.id.backBtn);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("car_id")) {
            // Retrieve the car ID from the intent
            int carId = getIntent().getIntExtra("car_id", 0);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cars");

            databaseReference.orderByKey().equalTo(String.valueOf(carId))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Car car = child.getValue(Car.class);
                                    ImageView imageViewCar = findViewById(R.id.imgCar);
                                    TextView tlModel = findViewById(R.id.tlModel);
                                    TextView txtModel = findViewById(R.id.txtModel);
                                    TextView txtDesc = findViewById(R.id.txtDesc);
                                    TextView txtCat = findViewById(R.id.txtCat);
                                    TextView txtCap = findViewById(R.id.txtCap);
                                    TextView txtTrans = findViewById(R.id.txtTrans);
                                    TextView txtPrice = findViewById(R.id.txtPrice);
                                    YouTubePlayerView youTubePlayerView = findViewById(R.id.carVideo);
                                    getLifecycle().addObserver(youTubePlayerView);

                                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                        @Override
                                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                            String videoId = car.getVideo();
                                            youTubePlayer.loadVideo(videoId, 0);
                                        }
                                    });
                                    tlModel.setText(car.getModel());
                                    txtModel.setText(car.getModel());
                                    txtDesc.setText(car.getDescription());
                                    txtCat.setText(car.getCategory_name());
                                    txtCap.setText(car.getCapacity());
                                    txtTrans.setText(car.getTransmission());
                                    txtPrice.setText("$"+car.getPrice());

                                    Glide.with(CarDetailActivity.this)
                                            .load(car.getImage_url())
                                            .into(imageViewCar);
                                    btnBuy.setOnClickListener(v -> {
                                        Intent purchaseIntent = new Intent(CarDetailActivity.this, PurchaseActivity.class);
                                        purchaseIntent.putExtra("car_id", carId);
                                        startActivity(purchaseIntent);
                                    });


                                }
                            } else {
                                String errormsg = "Car Does Not Exist";
                                Toast.makeText(CarDetailActivity.this,errormsg,Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });

        } else {
            Log.e("CarDetailActivity", "No car ID provided in the intent");
        }
    }
}

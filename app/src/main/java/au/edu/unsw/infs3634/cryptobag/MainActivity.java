package au.edu.unsw.infs3634.cryptobag;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unsw.infs3634.cryptobag.Entities.Coin;
import au.edu.unsw.infs3634.cryptobag.Entities.CoinLoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private boolean mTwoPane;
    public List<Coin> mCoins;
    private String TAG ="Errors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }

        RecyclerView mRecyclerView = findViewById(R.id.rvList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new CoinAdapter(this, new ArrayList<Coin>(), mTwoPane);
        mRecyclerView.setAdapter(mAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coinlore.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CoinService service = retrofit.create(CoinService.class);
        Call<CoinLoreResponse> coinsCall = service.getCoins();
        coinsCall.enqueue(new Callback<CoinLoreResponse>() {
            @Override
            public void onResponse(Call<CoinLoreResponse> call, Response<CoinLoreResponse> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse ");
                    List<Coin> coins = response.body().getData();
                    ((CoinAdapter)mAdapter).setCoins(coins);
                } else {
                    Log.d(TAG,"Error");
                }
            }

            @Override
            public void onFailure(Call<CoinLoreResponse> call, Throwable t) {
                Log.d(TAG,"Failure");
            }
        });

    }
}

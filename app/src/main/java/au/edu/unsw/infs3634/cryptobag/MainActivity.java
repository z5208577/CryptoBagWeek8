package au.edu.unsw.infs3634.cryptobag;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;

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

       myTask asyncTask = new myTask(this);
        asyncTask.execute();

    }

    public class myTask extends AsyncTask<Void,Void, RecyclerView.Adapter > {
        public MainActivity activity;
        public myTask(MainActivity mainActivity) {
            this.activity = mainActivity;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected RecyclerView.Adapter  doInBackground(Void... voids) {

            RecyclerView.Adapter mAdapter = new CoinAdapter(activity, new ArrayList<Coin>(), mTwoPane);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.coinlore.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CoinService service = retrofit.create(CoinService.class);
            Call<CoinLoreResponse> coinsCall = service.getCoins();
            Response<CoinLoreResponse> coinsResponse= null;
            try {
                coinsResponse = coinsCall.execute();
                List<Coin> coins = coinsResponse.body().getData();
                ((CoinAdapter)mAdapter).setCoins(coins);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mAdapter;
        }

        @Override
        protected void onPostExecute(RecyclerView.Adapter adapter) {
            super.onPostExecute(adapter);
            RecyclerView mRecyclerView = findViewById(R.id.rvList);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setAdapter(adapter);

        }

        @Override
        protected void onCancelled(RecyclerView.Adapter  mAdapter) {
            super.onCancelled(mAdapter);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

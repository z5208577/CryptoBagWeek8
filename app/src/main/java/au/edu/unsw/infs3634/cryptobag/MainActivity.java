package au.edu.unsw.infs3634.cryptobag;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

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
    public MainActivity mainActivity;
    public Button refreshDatabase;
    public Context context;
    public List<Coin> mCoins;
    private String TAG ="Errors";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        mainActivity=this;
        setContentView(R.layout.activity_main);
        CoinDatabase coinDatabase = Room.databaseBuilder(getApplicationContext(),CoinDatabase.class,"myDB")
                .build();
        coinDatabase.coinDao();


        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }



        myTask asyncTask = new myTask(this,coinDatabase);
        asyncTask.execute();


       populateRecycler asyncTask2 = new populateRecycler(this,coinDatabase);
       asyncTask2.execute();



    }

    public class populateRecycler extends AsyncTask<Void,Void, RecyclerView.Adapter > {
        public MainActivity activity;
        public CoinDatabase coinDatabase;
        public populateRecycler(MainActivity mainActivity,CoinDatabase coinDatabase) {
            this.activity = mainActivity;
            this.coinDatabase = coinDatabase;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected RecyclerView.Adapter  doInBackground(Void... voids) {

            RecyclerView.Adapter mAdapter = new CoinAdapter(activity, new ArrayList<Coin>(), mTwoPane);
            ((CoinAdapter)mAdapter).setCoins(coinDatabase.coinDao().getCoins());

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

    public class myTask extends AsyncTask<Void,Void, CoinDatabase > {
        public MainActivity activity;
        public CoinDatabase coinDatabase;
        public myTask(MainActivity mainActivity,CoinDatabase coinDatabase) {
            this.activity = mainActivity;
            this.coinDatabase = coinDatabase;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected CoinDatabase  doInBackground(Void... voids) {
            coinDatabase.coinDao().clearCoins();
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
                Coin[] coinList = coins.toArray(new Coin[coins.size()]);
                coinDatabase.coinDao().insertCoins(coinList);
                //populate database
            } catch (IOException e) {
                e.printStackTrace();
            }
            return coinDatabase;
        }

        @Override
        protected void onPostExecute(CoinDatabase coinDatabase) {
            super.onPostExecute(coinDatabase);
            /*
            RecyclerView mRecyclerView = findViewById(R.id.rvList);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setAdapter(coinDatabase);
            */
        }

        @Override
        protected void onCancelled(CoinDatabase coinDatabase) {
            super.onCancelled(coinDatabase);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

//
//}

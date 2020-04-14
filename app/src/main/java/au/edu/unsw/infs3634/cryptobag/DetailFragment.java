package au.edu.unsw.infs3634.cryptobag;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import au.edu.unsw.infs3634.cryptobag.Entities.Coin;
import au.edu.unsw.infs3634.cryptobag.Entities.CoinLoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    public String TAG;
    private Coin mCoin;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey(ARG_ITEM_ID)) {
            myTask asyncTask = new myTask(getArguments().getString(ARG_ITEM_ID));
            asyncTask.execute();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

       updateUI();

        return rootView;
    }

    private void searchCoin(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + name));
        startActivity(intent);
    }

    private void updateUI(){
        if(mCoin != null) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            ((TextView) getView().findViewById(R.id.tvName)).setText(mCoin.getName());
            ((TextView) getView().findViewById(R.id.tvSymbol)).setText(mCoin.getSymbol());
            ((TextView) getView().findViewById(R.id.tvValueField)).setText(mCoin.getPriceUsd());
            ((TextView) getView().findViewById(R.id.tvChange1hField)).setText(mCoin.getPercentChange1h() + " %");
            ((TextView) getView().findViewById(R.id.tvChange24hField)).setText(mCoin.getPercentChange24h() + " %");
            ((TextView) getView().findViewById(R.id.tvChange7dField)).setText(mCoin.getPercentChange7d() + " %");
            ((TextView) getView().findViewById(R.id.tvMarketcapField)).setText(mCoin.getMarketCapUsd());
            ((TextView) getView().findViewById(R.id.tvVolumeField)).setText(formatter.format(mCoin.getVolume24()));
            ((ImageView) getView().findViewById(R.id.ivSearch)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchCoin(mCoin.getName());
                }
            });
        }
    }

    public class myTask extends AsyncTask<Void,Void, Coin > {
        String itemID;

        public myTask(String itemID){
            this.itemID = itemID;
        }
        @Override
        protected void onPostExecute(Coin coin) {
            super.onPostExecute(coin);
            updateUI();
        }

        @Override
        protected Coin doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.coinlore.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CoinService service = retrofit.create(CoinService.class);
            Call<CoinLoreResponse> coinsCall = service.getCoins();
            try {
                Response<CoinLoreResponse> coinsResponse = coinsCall.execute();
                List<Coin> coins = coinsResponse.body().getData();
                for(Coin coin : coins){
                    if(coin.getSymbol().equals(itemID)){
                        mCoin = coin;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}

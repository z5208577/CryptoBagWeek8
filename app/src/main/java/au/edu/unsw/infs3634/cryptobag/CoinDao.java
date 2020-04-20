package au.edu.unsw.infs3634.cryptobag;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import au.edu.unsw.infs3634.cryptobag.Entities.Coin;

@Dao
public interface CoinDao {

    @Query("Select * from Coin")
    List<Coin> getCoins();

    @Insert
    void insertCoins(Coin...coins);

    @Query("DELETE FROM Coin")
    void clearCoins();

    @Query("Select * from Coin Where Coin.Symbol = :targetId")
    List<Coin> searchCoin(String targetId);

}

package com.udacity.stockhawk;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.stockhawk.data.Contract;

/**
 * StockPref Class.
 *
 * Implements Parcelable in order to be able to pass it as a parameter between activities.
 * Defines all the properties a stock object has.
 */
public class StockPref implements Parcelable {

    private String stock_symbol;
    private String stock_name;
    private float stock_price;
    private float stock_percent_change;
    private float stock_absolut_change;
    private String stock_history;

    public StockPref(Cursor mCursor){
        this.stock_symbol = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
        this.stock_name = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_NAME));
        this.stock_price = mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
        this.stock_percent_change = mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
        this.stock_absolut_change = mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
        this.stock_history = mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
    }


    protected StockPref(Parcel in) {
        stock_symbol = in.readString();
        stock_name = in.readString();
        stock_price = in.readFloat();
        stock_percent_change = in.readFloat();
        stock_absolut_change = in.readFloat();
        stock_history = in.readString();

    }

    public static final Creator<StockPref> CREATOR = new Creator<StockPref>() {
        @Override
        public StockPref createFromParcel(Parcel in) {
            return new StockPref(in);
        }

        @Override
        public StockPref[] newArray(int size) {
            return new StockPref[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(stock_symbol);
        out.writeString(stock_name);
        out.writeFloat(stock_price);
        out.writeFloat(stock_percent_change);
        out.writeFloat(stock_absolut_change);
        out.writeString(stock_history);
    }

    public String getStockSymbol() {
        return stock_symbol;
    }

    public String getStockName() {
        return stock_name;
    }

    public float getStockPrice() {
        return stock_price;
    }

    public float getStockPercentChange() {
        return stock_percent_change;
    }

    public float getStockAbsolutChange() {
        return stock_absolut_change;
    }

    public String getStockHistory() {
        return stock_history;
    }
}

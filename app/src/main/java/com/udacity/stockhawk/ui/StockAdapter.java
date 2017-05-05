package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.StockPref;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Stock Adapter Class extends RecyclerView.
 *
 */
class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final Context context;
    private Cursor mCursor;
    private final StockAdapterOnClickHandler clickHandler;

    private static final int VIEW_TYPE_TODAY = 0;

    StockAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    void setmCursor(Cursor mCursor) {
        this.mCursor = mCursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {

        mCursor.moveToPosition(position);
        return mCursor.getString(Contract.Quote.POSITION_SYMBOL);
    }

    String getPerChangeAtPosition(int position) {

        mCursor.moveToPosition(position);
        return mCursor.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
    }

    String getHistoryAtPosition(int position) {

        mCursor.moveToPosition(position);
        return mCursor.getString(Contract.Quote.POSITION_HISTORY);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        boolean useLongToday;
        mCursor.moveToPosition(position);

        switch (getItemViewType(position)) {
            case VIEW_TYPE_TODAY:
                useLongToday = true;
                break;
            default:
                useLongToday = false;
        }

        holder.symbol.setText(Utility.createSymbol(mCursor.getString(Contract.Quote.POSITION_SYMBOL), useLongToday));
        holder.price.setText(Utility.createPrice(mCursor.getFloat(Contract.Quote.POSITION_PRICE), useLongToday));


        float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = Utility.createAbsolute(rawAbsoluteChange, useLongToday);
        String percentage = Utility.createPercent(percentageChange, useLongToday);

        if (PrefUtils.getDisplayMode(context)
                .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            holder.change.setText(change);
        } else {
            holder.change.setText(percentage);
        }


    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }
        return count;
    }


    interface StockAdapterOnClickHandler {
        void onClick(StockPref stock);
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.change)
        TextView change;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            StockPref stock = createStockFromCursor(mCursor);
            clickHandler.onClick(stock);

        }

        private StockPref createStockFromCursor(Cursor mCursor) {
            return new StockPref(mCursor);
        }
    }

    /**
     * Method to swap the mCursor
     *
     * @param newCursor the new mCursor to use as CardAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}

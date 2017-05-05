package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.StockPref;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.utils.Utility;

/**
 * RemoteViewsService controlling the cursor being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] STOCK_COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_NAME,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_HISTORY
    };
    // these indices must match the projection
    private static final int INDEX_STOCK_ID = 0;
    private static final int INDEX_STOCK_SYMBOL = 1;
    private static final int INDEX_STOCK_NAME= 2;
    private static final int INDEX_STOCK_PRICE = 3;
    private static final int INDEX_STOCK_ABS_CHANGE = 4;
    private static final int INDEX_STOCK_PER_CHANGE = 5;
    private static final int INDEX_STOCK_HISTORY = 6;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (cursor != null) {
                    cursor.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // cursor. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Uri stockUri = Contract.Quote.URI;
                cursor = getContentResolver().query(stockUri,
                        STOCK_COLUMNS,
                        null,
                        null,
                        Contract.Quote.COLUMN_SYMBOL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                StockPref stock = new StockPref(cursor);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, "StockPref");
                }

                views.setTextViewText(R.id.widget_description, stock.getStockSymbol());
                views.setTextViewText(R.id.widget_stock_price, Utility.createPrice(stock.getStockPrice(), true));
                views.setTextViewText(R.id.widget_stock_percent_change, Utility.createPercent(stock.getStockPercentChange(), true));

                if (stock.getStockPercentChange() > 0.0){
                    views.setTextColor(R.id.widget_stock_percent_change, ContextCompat.getColor(getBaseContext(), R.color.material_green));
                }else {
                    views.setTextColor(R.id.widget_stock_percent_change, ContextCompat.getColor(getBaseContext(), R.color.material_red));
                }

                final Intent fillInIntent = new Intent();

                Uri stockUri = Contract.Quote.makeUriForStock(stock.getStockSymbol());

                fillInIntent.setData(stockUri);
                fillInIntent.putExtra("stock", stock);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_description, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position))
                    return cursor.getLong(INDEX_STOCK_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
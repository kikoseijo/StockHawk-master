package com.udacity.stockhawk.ui;

        import android.annotation.TargetApi;
        import android.content.Intent;
        import android.database.Cursor;
        import android.os.Binder;
        import android.os.Build;
        import android.widget.AdapterView;
        import android.widget.RemoteViews;
        import android.widget.RemoteViewsService;

        import com.udacity.stockhawk.R;
        import com.udacity.stockhawk.data.Contract;

/**
 * RemoteViewsService controlling the data being shown in the scrollable stock detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                String[] projection = {Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE, Contract.Quote.COLUMN_PERCENTAGE_CHANGE};
                data = getContentResolver().query(Contract.Quote.URI,
                        projection,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);

                System.out.println("onDataSetChanged, data from db: " + data.toString());
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                data.moveToPosition(position);
                String symbol = data.getString(1);
                String price = String.valueOf(Math.round(data.getDouble(2) * 100.0) / 100.0);
                String percentageChange = String.valueOf(Math.round(data.getDouble(3) * 100.0) / 100.0);



                views.setTextViewText(R.id.widget_symbol, symbol);
                views.setTextViewText(R.id.widget_price, price);
                views.setTextViewText(R.id.widget_change, (percentageChange + "%"));

                Intent historyIntent = new Intent(getApplicationContext(), HistoryActivity.class);
                historyIntent.putExtra("symbol", symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, historyIntent);
                return views;
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
                if (data.moveToPosition(position))
                    return data.getInt(0);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
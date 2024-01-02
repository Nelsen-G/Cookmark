package com.example.cookmark_app.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.cookmark_app.R;
import com.example.cookmark_app.model.MealPlan;

import java.util.ArrayList;
import java.util.List;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(getApplicationContext(),intent);
    }

    class ListRemoteViewsFactory implements RemoteViewsFactory{

        private List<MealPlan> mWidgetItems = new ArrayList<>();
        private Context mContext;
        private int mAppWidgetId;

        public ListRemoteViewsFactory(Context context,Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // Initialize the data set.
        @Override
        public void onCreate() {

            mWidgetItems.add(new MealPlan("1", null, null, "Garlic Bread"));
            mWidgetItems.add(new MealPlan("1", null, null, "Tomato Stirred Egg"));
            mWidgetItems.add(new MealPlan("1", null, null, "Strawberry Cake"));
            mWidgetItems.add(new MealPlan("1", null, null, "Beef Broccoli"));
            mWidgetItems.add(new MealPlan("1", null, null, "Fried Calamari"));

        }



        @Override
        public void onDestroy() {
            // In onDestroy() you should tear down anything that was setup for your data source,
            // eg. cursors, connections, etc.
            mWidgetItems.clear();
        }

        @Override
        public int getCount() {
            return 5;
        }


        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            rv.setTextViewText(R.id.widget_item, mWidgetItems.get(position).getRecipeid());

            // Return the remote views object.
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDataSetChanged() {

        }

    }

}


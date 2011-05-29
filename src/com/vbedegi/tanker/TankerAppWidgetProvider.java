package com.vbedegi.tanker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.util.Date;

public class TankerAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        int elapsedDays = getElapsedDays(context);

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.widget_text, Integer.toString(elapsedDays));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getElapsedDays(Context context) {
        try {
            Date lastDate = new DatabaseHelper(context).getLastDate();
            return DateUtils.getElapsedDays(lastDate);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return 0;
    }
}

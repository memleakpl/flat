package pl.maxmati.tobiasz.flat.bread.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import pl.maxmati.tobiasz.flat.activity.APIAuthActivity;
import pl.maxmati.tobiasz.flat.activity.LoginTask;
import pl.maxmati.tobiasz.flat.api.APIConnector;
import pl.maxmati.tobiasz.flat.api.session.SessionException;
import pl.maxmati.tobiasz.flat.api.session.SessionManager;
import pl.maxmati.tobiasz.flat.api.user.UserManager;

/**
 * Created by mmos on 11.02.16.
 *
 * @author mmos
 */
public class BreadWidgetConfigure extends APIAuthActivity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected AsyncTask<Void, Void, Boolean> getLoginTask(final String username, final String password) {
        /**
         * Represents an asynchronous login/registration task used to authenticate
         * the user.
         */
        return new LoginTask(BreadWidgetConfigure.this, username, password) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Boolean loginSuccess = super.doInBackground(voids);
                if(loginSuccess == null || !loginSuccess)
                    return false;

                BreadWidget.updateCounter(BreadWidgetConfigure.this);
                BreadWidget.subscribeCounterUpdates(BreadWidgetConfigure.this);
                try {
                    UserManager.storeUsers(BreadWidgetConfigure.this, UserManager.get(new
                            APIConnector(BreadWidgetConfigure.this, session)));
                } catch (SessionException e) {
                    Log.e(TAG, "User store failed: " + e.getMessage());
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if(success == null)
                    return;

                super.onPostExecute(success);

                if(!success)
                    return;

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

                setResult(RESULT_OK, resultValue);
                finish();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Widget configuration activity created");

        Bundle extras;

        extras = getIntent().getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // FIXME: make this activity blank, start another from here
        if(SessionManager.hasSessionInStore(this)) {
            Log.d(TAG, "Session already taken, leaving configuration activity");
            setResult(RESULT_OK);
            finish();
        }

        super.onCreate(savedInstanceState);
    }
}
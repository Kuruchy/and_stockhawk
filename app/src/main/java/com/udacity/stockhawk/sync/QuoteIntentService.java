package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import timber.log.Timber;

/**
 * Quote Intent Service Class.
 *
 */
public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        QuoteSyncJob.getQuotes(getApplicationContext());
    }
}

package ogren.collin.inventorytracker.aws.services;

import android.app.Activity;

public class LoadingCallback<T> implements ServiceCallback<T> {
    private final Activity activity;
    private final ServiceCallback<T> delegate;

    public LoadingCallback(Activity activity, ServiceCallback<T> delegate) {
        this.activity = activity;
        this.delegate = delegate;
        LoadingUtils.show(activity);
    }

    @Override
    public void onSuccess(T result) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.runOnUiThread(() -> {
            LoadingUtils.hide(activity);

            if (delegate != null) {
                delegate.onSuccess(result);
            }
        });
    }

    @Override
    public void onError(Exception e) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.runOnUiThread(() -> {
            LoadingUtils.hide(activity);

            if (delegate != null) {
                delegate.onError(e);
            }
        });
    }
}

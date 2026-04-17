package ogren.collin.inventorytracker.aws.services;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ogren.collin.inventorytracker.R;

public class LoadingUtils {

    public static void show(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) {
            return;
        }

        View overlay = root.findViewById(R.id.loadingOverlay);

        if (overlay == null) {
            overlay = LayoutInflater.from(activity).inflate(R.layout.loading_overlay, root, false);
            root.addView(overlay);
        }

        overlay.setVisibility(View.VISIBLE);
        overlay.bringToFront();
    }

    public static void hide(Activity activity) {
        if (activity == null) {
            return;
        }

        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) {
            return;
        }

        View overlay = root.findViewById(R.id.loadingOverlay);
        if (overlay != null) {
            overlay.setVisibility(View.GONE);
        }
    }
}

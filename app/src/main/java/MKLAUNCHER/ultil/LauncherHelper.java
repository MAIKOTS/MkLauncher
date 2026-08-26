package MKLAUNCHER.ultil;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class LauncherHelper {

    public static void launchInWindow(Context ctx, String packageName) {
        Intent launchIntent = ctx.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                // Margens e cálculo de proporção
                int left = (int) (width * 0.2);
                int top = (int) (height * 0.15);
                int right = (int) (width * 0.8);
                int bottom = (int) (height * 0.85);

                ActivityOptions options = ActivityOptions.makeBasic();
                options.setLaunchBounds(new Rect(left, top, right, bottom));

                ctx.startActivity(launchIntent, options.toBundle());
            } catch (Exception e) {
                ctx.startActivity(launchIntent);
            }
        }
    }
}

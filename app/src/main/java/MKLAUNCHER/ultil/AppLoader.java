package MKLAUNCHER.ultil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import MKLAUNCHER.AppModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppLoader {
    public static List<AppModel> loadInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> ril = pm.queryIntentActivities(mainIntent, 0);
        List<AppModel> apps = new ArrayList<>();

        for (ResolveInfo ri : ril) {
            if (!ri.activityInfo.packageName.equals(context.getPackageName())) {
                apps.add(new AppModel(ri.loadLabel(pm).toString(), ri.activityInfo.packageName, ri.loadIcon(pm)));
            }
        }
        Collections.sort(apps, (a, b) -> a.label.compareToIgnoreCase(b.label));
        return apps;
    }
}

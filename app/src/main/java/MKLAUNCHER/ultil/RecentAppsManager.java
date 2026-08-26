package MKLAUNCHER.ultil;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecentAppsManager {
    private static final String PREFS = "launcher_prefs";
    private static final String KEY = "recent_apps";

    public static void saveRecent(Context context, String packageName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String recents = prefs.getString(KEY, "");
        List<String> list = new ArrayList<>(Arrays.asList(recents.split(",")));
        list.remove(packageName);
        list.add(0, packageName);
        if (list.size() > 10) list = list.subList(0, 10);
        
        StringBuilder builder = new StringBuilder();
        for (String pkg : list) if (!pkg.isEmpty()) builder.append(pkg).append(",");
        prefs.edit().putString(KEY, builder.toString()).apply();
    }

    public static List<String> getRecents(Context context) {
        String recents = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "");
        return recents.isEmpty() ? new ArrayList<>() : Arrays.asList(recents.split(","));
    }
}

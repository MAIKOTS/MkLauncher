package MKLAUNCHER;

import android.graphics.drawable.Drawable;

public class AppModel {
    public String label;
    public String packageName;
    public Drawable icon;

    public AppModel(String label, String packageName, Drawable icon) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
    }
}

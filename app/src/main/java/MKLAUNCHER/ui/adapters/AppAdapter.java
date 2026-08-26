package MKLAUNCHER.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import MKLAUNCHER.AppModel;
import java.util.List;

public class AppAdapter extends ArrayAdapter<AppModel> {
    public AppAdapter(Context context, List<AppModel> apps) {
        super(context, 0, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppModel app = getItem(position);
        if (convertView == null) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.setPadding(32, 24, 32, 24);

            ImageView icon = new ImageView(getContext());
            int size = (int) (48 * getContext().getResources().getDisplayMetrics().density);
            icon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            
            TextView text = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.setMargins(32, 0, 0, 0);
            text.setLayoutParams(params);
            text.setTextColor(Color.WHITE);
            text.setTextSize(16f);

            layout.addView(icon);
            layout.addView(text);
            convertView = layout;
        }
        ((ImageView) ((LinearLayout) convertView).getChildAt(0)).setImageDrawable(app.icon);
        ((TextView) ((LinearLayout) convertView).getChildAt(1)).setText(app.label);
        return convertView;
    }
}

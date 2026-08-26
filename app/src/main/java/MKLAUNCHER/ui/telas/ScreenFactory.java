package MKLAUNCHER.ui.telas;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import MKLAUNCHER.AppModel;
import java.util.List;

public class ScreenFactory {
    
    public static View createAppsView(Context ctx, List<AppModel> apps, AdapterView.OnItemClickListener listener) {
        // Agora utiliza a nova AppsPage que criamos
        return AppsPage.create(ctx);
    }

    public static View createWidgetsView(Context ctx) {
        return WidgetsPage.create(ctx);
    }
}

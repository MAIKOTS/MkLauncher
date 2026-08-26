package MKLAUNCHER.ui.telas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TelaConfigs {

    public static View create(final Context ctx, final Runnable onOpenPermissions) {
        LinearLayout mainLayout = new LinearLayout(ctx);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#121212"));
        mainLayout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(ctx);
        title.setText("Configurações");
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setPadding(0, 0, 0, 40);
        mainLayout.addView(title);

        // Opção 1: Mudar Papel de Parede
        mainLayout.addView(createOption(ctx, "🖼️ Mudar Papel de Parede", v -> {
            AppsPage.openGalleryForWallpaper(ctx);
        }));

        // Opção 2: Gerenciar Permissões
        mainLayout.addView(createOption(ctx, "🛡️ Gerenciar Permissões", v -> {
            if (onOpenPermissions != null) {
                onOpenPermissions.run();
            }
        }));

        // Opção 3: Configurações do Sistema
        mainLayout.addView(createOption(ctx, "⚙️ Configurações do Sistema", v -> {
            ctx.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }));

        return mainLayout;
    }

    private static View createOption(Context ctx, String text, View.OnClickListener listener) {
        TextView option = new TextView(ctx);
        option.setText(text);
        option.setTextSize(17);
        option.setTypeface(null, Typeface.BOLD);
        option.setTextColor(Color.WHITE);
        option.setPadding(30, 30, 30, 30);

        // Estilo visual com cantos arredondados e efeito de clique
        float density = ctx.getResources().getDisplayMetrics().density;
        int radius = (int) (16 * density);

        GradientDrawable normalBg = new GradientDrawable();
        normalBg.setColor(Color.parseColor("#222222"));
        normalBg.setCornerRadius(radius);

        GradientDrawable pressedBg = new GradientDrawable();
        pressedBg.setColor(Color.parseColor("#333333"));
        pressedBg.setCornerRadius(radius);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, pressedBg);
        states.addState(new int[]{}, normalBg);
        option.setBackground(states);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, 20);
        option.setLayoutParams(params);

        option.setOnClickListener(listener);
        return option;
    }
}

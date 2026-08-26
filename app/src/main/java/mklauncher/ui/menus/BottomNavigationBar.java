package mklauncher.ui.menus;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import mklauncher.ui.icones.IconeApps;
import mklauncher.ui.icones.IconeConfiguracoes;
import mklauncher.ui.icones.IconeRecentes;
import mklauncher.ui.icones.IconeShortcuts;

public class BottomNavigationBar {

    private static final int COLOR_BG = Color.parseColor("#121212");
    private static final int COLOR_SELECTED = Color.parseColor("#222222");
    private static final int COLOR_DIVIDER = Color.parseColor("#2A2A2A");

    // changed from static to instance field
    private ImageView selectedBtn = null;

    public BottomNavigationBar() {
    }

    public View create(Context ctx, 
                              final View.OnClickListener onApps, 
                              final View.OnClickListener onShortcuts, 
                              final View.OnClickListener onRecents,
                              final View.OnClickListener onSettings) {
                               
        LinearLayout bar = new LinearLayout(ctx);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(COLOR_BG);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        float density = ctx.getResources().getDisplayMetrics().density;
        int paddingVert = (int) (10 * density);
        bar.setPadding(0, paddingVert, 0, paddingVert);

        final ImageView btnApps = createButton(ctx, new IconeApps(), density);
        final ImageView btnShortcuts = createButton(ctx, new IconeShortcuts(), density); 
        final ImageView btnRecents = createButton(ctx, new IconeRecentes(), density);
        final ImageView btnSettings = createButton(ctx, new IconeConfiguracoes(), density);

        // Reset da seleção para evitar lixo de memória
        selectedBtn = null;
        highlightButton(btnApps);

        btnApps.setOnClickListener(v -> {
            if (selectedBtn != btnApps) {
                highlightButton(btnApps);
                if (onApps != null) onApps.onClick(v);
            }
        });

        btnShortcuts.setOnClickListener(v -> {
            if (selectedBtn != btnShortcuts) {
                highlightButton(btnShortcuts);
                if (onShortcuts != null) onShortcuts.onClick(v);
            }
        });

        btnRecents.setOnClickListener(v -> {
            if (selectedBtn != btnRecents) {
                highlightButton(btnRecents);
                if (onRecents != null) onRecents.onClick(v);
            }
        });

        btnSettings.setOnClickListener(v -> {
            if (selectedBtn != btnSettings) {
                highlightButton(btnSettings);
                if (onSettings != null) onSettings.onClick(v);
            }
        });

        // Montagem das 4 abas
        bar.addView(btnApps);
        bar.addView(createDivider(ctx, density));
        bar.addView(btnShortcuts);
        bar.addView(createDivider(ctx, density));
        bar.addView(btnRecents);
        bar.addView(createDivider(ctx, density));
        bar.addView(btnSettings);

        return bar;
    }

    private ImageView createButton(Context ctx, Drawable icon, float density) {
        ImageView btn = new ImageView(ctx);
        btn.setImageDrawable(icon);
        btn.setScaleType(ScaleType.FIT_CENTER);
        
        int buttonHeight = (int) (44 * density);
        int paddingHoriz = (int) (16 * density);
        int paddingTopBottom = (int) (8 * density);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, buttonHeight, 1.0f);
        btn.setLayoutParams(params);
        btn.setPadding(paddingHoriz, paddingTopBottom, paddingHoriz, paddingTopBottom);
        
        return btn;
    }

    private View createDivider(Context ctx, float density) {
        View divider = new View(ctx);
        divider.setBackgroundColor(COLOR_DIVIDER);
        
        int width = (int) (1 * density);
        int height = (int) (20 * density);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER_VERTICAL;
        divider.setLayoutParams(params);
        
        return divider;
    }

    private void highlightButton(ImageView btn) {
        if (selectedBtn != null) {
            selectedBtn.setBackgroundColor(Color.TRANSPARENT);
        }
        selectedBtn = btn;
        selectedBtn.setBackgroundColor(COLOR_SELECTED);
    }
}

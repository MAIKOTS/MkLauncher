package MKLAUNCHER.ui.popup;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import MKLAUNCHER.AppModel;
import MKLAUNCHER.ui.widgets.SplitScreenManager;

public class AppOptionsPopup {

    public static void show(Context ctx, AppModel app) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#EE121212"));
        bg.setCornerRadius(32);
        bg.setStroke(2, Color.parseColor("#44FFFFFF"));
        root.setBackground(bg);

        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, 24);

        ImageView iconView = new ImageView(ctx);
        int iconSize = (int) (48 * ctx.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMargins(0, 0, 16, 0);
        iconView.setLayoutParams(iconParams);
        iconView.setImageDrawable(app.icon);

        LinearLayout titleLayout = new LinearLayout(ctx);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));

        TextView tvTitle = new TextView(ctx);
        tvTitle.setText(app.label);
        tvTitle.setTextSize(18);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvPackage = new TextView(ctx);
        tvPackage.setText(app.packageName);
        tvPackage.setTextSize(11);
        tvPackage.setTextColor(Color.parseColor("#AAAAAAAA"));

        titleLayout.addView(tvTitle);
        titleLayout.addView(tvPackage);

        header.addView(iconView);
        header.addView(titleLayout);

        root.addView(header);

        AlertDialog dialog = builder.setView(root).create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        root.addView(createOptionButton(ctx, "📱   Abrir em Tela Dividida", () -> {
            dialog.dismiss();
            SplitScreenManager.selectForSplit(ctx, app.packageName, app.label);
        }));

        root.addView(createOptionButton(ctx, "ℹ️   Informações do App", () -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + app.packageName));
            ctx.startActivity(intent);
            dialog.dismiss();
        }));

        root.addView(createOptionButton(ctx, "📋   Copiar Nome do Pacote", () -> {
            ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Package", app.packageName);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ctx, "Pacote copiado!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }));

        root.addView(createOptionButton(ctx, "🗑️   Desinstalar", () -> {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + app.packageName));
            ctx.startActivity(intent);
            dialog.dismiss();
        }));

        dialog.show();
    }

    private static TextView createOptionButton(Context ctx, String text, Runnable onClick) {
        TextView btn = new TextView(ctx);
        btn.setText(text);
        btn.setTextSize(14);
        btn.setTextColor(Color.WHITE);
        btn.setPadding(24, 20, 24, 20);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 8, 0, 8);
        btn.setLayoutParams(params);

        GradientDrawable normalBg = new GradientDrawable();
        normalBg.setColor(Color.parseColor("#22FFFFFF"));
        normalBg.setCornerRadius(16);

        GradientDrawable pressedBg = new GradientDrawable();
        pressedBg.setColor(Color.parseColor("#55FFFFFF"));
        pressedBg.setCornerRadius(16);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, pressedBg);
        states.addState(new int[]{}, normalBg);

        btn.setBackground(states);
        btn.setOnClickListener(v -> onClick.run());

        return btn;
    }
}
 
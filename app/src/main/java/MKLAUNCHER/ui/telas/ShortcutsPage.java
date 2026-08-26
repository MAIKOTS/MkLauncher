package MKLAUNCHER.ui.telas;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShortcutsPage {

    private static final String PREFS_NAME = "MKLauncher_ActivityShortcuts";
    private static final String KEY_SHORTCUTS = "saved_activity_shortcuts";

    private static View cachedView;
    private static LinearLayout shortcutsContainer;
    private static TextView tvCounter;

    public static View create(final Context ctx) {
        if (cachedView != null) {
            return cachedView;
        }

        ScrollView scrollView = new ScrollView(ctx);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        scrollView.setBackgroundColor(Color.parseColor("#121212"));

        LinearLayout mainLayout = new LinearLayout(ctx);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 32, 24, 32);

        // --- Cabeçalho ---
        TextView tvTitle = new TextView(ctx);
        tvTitle.setText("Atalhos de Activities");
        tvTitle.setTextSize(22);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.WHITE);

        tvCounter = new TextView(ctx);
        tvCounter.setText("0 atalhos configurados");
        tvCounter.setTextSize(13);
        tvCounter.setTextColor(Color.parseColor("#AAAAAA"));
        tvCounter.setPadding(0, 4, 0, 24);

        // --- Botão de Criar Atalho ---
        Button btnAddShortcut = new Button(ctx);
        btnAddShortcut.setText("+ Criar Atalho de Activity");
        btnAddShortcut.setTextColor(Color.WHITE);
        btnAddShortcut.setTextSize(14);
        btnAddShortcut.setTypeface(null, Typeface.BOLD);
        
        // Estilo do Botão Arredondado
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(Color.parseColor("#1E88E5"));
        btnBg.setCornerRadius(16);
        btnAddShortcut.setBackground(btnBg);

        btnAddShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppPickerForActivityDialog(ctx);
            }
        });

        // --- Container para a lista de atalhos ---
        shortcutsContainer = new LinearLayout(ctx);
        shortcutsContainer.setOrientation(LinearLayout.VERTICAL);
        shortcutsContainer.setPadding(0, 24, 0, 0);

        mainLayout.addView(tvTitle);
        mainLayout.addView(tvCounter);
        mainLayout.addView(btnAddShortcut);
        mainLayout.addView(shortcutsContainer);

        scrollView.addView(mainLayout);

        loadSavedShortcuts(ctx);

        cachedView = scrollView;
        return cachedView;
    }

    private static void updateCounter() {
        if (tvCounter != null && shortcutsContainer != null) {
            int count = shortcutsContainer.getChildCount();
            tvCounter.setText(count + (count == 1 ? " atalho configurado" : " atalhos configurados"));
        }
    }

    // Passo 1: Selecionar o App
    private static void showAppPickerForActivityDialog(final Context ctx) {
        final PackageManager pm = ctx.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

        ListView listView = new ListView(ctx);
        listView.setPadding(16, 16, 16, 16);

        final AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setTitle("Selecione o Aplicativo")
                .setView(listView)
                .setNegativeButton("Cancelar", null)
                .create();

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() { return apps.size(); }
            @Override
            public Object getItem(int pos) { return apps.get(pos); }
            @Override
            public long getItemId(int pos) { return pos; }

            @Override
            public View getView(int pos, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(ctx);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setGravity(Gravity.CENTER_VERTICAL);
                layout.setPadding(16, 20, 16, 20);

                ImageView icon = new ImageView(ctx);
                int size = (int) (40 * ctx.getResources().getDisplayMetrics().density);
                icon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                icon.setImageDrawable(apps.get(pos).loadIcon(pm));

                TextView text = new TextView(ctx);
                text.setText(apps.get(pos).loadLabel(pm));
                text.setTextColor(Color.WHITE);
                text.setTextSize(16);
                text.setPadding(24, 0, 0, 0);

                layout.addView(icon);
                layout.addView(text);
                return layout;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                String pkg = apps.get(position).activityInfo.packageName;
                showActivitiesDialog(ctx, pkg);
            }
        });

        dialog.show();
    }

    // Passo 2: Listar todas as Activities exportadas
    private static void showActivitiesDialog(final Context ctx, final String pkg) {
        final PackageManager pm = ctx.getPackageManager();
        final List<ActivityInfo> activitiesList = new ArrayList<>();

        try {
            PackageInfo info = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            if (info.activities != null) {
                for (ActivityInfo activity : info.activities) {
                    if (activity.exported) {
                        activitiesList.add(activity);
                    }
                }
            }
        } catch (Exception ignored) {}

        if (activitiesList.isEmpty()) {
            new AlertDialog.Builder(ctx)
                    .setTitle("Aviso")
                    .setMessage("Nenhuma Activity pública acessível foi encontrada neste app.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        ListView listView = new ListView(ctx);
        listView.setPadding(16, 16, 16, 16);

        final AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setTitle("Selecione a Activity")
                .setView(listView)
                .setNegativeButton("Cancelar", null)
                .create();

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() { return activitiesList.size(); }
            @Override
            public Object getItem(int pos) { return activitiesList.get(pos); }
            @Override
            public long getItemId(int pos) { return pos; }

            @Override
            public View getView(int pos, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(ctx);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(16, 16, 16, 16);

                ActivityInfo ai = activitiesList.get(pos);
                CharSequence label = ai.loadLabel(pm);
                String name = (label != null && !label.toString().isEmpty()) ? label.toString() : ai.name;

                TextView tvName = new TextView(ctx);
                tvName.setText(name);
                tvName.setTextSize(15);
                tvName.setTypeface(null, Typeface.BOLD);
                tvName.setTextColor(Color.WHITE);

                TextView tvClass = new TextView(ctx);
                tvClass.setText(ai.name);
                tvClass.setTextSize(12);
                tvClass.setTextColor(Color.parseColor("#80DEEA")); // Destaque Cyan
                tvClass.setPadding(0, 4, 0, 0);

                layout.addView(tvName);
                layout.addView(tvClass);
                return layout;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityInfo ai = activitiesList.get(position);
                CharSequence label = ai.loadLabel(pm);
                String name = (label != null && !label.toString().isEmpty()) ? label.toString() : ai.name;

                renderShortcut(ctx, pkg, ai.name, name);
                saveShortcut(ctx, pkg, ai.name, name);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Renderiza o atalho na lista em formato de Cartão
    private static void renderShortcut(final Context ctx, final String pkg, final String activityClass, final String label) {
        final PackageManager pm = ctx.getPackageManager();

        // Card Container
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(24, 20, 24, 20);

        // Fundo estilo Card Escuro
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.parseColor("#1E1E1E"));
        cardBg.setCornerRadius(12);
        cardBg.setStroke(2, Color.parseColor("#2C2C2C"));
        card.setBackground(cardBg);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);

        // Ícone
        int iconSize = (int) (42 * ctx.getResources().getDisplayMetrics().density);
        ImageView iconView = new ImageView(ctx);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMargins(0, 0, 20, 0);
        iconView.setLayoutParams(iconParams);

        try {
            Drawable icon = pm.getActivityIcon(new ComponentName(pkg, activityClass));
            iconView.setImageDrawable(icon);
        } catch (Exception e) {
            try {
                iconView.setImageDrawable(pm.getApplicationIcon(pkg));
            } catch (Exception ignored) {}
        }

        // Informações Textuais
        LinearLayout textLayout = new LinearLayout(ctx);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));

        TextView tvLabel = new TextView(ctx);
        tvLabel.setText(label);
        tvLabel.setTextSize(15);
        tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setTextColor(Color.WHITE);

        TextView tvClass = new TextView(ctx);
        tvClass.setText(activityClass);
        tvClass.setTextSize(11);
        tvClass.setTypeface(Typeface.MONOSPACE, Typeface.BOLD); // Fonte estilo código/caminho
        tvClass.setTextColor(Color.parseColor("#80DEEA"));      // Cor visível e destacada
        tvClass.setPadding(0, 6, 0, 0);

        textLayout.addView(tvLabel);
        textLayout.addView(tvClass);

        // Botão de Excluir Rápido (X)
        TextView btnDelete = new TextView(ctx);
        btnDelete.setText("✕");
        btnDelete.setTextSize(16);
        btnDelete.setTextColor(Color.parseColor("#FF5252"));
        btnDelete.setPadding(16, 8, 8, 8);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortcutsContainer.removeView(card);
                removeShortcut(ctx, pkg, activityClass);
                updateCounter();
            }
        });

        card.addView(iconView);
        card.addView(textLayout);
        card.addView(btnDelete);

        // Clique para Executar a Activity
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(pkg, activityClass));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                } catch (Exception e) {
                    new AlertDialog.Builder(ctx)
                            .setTitle("Erro")
                            .setMessage("Não foi possível iniciar esta Activity.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        shortcutsContainer.addView(card);
        updateCounter();
    }

    private static void saveShortcut(Context ctx, String pkg, String activityClass, String label) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_SHORTCUTS, new HashSet<String>()));
        set.add(pkg + "|" + activityClass + "|" + label);
        prefs.edit().putStringSet(KEY_SHORTCUTS, set).apply();
    }

    private static void removeShortcut(Context ctx, String pkg, String activityClass) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_SHORTCUTS, new HashSet<String>()));

        Set<String> newSet = new HashSet<>();
        for (String item : set) {
            if (!item.startsWith(pkg + "|" + activityClass + "|")) {
                newSet.add(item);
            }
        }
        prefs.edit().putStringSet(KEY_SHORTCUTS, newSet).apply();
    }

    private static void loadSavedShortcuts(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_SHORTCUTS, new HashSet<String>());

        for (String item : set) {
            String[] parts = item.split("\\|");
            if (parts.length >= 3) {
                renderShortcut(ctx, parts[0], parts[1], parts[2]);
            }
        }
    }

    public static void clearCache() {
        cachedView = null;
    }
}

package MKLAUNCHER.ui.telas;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Importação da classe global AppModel e do Popup de Opções
import MKLAUNCHER.AppModel;
import MKLAUNCHER.ui.popup.AppOptionsPopup;

public class AppsPage {

    public static final int REQUEST_CODE_WALLPAPER = 1001;

    private static View cachedView;
    private static List<AppModel> cachedAppsList;
    private static AppsAdapter adapter;

    public static View create(final Context ctx) {
        if (cachedView != null) {
            return cachedView;
        }

        LinearLayout mainLayout = new LinearLayout(ctx);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));

        applyWallpaper(ctx, mainLayout);

        if (cachedAppsList == null) {
            cachedAppsList = loadInstalledApps(ctx);
        }

        // --- TOP BAR COM BUSCA EMBUTIDA ---
        LinearLayout topBar = new LinearLayout(ctx);
        topBar.setOrientation(LinearLayout.VERTICAL);
        topBar.setPadding(24, 24, 24, 16);

        GradientDrawable topBarBg = new GradientDrawable();
        topBarBg.setColor(Color.parseColor("#33000000"));
        topBarBg.setStroke(1, Color.parseColor("#22FFFFFF"));
        topBar.setBackground(topBarBg);

        LinearLayout headerRow = new LinearLayout(ctx);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout infoLayout = new LinearLayout(ctx);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));

        TextView tvTitle = new TextView(ctx);
        tvTitle.setText("Aplicativos");
        tvTitle.setTextSize(20);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvCount = new TextView(ctx);
        tvCount.setText(cachedAppsList.size() + " apps instalados");
        tvCount.setTextSize(12);
        tvCount.setTextColor(Color.parseColor("#B0B0B0"));

        infoLayout.addView(tvTitle);
        infoLayout.addView(tvCount);

        // Botão Recarregar
        TextView btnRefresh = new TextView(ctx);
        btnRefresh.setText("🔄");
        btnRefresh.setTextSize(16);
        btnRefresh.setGravity(Gravity.CENTER);
        btnRefresh.setPadding(20, 12, 20, 12);
        btnRefresh.setBackground(createButtonDrawable());
        btnRefresh.setOnClickListener(v -> {
            invalidateCache();
            if (ctx instanceof Activity) {
                ((Activity) ctx).recreate();
            }
        });

        headerRow.addView(infoLayout);
        headerRow.addView(btnRefresh);

        // Campo de Pesquisa Integrado
        EditText etSearch = new EditText(ctx);
        etSearch.setHint("🔍 Buscar aplicativos...");
        etSearch.setHintTextColor(Color.parseColor("#88FFFFFF"));
        etSearch.setTextColor(Color.WHITE);
        etSearch.setTextSize(14);
        etSearch.setPadding(28, 18, 28, 18);
        etSearch.setSingleLine(true);

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(Color.parseColor("#22FFFFFF"));
        searchBg.setCornerRadius(24);
        searchBg.setStroke(1, Color.parseColor("#33FFFFFF"));
        etSearch.setBackground(searchBg);

        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, -2);
        searchParams.setMargins(0, 16, 0, 0);
        etSearch.setLayoutParams(searchParams);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        topBar.addView(headerRow);
        topBar.addView(etSearch);

        // --- GRID VIEW DE APPS ---
        GridView gridView = new GridView(ctx);
        gridView.setNumColumns(2);

        float density = ctx.getResources().getDisplayMetrics().density;
        int spacing = (int) (12 * density);
        int padding = (int) (16 * density);

        gridView.setHorizontalSpacing(spacing);
        gridView.setVerticalSpacing(spacing);
        gridView.setPadding(padding, padding, padding, padding);
        gridView.setClipToPadding(false);

        adapter = new AppsAdapter(ctx, cachedAppsList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            AppModel app = adapter.getItem(position);
            Intent launchIntent = ctx.getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                try {
                    DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

                    int left = (int) (width * 0.1);
                    int top = (int) (height * 0.1);
                    int right = (int) (width * 0.9);
                    int bottom = (int) (height * 0.9);

                    ActivityOptions options = ActivityOptions.makeBasic();
                    options.setLaunchBounds(new Rect(left, top, right, bottom));

                    ctx.startActivity(launchIntent, options.toBundle());
                } catch (Exception e) {
                    ctx.startActivity(launchIntent);
                }
            }
        });

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            AppModel app = adapter.getItem(position);
            AppOptionsPopup.show(ctx, app);
            return true;
        });

        mainLayout.addView(topBar);
        mainLayout.addView(gridView);

        cachedView = mainLayout;
        return cachedView;
    }

    private static Drawable createButtonDrawable() {
        GradientDrawable normalBg = new GradientDrawable();
        normalBg.setColor(Color.parseColor("#22FFFFFF"));
        normalBg.setCornerRadius(20);
        normalBg.setStroke(1, Color.parseColor("#4DFFFFFF"));

        GradientDrawable pressedBg = new GradientDrawable();
        pressedBg.setColor(Color.parseColor("#55FFFFFF"));
        pressedBg.setCornerRadius(20);
        pressedBg.setStroke(1, Color.parseColor("#80FFFFFF"));

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, pressedBg);
        states.addState(new int[]{android.R.attr.state_focused}, pressedBg);
        states.addState(new int[]{}, normalBg);

        return states;
    }

    public static void openGalleryForWallpaper(Context ctx) {
        if (ctx instanceof Activity) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((Activity) ctx).startActivityForResult(intent, REQUEST_CODE_WALLPAPER);
        } else {
            Toast.makeText(ctx, "Não foi possível abrir a galeria.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void handleWallpaperResult(Context ctx, Intent data) {
        if (data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                InputStream inputStream = ctx.getContentResolver().openInputStream(imageUri);
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(ctx);
                wallpaperManager.setStream(inputStream);

                Toast.makeText(ctx, "Papel de parede atualizado!", Toast.LENGTH_SHORT).show();
                invalidateCache();

                if (ctx instanceof Activity) {
                    ((Activity) ctx).recreate();
                }
            } catch (Exception e) {
                Toast.makeText(ctx, "Erro ao alterar papel de parede.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void applyWallpaper(Context ctx, View view) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(ctx);
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            if (wallpaperDrawable != null) {
                view.setBackground(wallpaperDrawable);
                return;
            }
        } catch (Exception ignored) {}
        view.setBackgroundColor(Color.parseColor("#121212"));
    }

    private static List<AppModel> loadInstalledApps(Context ctx) {
        List<AppModel> apps = new ArrayList<>();
        PackageManager pm = ctx.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : allApps) {
            String label = ri.loadLabel(pm).toString();
            String packageName = ri.activityInfo.packageName;
            Drawable icon = ri.loadIcon(pm);

            apps.add(new AppModel(label, packageName, icon));
        }

        Collections.sort(apps, Comparator.comparing(a -> a.label, String.CASE_INSENSITIVE_ORDER));
        return apps;
    }

    public static void invalidateCache() {
        cachedView = null;
        cachedAppsList = null;
    }

    private static class ViewHolder {
        ImageView iconView;
        TextView labelView;
        TextView packageView;
    }

    private static class AppsAdapter extends BaseAdapter {
        private final Context context;
        private final List<AppModel> originalApps;
        private final List<AppModel> filteredApps;
        private final int iconSize;

        public AppsAdapter(Context context, List<AppModel> apps) {
            this.context = context;
            this.originalApps = apps;
            this.filteredApps = new ArrayList<>(apps);
            this.iconSize = (int) (36 * context.getResources().getDisplayMetrics().density);
        }

        public void filter(String query) {
            filteredApps.clear();
            if (TextUtils.isEmpty(query)) {
                filteredApps.addAll(originalApps);
            } else {
                String q = query.toLowerCase().trim();
                for (AppModel app : originalApps) {
                    if (app.label.toLowerCase().contains(q) || app.packageName.toLowerCase().contains(q)) {
                        filteredApps.add(app);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override public int getCount() { return filteredApps.size(); }
        @Override public AppModel getItem(int position) { return filteredApps.get(position); }
        @Override public long getItemId(int position) { return position; }

        private Drawable createGlassCardDrawable() {
            GradientDrawable normalBg = new GradientDrawable();
            normalBg.setColor(Color.parseColor("#29000000"));
            normalBg.setCornerRadius(20);
            normalBg.setStroke(1, Color.parseColor("#33FFFFFF"));

            GradientDrawable pressedBg = new GradientDrawable();
            pressedBg.setColor(Color.parseColor("#55000000"));
            pressedBg.setCornerRadius(20);
            pressedBg.setStroke(1, Color.parseColor("#66FFFFFF"));

            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{android.R.attr.state_pressed}, pressedBg);
            states.addState(new int[]{android.R.attr.state_focused}, pressedBg);
            states.addState(new int[]{}, normalBg);

            return states;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LinearLayout card = new LinearLayout(context);
                card.setOrientation(LinearLayout.HORIZONTAL);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(16, 16, 16, 16);
                card.setBackground(createGlassCardDrawable());

                holder = new ViewHolder();

                holder.iconView = new ImageView(context);
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
                iconParams.setMargins(0, 0, 12, 0);
                holder.iconView.setLayoutParams(iconParams);

                LinearLayout textLayout = new LinearLayout(context);
                textLayout.setOrientation(LinearLayout.VERTICAL);
                textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));

                holder.labelView = new TextView(context);
                holder.labelView.setTextSize(14);
                holder.labelView.setTypeface(null, Typeface.BOLD);
                holder.labelView.setTextColor(Color.WHITE);
                holder.labelView.setSingleLine(true);
                holder.labelView.setEllipsize(TextUtils.TruncateAt.END);

                holder.packageView = new TextView(context);
                holder.packageView.setTextSize(10);
                holder.packageView.setTextColor(Color.parseColor("#CCCCCC"));
                holder.packageView.setSingleLine(true);
                holder.packageView.setEllipsize(TextUtils.TruncateAt.END);
                holder.packageView.setPadding(0, 2, 0, 0);

                textLayout.addView(holder.labelView);
                textLayout.addView(holder.packageView);

                card.addView(holder.iconView);
                card.addView(textLayout);

                convertView = card;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppModel app = getItem(position);
            holder.iconView.setImageDrawable(app.icon);
            holder.labelView.setText(app.label);
            holder.packageView.setText(app.packageName);

            return convertView;
        }
    }
}

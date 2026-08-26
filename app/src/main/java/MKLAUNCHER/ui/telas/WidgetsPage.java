package MKLAUNCHER.ui.telas;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetsPage {

    public static final int REQUEST_BIND_APPWIDGET = 2001;
    public static final int REQUEST_CREATE_APPWIDGET = 2002;
    public static final int APPWIDGET_HOST_ID = 2026;
    
    private static final String PREFS_NAME = "MKLauncher_Widgets";
    private static final String KEY_WIDGET_ORDER = "saved_widget_order_list";

    private static AppWidgetHost appWidgetHost;
    private static AppWidgetManager appWidgetManager;
    private static LinearLayout widgetsContainer;
    private static View cachedView; // Cache da tela inteira
    private static int pendingAppWidgetId = -1;

    public static View create(final Context ctx) {
        // Se a tela já foi criada anteriormente, retorna a instância em cache (elimina a piscada)
        if (cachedView != null) {
            return cachedView;
        }

        if (appWidgetHost == null) {
            appWidgetManager = AppWidgetManager.getInstance(ctx);
            appWidgetHost = new AppWidgetHost(ctx, APPWIDGET_HOST_ID);
            appWidgetHost.startListening();
        }

        ScrollView scrollView = new ScrollView(ctx);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));

        widgetsContainer = new LinearLayout(ctx);
        widgetsContainer.setOrientation(LinearLayout.VERTICAL);
        widgetsContainer.setBackgroundColor(Color.BLACK);
        widgetsContainer.setPadding(16, 32, 16, 32);

        Button btnAddWidget = new Button(ctx);
        btnAddWidget.setText("+ Adicionar Widget");
        btnAddWidget.setTextColor(Color.WHITE);
        btnAddWidget.setBackgroundColor(Color.parseColor("#222222"));
        
        btnAddWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWidgetPickerDialog((Activity) ctx);
            }
        });

        widgetsContainer.addView(btnAddWidget);
        scrollView.addView(widgetsContainer);

        loadSavedWidgets(ctx);

        cachedView = scrollView; // Salva a View em cache
        return cachedView;
    }

    private static void showWidgetPickerDialog(final Activity activity) {
        List<AppWidgetProviderInfo> providers;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            providers = appWidgetManager.getInstalledProvidersForProfile(null);
        } else {
            providers = appWidgetManager.getInstalledProviders();
        }

        PackageManager pm = activity.getPackageManager();

        Map<String, List<AppWidgetProviderInfo>> grouped = new HashMap<>();
        for (AppWidgetProviderInfo info : providers) {
            String pkg = info.provider.getPackageName();
            if (!grouped.containsKey(pkg)) {
                grouped.put(pkg, new ArrayList<AppWidgetProviderInfo>());
            }
            grouped.get(pkg).add(info);
        }

        ScrollView dialogScroll = new ScrollView(activity);
        LinearLayout listLayout = new LinearLayout(activity);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        listLayout.setPadding(32, 32, 32, 32);
        dialogScroll.addView(listLayout);

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Selecione um Widget")
                .setView(dialogScroll)
                .setNegativeButton("Cancelar", null)
                .create();

        for (Map.Entry<String, List<AppWidgetProviderInfo>> entry : grouped.entrySet()) {
            String pkg = entry.getKey();
            List<AppWidgetProviderInfo> widgetList = entry.getValue();

            String tempAppName;
            try {
                tempAppName = pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                tempAppName = pkg;
            }
            final String appName = tempAppName;

            final TextView tvApp = new TextView(activity);
            tvApp.setText("▼ " + appName + " (" + widgetList.size() + ")");
            tvApp.setTextSize(16);
            tvApp.setTypeface(null, Typeface.BOLD);
            tvApp.setTextColor(Color.WHITE);
            tvApp.setPadding(16, 24, 16, 24);

            final LinearLayout subContainer = new LinearLayout(activity);
            subContainer.setOrientation(LinearLayout.VERTICAL);
            subContainer.setVisibility(View.GONE);

            for (final AppWidgetProviderInfo info : widgetList) {
                TextView tvWidget = new TextView(activity);
                CharSequence label = info.loadLabel(pm);
                tvWidget.setText("  • " + (label != null ? label : "Widget"));
                tvWidget.setTextSize(14);
                tvWidget.setTextColor(Color.LTGRAY);
                tvWidget.setPadding(32, 16, 16, 16);

                tvWidget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        selectWidgetProvider(activity, info);
                    }
                });
                subContainer.addView(tvWidget);
            }

            tvApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (subContainer.getVisibility() == View.GONE) {
                        subContainer.setVisibility(View.VISIBLE);
                        tvApp.setText("▲ " + appName + " (" + subContainer.getChildCount() + ")");
                    } else {
                        subContainer.setVisibility(View.GONE);
                        tvApp.setText("▼ " + appName + " (" + subContainer.getChildCount() + ")");
                    }
                }
            });

            listLayout.addView(tvApp);
            listLayout.addView(subContainer);
        }

        dialog.show();
    }

    private static void selectWidgetProvider(Activity activity, AppWidgetProviderInfo info) {
        pendingAppWidgetId = appWidgetHost.allocateAppWidgetId();
        boolean allowed = appWidgetManager.bindAppWidgetIdIfAllowed(pendingAppWidgetId, info.provider);

        if (allowed) {
            configureAndCreateWidget(activity, info, pendingAppWidgetId);
        } else {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingAppWidgetId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.provider);
            activity.startActivityForResult(intent, REQUEST_BIND_APPWIDGET);
        }
    }

    public static void handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_BIND_APPWIDGET) {
            int appWidgetId = data != null ? data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingAppWidgetId) : pendingAppWidgetId;
            AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(appWidgetId);
            configureAndCreateWidget(activity, info, appWidgetId);
        } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
            int appWidgetId = data != null ? data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingAppWidgetId) : pendingAppWidgetId;
            renderWidget(activity, appWidgetId);
            saveCurrentLayoutOrder(activity);
        }
    }

    private static void configureAndCreateWidget(Activity activity, AppWidgetProviderInfo info, int appWidgetId) {
        if (info != null && info.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(info.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            activity.startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            renderWidget(activity, appWidgetId);
            saveCurrentLayoutOrder(activity);
        }
    }

    private static void renderWidget(final Context ctx, final int appWidgetId) {
        AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(appWidgetId);
        if (info == null) return;

        final AppWidgetHostView hostView = appWidgetHost.createView(ctx, appWidgetId, info);
        hostView.setAppWidget(appWidgetId, info);
        hostView.setTag(appWidgetId);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 16, 0, 16);
        hostView.setLayoutParams(params);

        hostView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showWidgetOptionsMenu(ctx, hostView, appWidgetId);
                return true;
            }
        });

        if (widgetsContainer != null) {
            widgetsContainer.addView(hostView);
        }
    }

    private static void showWidgetOptionsMenu(final Context ctx, final AppWidgetHostView hostView, final int appWidgetId) {
        CharSequence[] options = new CharSequence[]{"Mover para Cima", "Mover para Baixo", "Duplicar", "Remover"};

        new AlertDialog.Builder(ctx)
                .setTitle("Opções do Widget")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int index = widgetsContainer.indexOfChild(hostView);
                        switch (which) {
                            case 0:
                                if (index > 1) {
                                    widgetsContainer.removeView(hostView);
                                    widgetsContainer.addView(hostView, index - 1);
                                    saveCurrentLayoutOrder(ctx);
                                }
                                break;
                            case 1:
                                if (index < widgetsContainer.getChildCount() - 1) {
                                    widgetsContainer.removeView(hostView);
                                    widgetsContainer.addView(hostView, index + 1);
                                    saveCurrentLayoutOrder(ctx);
                                }
                                break;
                            case 2:
                                AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(appWidgetId);
                                if (info != null) {
                                    selectWidgetProvider((Activity) ctx, info);
                                }
                                break;
                            case 3:
                                removeWidget(ctx, hostView, appWidgetId);
                                break;
                        }
                    }
                })
                .show();
    }

    private static void removeWidget(Context ctx, AppWidgetHostView hostView, int appWidgetId) {
        if (widgetsContainer != null) {
            widgetsContainer.removeView(hostView);
        }
        if (appWidgetHost != null) {
            appWidgetHost.deleteAppWidgetId(appWidgetId);
        }
        saveCurrentLayoutOrder(ctx);
    }

    private static void saveCurrentLayoutOrder(Context ctx) {
        if (widgetsContainer == null) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < widgetsContainer.getChildCount(); i++) {
            View child = widgetsContainer.getChildAt(i);
            if (child instanceof AppWidgetHostView) {
                Object tag = child.getTag();
                if (tag != null) {
                    sb.append(tag.toString()).append(",");
                }
            }
        }

        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_WIDGET_ORDER, sb.toString()).apply();
    }

    private static void loadSavedWidgets(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedOrder = prefs.getString(KEY_WIDGET_ORDER, "");

        if (!savedOrder.isEmpty()) {
            String[] ids = savedOrder.split(",");
            for (String idStr : ids) {
                try {
                    if (!idStr.trim().isEmpty()) {
                        int id = Integer.parseInt(idStr.trim());
                        renderWidget(ctx, id);
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public static void clearHost() {
        if (appWidgetHost != null) {
            appWidgetHost.stopListening();
            appWidgetHost = null;
        }
        cachedView = null; // Libera a referência se a atividade for destruída
    }
}

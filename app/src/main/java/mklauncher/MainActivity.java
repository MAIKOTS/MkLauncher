package mklauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mklauncher.ui.menus.BottomNavigationBar;
import mklauncher.ui.telas.AppsPage;
import mklauncher.ui.telas.ScreenFactory;
import mklauncher.ui.telas.ShortcutsPage;
import mklauncher.ui.telas.TelaConfigs;
import mklauncher.ui.telas.TelaRecentes;
import mklauncher.ui.telas.subtelas.configs.TelaPermissoes;
import mklauncher.ui.widgets.SplitScreenManager;
import mklauncher.util.AppLoader;
import mklauncher.util.LauncherHelper;

public class MainActivity extends Activity {

    private FrameLayout contentContainer;
    private LinearLayout loadingOverlay;
    private Handler mainHandler;
    private List<AppModel> appList = new ArrayList<>();
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        setupLayout();
        loadApps();
    }

    private void setupLayout() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.BLACK);

        FrameLayout rootContainer = new FrameLayout(this);
        rootContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));

        contentContainer = new FrameLayout(this);
        contentContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));

        loadingOverlay = createLoadingOverlay();

        rootContainer.addView(contentContainer);
        rootContainer.addView(loadingOverlay);

        mainLayout.addView(rootContainer);
        // BottomNavigationBar is now instanced
        mainLayout.addView(new BottomNavigationBar().create(this,
            v -> showApps(),
            v -> showShortcuts(),
            v -> showRecents(),
            v -> showSettings()));

        setContentView(mainLayout);
    }

    private LinearLayout createLoadingOverlay() {
        float density = getResources().getDisplayMetrics().density;
        int size = (int) (48 * density);
        int padding = (int) (12 * density);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(android.graphics.Color.parseColor("#99000000"));
        layout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(size, size);
        pbParams.gravity = Gravity.CENTER;
        pb.setLayoutParams(pbParams);
        pb.setPadding(padding, padding, padding, padding);

        android.graphics.drawable.GradientDrawable glassBg = new android.graphics.drawable.GradientDrawable();
        glassBg.setColor(android.graphics.Color.parseColor("#33FFFFFF"));
        glassBg.setCornerRadius(24 * density);
        glassBg.setStroke(1, android.graphics.Color.parseColor("#44FFFFFF"));
        pb.setBackground(glassBg);

        layout.addView(pb);
        return layout;
    }

    public void showLoading() {
        if (mainHandler != null) {
            mainHandler.post(() -> {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);
            });
        }
    }

    public void hideLoading() {
        if (mainHandler != null) {
            mainHandler.post(() -> {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
            });
        }
    }

    public void loadApps() {
        showLoading();
        executor.execute(() -> {
            List<AppModel> loaded = AppLoader.loadInstalledApps(this);
            mainHandler.post(() -> {
                appList.clear();
                appList.addAll(loaded);
                showApps();
                hideLoading();
            });
        });
    }

    private void showApps() {
        contentContainer.removeAllViews();
        contentContainer.addView(ScreenFactory.createAppsView(this, appList, (parent, v, pos, id) -> {
            AppModel selectedApp = appList.get(pos);

            if (SplitScreenManager.isWaitingSecondApp()) {
                SplitScreenManager.selectForSplit(this, selectedApp.packageName, selectedApp.label);
            } else {
                LauncherHelper.launchInWindow(this, selectedApp.packageName);
            }
        }));
    }

    private void showShortcuts() {
        contentContainer.removeAllViews();
        contentContainer.addView(ShortcutsPage.create(this));
    }

    private void showRecents() {
        contentContainer.removeAllViews();
        contentContainer.addView(TelaRecentes.create(this));
    }

    private void showSettings() {
        contentContainer.removeAllViews();
        contentContainer.addView(TelaConfigs.create(this, () -> {
            contentContainer.removeAllViews();
            contentContainer.addView(TelaPermissoes.create(this));
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppsPage.REQUEST_CODE_WALLPAPER && resultCode == RESULT_OK) {
            AppsPage.handleWallpaperResult(this, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up executor and handlers to avoid leaks
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        ShortcutsPage.clearCache();
    }
}

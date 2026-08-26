package MKLAUNCHER.ui.telas.subtelas.configs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TelaPermissoes {

    public static View create(final Context ctx) {
        ScrollView scrollView = new ScrollView(ctx);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        scrollView.setBackgroundColor(Color.parseColor("#121212"));

        LinearLayout mainLayout = new LinearLayout(ctx);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(40, 40, 40, 40);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        // Título
        TextView title = new TextView(ctx);
        title.setText("🛡️ Central de Permissões");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setPadding(0, 0, 0, 16);
        mainLayout.addView(title);

        // Descrição
        TextView description = new TextView(ctx);
        description.setText("Gerencie manualmente os acessos e permissões especiais do Launcher.");
        description.setTextSize(14);
        description.setTextColor(Color.parseColor("#AAAAAA"));
        description.setPadding(0, 0, 0, 30);
        mainLayout.addView(description);

        // 1. Definir como Launcher Padrão
        mainLayout.addView(createPermissionOption(ctx, "🏠 Definir como Launcher Padrão", 
            "Configurar este aplicativo como a tela inicial padrão do dispositivo.", v -> {
                safeStartActivity(ctx, new Intent(Settings.ACTION_HOME_SETTINGS));
            }));

        // 2. Permissão de Armazenamento Básico (Pop-up do Sistema)
        mainLayout.addView(createPermissionOption(ctx, "📦 Permissão de Armazenamento", 
            "Solicita acesso de leitura ao armazenamento do dispositivo.", v -> {
                if (ctx instanceof Activity) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((Activity) ctx).requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                    }
                } else {
                    openAppDetails(ctx);
                }
            }));

        // 3. Acesso a Todos os Arquivos (Android 11+)
        mainLayout.addView(createPermissionOption(ctx, "📂 Acesso a Todos os Arquivos", 
            "Permitir que o launcher visualize e gerencie todos os arquivos.", v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + ctx.getPackageName()));
                    if (!safeStartActivity(ctx, intent)) {
                        safeStartActivity(ctx, new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                    }
                } else {
                    openAppDetails(ctx);
                }
            }));

        // 4. Permissão de Notificações
        mainLayout.addView(createPermissionOption(ctx, "🔔 Permissão de Notificações", 
            "Permitir que o launcher exiba avisos e alertas.", v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ctx instanceof Activity) {
                    ((Activity) ctx).requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 102);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, ctx.getPackageName());
                    if (!safeStartActivity(ctx, intent)) {
                        openAppDetails(ctx);
                    }
                } else {
                    openAppDetails(ctx);
                }
            }));

        // 5. Acesso ao Conteúdo de Notificações (Badges e Contadores)
        mainLayout.addView(createPermissionOption(ctx, "🔴 Acesso às Notificações (Badges)", 
            "Permite ler contadores de mensagens nos ícones dos apps.", v -> {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                if (!safeStartActivity(ctx, intent)) {
                    safeStartActivity(ctx, new Intent(Settings.ACTION_SETTINGS));
                }
            }));

        // 6. Apps Recentes / Acesso ao Uso
        mainLayout.addView(createPermissionOption(ctx, "📊 Acesso ao Uso (Apps Recentes)", 
            "Necessário para monitorar e listar tarefas recentes.", v -> {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                if (!safeStartActivity(ctx, intent)) {
                    safeStartActivity(ctx, new Intent(Settings.ACTION_SETTINGS));
                }
            }));

        // 7. Sobreposição de Tela
        mainLayout.addView(createPermissionOption(ctx, "🪟 Sobreposição de Tela", 
            "Permitir desenhar elementos por cima de outros aplicativos.", v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + ctx.getPackageName()));
                    if (!safeStartActivity(ctx, intent)) {
                        openAppDetails(ctx);
                    }
                } else {
                    openAppDetails(ctx);
                }
            }));

        // 8. Otimização de Bateria
        mainLayout.addView(createPermissionOption(ctx, "🔋 Economia de Bateria (Sem Restrições)", 
            "Evita que o sistema encerre o launcher em segundo plano.", v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + ctx.getPackageName()));
                    if (!safeStartActivity(ctx, intent)) {
                        safeStartActivity(ctx, new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                    }
                } else {
                    safeStartActivity(ctx, new Intent(Settings.ACTION_SETTINGS));
                }
            }));

        // 9. Serviços de Acessibilidade
        mainLayout.addView(createPermissionOption(ctx, "♿ Serviço de Acessibilidade", 
            "Necessário para gestos avançados de tela.", v -> {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                safeStartActivity(ctx, intent);
            }));

        // 10. Informações Básicas do App
        mainLayout.addView(createPermissionOption(ctx, "⚙️ Detalhes e Configurações do App", 
            "Abrir página de gerenciamento do aplicativo no Android.", v -> {
                openAppDetails(ctx);
            }));

        scrollView.addView(mainLayout);
        return scrollView;
    }

    private static boolean safeStartActivity(Context ctx, Intent intent) {
        try {
            if (!(ctx instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            ctx.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(ctx, "Configuração não suportada neste dispositivo", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static void openAppDetails(Context ctx) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + ctx.getPackageName()));
        safeStartActivity(ctx, intent);
    }

    private static View createPermissionOption(Context ctx, String title, String subtitle, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(30, 24, 30, 24);

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
        card.setBackground(states);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, 20);
        card.setLayoutParams(params);

        TextView tvTitle = new TextView(ctx);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvSubtitle = new TextView(ctx);
        tvSubtitle.setText(subtitle);
        tvSubtitle.setTextSize(13);
        tvSubtitle.setTextColor(Color.parseColor("#AAAAAA"));
        tvSubtitle.setPadding(0, 6, 0, 0);

        card.addView(tvTitle);
        card.addView(tvSubtitle);

        card.setOnClickListener(listener);
        return card;
    }
}

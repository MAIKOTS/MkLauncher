package MKLAUNCHER.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

public class SplitScreenManager {

    private static String firstPackageName = null;
    private static String firstAppTitle = "";
    private static boolean isWaitingSecondApp = false;

    /**
     * Registra o app selecionado ou dispara a abertura em Tela Dividida
     */
    public static void selectForSplit(Context context, String packageName, String appTitle) {
        if (!isWaitingSecondApp) {
            // PASSO 1: Salva o primeiro aplicativo e orienta o usuário
            firstPackageName = packageName;
            firstAppTitle = appTitle;
            isWaitingSecondApp = true;

            Toast.makeText(context, "Selecione o 2º app para dividir a tela", Toast.LENGTH_LONG).show();
        } else {
            // PASSO 2: O segundo aplicativo foi clicado -> Dispara o Split Nativo
            launchSplitNative(context, firstPackageName, packageName);
            resetState();
        }
    }

    /**
     * Executa a abertura dos dois aplicativos usando as APIs do Android (Multi-Window)
     */
    private static void launchSplitNative(Context context, String pkg1, String pkg2) {
        PackageManager pm = context.getPackageManager();
        Intent intent1 = pm.getLaunchIntentForPackage(pkg1);
        Intent intent2 = pm.getLaunchIntentForPackage(pkg2);

        if (intent1 == null || intent2 == null) {
            Toast.makeText(context, "Erro ao obter dados dos aplicativos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configuração do 1º App (Task Principal)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent1);

        // Configuração do 2º App (Janela Adjacente)
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent2.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
        }

        try {
            context.startActivity(intent2);
        } catch (Exception e) {
            Toast.makeText(context, "Este dispositivo não suporta o modo janela adjacente.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void resetState() {
        firstPackageName = null;
        firstAppTitle = "";
        isWaitingSecondApp = false;
    }

    public static boolean isWaitingSecondApp() {
        return isWaitingSecondApp;
    }
}

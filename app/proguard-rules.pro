# Regras de Depuração e Dicionário
-keepattributes SourceFile,LineNumberTable

# Preserve apenas o código essencial do MK Launcher
-keep class MKLAUNCHER.** { *; }
-keepclassmembers class MKLAUNCHER.** { *; }

# Mantém a integridade do modelo de dados para o AppLoader e a listagem de apps
-keepclassmembers class MKLAUNCHER.model.** { *; }

# Otimizações Agressivas do R8
-dontnote **
-dontwarn **
-ignorewarnings

# Permite que o R8 remova métodos e classes não utilizados de bibliotecas externas
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# Regras Padrão do Projeto
-keepattributes SourceFile,LineNumberTable

# Preserve todas as classes e membros do MK Launcher
# Isso impede que o R8 apague chamadas dinâmicas e layouts programáticos
-keep class MKLAUNCHER.** { *; }
-keepclassmembers class MKLAUNCHER.** { *; }

# Mantém os modelos de dados (AppModel) para evitar falhas ao listar os apps
-keepclassmembers class MKLAUNCHER.model.** { *; }

# Mantém as bibliotecas do AndroidX e Material Design
-keep class androidx.** { *; }
-keep class com.google.android.material.** { *; }

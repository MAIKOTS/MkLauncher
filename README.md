# 🚀 MK Launcher

Um Launcher Android leve, moderno e customizável desenvolvido em Java puro nativo, focado em desempenho, design elegante (estilo Glassmorphism) e recursos avançados de multitarefa.

---

## 📱 Screenshots

<details>
<summary>📸 <b>Clique para expandir/ocultar as capturas de tela</b></summary>

<br />

| Interface Principal | Menu de Opções | Configurações / Mídia |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/04d21034-aa83-47f1-9321-2c6c2f63eb53" width="220" alt="Tela Principal" /> | <img src="https://github.com/user-attachments/assets/c90dd758-66d9-45db-9914-a585a43a740d" width="220" alt="Menu Popup" /> | <img src="https://github.com/user-attachments/assets/ff60d179-7b6a-4854-a010-875d7cdf6c2f" width="220" alt="Opções do Launcher" /> |

</details>

---

## ✨ Principais Funcionalidades

- **Gerenciamento de Aplicativos:** Listagem completa de todos os apps instalados no dispositivo com suporte a busca em tempo real.
- **Menu de Opções do App (Popup):** Menu flutuante de acesso rápido por clique longo em cima de qualquer ícone, contendo:
  - 📱 Abertura em Tela Dividida.
  - ℹ️ Atalho direto para as Informações do Aplicativo nas configurações do sistema.
  - 📋 Cópia rápida do nome do pacote para a área de transferência.
  - 🗑️ Desinstalação facilitada.
- **Personalização de Papel de parede:** Suporte para carregar e definir fundos de tela personalizados diretamente pela galeria.
- **Interface Visual Moderna:** Estilo *Glassmorphism* com efeitos translúcidos, cantos arredondados, barras de navegação inferiores customizadas e indicador de carregamento animado.
- **Tela de Atalhos, Recentes e Configurações:** Páginas modulares integradas para gerenciar atalhos e permissões de sistema.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java (Android SDK Nativo)
- **UI:** Programmatic UI Layouts (Sem arquivos XML pesados para as telas, gerando views dinâmicas otimizadas).
- **Multitarefa & Intents:** Gerenciamento avançado de pacotes e ciclos de vida de Activities.

---

## 📦 Estrutura do Projeto

```text
MKLAUNCHER/
│
├── MainActivity.java                # Activity principal e gerenciador de containers
├── AppModel.java                    # Modelo de dados estruturado para os aplicativos
│
├── ui/
│   ├── menus/
│   │   └── BottomNavigationBar.java # Barra de navegação inferior
│   ├── popup/
│   │   └── AppOptionsPopup.java     # Menu flutuante de ações do app
│   ├── telas/
│   │   ├── AppsPage.java            # Página principal de listagem e busca de apps
│   │   └── ScreenFactory.java       # Fábrica de views das páginas

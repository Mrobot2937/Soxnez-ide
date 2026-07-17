# Soxnez (MVP)

Projeto Android nativo (Kotlin + Jetpack Compose) com visual inspirado no VSCode Dark+.

## O que já funciona nesta versão
- Nome e ícone próprios: **Soxnez** (monograma "S" em curvas, azul→teal, sobre fundo escuro)
- Tema dark idêntico ao VSCode (cores, ícones, layout)
- Explorador de arquivos lateral (árvore recursiva, expandir/colapsar pastas)
- **Abrir qualquer pasta do celular** via Storage Access Framework (ícone de pasta na barra superior)
- Múltiplas abas de arquivos abertos, com indicador de "não salvo"
- Editor com numeração de linha e syntax highlighting (Python, JS, Java, Kotlin)
- **Executar código Python de verdade**, localmente no aparelho, via Chaquopy
  (botão ▶ na barra superior; a saída aparece num console na parte de baixo da tela)
- Barra de status inferior (linha/coluna/linguagem), estilo VSCode
- Botão salvar — grava no armazenamento interno do app OU de volta no arquivo
  original, se ele foi aberto de uma pasta do celular via SAF

## O que falta (próximos passos)
- **Executar outras linguagens** (JS, Java, Kotlin) — hoje só Python roda de
  fato. JS daria pra viabilizar com `nodejs-mobile`; linguagens compiladas
  (Java/Kotlin/C) são bem mais complexas e provavelmente exigiriam um terminal
  Linux embutido (estilo Termux/PRoot).
- **Autocomplete real** e detecção de erros (precisaria de um language server
  ou de regras mais avançadas — hoje o highlighting é feito por regex, não
  entende a estrutura do código).
- Instalar pacotes Python (pip) direto pelo app.

## ⚠️ Aviso sobre licença do Chaquopy
O Chaquopy é gratuito para desenvolvimento e testes, mas cobra uma licença para
apps publicados comercialmente (o que inclui a Play Store). Antes de publicar,
confira os termos atuais em https://chaquo.com/chaquopy/pricing/ — pode ser
necessário comprar uma licença dependendo do seu volume de usuários/receita.

## Como gerar o APK

1. Baixe e instale o **Android Studio** (https://developer.android.com/studio)
2. Abra este projeto: `File > Open` e selecione a pasta `MobileIDE`
3. Deixe o Gradle sincronizar (vai baixar as dependências automaticamente,
   precisa de internet nessa etapa)
4. Para testar: conecte um celular Android (ou use um emulador) e clique em **Run**
5. Para gerar o APK final: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - O arquivo vai aparecer em `app/build/outputs/apk/debug/app-debug.apk`
6. Para publicar na Play Store, você vai precisar gerar uma versão **assinada**
   (release), em `Build > Generate Signed Bundle / APK`

## Estrutura do projeto
```
MobileIDE/
├── app/
│   └── src/main/
│       ├── java/com/mobileide/app/
│       │   ├── MainActivity.kt       (tela principal)
│       │   ├── model/FileNode.kt     (dados de arquivos/abas)
│       │   └── ui/                   (componentes visuais)
│       └── res/                      (temas, strings)
├── build.gradle.kts
└── settings.gradle.kts
```

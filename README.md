# TwitHitter用リポジトリ
## 概要
TwitHitter関連のスクリプト及びプロジェクトです。

## 注意事項
- Geb（Selenium）を使用しているため、ブラウザのバージョンによっては動きません。
- その場合にはJava、Groovy、Geb、ドライバーなどのバージョンを上げてください。

## 共通準備
- Java
- Groovy

## selectTargetPlayer.groovy
### 事前準備
- Chrome
- ./driver/chromedriver
- GebConfig.groovy
  - loginTwitterId : TwitterのログインID
  - loginTwitterPassword : Twitterのログインパスワード
  - googleWebAplicationId : SpreadSheetのアプリケーションID // 変数名おかしいような。。。

### 実行
- groovy selectTargetPlayer.groovy
- エラーが出る場合があるので（ブラウザの問題？）嫌な場合はシェルで実行。
  - ./selectTargetPlayer.sh

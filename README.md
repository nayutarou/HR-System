# 人事管理システム (HR Management System)

これはJavaとSpring Bootで構築されたシンプルな人事管理システムです。従業員と部署を管理するためのWebベースのインターフェースとRESTful APIを提供します。

## 主な機能

-   **従業員管理:**
    -   全従業員の一覧表示
    -   従業員の追加、更新、削除
-   **部署管理:**
    -   全部署の一覧表示
    -   部署の追加、更新、削除
-   簡単なデータ操作のためのWebインターフェース
-   プログラムからのアクセスのためのRESTful API

## 技術スタック

-   **バックエンド:**
    -   Java 17
    -   Spring Boot 3.5.6
    -   Spring Web
    -   MyBatis 3.0.5 (データマッパー)
    -   Lombok
-   **フロントエンド:**
    -   Thymeleaf
-   **データベース:**
    -   PostgreSQL
-   **ビルドツール:**
    -   Maven

## 前提条件

-   Java 17 以降
-   Maven 3.6 以降
-   PostgreSQLデータベースが起動していること
-   データベース接続のために以下の環境変数を設定してください:
    -   `DATABASE_HOST`
    -   `DATABASE_PORT`
    -   `DATABASE_NAME`
    -   `DATABASE_USER`
    -   `DATABASE_PASSWORD`

## 実行方法

1.  **リポジトリをクローンします:**
    ```bash
    git clone <repository-url>
    cd HR-System
    ```

2.  **データベースをセットアップします:**
    PostgreSQLサーバーが起動しており、環境変数が正しく設定されていることを確認してください。

3.  **アプリケーションを実行します:**
    ```bash
    ./mvnw spring-boot:run
    ```
    アプリケーションは `http://localhost:8080` でアクセス可能になります。

## APIエンドポイント

このアプリケーションは以下のRESTエンドポイントを公開しています:

-   **従業員:** `/api/employees`
-   **部署:** `/api/departments`

詳細については、[SPECIFICATIONS.md](SPECIFICATIONS.md) ファイルを参照してください。  
テストについては、[TESTING.md](TESTING.md) ファイルを参照してください。

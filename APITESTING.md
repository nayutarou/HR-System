# RESTful API テスト

このドキュメントは、`DepartmentsController` および `EmployeesController` によって公開されるRESTful APIのテストについて説明します。

## 概要

このプロジェクトのRESTful APIは、JSON形式でデータを提供し、外部のアプリケーションやフロントエンドフレームワークとの連携を可能にします。APIの品質と信頼性を保証するため、`MockMvc` を利用したAPIテストが実装されています。

これらのテストは、実際のHTTPリクエストをシミュレートし、コントローラーが期待通りのHTTPステータスコードとJSONレスポンスを返すことを検証します。

## テストファイル

-   **`src/test/java/com/example/hrsystem/RestfulApiTest.java`**

    このファイルには、部署と従業員のRESTful APIに関するすべてのテストケースが含まれています。

## 技術スタック

-   **Spring Boot Test (`@WebMvcTest`)**: 特定のコントローラー (`DepartmentsController`, `EmployeesController`) に絞ったテストコンテキストを提供します。
-   **MockMvc**: サーバーを起動せずにSpring MVCの動作をテストするための主要なツールです。
-   **Mockito (`@MockBean`)**: Service層 (`DepartmentService`, `EmployeesService`) をモック化し、テスト対象をコントローラーのロジックに限定します。
-   **Jackson (`ObjectMapper`)**: JavaオブジェクトとJSON文字列の相互変換に使用します。
-   **JsonPath**: レスポンスのJSONボディの内容を検証するために使用します。

## 主なテストケース

### 1. 部署管理API (`/api/departments`)

-   `GET /api/departments`: すべての部署をリストとして取得できること (ステータスコード 200 OK)。
-   `GET /api/departments/{id}` (成功): 存在するIDを指定した場合、単一の部署データを取得できること (200 OK)。
-   `GET /api/departments/{id}` (失敗): 存在しないIDを指定した場合、`404 Not Found` エラーが返されること。
-   `POST /api/departments`: 新しい部署データをリクエストボディに含めて送信すると、データが作成され、`201 Created` ステータスと `Location` ヘッダーが返されること。
-   `PUT /api/departments/{id}`: 既存のIDと更新情報を送信すると、データが更新され、更新後のデータが返されること (200 OK)。
-   `DELETE /api/departments/{id}` (成功): 存在するIDを指定した場合、データが削除され、`204 No Content` ステータスが返されること。
-   `DELETE /api/departments/{id}` (失敗): 存在しないIDを指定した場合、`404 Not Found` エラーが返されること。

### 2. 従業員管理API (`/api/employees`)

-   `GET /api/employees`: すべての従業員をリストとして取得できること (200 OK)。
-   `GET /api/employees/{id}` (成功): 存在するIDを指定した場合、単一の従業員データを取得できること (200 OK)。
-   `GET /api/employees/{id}` (失敗): 存在しないIDを指定した場合、`404 Not Found` エラーが返されること。
-   `POST /api/employees`: 新しい従業員データをリクエストボディに含めて送信すると、データが作成され、`201L Created` ステータスと `Location` ヘッダーが返されること。
-   `PUT /api/employees/{id}`: 既存のIDと更新情報を送信すると、データが更新され、更新後のデータが返されること (200 OK)。
-   `DELETE /api/employees/{id}` (成功): 存在するIDを指定した場合、データが削除され、`204 No Content` ステータスが返されること。
-   `DELETE /api/employees/{id}` (失敗): 存在しないIDを指定した場合、`404 Not Found` エラーが返されること。

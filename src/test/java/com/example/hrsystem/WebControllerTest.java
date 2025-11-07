package com.example.hrsystem;

import com.example.hrsystem.controller.WebController;
import com.example.hrsystem.dto.Departments;
import com.example.hrsystem.dto.Employees;
import com.example.hrsystem.exception.ResourceNotFoundException;
import com.example.hrsystem.service.DepartmentService;
import com.example.hrsystem.service.EmployeesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// (1) このコントローラーだけをテストするぞ！と宣言
@WebMvcTest(WebController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc; // ◀︎ (2) Webリクエストをシミュレートする道具

    // (3) 本物のServiceの「偽物」を用意。DBに接続しないようにする
    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private EmployeesService employeesService;

    // テスト (4) の修正例
    @Test
    public void testShowDepartmentsList() throws Exception {

        // org.mockito.Mockito.when(...) ではなく when(...)
        when(departmentService.findAll()).thenReturn(List.of());

        // org.springframework...get(...) ではなく get(...)
        mockMvc.perform(get("/web/departments"))
                // org.springframework...status() ではなく status()
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attributeExists("departments"));
    }

    // テスト (8) の修正例
    @Test
    public void testRegisterDepartment_withValidationError() throws Exception {

        // org.springframework...post(...) ではなく post(...)
        mockMvc.perform(post("/web/departments")
                        .param("name", "テスト部署")
                        .param("location", "")
                )
                .andExpect(status().isOk()) // status()
                .andExpect(view().name("departments/form")) // view()
                .andExpect(model().hasErrors()); // model()
    }

    // (9) 「新規登録フォーム」の表示テスト
    @Test
    public void testShowDepartmentForm() throws Exception {

        // (実行) 「GET /web/departments/new」を実行
        mockMvc.perform(get("/web/departments/new"))

                // (検証)
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("departments/form")) // form.html を表示
                // "department" という名前の空のオブジェクトがModelにあるか
                .andExpect(model().attributeExists("department"));
    }

    // (10) 「部署登録（POST）」のテスト (登録成功)
    @Test
    public void testRegisterDepartment_Success() throws Exception {

        // (実行)
        // 「POST /web/departments」を実行
        // バリデーションが通るように、両方のパラメータを送る
        mockMvc.perform(post("/web/departments")
                        .param("name", "テスト部署")
                        .param("location", "テストビル")
                )
                // (検証)
                // (A) ステータスが302 (リダイレクト) であることを期待
                .andExpect(status().isFound())

                // (B) リダイレクト先が "/web/departments" であることを期待
                .andExpect(redirectedUrl("/web/departments"));

        // (おまけ検証)
        // ちゃんと service.insert が1回呼ばれたか？のチェック
        // (引数が何であれ、とにかく Departments クラスのオブジェクトで呼ばれたか)
        verify(departmentService, times(1)).insert(any(Departments.class));
    }

    // (11) 「部署更新フォーム」の表示テスト
    @Test
    public void testShowEditForm() throws Exception {
        // (準備)
        // ID=1 のダミー部署データを作成
        Departments dummyDept = new Departments();
        dummyDept.setId(1L);
        dummyDept.setName("営業部");

        // service.findById(1) が呼ばれたら、このダミーを返すフリをしろ
        when(departmentService.findById(1L)).thenReturn(dummyDept);

        // (実行)
        // 「GET /web/departments/edit/1」を実行
        mockMvc.perform(get("/web/departments/edit/1"))

                // (検証)
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("departments/form")) // form.html を表示
                // Modelに "department" があり、その name が "営業部" であること
                .andExpect(model().attribute("department", hasProperty("name", is("営業部"))));
    }

    // (12) 「部署更新（POST）」のテスト (更新成功)
    @Test
    public void testUpdateDepartment_Success() throws Exception {

        // (実行)
        // 「POST /web/departments/update/1」を実行
        // バリデーションが通るデータで送信
        mockMvc.perform(post("/web/departments/update/1")
                        .param("name", "営業本部")
                        .param("location", "新ビル")
                )
                // (検証)
                // (A) リダイレクト (302) すること
                .andExpect(status().isFound())

                // (B) リダイレクト先が "/web/departments" であること
                .andExpect(redirectedUrl("/web/departments"));

        // (おまけ検証)
        // service.update が「ID=1」で「何らかのDepartmentsオブジェクト」を引数に
        // 1回呼ばれたことを確認
        verify(departmentService, times(1)).update(eq(1L), any(Departments.class));
    }

    // (13) 「部署更新（POST）」のテスト (バリデーションエラー)
    @Test
    public void testUpdateDepartment_ValidationError() throws Exception {

        // (実行)
        // 「POST /web/departments/update/1」を実行
        // 'name' を空にして送信（バリデーション違反）
        mockMvc.perform(post("/web/departments/update/1")
                        .param("name", "") // ◀︎ バリデーション違反
                        .param("location", "新ビル")
                )
                // (検証)
                // (A) エラーなのでリダイレクトせず、フォームに戻る (200 OK)
                .andExpect(status().isOk())

                // (B) "departments/form" を表示すること
                .andExpect(view().name("departments/form"))

                // (C) Modelにエラー情報が含まれていること
                .andExpect(model().hasErrors());
    }

    // (14) 「部署削除（POST）」のテスト (削除成功)
    @Test
    public void testDeleteDepartment_Success() throws Exception {

        // (実行)
        // 「POST /web/departments/delete/1」を実行
        mockMvc.perform(post("/web/departments/delete/1"))

                // (検証)
                // (A) リダイレクト (302) すること
                .andExpect(status().isFound())

                // (B) リダイレクト先が "/web/departments" であること
                .andExpect(redirectedUrl("/web/departments"));

        // (おまけ検証)
        // service.deleteById が「ID=1」を引数に1回呼ばれたこと
        verify(departmentService, times(1)).deleteById(1L);
    }

    // --- Employees ---

    // (15) 「従業員一覧」の表示テスト
    @Test
    public void testListEmployees() throws Exception {

        // (準備)
        // employeesService.findAll() が呼ばれたら、空リストを返すフリ
        when(employeesService.findAll()).thenReturn(List.of());
        // departmentService.findAll() が呼ばれたら、空リストを返すフリ
        when(departmentService.findAll()).thenReturn(List.of());

        // (実行)
        mockMvc.perform(get("/web/employees"))

                // (検証)
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("employees/list")) // list.html
                .andExpect(model().attributeExists("employees")); // "employees" がModelにある
    }

    // (16) 「従業員登録フォーム」の表示テスト (GET)
    @Test
    public void testShowEmployeeForm() throws Exception {

        // (準備)
        // <select> に部署リストを表示するため、モックが必要
        when(departmentService.findAll()).thenReturn(List.of(new Departments())); // 空でもいい

        // (実行)
        mockMvc.perform(get("/web/employees/new"))

                // (検証)
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("employees/form")) // form.html
                .andExpect(model().attributeExists("employee")) // 空の employee がある
                .andExpect(model().attributeExists("departments")); // 部署リストがある
    }

    // (17) 「従業員登録（POST）」のテスト (バリデーションエラー)
    @Test
    public void testRegisterEmployee_ValidationError() throws Exception {

        // (準備)
        // ★重要★
        // バリデーションエラーでフォームに戻る際、コントローラーは
        // 部署リストを再度 Model に詰める。そのためのモック。
        when(departmentService.findAll()).thenReturn(List.of());

        // (実行)
        // 「POST /web/employees」を実行
        // 'lastName' を空にしてバリデーション違反
        mockMvc.perform(post("/web/employees")
                        .param("firstName", "太郎")
                        .param("lastName", "") // ◀︎ 違反
                        .param("email", "taro@example.com")
                        .param("departmentId", "1")
                        .param("position", "新人")
                        .param("hireDate", "2025-11-06")
                )
                // (検証)
                .andExpect(status().isOk()) // リダイレクトせず 200 OK
                .andExpect(view().name("employees/form")) // form.html に戻る
                .andExpect(model().hasErrors()) // Modelにエラーがある
                // ★重要★ エラーで戻っても部署リストがちゃんとあるか
                .andExpect(model().attributeExists("departments"));
    }

    // (18) 「従業員登録（POST）」のテスト (登録成功)
    @Test
    public void testRegisterEmployee_Success() throws Exception {

        // (実行)
        // 全ての必須項目を正しくPOST
        mockMvc.perform(post("/web/employees")
                        .param("firstName", "花子")
                        .param("lastName", "山田")
                        .param("email", "hanako@example.com")
                        .param("departmentId", "1")
                        .param("position", "リーダー")
                        .param("hireDate", "2024-04-01")
                )
                // (検証)
                .andExpect(status().isFound()) // リダイレクト (302)
                .andExpect(redirectedUrl("/web/employees")); // 一覧へ

        // (おまけ検証)
        // service.insert が1回呼ばれたか
        verify(employeesService, times(1)).insert(any(Employees.class));
    }

    // (19) 「従業員更新フォーム」の表示テスト (GET)
    @Test
    public void testShowEmployeeEditForm() throws Exception {

        // (準備)
        // ID=1 のダミー従業員
        Employees dummyEmp = new Employees();
        dummyEmp.setId(1L);
        dummyEmp.setLastName("テスト");
        // employeesService.findById(1) が呼ばれたら、ダミーを返すフリ
        when(employeesService.findById(1L)).thenReturn(dummyEmp);

        // ★もちろん、部署リストのモックも必要
        when(departmentService.findAll()).thenReturn(List.of());

        // (実行)
        mockMvc.perform(get("/web/employees/edit/1"))

                // (検証)
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("employees/form")) // form.html
                .andExpect(model().attributeExists("employee")) // 従業員データがある
                .andExpect(model().attributeExists("departments")) // 部署リストがある
                // Modelの "employee" の lastName が "テスト" であること
                .andExpect(model().attribute("employee", hasProperty("lastName", is("テスト"))));
    }

    // (20) 「従業員更新（POST）」のテスト (更新成功)
    @Test
    public void testUpdateEmployee_Success() throws Exception {

        // (実行)
        // 「POST /web/employees/update/1」を実行
        // バリデーションが通るデータで送信
        mockMvc.perform(post("/web/employees/update/1")
                        .param("firstName", "更新")
                        .param("lastName", "太郎")
                        .param("email", "update@example.com")
                        .param("departmentId", "2")
                        .param("position", "マネージャー")
                        .param("hireDate", "2020-01-01") // 更新時も日付は必要
                )
                // (検証)
                .andExpect(status().isFound()) // リダイレクト (302)
                .andExpect(redirectedUrl("/web/employees")); // 一覧へ

        // (おまけ検証)
        // service.update が「ID=1」で「何らかのEmployeesオブジェクト」を引数に
        // 1回呼ばれたことを確認
        verify(employeesService, times(1)).update(eq(1L), any(Employees.class));
    }

    // (21) 「従業員更新（POST）」のテスト (バリデーションエラー)
    @Test
    public void testUpdateEmployee_ValidationError() throws Exception {

        // (準備)
        // ★重要★ エラーでフォームに戻るため、部署リストのモックが必要
        when(departmentService.findAll()).thenReturn(List.of());

        // (実行)
        // 「POST /web/employees/update/1」を実行
        // 'email' を不正な形式にしてバリデーション違反
        mockMvc.perform(post("/web/employees/update/1")
                        .param("firstName", "更新")
                        .param("lastName", "太郎")
                        .param("email", "bad-email") // ◀︎ 違反
                        .param("departmentId", "2")
                        .param("position", "マネージャー")
                        .param("hireDate", "2020-01-01")
                )
                // (検証)
                .andExpect(status().isOk()) // 200 OK
                .andExpect(view().name("employees/form")) // form.html に戻る
                .andExpect(model().hasErrors()) // Modelにエラーがある
                .andExpect(model().attributeExists("departments")); // 部署リストもちゃんとある
    }

    // (22) 「従業員削除（POST）」のテスト (削除成功)
    @Test
    public void testDeleteEmployee_Success() throws Exception {

        // (実行)
        // 「POST /web/employees/delete/1」を実行
        mockMvc.perform(post("/web/employees/delete/1"))

                // (検証)
                .andExpect(status().isFound()) // リダイレクト (302)
                .andExpect(redirectedUrl("/web/employees")); // 一覧へ

        // (おまけ検証)
        // service.delete が「ID=1」を引数に1回呼ばれたこと
        verify(employeesService, times(1)).delete(1L);
    }

    // (23) 「存在しないID」で編集ページを開こうとした時 (GET)
    @Test
    @DisplayName("編集GET: 存在しないIDは、一覧にリダイレクトされるべき")
    public void testShowEditForm_NotFound() throws Exception {

        // (準備)
        // service.findById(999L) が呼ばれたら、例外を投げるフリ
        when(departmentService.findById(999L))
                .thenThrow(new ResourceNotFoundException("ID 999 not found"));

        // (実行)
        mockMvc.perform(get("/web/departments/edit/999")) // 幽霊ID

                // (検証)
                .andExpect(status().isFound()) // 302 リダイレクト
                // Controllerが ?not_found を付けて一覧に戻すはず
                .andExpect(redirectedUrl("/web/departments?not_found"));
    }

    // (24) 「存在しないID」を更新しようとした時 (POST)
    @Test
    @DisplayName("更新POST: 存在しないIDは、フォームを200で再表示すべき")
    public void testUpdateDepartment_NotFound() throws Exception {

        // (準備)
        when(departmentService.update(eq(999L), any(Departments.class)))
                .thenThrow(new ResourceNotFoundException("ID 999 not found"));

        // (実行)
        mockMvc.perform(post("/web/departments/update/999")
                        .param("name", "更新テスト")
                        .param("location", "更新ビル")
                )
                // (検証)
                // ↓↓↓ ここを修正 ↓↓↓

                // (A) リダイレクト(302)ではなく、200 (OK) を期待
                .andExpect(status().isOk())

                // (B) "departments/form" が表示されることを期待
                .andExpect(view().name("departments/form"))

                // (C) Modelに（フィールドに紐づかない）グローバルエラーがあることを期待
                .andExpect(model().hasErrors());
    }

    // (25) 「存在しないID」を削除しようとした時 (POST)
    @Test
    @DisplayName("削除POST: 存在しないIDは、エラー付きで一覧にリダイレクトされるべき")
    public void testDeleteDepartment_NotFound() throws Exception {

        // (準備)
        // service.deleteById(999L) が呼ばれたら、例外を投げるフリ
        when(departmentService.deleteById(999L))
                .thenThrow(new ResourceNotFoundException("ID 999 not found"));

        // (実行)
        mockMvc.perform(post("/web/departments/delete/999")) // 幽霊ID

                // (検証)
                .andExpect(status().isFound()) // 302 リダイレクト
                // Controllerが ?delete_error を付けて一覧に戻すはず
                .andExpect(redirectedUrl("/web/departments?delete_error"));
    }
}
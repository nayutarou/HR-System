package com.example.hrsystem.controller;

import com.example.hrsystem.dto.Departments;
import com.example.hrsystem.dto.Employees;
import com.example.hrsystem.exception.BadRequestException;
import com.example.hrsystem.exception.ResourceNotFoundException;
import com.example.hrsystem.service.DepartmentService; // ◀︎ Serviceをインポート
import com.example.hrsystem.service.EmployeesService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ◀︎ Modelをインポート
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/web","/"})
public class WebController {

    private final DepartmentService departmentService;
    private final EmployeesService employeesService;

    // コンストラクタでServiceをインジェクション（DI）
    public WebController(DepartmentService departmentService, EmployeesService employeesService) {
        this.departmentService = departmentService;
        this.employeesService = employeesService;
    }

    @GetMapping({"", "/"}) // "/" または "/web" でアクセスできるようにする
    public String home() {
        return "home"; // テンプレート名: home.html
    }

    @GetMapping("/departments")
    public String showDepartmentsList(Model model) { // ◀︎ Modelオブジェクトを受け取る

        // 1. Service層から全部署データを取得
        List<Departments> departments = departmentService.findAll();

        // 2. 取得したリストを "departments" という名前でViewに渡す
        model.addAttribute("departments", departments);

        // 3. テンプレート名（/templates/departments/list.html）を返す
        return "departments/list";
    }

    // 部署登録フォーム表示
    @GetMapping("/departments/new")
    public String showDepartmentForm(Model model) {
        // Thymeleafに空のDepartmentsオブジェクトを渡してフォームと紐づける
        model.addAttribute("department", new Departments());
        return "departments/form"; // テンプレート名: departments/form.html
    }

    // 部署登録処理
// 部署登録処理 (修正版)
    @PostMapping("/departments")
    public String registerDepartment(
            @Valid @ModelAttribute("department") Departments department, // ◀︎ (1) @Validで検証
            BindingResult bindingResult, // ◀︎ (2) エラー結果を受け取る
            Model model) { // ◀︎ (3) フォームに戻すためにModelを追加

        // (4) バリデーションエラーをチェック
        if (bindingResult.hasErrors()) {
            // エラーあり！
            // 情報を保持したまま (department は自動でModelに入る)
            // フォームのHTMLを返す (リダイレクトしない！)
            return "departments/form";
        }

        // (5) バリデーションはOK、Serviceの処理へ
        try {
            departmentService.insert(department);
            return "redirect:/web/departments"; // 成功

        } catch (BadRequestException e) {
            // (6) Service層で起きたエラー (例: 部署名重複など) をキャッチ
            // 'name' フィールドに関連付けてエラーメッセージを追加
            bindingResult.rejectValue("name", "duplicate.error", e.getMessage());
            return "departments/form"; // やはりフォームに戻す

        } catch (Exception e) {
            // (7) その他の予期せぬエラー
            bindingResult.reject("global.error", "予期せぬエラーが発生しました。");
            return "departments/form";
        }
    }

    // 部署更新フォーム表示
    @GetMapping("/departments/edit/{id}")
    public String showEditForm(@PathVariable long id, Model model) {

        try {
            // 既存データをServiceから取得し、フォームに初期値として渡す
            Departments department = departmentService.findById(id);
            model.addAttribute("department", department);

            return "departments/form"; // 作成と同じ form.html を再利用

        } catch (ResourceNotFoundException e) {
            // IDが見つからない場合は、エラーメッセージと共に一覧に戻すなど (ここではシンプルに一覧へ)
            return "redirect:/web/departments?not_found";
        }
    }

    // 部署更新処理
    @PostMapping("/departments/update/{id}")
    public String updateDepartment(@PathVariable long id,
                                   @Valid @ModelAttribute("department") Departments department, // ◀︎ (1)
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "departments/form"; // ◀︎ (3) エラーなら 200 でフォームに戻す
        }

        try {
            // Service層で更新処理を実行 (IDはURLから取得、データはフォームから取得)
            departmentService.update(id, department);

            // 更新成功後、一覧画面にリダイレクト
            return "redirect:/web/departments";
        }catch (Exception e) {
            bindingResult.reject("global.error", e.getMessage());
            return "departments/form"; // ◀︎ (4) catchでもフォームに戻す
        }
    }

    // 部署削除処理
    // 注: ブラウザの標準フォームからDELETEリクエストを送るのは難しいため、
    //     ここでは /delete/{id} への POST リクエストとして処理します。
    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable long id) {

        try {
            // Service層で削除処理を実行
            departmentService.deleteById(id);

            // 削除成功後、一覧画面にリダイレクト
            return "redirect:/web/departments";
        } catch (ResourceNotFoundException | BadRequestException e) {
            // 404/400エラーの場合、エラーメッセージと共に一覧に戻る
            return "redirect:/web/departments?delete_error";
        }
    }

//    *********** employees **************    //

    // 従業員一覧表示
    @GetMapping("/employees")
    public String listEmployees(Model model) {

        // 1. 従業員リストを取得
        List<Employees> employees = employeesService.findAll();

        // 2. 部署リストを取得 (ID検索を効率化するためMapに変換)
        List<Departments> departments = departmentService.findAll();
        Map<Long, String> departmentMap = new HashMap<>();
        for (Departments dept : departments) {
            departmentMap.put(dept.getId(), dept.getName());
        }

        // 3. 従業員データと部署名を結合した新しいリストを作成
        // Thymeleafに渡すための新しいリスト
        List<Map<String, Object>> employeeDisplayList = new ArrayList<>();

        for (Employees emp : employees) {
            Map<String, Object> displayItem = new HashMap<>();

            // 従業員データをそのままコピー
            displayItem.put("id", emp.getId());
            displayItem.put("lastName", emp.getLastName());
            displayItem.put("firstName", emp.getFirstName());
            displayItem.put("email", emp.getEmail());
            displayItem.put("position", emp.getPosition());
            displayItem.put("hireDate", emp.getHireDate());
            displayItem.put("departmentId", emp.getDepartmentId()); // IDも念のため残す

            // 部署IDをキーに部署名を取得し、追加
            if (emp.getDepartmentId() != null) {
                String deptName = departmentMap.getOrDefault(emp.getDepartmentId(), "不明");
                displayItem.put("departmentName", deptName); // ★部署名を追加★
            } else {
                displayItem.put("departmentName", "未所属");
            }

            employeeDisplayList.add(displayItem);
        }

        // Thymeleafに結合済みリストを渡す
        model.addAttribute("employees", employeeDisplayList);

        return "employees/list";
    }

    // 従業員登録フォーム表示
    @GetMapping("/employees/new")
    public String showEmployeeForm(Model model) {

        // 1. 空のEmployeesオブジェクトをフォームにバインド
        model.addAttribute("employee", new Employees());

        // 2. 部署リストを取得し、ドロップダウン（<select>）用に渡す
        List<Departments> departments = departmentService.findAll();
        model.addAttribute("departments", departments);

        return "employees/form"; // テンプレート名: employees/form.html
    }

    // 従業員登録処理
    @PostMapping("/employees")
    public String registerEmployee(
            @Valid @ModelAttribute("employee") Employees employee,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            return "employees/form";
        }

        try {
            employeesService.insert(employee);
            return "redirect:/web/employees";

        } catch (Exception e) { // ◀︎ 雑に Exception e で受ける

            // ↓↓↓ ここで通訳する ↓↓↓
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("employees_email_key")) {
                // (A) 「メール重複」だと判明した場合
                bindingResult.rejectValue("email", "duplicate.email", "このメールアドレスは既に使用されています。");
            } else {
                // (B) よく分からない、その他のエラーの場合
                bindingResult.reject("global.error", "登録処理中に予期せぬエラーが発生しました。");
            }
            // ↑↑↑ 通訳ここまで ↑↑↑

            model.addAttribute("departments", departmentService.findAll());
            return "employees/form";
        }
    }

    // 従業員更新フォーム表示
    @GetMapping("/employees/edit/{id}")
    public String showEmployeeEditForm(@PathVariable long id, Model model) {

        try {
            // 1. 既存の従業員データを取得
            Employees employee = employeesService.findById(id);
            model.addAttribute("employee", employee);

            // 2. 部署リストも取得し、ドロップダウン用に渡す
            List<Departments> departments = departmentService.findAll();
            model.addAttribute("departments", departments);

            return "employees/form"; // 作成と同じ form.html を再利用

        } catch (ResourceNotFoundException e) {
            // IDが見つからない場合は一覧に戻す
            return "redirect:/web/employees?not_found";
        }
    }

    // 従業員更新処理
    @PostMapping("/employees/update/{id}")
    public String updateEmployee(@PathVariable long id,
                                 @Valid @ModelAttribute("employee") Employees employee,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            return "employees/form";
        }

        try {
            employeesService.update(id, employee);
            return "redirect:/web/employees";

        } catch (Exception e) { // ◀︎ 雑に Exception e で受ける

            // ↓↓↓ ここで通訳する ↓↓↓
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.contains("employees_email_key")) {
                // (A) 「メール重複」だと判明した場合
                bindingResult.rejectValue("email", "duplicate.email", "このメールアドレスは既に使用されています。");
            } else {
                // (B) よく分からない、その他のエラーの場合
                bindingResult.reject("global.error", "更新処理中に予期せぬエラーが発生しました。");
            }
            // ↑↑↑ 通訳ここまで ↑↑↑

            model.addAttribute("departments", departmentService.findAll());
            return "employees/form"; // フォームに戻す
        }
    }

    // 従業員削除処理
    // 注: ブラウザの標準フォームからDELETEリクエストを送るのは難しいため、POSTで処理
    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable long id) {

        try {
            // Service層で削除処理を実行
            employeesService.delete(id);

            // 削除成功後、一覧画面にリダイレクト
            return "redirect:/web/employees";
        } catch (ResourceNotFoundException | BadRequestException e) {
            // 404/400エラーの場合、エラーメッセージと共に一覧に戻す
            return "redirect:/web/employees?delete_error";
        }
    }
}
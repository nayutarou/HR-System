package com.example.hrsystem.controller;

import com.example.hrsystem.dto.Departments;
import com.example.hrsystem.service.DepartmentService;
import org.apache.ibatis.annotations.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentsController {

    private final DepartmentService departmentService;

    public DepartmentsController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // 一覧
    @GetMapping
    public ResponseEntity<List<Departments>> findAll() {

        List<Departments> departments = departmentService.findAll();

//        public static <T> ResponseEntity<T> ok(@Nullable T body) {
//            return ok().body(body);
//        }
        return ResponseEntity.ok(departments);

    }

    // 追加
    @PostMapping
    public ResponseEntity<Departments> save(@RequestBody Departments departments,
                                            UriComponentsBuilder uriComponentsBuilder) {

        Departments createDepartment = departmentService.insert(departments);

        URI location =
                uriComponentsBuilder.path("/api/departments/{id}").
                        buildAndExpand(createDepartment.getId()).toUri();

//       public static BodyBuilder created(URI location) {
//          return (BodyBuilder)status(HttpStatus.CREATED).location(location);
//       }
        return ResponseEntity.created(location).body(createDepartment);

    }

    // 単品表示
    @GetMapping("/{id}")
    public ResponseEntity<Departments> findById(@PathVariable long id) {
        Departments departments = departmentService.findById(id);

        return ResponseEntity.ok(departments);
    }

    // 削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Departments> deleteById(@PathVariable long id) {
        departmentService.deleteById(id);

        // 削除成功204 noContentを返す body部が今回ないのでbuildを付ける
//        public static ResponseEntity.HeadersBuilder<?> noContent() {
//            return status(HttpStatus.NO_CONTENT);
//        }
        return ResponseEntity.noContent().build();
    }

    // 更新
    @PutMapping("/{id}")
    public ResponseEntity<Departments> updateById(@PathVariable long id,
                                                  @RequestBody Departments departments){

        Departments updateDepartments = departmentService.update(id,departments);

        return ResponseEntity.ok(updateDepartments);

    }
}

package com.example.hrsystem.controller;

import com.example.hrsystem.dto.Employees;
import com.example.hrsystem.service.EmployeesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeesController {

    private final EmployeesService employeesService;

    public EmployeesController(EmployeesService employeesService) {
        this.employeesService = employeesService;
    }

    // 一覧
    @GetMapping
    public ResponseEntity<List<Employees>> findAll(){

        List<Employees> employees = employeesService.findAll();

        return ResponseEntity.ok(employees);

    }

    // 追加
    @PostMapping
    public ResponseEntity<Employees> save(@RequestBody Employees employees,
                                          UriComponentsBuilder uriComponentsBuilder){

        Employees createEmployee = employeesService.insert(employees);

        URI location =
                uriComponentsBuilder.path("/api/employees/{id}").
                        buildAndExpand(createEmployee.getId()).toUri();

        return ResponseEntity.created(location).body(createEmployee);

    }

    // 単品表示
    @GetMapping("/{id}")
    public ResponseEntity<Employees> findById(@PathVariable long id){

        Employees employees = employeesService.findById(id);

        return ResponseEntity.ok(employees);

    }

    // 削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Employees> deleteById(@PathVariable long id){

        employeesService.delete(id);

        return ResponseEntity.noContent().build();

    }

    // 更新
    //    @PutMapping("/{id}")
    @PutMapping("/{id}")
    public ResponseEntity<Employees> updateById(@PathVariable long id,
                                                @RequestBody Employees employees){

        Employees updateEmployees = employeesService.update(id,employees);

        return ResponseEntity.ok(updateEmployees);

    }

}

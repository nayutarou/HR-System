package com.example.hrsystem.repository;

import com.example.hrsystem.dto.Employees;
import com.example.hrsystem.mapper.EmployeesMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeesRepository {

    private final EmployeesMapper employeesMapper;

    public EmployeesRepository(EmployeesMapper employeesMapper) {
        this.employeesMapper = employeesMapper;
    }

    // 一覧
    public List<Employees> findAll(){
        return employeesMapper.findAllEmployees();
    }

    // 単品表示
    public Employees findById(long id){
        return employeesMapper.findByEmployeesId(id);
    }

    // 追加
    public int insert(Employees employee){
        return employeesMapper.insertEmployee(employee);
    }

    // 削除
    public int delete(long id){
        return employeesMapper.deleteEmployee(id);
    }

    // 更新
    public int update(Employees employees){
        return employeesMapper.updateEmployee(employees);
    }

}

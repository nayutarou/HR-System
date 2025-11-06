package com.example.hrsystem.repository;

import com.example.hrsystem.dto.Departments;
import com.example.hrsystem.mapper.DepartmentsMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentsRepository {

    private final DepartmentsMapper departmentsMapper;

    public DepartmentsRepository(DepartmentsMapper departmentsMapper) {
        this.departmentsMapper = departmentsMapper;
    }

    // 一覧
    public List<Departments> findAll(){
        return departmentsMapper.findAllDepartments();
    }

    // 単品表示
    public Departments findById(long id){
        return departmentsMapper.findByDepartmentsId(id);
    }

    // 追加
    public int insert(Departments department){
        return departmentsMapper.insertDepartments(department);
    }

    // 削除
    public int deleteById(long id){
        // 戻り値より大きいならば削除成功 idが0より大きければ成功となる
        return departmentsMapper.deleteDepartments(id);
    }

    // 更新
    public int update(Departments department){
        return departmentsMapper.updateDepartments(department);
    }
}

package com.example.hrsystem.service;

import com.example.hrsystem.dto.Departments;
import com.example.hrsystem.exception.BadRequestException;
import com.example.hrsystem.exception.ResourceNotFoundException;
import com.example.hrsystem.repository.DepartmentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentsRepository departmentsRepository;

    public DepartmentService(DepartmentsRepository departmentsRepository) {
        this.departmentsRepository = departmentsRepository;
    }

    // 一覧
    public List<Departments> findAll(){
        return departmentsRepository.findAll();
    }

    // 単品表示
    public Departments findById(long id){
        Departments departments = departmentsRepository.findById(id);
        // 見つからないとき
        if (departments == null){
            throw new ResourceNotFoundException(id);
        }
        // departmentsにはidを返されている
        return departments;
    }

    // 追加
    public Departments insert(Departments department){
        // 名前が空の時
        if (department.getName() == null || department.getName().isEmpty()){
            throw new BadRequestException("name is not empty");
        }

        // locationが空の時
        if (department.getLocation() == null || department.getLocation().isEmpty()){
            throw new BadRequestException("location isn't empty");
        }

        departmentsRepository.insert(department);

        return department;
    }

    // 削除
    public int deleteById(long id){

        // 1. 【バリデーション】IDが論理的に不正な値かチェック (400 Bad Request)
        if (id <= 0) {
            throw new BadRequestException("IDは正の整数である必要があります。");
        }

        // 2. 【存在チェック】リソースがDBに存在するかチェック (404 Not Found)
        if (departmentsRepository.findById(id) == null){
            throw new ResourceNotFoundException(id);
        }

        // 3. 存在が確認できたら削除を実行
        //    ※ 成功すれば 1 が返る。失敗しても、事前チェックを通過しているため、
        //       DBエラーとして Spring が 500 Internal Server Error に処理します。
        return departmentsRepository.deleteById(id);
    }

    // 更新
    // DepartmentService.java (推奨される形)
    public Departments update(long id, Departments department){

        // 名前が空の時
        if (department.getName() == null || department.getName().isEmpty()){
            throw new BadRequestException("name is not empty");
        }

        // locationが空の時
        if (department.getLocation() == null || department.getLocation().isEmpty()){
            throw new BadRequestException("location isn't empty");
        }

        // 見つからないとき
        if (departmentsRepository.findById(id) == null){
            throw new ResourceNotFoundException(id);
        }

        department.setId(id);
        // 3. Repositoryに処理を依頼
        departmentsRepository.update(department);
        // 4. 更新後の最新データをDBから取得し直して返す
        return departmentsRepository.findById(id);
    }
}

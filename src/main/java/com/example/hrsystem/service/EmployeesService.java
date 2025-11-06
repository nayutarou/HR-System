package com.example.hrsystem.service;

import com.example.hrsystem.dto.Employees;
import com.example.hrsystem.exception.BadRequestException;
import com.example.hrsystem.exception.ResourceNotFoundException;
import com.example.hrsystem.repository.EmployeesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeesService {

    private final EmployeesRepository employeesRepository;

    public EmployeesService(EmployeesRepository employeesRepository) {
        this.employeesRepository = employeesRepository;
    }

    // 一覧
    public List<Employees> findAll(){
        return employeesRepository.findAll();
    }

    // 単品表示
    public Employees findById(long id){

        Employees employees = employeesRepository.findById(id);

        if (employees == null){
            throw new ResourceNotFoundException(id);
        }

        return employees;
    }

    // 追加
    public Employees insert(Employees employee){

        // 名前が空の時
        // lastNameがnullまたは空、あるいはfirstNameがnullまたは空の場合
        if (employee.getLastName() == null || employee.getLastName().isEmpty() ||
                employee.getFirstName() == null || employee.getFirstName().isEmpty()) {
            throw new BadRequestException("Name (lastName and firstName) cannot be empty.");
        }

        if (employee.getEmail() == null){
            throw new BadRequestException(" email isn't empty ");
        }
        if (employee.getPosition() == null ){
            throw new BadRequestException(" position isn't empty");
        }
//        更新時には必要ない
//        if (employee.getHireDate() == null){
//            throw new BadRequestException(" hireDate isn't empty ");
//        }

        employeesRepository.insert(employee);

        return employee;
    }

    // 削除
    public int delete(long id){

        // 1. 【バリデーション】IDが論理的に不正な値かチェック (400 Bad Request)
        if (id <= 0) {
            throw new BadRequestException("IDは正の整数である必要があります。");
        }

        // 2. 【存在チェック】リソースがDBに存在するかチェック (404 Not Found)
        if (employeesRepository.findById(id) == null){
            throw new ResourceNotFoundException(id);
        }

        // 3. 存在が確認できたら削除を実行
        //    ※ 成功すれば 1 が返る。失敗しても、事前チェックを通過しているため、
        //       DBエラーとして Spring が 500 Internal Server Error に処理します。
        return employeesRepository.delete(id);
    }

    // 更新
    public Employees update(long id,Employees employees){

        // 名前が空の時
        // lastNameがnullまたは空、あるいはfirstNameがnullまたは空の場合
        if (employees.getLastName() == null || employees.getLastName().isEmpty() ||
                employees.getFirstName() == null || employees.getFirstName().isEmpty()) {
            throw new BadRequestException("Name (lastName and firstName) cannot be empty.");
        }

        if (employees.getEmail() == null){
            throw new BadRequestException(" email isn't empty ");
        }
        if (employees.getPosition() == null ){
            throw new BadRequestException(" position isn't empty");
        }
        if (employees.getHireDate() == null){
            throw new BadRequestException(" hireDate isn't empty ");
        }

        if (employeesRepository.findById(id) == null){
            throw new ResourceNotFoundException(id);
        }

        // 必須 1: IDを設定
        employees.setId(id);

        // 必須 2: 更新実行
        employeesRepository.update(employees);

        // 必須 3: 最新データを取得して返す
        return employeesRepository.findById(id);

    }

}

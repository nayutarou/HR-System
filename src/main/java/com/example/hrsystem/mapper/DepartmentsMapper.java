package com.example.hrsystem.mapper;

import com.example.hrsystem.dto.Departments;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DepartmentsMapper {

    // 部署一覧
    @Select("SELECT id, name, location, created_at, updated_at FROM departments")
    List<Departments> findAllDepartments();

    // 部署1つだけ表示
    @Select("SELECT id, name, location, created_at, updated_at FROM departments WHERE id = #{id}")
    Departments findByDepartmentsId(long id);

    // 追加
    @Insert("INSERT INTO departments (name,location) VALUES (#{name},#{location})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int insertDepartments(Departments departments);

    // 削除
    @Delete("DELETE FROM departments WHERE id = #{id}")
    int deleteDepartments(long id);

    // 更新(部署名とか)
    @Update("UPDATE departments SET name = #{name},location = #{location},updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateDepartments(Departments departments);

}

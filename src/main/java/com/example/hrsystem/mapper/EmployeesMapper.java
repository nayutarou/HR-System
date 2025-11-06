package com.example.hrsystem.mapper;

import com.example.hrsystem.dto.Employees;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeesMapper {

    // 従業員一覧表示
    @Select("SELECT id,last_name,first_name,email,department_id,position,hire_date,created_at,updated_at FROM employees ")
    List<Employees> findAllEmployees();

    // 1人だけ表示
    @Select("SELECT id,last_name,first_name,email,department_id,position,hire_date,created_at,updated_at FROM employees WHERE id = #{id}")
    Employees findByEmployeesId(long id);

    // 追加
    @Insert("""
            INSERT INTO employees
             (last_name,first_name,email,department_id,position,hire_date)
              VALUES (#{lastName},#{firstName},#{email},#{departmentId},#{position},#{hireDate})
            """)
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertEmployee(Employees employee);

    // 削除
    @Delete("""
            DELETE FROM employees WHERE id = #{id}
            """)
    int deleteEmployee(long id);

    // 更新(部署とか苗字とかメルアドとか)
    @Update("""
            UPDATE employees SET
             last_name = #{lastName},
             first_name = #{firstName},
             email = #{email},
             department_id = #{departmentId},
             position = #{position},
             updated_at = CURRENT_TIMESTAMP
             WHERE id = #{id}
            """)
    int updateEmployee(Employees employee);

//    private long id;
//    private String firstName;
//    private String lastName;
//    private String email;
//    // ★ 修正点: 所属部署ID (FK) - 命名を departmentId に変更
//    private long departmentId;
//    private String position;
//    // ★ 修正点: 採用日 - hireDate に変更し、LocalDate を使用
//    private LocalDate hireDate;
//    private LocalDateTime createdAt;
//    // 更新日時 (レスポンスとして追加推奨)
//    private LocalDateTime updatedAt;
}

package com.example.hrsystem;

import com.example.hrsystem.controller.DepartmentsController;
import com.example.hrsystem.controller.EmployeesController;
import com.example.hrsystem.dto.Departments;
import com.example.hrsystem.dto.Employees;
import com.example.hrsystem.exception.ResourceNotFoundException;
import com.example.hrsystem.service.DepartmentService;
import com.example.hrsystem.service.EmployeesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({DepartmentsController.class, EmployeesController.class})
@DisplayName("RESTful API Tests")
public class RestfulApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private EmployeesService employeesService;

    // --- Departments API Tests ---

    @Test
    @DisplayName("[Departments] GET /api/departments - Should return all departments")
    void testFindAllDepartments() throws Exception {
        Departments dept1 = new Departments();
        dept1.setId(1L);
        dept1.setName("HR");
        Departments dept2 = new Departments();
        dept2.setId(2L);
        dept2.setName("Engineering");

        when(departmentService.findAll()).thenReturn(List.of(dept1, dept2));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("HR"))
                .andExpect(jsonPath("$[1].name").value("Engineering"));
    }

    @Test
    @DisplayName("[Departments] GET /api/departments/{id} - Should return a department")
    void testFindDepartmentById() throws Exception {
        Departments dept = new Departments();
        dept.setId(1L);
        dept.setName("HR");

        when(departmentService.findById(1L)).thenReturn(dept);

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("HR"));
    }

    @Test
    @DisplayName("[Departments] GET /api/departments/{id} - Should return 404 for not found")
    void testFindDepartmentById_NotFound() throws Exception {
        when(departmentService.findById(99L)).thenThrow(new ResourceNotFoundException("Department not found"));

        mockMvc.perform(get("/api/departments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[Departments] POST /api/departments - Should create a new department")
    void testCreateDepartment() throws Exception {
        Departments newDept = new Departments();
        newDept.setName("Finance");
        newDept.setLocation("5F");

        Departments createdDept = new Departments();
        createdDept.setId(1L);
        createdDept.setName("Finance");
        createdDept.setLocation("5F");

        when(departmentService.insert(any(Departments.class))).thenReturn(createdDept);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDept)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/departments/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Finance"));
    }

    @Test
    @DisplayName("[Departments] PUT /api/departments/{id} - Should update a department")
    void testUpdateDepartment() throws Exception {
        Departments updatedInfo = new Departments();
        updatedInfo.setName("Human Resources");
        updatedInfo.setLocation("10F");

        Departments returnedDept = new Departments();
        returnedDept.setId(1L);
        returnedDept.setName("Human Resources");
        returnedDept.setLocation("10F");

        when(departmentService.update(eq(1L), any(Departments.class))).thenReturn(returnedDept);

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Human Resources"))
                .andExpect(jsonPath("$.location").value("10F"));
    }

    @Test
    @DisplayName("[Departments] DELETE /api/departments/{id} - Should delete a department")
    void testDeleteDepartment() throws Exception {
        when(departmentService.deleteById(1L)).thenReturn(1); // 削除成功時、1を返すと仮定

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @DisplayName("[Departments] DELETE /api/departments/{id} - Should return 404 for not found")
    void testDeleteDepartment_NotFound() throws Exception {
        when(departmentService.deleteById(99L)).thenThrow(new ResourceNotFoundException("Department not found"));

        mockMvc.perform(delete("/api/departments/99"))
                .andExpect(status().isNotFound());
    }

    // --- Employees API Tests ---

    @Test
    @DisplayName("[Employees] GET /api/employees - Should return all employees")
    void testFindAllEmployees() throws Exception {
        Employees emp1 = new Employees();
        emp1.setId(1L);
        emp1.setFirstName("Taro");
        Employees emp2 = new Employees();
        emp2.setId(2L);
        emp2.setFirstName("Hanako");

        when(employeesService.findAll()).thenReturn(List.of(emp1, emp2));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Taro"))
                .andExpect(jsonPath("$[1].firstName").value("Hanako"));
    }

    @Test
    @DisplayName("[Employees] GET /api/employees/{id} - Should return an employee")
    void testFindEmployeeById() throws Exception {
        Employees emp = new Employees();
        emp.setId(1L);
        emp.setFirstName("Taro");

        when(employeesService.findById(1L)).thenReturn(emp);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Taro"));
    }
    
    @Test
    @DisplayName("[Employees] GET /api/employees/{id} - Should return 404 for not found")
    void testFindEmployeeById_NotFound() throws Exception {
        when(employeesService.findById(99L)).thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[Employees] POST /api/employees - Should create a new employee")
    void testCreateEmployee() throws Exception {
        Employees newEmp = new Employees();
        newEmp.setFirstName("Jiro");
        newEmp.setLastName("Suzuki");
        newEmp.setEmail("jiro@example.com");

        Employees createdEmp = new Employees();
        createdEmp.setId(1L);
        createdEmp.setFirstName("Jiro");
        createdEmp.setLastName("Suzuki");
        createdEmp.setEmail("jiro@example.com");

        when(employeesService.insert(any(Employees.class))).thenReturn(createdEmp);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmp)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/employees/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Jiro"));
    }

    @Test
    @DisplayName("[Employees] PUT /api/employees/{id} - Should update an employee")
    void testUpdateEmployee() throws Exception {
        Employees updatedInfo = new Employees();
        updatedInfo.setFirstName("Saburo");
        updatedInfo.setEmail("saburo@example.com");

        Employees returnedEmp = new Employees();
        returnedEmp.setId(1L);
        returnedEmp.setFirstName("Saburo");
        returnedEmp.setEmail("saburo@example.com");

        when(employeesService.update(eq(1L), any(Employees.class))).thenReturn(returnedEmp);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Saburo"));
    }

    @Test
    @DisplayName("[Employees] DELETE /api/employees/{id} - Should delete an employee")
    void testDeleteEmployee() throws Exception {
        when(employeesService.delete(1L)).thenReturn(1); // 削除成功時、1を返すと仮定

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @DisplayName("[Employees] DELETE /api/employees/{id} - Should return 404 for not found")
    void testDeleteEmployee_NotFound() throws Exception {
        when(employeesService.delete(99L)).thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }
}

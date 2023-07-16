package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/addemployee")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        if (savedEmployee != null) {
            String message = "Employee successfully added with ID: " + savedEmployee.getId();
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.badRequest().body("Failed to add employee");
        }
    }



    @GetMapping("/allemployee")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }


    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/employee/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @RequestBody Employee updatedEmployee) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee existingEmployee = employee.get();
            existingEmployee.setName(updatedEmployee.getName());
            existingEmployee.setLastname(updatedEmployee.getLastname());
            existingEmployee.setEmail(updatedEmployee.getEmail());
            existingEmployee.setAddress(updatedEmployee.getAddress());

            Employee savedEmployee = employeeRepository.save(existingEmployee);
            return ResponseEntity.ok(savedEmployee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String id) {
        boolean employeeExists = employeeRepository.existsById(id);
        if (employeeExists) {
            employeeRepository.deleteById(id);
            return ResponseEntity.ok("Employee successfully deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}

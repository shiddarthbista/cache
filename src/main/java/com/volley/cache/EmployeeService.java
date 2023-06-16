package com.volley.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.io.FileNotFoundException;
import java.util.Optional;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private RedisCacheService redisCacheService;

    public Employee createEmployee(Employee employeeRequest) {

        log.info("Inside create employee service");

        Employee employee = Employee.builder()
                .id(employeeRequest.getId())
                .firstName(employeeRequest.getFirstName())
                .lastName(employeeRequest.getLastName())
                .joinedDate(employeeRequest.getJoinedDate())
                .salary(employeeRequest.getSalary())
                .build();
        Employee response = null;

        try {

            // Store the Employee object in the cache. Employee id is a key and employee object as a value.
            redisCacheService.storeEmployee(employee.getId(), employee);

            response = Employee.builder()
                    .id(employeeRequest.getId())
                    .firstName(employeeRequest.getFirstName())
                    .lastName(employeeRequest.getLastName())
                    .joinedDate(employeeRequest.getJoinedDate())
                    .salary(employeeRequest.getSalary())
                    .build();

        } catch (HttpServerErrorException hse) {
            log.error("server exception");
            hse.printStackTrace();
        } catch (Exception e) {
            log.error("Error found :{} " + e.getMessage());
            e.printStackTrace();
        }

        log.info("Successfully saved new employee");

        return response;
    }

    public Employee retrieveEmployeeById(String employeeId) throws FileNotFoundException {
        log.info("Inside retrieve employee by id, service");

        // First trying to retrieve the employee object using employeeId from the cache.
        log.info("calling cache");
        Optional<Employee> employeeOptional = redisCacheService.retrieveEmployee(employeeId);

        //If the employee object not available in the cache DB, then need to retrieve it from the Mongo DB.
        if (employeeOptional.isEmpty()) {
            log.info("Not found in cache");
            throw new FileNotFoundException();
        } else {
            Employee employeeResponse = Employee.builder()
                    .id(employeeOptional.get().getId())
                    .firstName(employeeOptional.get().getFirstName())
                    .lastName(employeeOptional.get().getLastName())
                    .joinedDate(employeeOptional.get().getJoinedDate())
                    .salary(employeeOptional.get().getSalary())
                    .build();

            log.info("Successfully returned employee by id");
            return employeeResponse;
        }
    }
}

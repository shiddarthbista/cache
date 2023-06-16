package com.volley.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * Create new employee
     *
     */
    @PostMapping("/employee")
    public @ResponseBody Employee createEmployee(@RequestBody Employee request) {


        log.info("Inside > create new employee endpoint ");

        return employeeService.createEmployee(request);

    }

    /**
     * Retrieve employee by employee id
     *
     */
    @GetMapping("/employee/{employeeId}")
    public @ResponseBody Employee retrieveEmployeeById(
            @PathVariable String employeeId) throws FileNotFoundException {

        log.info("Inside > retrieve employee by id endpoint ");

        return employeeService.retrieveEmployeeById(employeeId);
    }


    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleMyException1(Exception ex){
        //Do something
        log.info("Kei chaina mula");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("KEI CHAINA MULA");
    }

}

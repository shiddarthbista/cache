package com.volley.cache;

import java.util.Optional;

public interface RedisCacheService {

    void storeEmployee(String employeeId, Employee employee);

    /**
     * Retrieve Employee object
     *
     * @param employeeId as a key
     * @return Employee
     */
    Optional<Employee> retrieveEmployee(String employeeId);

    /**
     * Flush employee object using given employeeId
     *
     * @param employeeId as a key
     */
    void flushEmployeeCache(String employeeId);

    /**
     * Flush all data
     *
     */
    void clearAll();
}

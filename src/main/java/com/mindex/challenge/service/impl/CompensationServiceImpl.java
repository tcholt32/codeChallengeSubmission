package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;

@Service
public class CompensationServiceImpl implements CompensationService {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Override
    public Compensation read(String id){
        Employee employee=employeeService.read(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }
        return employee.getCompensation();
    }
    @Override
    public Employee update(String id,Compensation compensation){
        Employee currentEmployeeRecord=employeeService.read(id);
        if (currentEmployeeRecord == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }
        Compensation currentEmployeeCompensation=
                Compensation.builder()
                        .salary(compensation.getSalary())
                        .effectiveDate(ObjectUtils.isEmpty(compensation.getEffectiveDate())? new Date().toString():compensation.getEffectiveDate())
                        .build();
        currentEmployeeRecord.setCompensation(currentEmployeeCompensation);

        return employeeRepository.save(currentEmployeeRecord);
    }
}

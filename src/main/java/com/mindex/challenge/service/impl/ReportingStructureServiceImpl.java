package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);
    @Autowired
    private EmployeeService employeeService;
    @Override
    public ReportingStructure retrieve(String id) {
        LOG.debug("Retrieving reporting structure for employee [{}]", id);
        Employee targetEmployee=employeeService.read(id);
        if (targetEmployee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }
        ReportingStructure reportingStructure= ReportingStructure.builder().employee(targetEmployee).build();

        if(ObjectUtils.isEmpty(targetEmployee.getDirectReports())){
            LOG.debug("no directReports found for reporting id");
            reportingStructure.setNumberOfReports(0);
            return reportingStructure;
        }
        List<Employee> outerDirectReports=targetEmployee.getDirectReports();
        LOG.debug("number of directReports found {} for reporting id",targetEmployee.getDirectReports().size());
        int totalReports=outerDirectReports.size();
        for (Employee outerDirectReport: outerDirectReports) {
            List<Employee> innerDirectReports=new ArrayList<Employee>();
            Employee innerEmployee=employeeService.read(outerDirectReport.getEmployeeId());

            if(!ObjectUtils.isEmpty(innerEmployee.getDirectReports())) {
                LOG.debug("number of directReports found {} for id [{}]",innerEmployee.getDirectReports().size(),innerEmployee.getEmployeeId());
                innerDirectReports=innerEmployee.getDirectReports();
                totalReports = totalReports + innerEmployee.getDirectReports().size();
            }
            else{
                LOG.debug("no directReports found for id [{}]",innerEmployee.getEmployeeId());
            }
            for (Employee innerDirectReport : innerDirectReports) {
                innerEmployee=employeeService.read(innerDirectReport.getEmployeeId());
                if(!ObjectUtils.isEmpty(innerEmployee.getDirectReports())) {
                    LOG.debug("number of directReports found {} for id [{}]",innerEmployee.getDirectReports().size(),innerEmployee.getEmployeeId());
                    totalReports = totalReports + innerEmployee.getDirectReports().size();
                }
                else{
                    LOG.debug("no directReports found for id [{}]",innerEmployee.getEmployeeId());
                }
            }
        }
        reportingStructure.setNumberOfReports(totalReports);
        return reportingStructure;
    }

}

package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String employeeUrl;
    private String employeeIdReportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";

    }

    @Test
    public void testRetrieve() {
        Employee testEmployee1 = new Employee();
        testEmployee1.setFirstName("John");
        testEmployee1.setLastName("Doe");
        testEmployee1.setDepartment("Engineering");
        testEmployee1.setPosition("Developer");
        Employee testEmployee4 = new Employee();
        testEmployee4.setFirstName("Jake");
        testEmployee4.setLastName("Torque");
        testEmployee4.setDepartment("Engineering");
        testEmployee4.setPosition("Dev Manager");
        Employee testEmployee2 = new Employee();
        testEmployee2.setFirstName("Jim");
        testEmployee2.setLastName("Brown");
        testEmployee2.setDepartment("Information Technology");
        testEmployee2.setPosition("Assistant");
        Employee testEmployee3 = new Employee();
        testEmployee3.setFirstName("Doug");
        testEmployee3.setLastName("Henrik");
        testEmployee3.setDepartment("Information Technology");
        testEmployee3.setPosition("Director");

        // Create checks
        Employee createdEmployee1 = restTemplate.postForEntity(employeeUrl, testEmployee1, Employee.class).getBody();
        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee2, Employee.class).getBody();
        Employee createdEmployee3 = restTemplate.postForEntity(employeeUrl, testEmployee3, Employee.class).getBody();
        Employee createdEmployee4 = restTemplate.postForEntity(employeeUrl, testEmployee4, Employee.class).getBody();
        assertNotNull(createdEmployee1.getEmployeeId());
        assertNotNull(createdEmployee2.getEmployeeId());
        assertNotNull(createdEmployee3.getEmployeeId());
        assertNotNull(createdEmployee4.getEmployeeId());
        assertEmployeeEquivalence(testEmployee1, createdEmployee1);
        assertEmployeeEquivalence(testEmployee2, createdEmployee2);
        assertEmployeeEquivalence(testEmployee3, createdEmployee3);
        assertEmployeeEquivalence(testEmployee4, createdEmployee4);

        List<Employee> directReports3=new ArrayList<Employee>();
        directReports3.add(createdEmployee4);
        directReports3.add(createdEmployee2);
        createdEmployee3.setDirectReports(directReports3);
        String employeeIdUrL= "http://localhost:" + port + "/employee/{id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Employee updatedEmployee3 =
                restTemplate.exchange(employeeIdUrL,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(createdEmployee3, headers),
                        Employee.class,
                        createdEmployee3.getEmployeeId()).getBody();

        List<Employee> directReports4=new ArrayList<Employee>();
        directReports4.add(createdEmployee1);
        createdEmployee4.setDirectReports(directReports4);
        Employee updatedEmployee4 =
                restTemplate.exchange(employeeIdUrL,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(createdEmployee4, headers),
                        Employee.class,
                        createdEmployee4.getEmployeeId()).getBody();
        employeeIdReportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reportStructure";
        // Read checks
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdReportingStructureUrl, ReportingStructure.class, createdEmployee3.getEmployeeId()).getBody();
        assertEquals(reportingStructure.getEmployee().getEmployeeId(), createdEmployee3.getEmployeeId());
        assertEquals(reportingStructure.getNumberOfReports(),3);

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}

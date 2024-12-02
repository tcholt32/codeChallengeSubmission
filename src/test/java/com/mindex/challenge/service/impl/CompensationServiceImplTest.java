package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String employeeUrl;
    private String employeeIdCompensationUrl;

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
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        //ensure object is created
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);
        employeeIdCompensationUrl = "http://localhost:" + port + "/employee/"+createdEmployee.getEmployeeId()+"/compensation";
        Compensation compensation= Compensation.builder().salary(100000.0).effectiveDate("2012-04-23T18:25:43.511Z").build();
        testEmployee.setCompensation(compensation);

        //using id from the created employee object use put request to verify updating the new compensation.
        final ResponseEntity<Employee> response = restTemplate.exchange(
                employeeIdCompensationUrl,
                HttpMethod.PUT,
                new HttpEntity<>(compensation),
                Employee.class
        );
        assertNotNull(response);
        assertEmployeeEquivalence(testEmployee, response.getBody());


        // Read checks for compensation lookup service
        Compensation readCompensation = restTemplate.getForEntity(employeeIdCompensationUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assertCompensationEquivalence(testEmployee.getCompensation(),readCompensation);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertCompensationEquivalence(expected.getCompensation(),actual.getCompensation());
    }
    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        if(ObjectUtils.isEmpty(expected)&&ObjectUtils.isEmpty(actual)){

        }
        else {
            assertEquals(expected.getSalary(), actual.getSalary());
            assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        }
    }
}

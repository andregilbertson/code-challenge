package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;




@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateAndReadCompensation() {
        //create test compensation
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testCompensation.setSalary(new BigDecimal("100000"));
        testCompensation.setEffectiveDate(LocalDate.of(2025, 1, 1));
        testCompensation.setPaidTimeOff(14);
        testCompensation.setBonusAmount(new BigDecimal("10000"));
        testCompensation.setStockOptions(100);

        //create tests
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        assertNotNull(createdCompensation);
        assertNotNull(createdCompensation.getEmployeeId());
        assertCompensationEquivalence(testCompensation, createdCompensation);

        //read tests
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdCompensation.getEmployeeId()).getBody();
        assertNotNull(readCompensation);
        assertEquals(createdCompensation.getEmployeeId(), readCompensation.getEmployeeId());
        assertCompensationEquivalence(createdCompensation, readCompensation);
    }

    @Test
    public void testCreateMultipleCompensation() {
        //create test compensation
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testCompensation.setSalary(new BigDecimal("100000"));
        testCompensation.setEffectiveDate(LocalDate.of(2025, 1, 1));
        testCompensation.setPaidTimeOff(14);
        testCompensation.setBonusAmount(new BigDecimal("10000"));
        testCompensation.setStockOptions(100);

        //add compensation to database
        restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        //modify test compensation
        testCompensation.setSalary(new BigDecimal("200000"));
        testCompensation.setEffectiveDate(LocalDate.of(2026, 1, 1));
        testCompensation.setPaidTimeOff(21);
        testCompensation.setBonusAmount(new BigDecimal("20000"));
        testCompensation.setStockOptions(200);

        //add modified test compensation to database
        restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        //read compensation from database
        Compensation newTestCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, "16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();
        assertNotNull(newTestCompensation);
        assertCompensationEquivalence(testCompensation, newTestCompensation);
    }


    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getPaidTimeOff(), actual.getPaidTimeOff());
        assertEquals(expected.getBonusAmount(), actual.getBonusAmount());
        assertEquals(expected.getStockOptions(), actual.getStockOptions());
    }
}
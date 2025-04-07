package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationHistory;

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
    private String compensationHistoryIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
        compensationHistoryIdUrl = "http://localhost:" + port + "/compensation/{id}/history";
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

    @Test
    public void testCompensationHistory() {
        //create initial compensation for new employee (id = 123456789)
        Compensation initialCompensation = new Compensation();
        initialCompensation.setEmployeeId("123456789");
        initialCompensation.setSalary(new BigDecimal("100000"));
        initialCompensation.setEffectiveDate(LocalDate.of(2025, 1, 1));
        initialCompensation.setPaidTimeOff(14);
        initialCompensation.setBonusAmount(new BigDecimal("10000"));
        initialCompensation.setStockOptions(100);

        //add compensation to database
        restTemplate.postForEntity(compensationUrl, initialCompensation, Compensation.class).getBody();

        //update compensation
        Compensation secondCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, "123456789").getBody();
        secondCompensation.setSalary(new BigDecimal("200000"));
        secondCompensation.setEffectiveDate(LocalDate.of(2026, 1, 1));

        //add updated compensation to database (testing creation of new history List)
        restTemplate.postForEntity(compensationUrl, secondCompensation, Compensation.class).getBody();

        //update compensation again
        Compensation latestCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, "123456789").getBody();
        latestCompensation.setPaidTimeOff(21);
        latestCompensation.setBonusAmount(new BigDecimal("15000"));
        latestCompensation.setStockOptions(200);

        //add latest compensation (testing adding to existing history List)
        restTemplate.postForEntity(compensationUrl, latestCompensation, Compensation.class).getBody();

        //read compensation history
        CompensationHistory compensationHistory = restTemplate.getForEntity(compensationHistoryIdUrl, CompensationHistory.class, "123456789").getBody();

        //run tests for history
        assertNotNull(compensationHistory);
        assertEquals("123456789", compensationHistory.getEmployeeId());
        assertNotNull(compensationHistory.getHistory());
        assertEquals(2, compensationHistory.getHistory().size());
        assertCompensationEquivalence(initialCompensation, compensationHistory.getHistory().get(0));
        assertCompensationEquivalence(secondCompensation, compensationHistory.getHistory().get(1));

        //test that the latest compensation is stored in the Compensation database
        Compensation currentCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, "123456789").getBody();
        assertNotNull(currentCompensation);
        assertCompensationEquivalence(latestCompensation, currentCompensation);
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
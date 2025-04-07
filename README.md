# Employee Data API
*A solution to the Mindex Coding Challenge*  

---

## 1. Solution Overview
**Core Features:**  
✔ **Reporting Structure**: Calculates employee reporting structure on-the-fly (Task 1)  
✔ **Compensation Management**: Creates and reads persistent compensation data (Task 2)  
✔ **Compensation History Tracking**: Additional feature to store old compensation data

**Key Design Choices:**
- Recursive reporting structure calculation with cycle detection
- 1:1 relationship between employee and compensation
- Additional database to store compensation history for each employee

---

## 2. How to Run
**Prerequisites:**  
- Java 17+  
- Gradle  

**Steps:**  
```bash
git clone [your-repo-url]
cd employee-compensation-api
./gradlew bootRun  # Starts app on http://localhost:8080
```

---

## 3. Bug Fixes

### Existing

1. **Problem**: Updating employee data leads to database storing multiple versions of the same employee.
   
   **Cause**: employeeId was not a unique identifier.

   **Solution**: Add the @Id tag before employeeId in Employee.java
   ```java
   // BEFORE:
   public class Employee {
       private String employeeId;

       // rest of class
   }

   // AFTER:
   public class Employee {
       @Id
       private String employeeId;

       // rest of class
   }
   ```

2. **Problem**: Different function calls result in the same LOG message.

   **Cause**: Imprecise language in LOG messages.

   **Solution**: Change LOG messages to better reflect the function they are a part of.
   ```java
   // BEFORE:
   public Employee read(@PathVariable String id) {
       LOG.debug("Received employee create request for id [{}]", id);

       // rest of method
   }

   // AFTER:
   public Employee read(@PathVariable String id) {
       LOG.debug("Received employee read request for id [{}]", id);

       // rest of method
   }
   ```
   
---

## 4. Data Representation

### ER Diagram

![ER Diagram (2)](https://github.com/user-attachments/assets/58d992c9-357a-48b0-a8bc-5f1c2332dd17)

### Relationships
   1. **Employee-Compensation (1:1, optional)**:
      - Each employee has **at most one** compensation. 
      - Every compensation is linked to **one and only one** employee.
   2. **Employee-Compensation History (1:1, optional)**:
      - Each employee has **at most one** compensation history.
      - Every compensation history is linked to **one and only one** employee.
   3. **Compensation History-Archived Compensation (1:M, embedded)**:
      - Each compensation history is embedded with **one or many** archived compensations.
      - Every archived compensation belongs to **one and only one** compensation history.

### Reasoning

   
   
---

## 5. API Endpoints

|  Endpoint                        | Method | Description |
| ---------------------------      | ------ | ----------- |
| /reportingStructure/{employeeId} | GET | Get employee's reporting structure |
| /compensation | POST | Add new compensation data to the compensation database. Add preexisting compensation data to the compensation history database.|
| /compensation/{employeeId} | GET | Get employee's current compensation data |
| /compensation/{employeeId}/history | GET | Get employee's past compensation data |

---

## 6. 


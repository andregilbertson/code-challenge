# Employee Data API
*Solution to Mindex's Coding Challenge*  

---

## 1. Solution Overview
**Core Features:**  
✔ **Reporting Structure**: Calculates employee reporting structure on-the-fly (Task 1)  
✔ **Compensation Management**: Creates and reads persistent compensation data (Task 2)  
✔ **Compensation History Tracking**: Automatically stores past compensation data

**Key Design Choices:**
- Recursive reporting structure calculation with cycle detection
- 1:1 relationship between employee and current compensation
- Separate database for accessing past compensation data

---

## 2. How to Run
**Prerequisites:**  
- Java 17+  
- Gradle  

**Steps:**  
```bash
git clone github.com/andregilbertson/code-challenge
cd mindex-code-challenge
./gradlew bootRun  # starts app on http://localhost:8080
```
   
---

## 3. API Endpoints

|  Endpoint                        | Method | Description |
| ---------------------------      | ------ | ----------- |
| /reportingStructure/{employeeId} | GET | Get employee's reporting structure. |
| /compensation | POST | Add new compensation data to the compensation database. Add preexisting compensation data to the compensation history database.|
| /compensation/{employeeId} | GET | Get employee's current compensation data. |
| /compensation/{employeeId}/history | GET | Get employee's past compensation data. |

---

## 4. Design Choices

### Recursive Reports Calculation

#### Why Recursion?

1. **Optimal for Hierarchies**
   -  Employee hierarchy forms a tree
   -  Recursion allows easy traversal of this data structure
2. **Simplicity**
   - Easier to program and understand other, more complex solutions (i.e. manual iteration)
3. **Efficiency**
   - _O(n)_ runtime (where _n_ = # of employees)

#### Issues
- **Circular Reporting**: The given data types do not prevent cycles in the reporting structure (an employee being their own directReport).
   - Solution: Use a **HashSet** to track unique employees in the reporting structure.

### Compensation Data Handling

**Initial Thoughts**

When developing the Compensation data type, I wanted at most one compensation object to be stored in the database for each employee. This way, employeeId could be a unique foreign key for the compensation database. With this goal in mind, my first iteration deleted old compensation from the database if it was present, and replaced it with new compensation data. However, this felt bad as the data would be lost forever. I wanted to solve this issue, so I brainstormed a few solutions.

**Possible Solutions**
1. Store all compensation data in the same database
   - Pros: Keep current database design, smaller scope
   - Cons: Past and present compensation data in the same place may cause confusion
2. Store old compensation data in the compensation object of each employee
   - Pros: Same as 1, separates past and present data
   - Confusing data framework (old data stored within the new data)
3. Create new data type and database to store compensation history
   - Pros: Separates past and present data in a simple, easy-to-understand way
   - Cons: Increases size of scope, must implement a third database

**Final Decision**

After weighing the pros and cons of each option, I ultimately decided to go with option 3. It would take some extra time to implement, but I felt it was the best solution to the problem at hand. I still wanted to keep the scope somewhat small, so I chose to implement a simple, read-only database for compensation history. I figured this would be a good balance between functionality and simplicity. 

### Database Schema

![ER Diagram (2)](https://github.com/user-attachments/assets/58d992c9-357a-48b0-a8bc-5f1c2332dd17)

#### Relationships
   1. **Employee-Compensation (1:1, optional)**:
      - Each employee has **at most one** compensation. 
      - Every compensation is linked to **one and only one** employee.
   2. **Employee-Compensation History (1:1, optional)**:
      - Each employee has **at most one** compensation history.
      - Every compensation history is linked to **one and only one** employee.
   3. **Compensation History-Archived Compensation (1:M, embedded)**:
      - Each compensation history is embedded with **one or many** archived compensations.
      - Every archived compensation belongs to **one and only one** compensation history.

#### Reasoning
- Clear distinction between employee's current and past compensation.
- At most one compensation per employee stored in the compensation database.
   - Allows the compensation database to be indexed by employeeId without returning multiple compensation objects.
- Old compensation data is stored in the compensation history database.
   - Allows for data that was removed from the compensation database to remain accessible.
- Compensation history is extremely useful for HR and Auditing.
   - Currently read-only, but could be expanded and improved in the future (i.e. add functionality for accessing specific attributes of past compensation).
 
---

## 5. Bug Fixes

- **Problem**: Updating employee data leads to database storing multiple versions of the same employee.
   
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

- **Problem**: Different function calls result in the same LOG message.

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

   _Note: In addition to the in-code test suites, I also used Postman and MongoDB Compass to test the API endpoints and database mechanisms._
   
   ---

## 6. Future Improvements
1. **Expand Compensation History**
   - Add functionality for accessing specific attributes of past compensation
   - Implement separate Controller and Service classes for compensation history
   - Create complex functions for analyzing compensation trends (e.g. changes in salary, department-specific statistics, etc.)
2. **More Rigorous Testing**
   - Implement more test suites for handling more scenarios, larger data, and more complex hierarchies
   - Would have been part of initial solution if more time was available
3. **Docker Deployment**
   - Reduce set-up and deployment time
   - Improve scalability and portability
     
   ---

## 7. Acknowledgements
Thank you Mindex for this opportunity to test my programming skills! Any and all feedback is much appreciated.


package repository;

import domain.Employee;

public interface EmployeeRepository extends Repository<Employee> {

    Employee  findEmployee(String username);

}

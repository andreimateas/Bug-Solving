package repository;

import domain.Employee;
import domain.Programmer;
import domain.Tester;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class EmployeeDBRepository implements EmployeeRepository{

    SessionFactory sessionFactory;

    public EmployeeDBRepository(SessionFactory sessionFactory){
        this.sessionFactory=sessionFactory;
    }

    @Override
    public Employee findEmployee(String username) {
        Employee employee=null;

        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try{
                transaction=session.beginTransaction();
                employee= session.createQuery("from Programmer where username= :user",Programmer.class).setParameter("user",username).setMaxResults(1).uniqueResult();
                transaction.commit();
            }
            catch (RuntimeException re){
                if(transaction!=null)
                    transaction.rollback();
            }
        }
        if(employee==null){
            try(Session session=sessionFactory.openSession()){
                Transaction transaction=null;
                try{
                    transaction=session.beginTransaction();
                    employee= session.createQuery("from Tester where username= :user", Tester.class).setParameter("user",username).setMaxResults(1).uniqueResult();
                    transaction.commit();
                }
                catch (RuntimeException re){
                    if(transaction!=null)
                        transaction.rollback();
                }
            }
        }

        return employee;
    }

    @Override
    public Iterable<Employee> findAll() {
        return null;
    }

    @Override
    public Employee save(Employee entity) {
        return null;
    }

    @Override
    public Employee delete(Long id) {
        return null;
    }
}

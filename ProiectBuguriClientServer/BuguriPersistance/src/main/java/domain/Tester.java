package domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="Tester")
@Table(name="Testers")
public class Tester extends Employee{

    public Tester(){}

    public Tester(Long id, String username, String password, String name) {
        super(id, username, password, name);
    }
}

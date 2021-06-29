package domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="Programmer")
@Table(name="Programmers")
public class Programmer extends Employee{

    public Programmer(){}

    public Programmer(Long id, String username, String password, String name) {
        super(id, username, password, name);
    }
}

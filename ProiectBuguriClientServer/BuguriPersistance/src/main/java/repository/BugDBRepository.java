package repository;

import domain.Bug;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class BugDBRepository implements BugRepository{

    SessionFactory sessionFactory;

    public BugDBRepository(SessionFactory sessionFactory){
        this.sessionFactory=sessionFactory;
    }

    @Override
    public Iterable<Bug> getSolvedBugs() {
        List<Bug> bugs=new ArrayList<>();
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                bugs = session.createQuery("from Bug as b where b.state=true", Bug.class).list();
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return  bugs;

    }

    @Override
    public Iterable<Bug> getUnsolvedBugs() {
        List<Bug> bugs=new ArrayList<>();
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                bugs = session.createQuery("from Bug as b where b.state=false", Bug.class).list();
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return  bugs;
    }

    @Override
    public void modifyState(Long id,boolean newState,String newDescription) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                Bug bug = session.load(Bug.class, id );
                bug.setState(newState);
                bug.setDescription(bug.getDescription()+" "+newDescription);
                session.update(bug);
                tx.commit();

            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
    }

    @Override
    public void modifyName(Long id, String newName) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                Bug bug = session.load(Bug.class, id );
                bug.setName(newName);
                session.update(bug);
                tx.commit();

            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
    }

    @Override
    public Iterable<Bug> findAll() {
        return null;
    }

    @Override
    public Bug save(Bug entity) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(entity);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Bug delete(Long id) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Query q = session.createQuery("from Bug as b where b.id= :bid", Bug.class);
                q.setParameter("bid",id);
                Bug b = (Bug) q.setMaxResults(1)
                        .uniqueResult();
                session.delete(b);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }
}

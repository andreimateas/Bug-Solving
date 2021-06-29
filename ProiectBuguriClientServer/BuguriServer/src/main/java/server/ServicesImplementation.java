package server;

import domain.Bug;
import domain.Employee;
import domain.Programmer;
import repository.BugRepository;
import repository.EmployeeRepository;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImplementation implements IServices {

    private EmployeeRepository employeeRepo;
    private BugRepository bugRepo;
    private Map<Long,IObserver> loggedClients;

    private final int defaultThreads=5;

    public ServicesImplementation(EmployeeRepository employeeRepo, BugRepository bugRepo) {
        this.employeeRepo = employeeRepo;
        this.bugRepo = bugRepo;
        loggedClients=new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Employee findEmployee(String username, String password, IObserver clientProg,IObserver clientTest) throws ServiceException, RemoteException {
        Employee employee=employeeRepo.findEmployee(username);
        if (employee==null)
            throw new ServiceException("Non-existent user");
        if(!employee.getPassword().equals(password))
            throw new ServiceException("Wrong password");
        if(loggedClients.get(employee.getId())!=null)
            throw new ServiceException("User already logged in");
        if(employee.getClass()== Programmer.class)
            loggedClients.put(employee.getId(), clientProg);
        else
            loggedClients.put(employee.getId(),clientTest);
        return employee;
    }

    @Override
    public synchronized Iterable<Bug> getSolvedBugs() throws ServiceException, RemoteException {
        return bugRepo.getSolvedBugs();
    }

    @Override
    public synchronized Iterable<Bug> getUnsolvedBugs() throws ServiceException, RemoteException {
        return bugRepo.getUnsolvedBugs();
    }

    @Override
    public synchronized void modifyBugState(Long id, boolean newState, String newDescription) throws ServiceException, RemoteException {
        bugRepo.modifyState(id, newState,newDescription);
        if(newState==true)
            notifySolvedBug(id,newDescription);
        else{
            List<Bug> bugList= (List<Bug>) bugRepo.getUnsolvedBugs();
            Bug b=null;
            for(Bug bug:bugList){
                if(bug.getId().equals(id))
                    b=bug;
            }
            notifyUnsolvedBug(b);}
    }

    @Override
    public synchronized void modifyBugName(Long id, String newName) throws ServiceException, RemoteException {
        bugRepo.modifyName(id,newName);
        notifyChangedBugName(id,newName);
    }

    @Override
    public synchronized void addBug(Bug bug) throws ServiceException, RemoteException {
        bugRepo.save(bug);
        notifyAddedBug(bug);
    }

    @Override
    public synchronized void deleteBug(Long id) throws ServiceException, RemoteException {
        bugRepo.delete(id);
        notifyDeletedBug(id);
    }

    @Override
    public synchronized void logout(Employee employee, IObserver client) throws ServiceException, RemoteException {
        IObserver removedClient=loggedClients.remove(employee.getId());
        if(removedClient==null)
            throw new ServiceException("User not logged in");
    }

    private void notifySolvedBug(Long id, String newDescription) throws ServiceException{
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);
        for(IObserver client: loggedClients.values()){
            executor.execute(()->{
                try{
                    client.bugSolved(id,newDescription);
                }catch (ServiceException | RemoteException e){
                    System.err.println("Error notify solved bug" + e);
                }

            });
        }
        executor.shutdown();
    }

    private void notifyUnsolvedBug(Bug bug) throws ServiceException{
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);
        for(IObserver client: loggedClients.values()){
            executor.execute(()->{
                try{
                    client.bugUnsolved(bug);
                }catch (ServiceException | RemoteException e){
                    System.err.println("Error notify unsolved bug" + e);
                }

            });
        }
        executor.shutdown();
    }

    private void notifyChangedBugName(Long id, String newName) throws ServiceException{
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);
        for(IObserver client: loggedClients.values()){
            executor.execute(()->{
                try{
                    client.bugChangedName(id,newName);
                }catch (ServiceException | RemoteException e){
                    System.err.println("Error notify change bug name" + e);
                }

            });
        }
        executor.shutdown();
    }

    private void notifyAddedBug(Bug bug) throws ServiceException{
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);
        for(IObserver client: loggedClients.values()){
            executor.execute(()->{
                try{
                    client.bugAdded(bug);
                }catch (ServiceException | RemoteException e){
                    System.err.println("Error notify add bug" + e);
                }

            });
        }
        executor.shutdown();
    }

    private void notifyDeletedBug(Long id) throws ServiceException{
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);
        for(IObserver client: loggedClients.values()){
            executor.execute(()->{
                try{
                    client.bugRemoved(id);
                }catch (ServiceException | RemoteException e){
                    System.err.println("Error notify delete bug" + e);
                }

            });
        }
        executor.shutdown();
    }
}

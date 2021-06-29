package services;



import domain.Bug;
import domain.Employee;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalTime;

public interface IServices extends Remote {

    Employee findEmployee(String username, String password,IObserver clientProg,IObserver clientTest) throws ServiceException, RemoteException;

    Iterable<Bug> getSolvedBugs() throws ServiceException, RemoteException;

    Iterable<Bug> getUnsolvedBugs() throws ServiceException, RemoteException;

    void modifyBugState(Long id,boolean newState,String newDescription) throws ServiceException, RemoteException;

    void modifyBugName(Long id,String newName) throws ServiceException, RemoteException;

    void addBug(Bug bug) throws ServiceException, RemoteException;

    void deleteBug(Long id) throws ServiceException, RemoteException;

    void logout(Employee employee,IObserver client) throws ServiceException, RemoteException;
}

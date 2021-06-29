package services;


import domain.Bug;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {

    void bugAdded(Bug bug) throws ServiceException, RemoteException;

    void bugRemoved(Long id) throws ServiceException, RemoteException;

    void bugChangedName(Long id,String newName) throws ServiceException, RemoteException;

    void bugSolved(Long id,String newDescription) throws ServiceException, RemoteException;

    void bugUnsolved(Bug bug) throws ServiceException, RemoteException;
}

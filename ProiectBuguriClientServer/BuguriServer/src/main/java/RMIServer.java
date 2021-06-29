import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import repository.BugDBRepository;
import repository.BugRepository;
import repository.EmployeeDBRepository;
import repository.EmployeeRepository;
import server.ServicesImplementation;
import services.IServices;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

public class RMIServer {

    public static void main(String[] args){

        Properties serverProperties=new Properties();
        try {
            serverProperties.load(RMIServer.class.getResourceAsStream("/server.properties"));
            serverProperties.list(System.out);
        } catch (IOException e) {
            System.err.println("Properties not found "+e);
            return;
        }

        SessionFactory sessionFactory = null;
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Exception "+e);
            StandardServiceRegistryBuilder.destroy( registry );
        }

        EmployeeRepository employeeRepository=new EmployeeDBRepository(sessionFactory);
        BugRepository bugRepository=new BugDBRepository(sessionFactory);
        IServices serverService=new ServicesImplementation(employeeRepository,bugRepository);

        try{

            String name=serverProperties.getProperty("buguri.rmi.serverID","default");
            IServices stub=(IServices) UnicastRemoteObject.exportObject(serverService,0);
            Registry registry2= LocateRegistry.getRegistry();
            registry2.rebind(name,stub);
        }catch(Exception e){
            System.err.println("Server exception" + e);
            e.printStackTrace();
        }


    }

}

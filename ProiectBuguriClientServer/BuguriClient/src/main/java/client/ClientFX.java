package client;

import client.controller.LoginController;
import client.controller.ProgrammerController;
import client.controller.TesterController;
import domain.Tester;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.BugDBRepository;
import repository.BugRepository;
import repository.EmployeeDBRepository;
import repository.EmployeeRepository;
import services.IServices;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class ClientFX extends Application {

    private Stage primaryStage;

    private static String defaultServer = "localhost";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Properties clientProperties = new Properties();
        try {
            clientProperties.load(ClientFX.class.getResourceAsStream("/client.properties"));
            clientProperties.list(System.out);
        } catch (IOException e) {
            System.err.println("Client properties not found " + e);
            return;
        }

        String name=clientProperties.getProperty("buguri.rmi.serverID","default");
        String serverIP = clientProperties.getProperty("buguri.server.host", defaultServer);

        try {

            Registry registry= LocateRegistry.getRegistry(serverIP);
            IServices server = (IServices) registry.lookup(name);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            LoginController ctrl = loader.<LoginController>getController();
            ctrl.setServer(server);


            FXMLLoader cloader = new FXMLLoader();
            cloader.setLocation(getClass().getResource("/views/programmer.fxml"));
            Parent croot = cloader.load();
            ProgrammerController progCtrl = cloader.<ProgrammerController>getController();
            progCtrl.setServer(server);

            FXMLLoader dloader = new FXMLLoader();
            dloader.setLocation(getClass().getResource("/views/tester.fxml"));
            Parent droot = dloader.load();
            TesterController testCtrl = dloader.<TesterController>getController();
            testCtrl.setServer(server);

            ctrl.setProgrammerController(progCtrl);
            ctrl.setTesterController(testCtrl);
            ctrl.setParent(croot,droot);

            primaryStage.setScene(new Scene(root, 500, 350));
            primaryStage.show();
        }catch (Exception e) {
            System.err.println("Client exception:"+e);
            e.printStackTrace();
        }
    }
}

package client.controller;

import domain.Bug;
import domain.Employee;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class ProgrammerController implements IObserver , Initializable, Serializable {

    IServices server;
    Employee employee;

    ObservableList<Bug> modelUnsolvedBugs=FXCollections.observableArrayList();

    @FXML
    TextArea descriptionAreaP;
    @FXML
    TableView<Bug> bugTableP;
    @FXML
    TableColumn<Bug,String> nameColumnP;
    @FXML
    TableColumn<Bug,String> descriptionColumnP;


    public void setServer(IServices srv) {
        server =srv;
        try{
            UnicastRemoteObject.exportObject(this,0);
        }catch(RemoteException e){
            System.out.println(e);
        }
    }

    public void setUser(Employee employee){
        this.employee=employee;
    }

    public void init(){
        initModel();
    }

    @FXML
    public void initialize(){
        nameColumnP.setCellValueFactory(new PropertyValueFactory<Bug,String >("name"));
        descriptionColumnP.setCellValueFactory(new PropertyValueFactory<Bug,String >("description"));
        bugTableP.setItems(modelUnsolvedBugs);

        bugTableP.setRowFactory( tv -> {
            TableRow<Bug> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Bug rowData = row.getItem();
                    descriptionAreaP.clear();
                    List<String> list= Arrays.asList(rowData.getDescription().split("     "));
                    for(String el:list)
                        descriptionAreaP.appendText(el+"\n");
                }
            });
            return row ;
        });

        descriptionAreaP.setWrapText(true);
    }

    private void initModel(){
        Iterable<Bug> unsolvedBugs=null;
        try {
            unsolvedBugs = server.getUnsolvedBugs();
        }catch (RemoteException e) {
            e.printStackTrace();
        }
        List<Bug> unsolvedBugsList= StreamSupport.stream(unsolvedBugs.spliterator(),false).collect(Collectors.toList());
        modelUnsolvedBugs.setAll(unsolvedBugsList);
    }

    public void handleSolveBugP(ActionEvent actionEvent) {
        if(bugTableP.getSelectionModel().getSelectedItem()!=null) {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/bugInfo.fxml"));
                AnchorPane root  = (AnchorPane) loader.load();

                Stage modStage=new Stage();
                modStage.initModality(Modality.WINDOW_MODAL);
                Scene scene=new Scene(root);
                modStage.setScene(scene);
                SolveBugController controller=loader.getController();
                controller.setService(server,modStage,bugTableP.getSelectionModel().getSelectedItem());
                modStage.show();

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            Alert message=new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setContentText("No bug selected!");
            message.showAndWait();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumnP.setCellValueFactory(new PropertyValueFactory<Bug,String >("name"));
        descriptionColumnP.setCellValueFactory(new PropertyValueFactory<Bug,String >("description"));
        bugTableP.setItems(modelUnsolvedBugs);

        bugTableP.setRowFactory( tv -> {
            TableRow<Bug> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Bug rowData = row.getItem();
                    descriptionAreaP.clear();
                    List<String> list= Arrays.asList(rowData.getDescription().split("     "));
                    for(String el:list)
                        descriptionAreaP.appendText(el+"\n");
                }
            });
            return row ;
        });

        descriptionAreaP.setWrapText(true);
    }

    @Override
    public void bugAdded(Bug bug) throws ServiceException, RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTableP.getItems().add(bug);
            }
        });
    }

    @Override
    public void bugRemoved(Long id) throws ServiceException, RemoteException {

    }

    @Override
    public void bugChangedName(Long id, String newName) throws ServiceException, RemoteException {
        int poz=0;
        for(Bug bug:bugTableP.getItems()){
            if(bug.getId().equals(id))
                break;
            poz++;
        }
        final int finalPoz = poz;
        Bug newBug=bugTableP.getItems().get(finalPoz);
        newBug.setName(newName);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTableP.getItems().set(finalPoz,newBug);
            }
        });
    }

    @Override
    public void bugSolved(Long id, String newDescription) throws ServiceException, RemoteException {
        int poz=0;
        for(Bug bug:bugTableP.getItems()){
            if(bug.getId().equals(id))
                break;
            poz++;
        }
        final int finalPoz = poz;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTableP.getItems().remove(finalPoz);
            }
        });
    }

    @Override
    public void bugUnsolved(Bug bug) throws ServiceException, RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTableP.getItems().add(bug);
            }
        });
    }

    public void handleLogout(ActionEvent actionEvent) {
        try{
            server.logout(employee,this);
        }catch(RemoteException re){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setContentText(re.getMessage());
            message.showAndWait();
        }
        catch (ServiceException se){
            System.out.println("Logout error");
        }
        ((Node)actionEvent.getSource()).getScene().getWindow().hide();
    }
}

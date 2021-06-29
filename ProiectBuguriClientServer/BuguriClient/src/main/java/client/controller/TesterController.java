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

public class TesterController implements IObserver , Initializable, Serializable {

    IServices server;
    Employee employee;

    ObservableList<Bug> modelSolvedBugs= FXCollections.observableArrayList();
    ObservableList<Bug> modelUnsolvedBugs=FXCollections.observableArrayList();

    @FXML
    TextArea descriptionArea1T;
    @FXML
    TableView<Bug> bugTable1T;
    @FXML
    TableColumn<Bug,String> nameColumn1T;
    @FXML
    TableColumn<Bug,String> descriptionColumn1T;

    @FXML
    TextArea descriptionArea2T;
    @FXML
    TableView<Bug> bugTable2T;
    @FXML
    TableColumn<Bug,String> nameColumn2T;
    @FXML
    TableColumn<Bug,String> descriptionColumn2T;


    public void setServer(IServices service) {
        this.server = service;
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
        nameColumn1T.setCellValueFactory(new PropertyValueFactory<Bug,String >("name"));
        descriptionColumn1T.setCellValueFactory(new PropertyValueFactory<Bug,String >("description"));
        bugTable1T.setItems(modelUnsolvedBugs);

        nameColumn2T.setCellValueFactory(new PropertyValueFactory<Bug,String >("name"));
        descriptionColumn2T.setCellValueFactory(new PropertyValueFactory<Bug,String >("description"));
        bugTable2T.setItems(modelSolvedBugs);

        bugTable1T.setRowFactory( tv -> {
            TableRow<Bug> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Bug rowData = row.getItem();
                    descriptionArea1T.clear();
                    List<String> list= Arrays.asList(rowData.getDescription().split("     "));
                    for(String el:list)
                        descriptionArea1T.appendText(el+"\n");
                }
            });
            return row ;
        });

        bugTable2T.setRowFactory( tv -> {
            TableRow<Bug> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Bug rowData = row.getItem();
                    descriptionArea2T.clear();
                    List<String> list= Arrays.asList(rowData.getDescription().split("     "));
                    for(String el:list)
                        descriptionArea2T.appendText(el+"\n");
                }
            });
            return row ;
        });

        descriptionArea2T.setWrapText(true);
        descriptionArea1T.setWrapText(true);

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

        Iterable<Bug> solvedBugs=null;
        try {
            solvedBugs = server.getSolvedBugs();
        }catch (RemoteException e) {
            e.printStackTrace();
        }
        List<Bug> solvedBugsList= StreamSupport.stream(solvedBugs.spliterator(),false).collect(Collectors.toList());
        modelSolvedBugs.setAll(solvedBugsList);
    }

    public void handleAddBug(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/addBug.fxml"));
            AnchorPane root  = (AnchorPane) loader.load();

            Stage addStage=new Stage();
            addStage.initModality(Modality.WINDOW_MODAL);
            Scene scene=new Scene(root);
            addStage.setScene(scene);
            AddBugController controller=loader.getController();
            controller.setService(server,addStage);
            addStage.show();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void handleModifyName(ActionEvent actionEvent) {
        if(bugTable1T.getSelectionModel().getSelectedItem()!=null) {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/modifyName.fxml"));
                AnchorPane root  = (AnchorPane) loader.load();

                Stage modStage=new Stage();
                modStage.initModality(Modality.WINDOW_MODAL);
                Scene scene=new Scene(root);
                modStage.setScene(scene);
                ModifyNameController controller=loader.getController();
                controller.setService(server,modStage,bugTable1T.getSelectionModel().getSelectedItem());
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

    public void handleRemoveBug(ActionEvent actionEvent) {
        if(bugTable2T.getSelectionModel().getSelectedItem()!=null) {
            try{
                server.deleteBug(bugTable2T.getSelectionModel().getSelectedItem().getId());
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

    public void handleSetUnsolved(ActionEvent actionEvent) {
        if(bugTable2T.getSelectionModel().getSelectedItem()!=null) {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/setUnsolved.fxml"));
                AnchorPane root  = (AnchorPane) loader.load();

                Stage modStage=new Stage();
                modStage.initModality(Modality.WINDOW_MODAL);
                Scene scene=new Scene(root);
                modStage.setScene(scene);
                SetUnsolvedController controller=loader.getController();
                controller.setService(server,modStage,bugTable2T.getSelectionModel().getSelectedItem());
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
        nameColumn1T.setCellValueFactory(new PropertyValueFactory<Bug,String >("name"));
        descriptionColumn1T.setCellValueFactory(new PropertyValueFactory<Bug,String >("description"));
        bugTable1T.setItems(modelUnsolvedBugs);

        nameColumn2T.setCellValueFactory(new PropertyValueFactory<Bug,String >("name"));
        descriptionColumn2T.setCellValueFactory(new PropertyValueFactory<Bug,String >("description"));
        bugTable2T.setItems(modelSolvedBugs);

        bugTable1T.setRowFactory( tv -> {
            TableRow<Bug> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Bug rowData = row.getItem();
                    descriptionArea1T.clear();
                    List<String> list= Arrays.asList(rowData.getDescription().split("     "));
                    for(String el:list)
                        descriptionArea1T.appendText(el+"\n");
                }
            });
            return row ;
        });

        bugTable2T.setRowFactory( tv -> {
            TableRow<Bug> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Bug rowData = row.getItem();
                    descriptionArea2T.clear();
                    List<String> list= Arrays.asList(rowData.getDescription().split("     "));
                    for(String el:list)
                        descriptionArea2T.appendText(el+"\n");
                }
            });
            return row ;
        });

        descriptionArea2T.setWrapText(true);
        descriptionArea1T.setWrapText(true);
    }

    @Override
    public void bugAdded(Bug bug) throws ServiceException, RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTable1T.getItems().add(bug);
            }
        });
    }

    @Override
    public void bugRemoved(Long id) throws ServiceException, RemoteException {
        int poz=0;
        for(Bug bug:bugTable2T.getItems()){
            if(bug.getId().equals(id))
                break;
            poz++;
        }
        final int finalPoz = poz;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTable2T.getItems().remove(finalPoz);
            }
        });

    }

    @Override
    public void bugChangedName(Long id, String newName) throws ServiceException, RemoteException {
        int poz=0;
        for(Bug bug:bugTable1T.getItems()){
            if(bug.getId().equals(id))
                break;
            poz++;
        }
        final int finalPoz = poz;
        Bug newBug=bugTable1T.getItems().get(finalPoz);
        newBug.setName(newName);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTable1T.getItems().set(finalPoz,newBug);
            }
        });
    }

    @Override
    public void bugSolved(Long id, String newDescription) throws ServiceException, RemoteException {
        int poz=0;
        for(Bug bug:bugTable1T.getItems()){
            if(bug.getId().equals(id))
                break;
            poz++;
        }
        final int finalPoz = poz;
        Bug bug=bugTable1T.getItems().get(finalPoz);
        bug.setDescription(bug.getDescription()+newDescription);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTable1T.getItems().remove(finalPoz);
                bugTable2T.getItems().add(bug);
            }
        });
    }

    @Override
    public void bugUnsolved(Bug bug) throws ServiceException, RemoteException {
        int poz=0;
        for(Bug b:bugTable2T.getItems()){
            if(b.getId().equals(bug.getId()))
                break;
            poz++;
        }
        final int finalPoz = poz;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugTable2T.getItems().remove(finalPoz);
                bugTable1T.getItems().add(bug);
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

package client.controller;

import domain.Bug;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.IServices;
import services.ServiceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddBugController {

    IServices service;
    Stage addStage;

    @FXML
    TextField newName;
    @FXML
    TextArea newDescription;

    public void setService(IServices service, Stage addStage) {
        this.service=service;
        this.addStage=addStage;
    }

    public void handleAddNewBug(ActionEvent actionEvent) {
        if(newName.getText().equals("") || newDescription.getText().equals("")){
            Alert message=new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setContentText("You must enter a name and a description");
            message.showAndWait();
        }
        else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Bug bug = new Bug(0L, newName.getText(), "[ " + formatter.format(LocalDateTime.now()) + " ] : " + newDescription.getText() + "     ", false);
                service.addBug(bug);
                addStage.close();
            } catch (ServiceException se) {
                Alert message = new Alert(Alert.AlertType.ERROR);
                message.setTitle("Error");
                message.setContentText(se.getMessage());
                message.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

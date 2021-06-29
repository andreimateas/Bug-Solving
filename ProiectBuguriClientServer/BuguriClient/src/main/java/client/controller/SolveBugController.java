package client.controller;

import domain.Bug;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import services.IServices;
import services.ServiceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SolveBugController {

    IServices service;
    Stage modStage;
    Bug bug;

    @FXML
    TextArea addedDescription;

    public void setService(IServices service, Stage modStage, Bug selectedItem) {
        this.service=service;
        this.modStage=modStage;
        this.bug=selectedItem;
    }

    public void handleConfirm(ActionEvent actionEvent) {
        if(addedDescription.getText().equals("")){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setContentText("You must enter a description");
            message.showAndWait();
        }else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                service.modifyBugState(bug.getId(), true, "[ " + formatter.format(LocalDateTime.now()) + " ] : " + addedDescription.getText() + "     ");
                modStage.close();
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

package client.controller;

import domain.Bug;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.IServices;
import services.ServiceException;

public class ModifyNameController {

    IServices service;
    Stage modStage;
    Bug bug;

    @FXML
    TextField modifiedName;

    public void setService(IServices service, Stage modStage, Bug selectedItem) {
        this.service=service;
        this.modStage=modStage;
        this.bug=selectedItem;
    }

    public void handleConfirm(ActionEvent actionEvent) {
        if(modifiedName.getText().equals("")){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setContentText("You must enter a name");
            message.showAndWait();
        }else {
            try {
                service.modifyBugName(bug.getId(), modifiedName.getText());
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

package client.controller;

import domain.Employee;
import domain.Programmer;
import domain.Tester;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.IServices;
import services.ServiceException;

import java.io.Serializable;
import java.rmi.RemoteException;


public class LoginController implements Serializable {

    IServices server;
    ProgrammerController progCtrl;
    TesterController testCtrl;
    Employee currentUser;
    Parent progParent;
    Parent testParent;

    @FXML
    TextField usernameText;
    @FXML
    PasswordField passwordText;

    @FXML
    public void initialize(){
    }

    public void setServer(IServices srv){
        server =srv;
    }

    public void setParent(Parent p1,Parent p2){
        progParent =p1;
        testParent =p2;
    }

    public void setUser(Employee user){
        this.currentUser=user;
    }

    public void setProgrammerController(ProgrammerController progController){
        this.progCtrl=progController;
    }

    public void setTesterController(TesterController testController){
        this.testCtrl=testController;
    }

    public void handleLogin(ActionEvent actionEvent) {

        if(usernameText.getText().equals("") || passwordText.getText().equals("")){
            Alert message=new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error");
            message.setContentText("Type your username and password");
            message.showAndWait();
        }else {

            try {
                Employee employee = server.findEmployee(usernameText.getText(), passwordText.getText(),progCtrl,testCtrl);

                if(employee.getClass()== Tester.class){
                    Stage stage=new Stage();
                    stage.setTitle(employee.getUsername());
                    stage.setScene(new Scene(testParent));

                    stage.show();
                    testCtrl.setUser(employee);
                    testCtrl.init();
                    ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
                }
                else if(employee.getClass()== Programmer.class){
                    Stage stage=new Stage();
                    stage.setTitle(employee.getUsername());
                    stage.setScene(new Scene(progParent));

                    stage.show();
                    progCtrl.setUser(employee);
                    progCtrl.init();
                    ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
                }


            } catch (ServiceException se) {
                Alert message = new Alert(Alert.AlertType.ERROR);
                message.setTitle("Error");
                message.setContentText(se.getMessage());
                message.showAndWait();
            }catch(RemoteException re){
                Alert message = new Alert(Alert.AlertType.ERROR);
                message.setTitle("Error");
                message.setContentText(re.getMessage());
                message.showAndWait();
            }
            catch (Exception e) {
                Alert message = new Alert(Alert.AlertType.ERROR);
                message.setTitle("Error");
                message.setContentText(e.getMessage());
                message.showAndWait();
            }
        }

        usernameText.clear();
        passwordText.clear();

    }
}

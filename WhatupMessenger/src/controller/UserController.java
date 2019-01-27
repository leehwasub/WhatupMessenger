package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import network.Client;
import utils.MessageList;

public class UserController implements Initializable {

	@FXML
	private JFXButton viewProfile;

	@FXML
	private JFXButton sendMessage;

	@FXML
	private Label message;

	@FXML
	private JFXButton talkAlone;
	
	private String userId;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public void setMessage(String message1) {
		message.setText(message1 + "´Ô ÀÔ´Ï´Ù.");
		this.userId = message1;
	}
	
	public void talkAloneAction(ActionEvent event) {
		Client.send("requestTalk/" + Client.getUserId() + "/" + userId);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(userId + "´Ô¿¡°Ô ±Ó¼Ó¸» ¿äÃ»À» º¸³Â½À´Ï´Ù.");
		alert.showAndWait();
	}
	
	public void sendMessageAction(ActionEvent event) {
		Stage messageBoxSend = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/messageSendFXML.fxml"));
		try {
			Parent root = (Parent) loader.load();
			MessageSendController messageSendController = loader.getController();
			messageSendController.initReceiver(userId);
			Scene scene = new Scene(root);
			messageBoxSend.setScene(scene);
			messageBoxSend.setResizable(false);
			messageBoxSend.setTitle("messageSend");
			messageBoxSend.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void viewProfileAction(ActionEvent event) {
		Stage userInfo = new Stage();
		message.getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ProfileViewFXML.fxml"));
		try {
			Parent root = (Parent)loader.load();
			ProfileViewController profileViewController = loader.getController();
			profileViewController.setUserId(userId);
			Scene scene = new Scene(root);
			userInfo.setScene(scene);
			userInfo.setResizable(false);
			userInfo.setTitle(userId);
			userInfo.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}

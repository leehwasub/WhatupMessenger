package controller;

import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import network.Client;

public class TalkAloneController implements Initializable{

	@FXML
	private JFXTextArea chatArea;

	@FXML
	private JFXTextField chatField;

	@FXML
	private FontAwesomeIcon chatSend;
	
	private String anotherUserId;

	Socket socket;

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public void addmessage(String message) {
		String[] token = message.split("/");
		String sender = token[1];
		String content = token[3];
		chatArea.appendText(sender + " : " + content + "\n");
	}

	public void chatSendEnter() {
		if (chatField.getText().isEmpty())
			return;
		String message = "messageTalkAlone/" + Client.getUserId() + "/" + anotherUserId + "/" + chatField.getText();
		Client.send(message);
		chatField.setText("");
		chatField.requestFocus();
	}

	public void setAnotherUserId(String anotherUserId) {
		this.anotherUserId = anotherUserId;
	}
	
	
}

package controller;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import network.Client;
import utils.DatabaseUtil;

public class LoginController implements Initializable{
	
    @FXML
    private JFXPasswordField password;

    @FXML
    private Label forgotPassword;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXButton login;

    @FXML
    private JFXButton signup;
    
    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		id.setStyle("-fx-text-inner-color: #a0a2ab");
		password.setStyle("-fx-text-inner-color: #a0a2ab");
	}
	
	public void signupAction(ActionEvent event) {
		login.getScene().getWindow().hide();
		try {
			Stage signup = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("/FXML/SignupFXML.fxml"));
			Scene scene = new Scene(root, 368, 841);
			signup.setScene(scene);
			signup.show();
			signup.setResizable(false);
			signup.setTitle("Whatup Messenger");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void loginAction(ActionEvent event) {
		conn = DatabaseUtil.getConnection();
		String sql = "select * from users where userId = ? and userPassword = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id.getText());
			pstmt.setString(2, password.getText());
			rs = pstmt.executeQuery();
			int cnt = 0;
			while(rs.next()) {
				cnt++;
			}
			if(cnt == 0) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("username and password Is not correct");
				alert.show();
			} else if(!checkLoginState()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("the user is already logined.");
				alert.show();
				return;
			} else if(cnt == 1) {
				setUserLogin();
				login.getScene().getWindow().hide();
				Stage signup = new Stage();
				Client.setUserId(id.getText());
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/mainFXML.fxml"));
			        Parent root = (Parent)loader.load();
					MainController mainController = loader.getController();
					mainController.initUserId(id.getText());
					Scene scene = new Scene(root, 868, 495);
					signup.setScene(scene);
					signup.show();
					signup.setResizable(false);
					signup.setTitle("Whatup Messenger");
					Client.setMainController(mainController);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			rs.close();
			conn.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean checkLoginState() {
		conn = DatabaseUtil.getConnection();
		String sql = "select userIsLogin from users where userId = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id.getText());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getBoolean(1)) {
					return false;
				}
				else return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setUserLogin() {
		conn = DatabaseUtil.getConnection();
		String sql = "update users set userIsLogin = true where userId = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id.getText());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

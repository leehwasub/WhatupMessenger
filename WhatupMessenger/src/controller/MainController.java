package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.Client;
import utils.DatabaseUtil;

public class MainController implements Initializable {

	@FXML
	private Label logout;
	
	@FXML
	private Label messageBox;

    @FXML
	private Label home;
    
    @FXML
    private Label profile;
    
    @FXML
	private AnchorPane mainPane;

	private String userId;
	
	private AnchorPane subPage;
	
	private Stage stage;

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	private void setNode(Node node) {
		mainPane.getChildren().clear();
		mainPane.getChildren().add((Node)node);
		FadeTransition ft = new FadeTransition(Duration.millis(1500));
		ft.setNode(node);
		ft.setFromValue(0.1);
		ft.setToValue(1);
		ft.setCycleCount(1);
		ft.setAutoReverse(false);
		ft.play();
	}

	public void createProfilePage() {
		try {
			subPage = FXMLLoader.load(getClass().getResource("/FXML/profileFXML.fxml"));
			setNode(subPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createHomePage() {
		try {
			subPage = FXMLLoader.load(getClass().getResource("/FXML/homeFXML.fxml"));
			setNode(subPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createProfileEditPage() {
		try {
			subPage = FXMLLoader.load(getClass().getResource("/FXML/profileEditFXML.fxml"));
			setNode(subPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createMessageBoxPage() {
		try {
			subPage = FXMLLoader.load(getClass().getResource("/FXML/messageBoxFXML.fxml"));
			setNode(subPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		 createHomePage();
		 ProfileController.setMainController(this);
		 ProfileEditController.setMainController(this);
	}
	
	public void closeprogram(){
		System.out.println("프로그램 종료");
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("you really want to close whatup Messenger?");
		Optional<ButtonType> action = alert.showAndWait();
		if(action.get() == ButtonType.OK) {
			setUserLogout();
			Client.send("deleteUser/" + Client.getUserId());
			stage.close();
		}
	}

	public void initUserId(String userId) {
		this.userId = userId;
		Client.startClient("127.0.0.1", 8888);
		// showImage();
	}
	
	public void profileClick() {
		createProfilePage();
	}
	
	public void messageBoxClick(){
		createMessageBoxPage();
	}
	
	public void homeClick() {
		createHomePage();
		Client.send("addAllUsers/" + userId);
	}
	
	public void getWindow() {
		 stage = (Stage)profile.getScene().getWindow();
		 stage.setOnCloseRequest(e ->{
			 e.consume();
			 closeprogram(); 
		 });
	}
	
	public void logoutClick() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("you really want to logout?");
		Optional<ButtonType> action = alert.showAndWait();
		if(action.get() == ButtonType.OK) {
			Client.send("deleteUser/" + userId);
			setUserLogout();
			logout.getScene().getWindow().hide();
			try {
				Stage login = new Stage();
				Parent root = FXMLLoader.load(getClass().getResource("/FXML/LoginFXML.fxml"));
				Scene scene = new Scene(root, 356, 367);
				login.setScene(scene);
				login.show();
				login.setResizable(false);
				login.setTitle("Whatup Messenger");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setUserLogout() {
		conn = DatabaseUtil.getConnection();
		String sql = "update users set userIsLogin = false where userId = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}

package controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.DatabaseUtil;

public class SignupController implements Initializable{

	@FXML
	private JFXPasswordField password;

	@FXML
	private ToggleGroup gender;

	@FXML
	private JFXTextField phone;

	@FXML
	private JFXTextField name;

	@FXML
	private JFXTextField email;

	@FXML
	private JFXTextField id;

	@FXML
	private JFXRadioButton female;

	@FXML
	private JFXButton login;

	@FXML
	private JFXPasswordField passwordCheck;

	@FXML
	private JFXButton signup;

	@FXML
	private JFXRadioButton male;
	
	@FXML
	private JFXTextArea intro;
	
	@FXML
	private JFXButton browser;
	
	@FXML
	private ImageView imageView;
	private Image image;
	private FileInputStream fis;
	
	private FileChooser fileChooser;
	private File file;
	private Stage stage;
	private Desktop desktop = Desktop.getDesktop();
	
	
	 Connection conn;
	 PreparedStatement pstmt;
	 ResultSet rs;

	@Override
	public void initialize(URL loc, ResourceBundle resources) {
		id.setStyle("-fx-text-inner-color:  #a0a2ab");
		name.setStyle("-fx-text-inner-color:  #a0a2ab");
		password.setStyle("-fx-text-inner-color:  #a0a2ab");
		passwordCheck.setStyle("-fx-text-inner-color:  #a0a2ab");
		phone.setStyle("-fx-text-inner-color:  #a0a2ab");
		email.setStyle("-fx-text-inner-color:  #a0a2ab");
		intro.setStyle("-fx-text-inner-color:  #a0a2ab");
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
			//new FileChooser.ExtensionFilter("All files", "*.*"),
			new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
			//new FileChooser.ExtensionFilter("Text File", "*.txt")
		);
	}
	
	public void loginAction(ActionEvent event) {
		signup.getScene().getWindow().hide();
		try {
			Stage login = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("/FXML/LoginFXML.fxml"));
			Scene scene = new Scene(root, 356, 367);
			login.setScene(scene);
			login.show();
			login.setResizable(false);
			login.setTitle("Whatup Messenger");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void browserAction(ActionEvent event) {
		stage = (Stage)login.getScene().getWindow();
		file = fileChooser.showOpenDialog(stage);
		/*try {
			desktop.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		if(file != null) {
			System.out.println(""+file.getAbsolutePath());
			image = new Image(file.getAbsoluteFile().toURI().toString(), imageView.getFitWidth(), imageView.getFitHeight(), true, true);
			imageView.setImage(image);
			imageView.setPreserveRatio(true);
		}
	}
	
	public void signupAction(ActionEvent event) {
		if(id.getText().isEmpty() || password.getText().isEmpty() || getGender().equals("none") || name.getText().isEmpty()
				|| email.getText().isEmpty() || phone.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("All Fields have to be filled");
			alert.show();
			return;
		} else if(!password.getText().equals(passwordCheck.getText())) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("password is different with passwordCheck");
			alert.show();
			return;
		} else if(!idCheck(id.getText())) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("your userId is already used");
			alert.show();
			return;
		}
		conn = DatabaseUtil.getConnection();
		String sql = "insert into users(userId, userName, userPassword, userGender, userPhone, userEmail, userIntro, userImage, userIslogin) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id.getText());
			pstmt.setString(2, name.getText());
			pstmt.setString(3, password.getText());
			pstmt.setString(4, getGender());
			pstmt.setString(5, phone.getText());
			pstmt.setString(6, email.getText());
			pstmt.setString(7, intro.getText());
			if(file != null) {
				try {
					fis = new FileInputStream(file);
					pstmt.setBinaryStream(8, fis, file.length());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			else {
				pstmt.setBinaryStream(8, null);
			}
			pstmt.setBoolean(9, false);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setContentText("Signup success");
			alert.showAndWait();
			signup.getScene().getWindow().hide();
			try {
				Stage login = new Stage();
				Parent root = FXMLLoader.load(getClass().getResource("/FXML/LoginFXML.fxml"));
				Scene scene = new Scene(root, 356, 367);
				login.setScene(scene);
				login.show();
				login.setResizable(false);
				login.setTitle("Whatup Messenger");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getGender() {
		if(male.isSelected()) {
			return "male";
		} else if(female.isSelected()) {
			return "female";
		}
		return "none";
	}
	
	private boolean idCheck(String id) {
		conn = DatabaseUtil.getConnection();
		String sql = "select * from users where userId = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			int cnt = 0;
			while(rs.next()) {
				cnt++;
			}
			if(cnt == 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	

}

package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import network.Client;
import utils.DatabaseUtil;

public class ProfileEditController implements Initializable{

	@FXML
    private JFXRadioButton male;
	
	@FXML
    private JFXRadioButton female;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXTextArea intro;

    @FXML
    private JFXButton save;

    @FXML
    private JFXTextField name;

    @FXML
    private Label id;

    @FXML
    private ImageView picture;

    @FXML
    private JFXTextField email;
    
    @FXML
    private JFXButton browser;
    
    private String userId;
    
    private Image image;
    private FileChooser fileChooser;
	private File file;
	private Stage stage;
	private FileInputStream fis;
    
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	private static MainController mainController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.userId = Client.getUserId();
		getUserInfo();
	}
	
	public void browserAction(ActionEvent event) {
		stage = (Stage)phone.getScene().getWindow();
		file = fileChooser.showOpenDialog(stage);
		/*try {
			desktop.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		if(file != null) {
			System.out.println(""+file.getAbsolutePath());
			image = new Image(file.getAbsoluteFile().toURI().toString(), 200, 200, true, true);
			picture.setImage(image);
			picture.setPreserveRatio(true);
		}
	}
	
	private void getUserInfo() {
		conn = DatabaseUtil.getConnection();
		String fileName = "D://whatupMessenger/" + userId + ".jpg";
		String sql = "select * from users where userId = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				id.setText(rs.getString(1));
				name.setText(rs.getString(2));
				if(rs.getString(4).equals("male")) {
					male.setSelected(true);
					female.setSelected(false);
				}else if(rs.getString(4).equals("female")) {
					female.setSelected(true);
					male.setSelected(false);
				} 
				phone.setText(rs.getString(5));
				email.setText(rs.getString(6));
				intro.setText(rs.getString(7));
				InputStream is = rs.getBinaryStream(8);
				if(is != null) {
					Image image = new Image("file:" + fileName, 200, 200, true, true);
					picture.setImage(image);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void saveAction(ActionEvent event) {
		if(id.getText().isEmpty() || getGender().equals("none") || name.getText().isEmpty()
				|| email.getText().isEmpty() || phone.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("All Fields have to be filled");
			alert.show();
			return;
		} 
		conn = DatabaseUtil.getConnection();
		String sql = "update users set userName = ?, userGender = ?, userPhone = ?, userEmail = ?, userIntro = ? where userId = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name.getText());
			pstmt.setString(2, getGender());
			pstmt.setString(3, phone.getText());
			pstmt.setString(4, email.getText());
			pstmt.setString(5, intro.getText());
			pstmt.setString(6, userId);
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
			if(file != null) {
				updateUserImage();
			}
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setContentText("save success");
			alert.showAndWait();
			mainController.createProfilePage();
		}
	}
	
	private void updateUserImage() {
		conn = DatabaseUtil.getConnection();
		String sql = "update users set userImage = ? where userId = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			if(file != null) {
				try {
					fis = new FileInputStream(file);
					pstmt.setBinaryStream(1, fis, file.length());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
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

	public static MainController getMainController() {
		return mainController;
	}

	public static void setMainController(MainController mainController) {
		ProfileEditController.mainController = mainController;
	}

	
}

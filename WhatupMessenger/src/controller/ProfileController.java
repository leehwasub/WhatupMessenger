package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import network.Client;
import utils.DatabaseUtil;

public class ProfileController implements Initializable{

    @FXML
    private Label gender;

    @FXML
    private Label phone;

    @FXML
    private JFXButton edit;

    @FXML
    private JFXTextArea intro;

    @FXML
    private Label name;

    @FXML
    private Label id;

    @FXML
    private ImageView picture;

    @FXML
    private Label email;
    
    private String userId;
    
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	private static MainController mainController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.userId = Client.getUserId();
		getUserInfo();
	}
	
	public void editAction(ActionEvent event){
		mainController.createProfileEditPage();
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
				gender.setText(rs.getString(4));
				phone.setText(rs.getString(5));
				email.setText(rs.getString(6));
				intro.setText(rs.getString(7));
				Image image = new Image("file:" + fileName, 200, 200, true, true);
				picture.setImage(image);
				System.out.println(fileName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static MainController getMainController() {
		return mainController;
	}

	public static void setMainController(MainController mainController1) {
		mainController = mainController1;
	}

}

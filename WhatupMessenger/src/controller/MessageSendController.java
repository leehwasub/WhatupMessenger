package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import network.Client;
import utils.DatabaseUtil;
import utils.UserList;

public class MessageSendController implements Initializable {

	@FXML
	private ListView<UserList> userlist;

	private ObservableList<UserList> data = FXCollections.observableArrayList();

	@FXML
	private Label receiver;

	@FXML
	private JFXTextArea messageContent;
	
    @FXML
    private JFXButton submit;
    
    private String userId;

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getUsers();
		this.userId = Client.getUserId();
	}

	public void initReceiver(String receiver1) {
		receiver.setText(receiver1);
	}

	public void displayListView() {
		userlist.setCellFactory(new Callback<ListView<UserList>, ListCell<UserList>>() {
			@Override
			public ListCell<UserList> call(ListView<UserList> param) {
				ListCell<UserList> cell = new ListCell<UserList>() {
					protected void updateItem(UserList user, boolean btl) {
						super.updateItem(user, btl);
						if (btl) {
							setGraphic(null);
							setText(null);
						} else if (user != null) {
							Image image = new Image("file:" + user.getImage(), 80, 80, true, true);
							System.out.println("file:" + user.getImage());
							ImageView imageview = new ImageView(image);
							setGraphic(imageview);
							setText("  " + user.getName());
						}
					}
				};
				return cell;
			}
		});
		userlist.setItems(data);
	}

	public void getUsers() {
		conn = DatabaseUtil.getConnection();
		String sql = "select * from users";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println("유저찾음!");
				data.add(new UserList(rs.getString(1), rs.getString(1)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < data.size(); i++) {
			data.get(i).setImage(showImage(data.get(i).getName()));
		}
		displayListView();
	}

	private String showImage(String userId) {
		conn = DatabaseUtil.getConnection();
		String sql = "select userImage from users where userId = ?";
		String fileName = "D://whatupMessenger/" + userId + ".jpg";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				InputStream is = rs.getBinaryStream(1);
				OutputStream os = new FileOutputStream(new File(fileName));
				byte[] contents = new byte[1024];
				int size = 0;
				while ((size = is.read(contents)) != -1) {
					os.write(contents, 0, size);
				}
				return fileName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void userListClick() {
		ObservableList<UserList> users;
		users = userlist.getSelectionModel().getSelectedItems();
		if(!users.isEmpty()) {
			receiver.setText(users.get(0).getName());
		}
	}
	
	public void submitAction(ActionEvent event) {
		if(receiver.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("you have to select user to send!");
			alert.showAndWait();
			return;
		}
		conn = DatabaseUtil.getConnection();
		String sql = "insert into message(messageSender, messageReceiver, messageContent) values(?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, receiver.getText());
			pstmt.setString(3, messageContent.getText());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText("the message is sucessfully sended!");
		alert.showAndWait();
		submit.getScene().getWindow().hide();
	}
}

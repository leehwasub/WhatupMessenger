package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Callback;
import network.Client;
import utils.DatabaseUtil;
import utils.UserList;

public class HomeController implements Initializable{

	@FXML
	private ListView<UserList> userlist;

	private ObservableList<UserList> data = FXCollections.observableArrayList();

	@FXML
	private JFXTextArea chatArea;

	@FXML
	private JFXTextField chatField;

	@FXML
	private FontAwesomeIcon chatSend;

	Socket socket;

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Client.setHomeController(this);
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
		chatField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				chatSendEnter();
			}
		});
	}

	public void addUserList(String name) {
		data.add(new UserList(name, showImage(name)));
		displayListView();
	}

	public void addmessage(String message) {
		chatArea.appendText(message);
	}
	
	public void requestTalkAlert(String sender, String receiver) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText(sender + " requested a talk to you alone. Do you accept it?");
		Optional<ButtonType> action = alert.showAndWait();
		if(action.get() == ButtonType.OK) {
			Client.send("acceptTalk/" + sender + "/" + receiver);
		}else {
			Client.send("refuseTalk/" + sender + "/" + receiver);
		}
	}
	
	public void refuseTalkAlert(String sender, String receiver) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setHeaderText(null);
		alert.setContentText(receiver + " refused your request.");
		alert.showAndWait();
	}
	
	public void acceptTalk(String anotherUser) {
		Stage talkAlone = new Stage();
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/talkAloneFXML.fxml"));
	        root = (Parent)loader.load();
			TalkAloneController talkaloneController = loader.getController();
			talkaloneController.setAnotherUserId(anotherUser);
			Scene scene = new Scene(root);
			talkAlone.setScene(scene);
			talkAlone.show();
			talkAlone.setResizable(false);
			talkAlone.setTitle(Client.getUserId() + "丛狼 庇加富 芒, 惑措规 蜡历 : " + anotherUser);
			Client.setTalkAloneController(talkaloneController);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(String user) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getName().equals(user)) {
				data.remove(i);
				break;
			}
		}
	}

	public void chatSendEnter() {
		if (chatField.getText().isEmpty())
			return;
		String message = "message/" + Client.getUserId() + " : " + chatField.getText() + "\n";
		Client.send(message);
		chatField.setText("");
		chatField.requestFocus();
	}

	public void addItem(String name) {
		data.add(new UserList(name, showImage(name)));
	}

	public void removeItem(String name) {
		System.out.println("被");
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getName().equals(name)) {
				data.remove(i);
				break;
			}
		}
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
			try {
				Stage user = new Stage();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/userFXML.fxml"));
		        Parent root = (Parent)loader.load();
				UserController userController = loader.getController();
				userController.setMessage(users.get(0).getName());
				Scene scene = new Scene(root);
				user.setScene(scene);
				user.setResizable(false);
				user.setTitle(users.get(0).getName());
				user.showAndWait();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

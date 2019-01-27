package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import network.Client;
import utils.DatabaseUtil;
import utils.MessageList;

public class MessageBoxController implements Initializable {

	@FXML
	private TableView<MessageList> messageList;

	@FXML
	private JFXButton send;

	@FXML
	private JFXTextArea messageContent;

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	private String userId;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("MessageBox::initialize");
		this.userId = Client.getUserId();
		setTable();
	}

	public ObservableList<MessageList> getMessage() {
		conn = DatabaseUtil.getConnection();
		String sql = "select * from message where messageReceiver = ?";
		ObservableList<MessageList> messages = FXCollections.observableArrayList();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				messages.add(new MessageList(rs.getInt(1), rs.getString(2), rs.getString(4)));
				System.out.println(messages.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}

	public void getMessageContent() {
		MessageList messagelist = messageList.getSelectionModel().getSelectedItem();
		if (messagelist != null) {
			String sender = messagelist.getSender();
			String content = messagelist.getContent();
			messageContent.clear();
			messageContent.appendText("∫∏≥Ω¿Ã : " + sender + "\n");
			messageContent.appendText(content);
			messageContent.setMaxWidth(610);
		}
	}

	public void sendAction() {
		Stage messageBoxSend = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/messageSendFXML.fxml"));
		ObservableList<MessageList> messageSelected;
		messageSelected = messageList.getSelectionModel().getSelectedItems();
		try {
			Parent root = (Parent) loader.load();
			MessageSendController messageSendController = loader.getController();
			if(!messageSelected.isEmpty()) {
				messageSendController.initReceiver(messageSelected.get(0).getSender());
			}
			Scene scene = new Scene(root);
			messageBoxSend.setScene(scene);
			messageBoxSend.setResizable(false);
			messageBoxSend.setTitle("messageSend");
			messageBoxSend.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteAction() {
		ObservableList<MessageList> messageSelected, allMessages;
		allMessages = messageList.getItems();
		messageSelected = messageList.getSelectionModel().getSelectedItems();
		if (messageSelected.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("you have to select a message you want to delete");
			alert.showAndWait();
		} else {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setHeaderText(null);
			alert.setContentText("you really want to delete the selected message?");
			Optional<ButtonType> action = alert.showAndWait();
			if (action.get() == ButtonType.OK) {
				int messageId = messageSelected.get(0).getId();
				messageSelected.forEach(allMessages::remove);
				conn = DatabaseUtil.getConnection();
				String sql = "delete from message where messageId = ?";
				try {
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, messageId);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setTable() {
		TableColumn<MessageList, String> senderColumn = new TableColumn<>("sender");
		senderColumn.setMinWidth(150);
		senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));

		TableColumn<MessageList, String> contentColumn = new TableColumn<>("content");
		contentColumn.setMinWidth(484);
		contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

		messageList.setItems(getMessage());
		messageList.getColumns().addAll(senderColumn, contentColumn);
	}

}

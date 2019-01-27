package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import controller.HomeController;
import controller.MainController;
import controller.TalkAloneController;
import javafx.application.Platform;

public class Client {

	private static Socket socket;
	private static String userId;
	
	private static HomeController homeController;
	private static MainController mainController;
	private static TalkAloneController talkAloneController;
	
	public static void startClient(String IP, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					send("addUserList/" + userId);
					receive();
				} catch (Exception e) {
					e.printStackTrace();
					if (!socket.isClosed()) {
						stopClient();
						System.out.println("[���� ���� ����]");
						Platform.exit();
					}
				}
			}
		};
		thread.start();
	}

	
	public static void stopClient() {
		try {
			if (socket != null && !socket.isClosed()) {
				System.out.println("[���� ���� ������]");
				send("deleteUser/" + userId);
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ������ ���� �޼����� ���޹޴� �޼ҵ�
	public static void receive() {
		while (true) {
			try {
				InputStream in = socket.getInputStream();
				DataInputStream dis = new DataInputStream(in);
				String message = dis.readUTF();
				String[] token = message.split("/");
				System.out.println("���� -> Ŭ���̾�Ʈ �޼��� : " + message);
				Platform.runLater(() -> {
					if(token[0].equals("message")) {
						homeController.addmessage(token[1]);
					} else if(token[0].equals("addUserList")) {
						homeController.addUserList(token[1]);
					} else if(token[0].equals("oldUserList")) {
						homeController.addUserList(token[1]);
					} else if(token[0].equals("deleteUser")) {
						homeController.deleteUser(token[1]);
					} else if(token[0].equals("requestTalk")) {
						homeController.requestTalkAlert(token[1], token[2]);
					} else if(token[0].equals("refuseTalk")) {
						homeController.refuseTalkAlert(token[1], token[2]);
					} else if(token[0].equals("acceptTalk")) {
						homeController.acceptTalk(token[1]);
					} else if(token[0].equals("messageTalkAlone")) {
						System.out.println("�ϴܹ���");
						talkAloneController.addmessage(message);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				stopClient();
				break;
			}
		}
	}

	// ������ �޼����� ������ �޼ҵ�
	public static void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					DataOutputStream dos = new DataOutputStream(out);
					dos.writeUTF(message);
				} catch (Exception e) {
					e.printStackTrace();
					stopClient();
				}
			}
		};
		thread.start();
	}
	
	public static Socket getSocket() {
		return socket;
	}

	public static void setSocket(Socket socket) {
		Client.socket = socket;
	}

	public static String getUserId() {
		return userId;
	}

	public static void setUserId(String userId) {
		Client.userId = userId;
	}

	public static HomeController getHomeController() {
		return homeController;
	}

	public static void setHomeController(HomeController homeController) {
		Client.homeController = homeController;
	}

	public static MainController getMainController() {
		return mainController;
	}

	public static void setMainController(MainController mainController) {
		Client.mainController = mainController;
	}

	public static TalkAloneController getTalkAloneController() {
		return talkAloneController;
	}

	public static void setTalkAloneController(TalkAloneController talkAloneController) {
		Client.talkAloneController = talkAloneController;
	}
	
}

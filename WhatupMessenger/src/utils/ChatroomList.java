package utils;

public class ChatroomList {
	
	private int chatroomId;
	private String chatroomMaster;
	private String chatroomTitle;
	
	public ChatroomList(int chatroomId, String chatroomMaster, String chatroomTitle) {
		this.chatroomId = chatroomId;
		this.chatroomMaster = chatroomMaster;
		this.chatroomTitle = chatroomTitle;
	}

	public int getChatroomId() {
		return chatroomId;
	}

	public String getChatroomMaster() {
		return chatroomMaster;
	}

	public String getChatroomTitle() {
		return chatroomTitle;
	}

	public void setChatroomId(int chatroomId) {
		this.chatroomId = chatroomId;
	}

	public void setChatroomMaster(String chatroomMaster) {
		this.chatroomMaster = chatroomMaster;
	}

	public void setChatroomTitle(String chatroomTitle) {
		this.chatroomTitle = chatroomTitle;
	}
	
	
}

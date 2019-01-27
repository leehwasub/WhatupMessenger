package utils;

public class UserList {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	private String name;
	private String image;
	
	public UserList(String name, String image) {
		this.name = name;
		this.image = image;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}	
	@Override
	public String toString() {
		return "UserList [name=" + name + ", image=" + image + "]";
	}
	
}

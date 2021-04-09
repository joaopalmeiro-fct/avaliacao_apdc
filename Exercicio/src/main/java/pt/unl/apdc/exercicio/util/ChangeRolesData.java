package pt.unl.apdc.exercicio.util;

public class ChangeRolesData {

	public String username;
	public String tokenID;
	public String otherUser;
	public String role;
	
	public ChangeRolesData() {
		
	}
	
	public ChangeRolesData(String username, String tokenID, String otherUser, String role) {
		this.username = username;
		this.tokenID = tokenID;
		this.otherUser = otherUser;
		this.role = role;
	}
	
	public boolean checkUser (String role) {
		return role.equals("USER");
	}
	
	public boolean checkGBO (String role) {
		return role.equals("GBO");
	}
	
	public boolean checkGA (String role) {
		return role.equals("GA");
	}
	
	public boolean checkSU (String role) {
		return role.equals("SU");
	}
	
	
}

package pt.unl.apdc.exercicio.util;

public class StateResources {

	private String username;
	private String token;
	private String otherUser;
	private String newState;
	
	public StateResources() {
		
	}
	
	public StateResources(String username, String token, String otherUser, String newState) {
		this.username = username;
		this.token = token;
		this.otherUser = otherUser;
		this.newState = newState;
	}
	
	public String getUsername () {
		return username;
	}
	
	public String getOtherUser () {
		return otherUser;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getNewState () {
		return newState;
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

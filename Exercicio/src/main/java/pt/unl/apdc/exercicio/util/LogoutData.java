package pt.unl.apdc.exercicio.util;


public class LogoutData {

	public String username;
	public String tokenID;
	
	public LogoutData() {
		
	}
	
	public LogoutData (String username, String tokenID) {
		this.username = username;
		this.tokenID = tokenID;
	}
}

package pt.unl.apdc.exercicio.util;

public class RegisterData {

	public String username;
	public String password;
	
	public String confirmation;
	public String email;
	public String name;
	
	public RegisterData () {
		
	}
	
	public RegisterData (String username, String password, String confirmation, String email) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
	}
	
	public RegisterData (String username, String password, String confirmation, String email, String name) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.name = name;
	}

	public boolean validRegistration() {
		//for now, validating all the usernames and passwords which ate not empty
		return !username.isEmpty() && !password.isEmpty();
	}
	
	public boolean checkConformation () {
		//validating the confirmation password
		return password.equals(confirmation) && !email.isEmpty() && !name.isEmpty();
	}
}

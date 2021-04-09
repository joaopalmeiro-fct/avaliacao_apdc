package pt.unl.apdc.exercicio.util;

public class AccountData {

	public String username;
	public String password;
	public String confirmation;
	public String email;
	

	
	public AccountData () {}
	
	public AccountData (String username, String password, String confirmation, String email) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
	}
	
	/**
	 * Checks if the password has capital and lower case letters, as well as a non alphabetical character
	 * @return
	 */
	public boolean checkPassword () {
		char character;
	    boolean capitalLetter = false;
	    boolean lowerLetter = false;
	    boolean specialCharacter = false;
	    for(int i=0;i < password.length();i++) {
	        character = password.charAt(i);
	        if( !Character.isAlphabetic(character)) {
	            specialCharacter = true;
	        }
	        else if (Character.isUpperCase(character)) {
	            capitalLetter = true;
	        } else if (Character.isLowerCase(character)) {
	            lowerLetter = true;
	        }
	        if(capitalLetter && lowerLetter && specialCharacter)
	            return true;
	    }
	    return false;
	}
	
	/**
	 * Checks if the confirmation and password are the same
	 * @return
	 */
	public boolean checkConfirmation () {
		return password.equals(confirmation);
	}
	
	public boolean checkEmail () {
		boolean aux = email.contains("@");
		if (!aux) {
			return false;
		}
		return true;
	}
}

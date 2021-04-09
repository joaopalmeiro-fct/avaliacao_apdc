package pt.unl.apdc.exercicio.util;

public class AccountComplementarData {

	public String username;
	public String tokenID;
	
	public String whatToChange;
	public String attribute;
	
	public String profile;
	public String landline;
	public String phoneNumber;
	public String address;
	public String complementarAddress;
	public String locality;
	
	public AccountComplementarData() {
		
	}
	
	public AccountComplementarData(String username, String tokenID, String whatToChange, String attribute) {
		this.username = username;
		this.tokenID = tokenID;
		this.whatToChange = whatToChange;
		this.attribute = attribute;
	}
	
	public AccountComplementarData(String username, String tokenID, String profile, String landline, 
			String phoneNumber, String address, String complementarAdreess, String locality) {
		this.attribute = null;
		this.username = username;
		this.tokenID = tokenID;
		
		this.profile = profile;
		this.landline = landline;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.complementarAddress = complementarAdreess;
		this.locality = locality;
	}
	
	public String getChange () {
		return whatToChange;
	}
	
	public String getUpdate() {
		switch (whatToChange) {
		case "profile":
			return addProfile(attribute);
		case "landline":
			return addLandline(attribute);
		case "phoneNumber":
			return addPhoneNumber(attribute);
		case "address":
			return addAddress(attribute);
		case "complementarAddress":
			return addComplementarAddress(attribute);
		case "locality":
			return addLocality(attribute);
		default:
			return "";
		}
	}
	
	private String addProfile (String profile) {
		this.profile = profile;
		return profile;
	}
	
	private String addLandline (String landline) {
		this.phoneNumber = landline;
		return profile;
	}

	private String addPhoneNumber (String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return phoneNumber;
	}
	
	private String addAddress (String address) {
		this.address = address;
		return address;
	}
	
	private String addComplementarAddress (String address) {
		this.complementarAddress = address;
		return complementarAddress;
	}
	
	private String addLocality (String locality) {
		this.locality = locality;
		return locality;
	}
	
	public boolean checkers () {
		return checkProfile(profile) && checkPhoneNumber(landline) && checkPhoneNumber(phoneNumber);
	}
	
	public boolean checkProfile (String profile) {
		if (profile.equals("Público") || profile.equals("Privado"))
			return true;
		else 
			return false;
	}
	
	public boolean checkPhoneNumber(String phoneNumber) {
		char character;
		boolean fail = false;
		if (phoneNumber.charAt(0) != '+')
			fail = true;
		for (int i = 1; i < 4 && !fail; i++) {
			character = phoneNumber.charAt(i);
			if (!Character.isDigit(character))
				fail = true;
		}
		if (phoneNumber.charAt(4) != ' ')
			fail = true;
		int counter = 0;
		for (int i = 1; i < phoneNumber.length() && !fail; i++) {
			character = phoneNumber.charAt(i);
			if (!Character.isDigit(character))
				fail = true;
			counter ++;
		}
		return fail && counter != 9;
	}
	
}

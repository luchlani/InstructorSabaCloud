package com.arc.instructor.model;

public class User {
	
	public final String FEE_BASED = "Fee";
	public final String NON_FEE_BASED = "Non-fee";
	public final String FACILITY_FEE_BASED = "Facility-fee";
	public final String SCHEDULING = "Scheduling";
	
    private String username;

    private String password;
    
    private String sabaMessage;
    
    private String personID;
    
    private String feeOption;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;

    }

	public String getSabaMessage() {
		return sabaMessage;
	}

	public void setSabaMessage(String sabaMessage) {
		this.sabaMessage = sabaMessage;
	}

	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public String getFeeOption() {
		return feeOption;
	}

	public void setFeeOption(String feeOption) {
		this.feeOption = feeOption;
	}
}
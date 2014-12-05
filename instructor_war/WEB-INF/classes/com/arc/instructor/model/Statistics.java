package com.arc.instructor.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Statistics implements Serializable {
	
	
	public static final String LowIncome = "Low Income";
	public static final String Minority = "Minority";
	public static final String Seniors = "Seniors";
	public static final String FunctionalNeeds = "Functional Needs";
	public static final String Rural = "Rural";
	public static final String Youth = "Youth";
	public static final String Military = "Military";

	
	private boolean lowIncome;
	private boolean adults;
	private boolean seniors;
	private boolean functionalNeeds;
	private boolean rural;
	private boolean youth;
	private boolean military;
	private boolean minority;
	
	private String learnToSwim;
	private String ratingMember;
	private String ameriCorps;
	
	public boolean isLowIncome() {
		return lowIncome;
	}
	public void setLowIncome(boolean lowIncome) {
		this.lowIncome = lowIncome;
	}
	public boolean isSeniors() {
		return seniors;
	}
	public void setSeniors(boolean seniors) {
		this.seniors = seniors;
	}
	public boolean isFunctionalNeeds() {
		return functionalNeeds;
	}
	public void setFunctionalNeeds(boolean functionalNeeds) {
		this.functionalNeeds = functionalNeeds;
	}
	public boolean isRural() {
		return rural;
	}
	public void setRural(boolean rural) {
		this.rural = rural;
	}
	public boolean isYouth() {
		return youth;
	}
	public void setYouth(boolean youth) {
		this.youth = youth;
	}
	public boolean isAdults() {
		return adults;
	}
	public void setAdults(boolean adults) {
		this.adults = adults;
	}
	public boolean isMilitary() {
		return military;
	}
	public void setMilitary(boolean military) {
		this.military = military;
	}
	public boolean isMinority() {
		return minority;
	}
	public void setMinority(boolean minority) {
		this.minority = minority;
	}
	public String getLearnToSwim() {
		if(learnToSwim == null)
			setLearnToSwim("Unknown");
		return learnToSwim;
	}
	public int getIsLearnToSwim() {
		if(learnToSwim.equals("Yes"))
			return 1;
		else if(learnToSwim.equals("No"))
			return 2;

		return 0;
	}
	public void setLearnToSwim(String learnToSwim) {
		this.learnToSwim = learnToSwim;
	}
	public String getRatingMember() {
		if(ratingMember == null)
			setRatingMember("Unknown");
		return ratingMember;
	}
	public int getIsRatingMember() {
		if(ratingMember.equals("Yes"))
			return 1;
		else if(ratingMember.equals("No"))
			return 2;
			
		return 0;
	}
	public void setRatingMember(String ratingMember) {
		this.ratingMember = ratingMember;
	}
	public String getAmeriCorps() {
		if(ameriCorps == null)
			setAmeriCorps("Unknown");
		return ameriCorps;
	}
	public int getIsAmeriCorps() {
		if(ameriCorps.equals("Yes"))
			return 1;
		else if (ameriCorps.equals("No"))
			return 2;
		
		return 0;
	}
	public void setAmeriCorps(String ameriCorps) {
		this.ameriCorps = ameriCorps;
	}
}

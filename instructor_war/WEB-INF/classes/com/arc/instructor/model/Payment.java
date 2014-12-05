package com.arc.instructor.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Payment implements Serializable {
	
	private String action;
	private String originalPrice;
	private String finalPrice;
	private String totalPrice;
	private String paymentTypeID;
	private String paymentType;
	private String paymentReference;
	private String purchaseOrderID;
	private String purchaseOrder;
	private String cardAccountNumber;  // Only reveals the last 4 digits, needed for print summary
	private String promotionalCode;
	private boolean agreement;
	private String ccErrMsg;
	private String sabaErrMsg;
	
	
	// Constructor
    public Payment() {

    }
    
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public String getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}
	
	public String getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;
	}
	
	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public String getPaymentTypeID() {
		return paymentTypeID;
	}

	public void setPaymentTypeID(String paymentTypeID) {
		this.paymentTypeID = paymentTypeID;
	}
	
	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}
	
	public String getPurchaseOrderID() {
		return purchaseOrderID;
	}

	public void setPurchaseOrderID(String purchaseOrderID) {
		this.purchaseOrderID = purchaseOrderID;
	}
	
	public String getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	
	public String getCardAccountNumber() {
		return cardAccountNumber;
	}

	public void setCardAccountNumber(String cardAccountNumber) {
		this.cardAccountNumber = cardAccountNumber;
	}
	
	public String getPromotionalCode() {
		return promotionalCode;
	}

	public void setPromotionalCode(String promotionalCode) {
		this.promotionalCode = promotionalCode;
	}
	
	public boolean getAgreement() {
		return agreement;
	}

	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}
	
	public String getCreditCardErrMsg(){
		return ccErrMsg;
	}
	
	public void setCreditCardErrMsg(String errMsg){
		this.ccErrMsg = errMsg;
	}
	
	public String getSabaErrMsg(){
		return sabaErrMsg;
	}
	
	public void setSabaErrMsg(String errMsg){
		this.sabaErrMsg = errMsg;
	}

}

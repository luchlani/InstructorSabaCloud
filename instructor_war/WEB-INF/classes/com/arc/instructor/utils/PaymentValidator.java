package com.arc.instructor.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.arc.instructor.model.Payment;

public class PaymentValidator implements Validator {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return Payment.class.isAssignableFrom(clazz);
    }
	
    public void validate(Object obj, Errors errors) {
        Payment payment = (Payment) obj;
        logger.debug("Validating Payment Entry");
        
        if (payment == null) {
        	errors.rejectValue("sabaMessage", null, null, "Please fill in requred fields");
        } else {
        	logger.debug("Validating paymentType: "
                    + payment.getPaymentTypeID());
        	if (payment.getAgreement() == false) {
        		errors.rejectValue("agreement", "error.payment.invalid-agreement",
                        null, "Please check the box above");
        	} else if (payment.getPaymentTypeID() == null || payment.getPaymentTypeID().equals("Select One")) {
                errors.rejectValue("paymentTypeID", "error.payment.invalid-payment-type",
                        null, "Incorrect Payment Type");
            } else if (payment.getPaymentTypeID().equals("Purchase Order")) {
            	if (payment.getPurchaseOrderID() == null || payment.getPurchaseOrderID().equals("")) {
                    errors.rejectValue("purchaseOrderID", "error.payment.invalid-purchase-order",
                            null, "Incorrect Purchase Order");
                }
            }
        }
    }

}

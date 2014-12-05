package com.arc.instructor.utils;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name = "option")
@XmlType(name ="option",propOrder = { "key", "value" })

public class DropDown implements Serializable {
	String key;
	String value;
	
	@XmlElement(name = "key")
	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	@XmlElement(name = "value")
	public String getValue() {
		return this.value;
	}	
	
	public void setValue(String value) {
		this.value = value;
	}	
}

package com.mashreq.paymentTracker.type;

import java.util.HashMap;
import java.util.Map;

public enum CountryType {
	UAE("AE", "UAE", "BOMLAEA", "019"), EGYPT("EG", "EGYPT", "MSHQEGC", "059"), QATAR("QA", "DOHA", "MSHQQAQ", "029"),
	KUWAIT("KW", "KUWAIT", "MSHQKWK", "069"), BAHRAIN("BH", "BAHRAIN", "BOMLBHB", "039");

	private String value;
	private String host;
	private String mesgSender;
	private String loanPrefix;
	public static final Map<String, CountryType> COUNTRY_MAP = new HashMap<String, CountryType>();

	static {
		for (CountryType countryType : CountryType.values()) {
			COUNTRY_MAP.put(countryType.value, countryType);
		}
	}

	private CountryType(String value, String host, String mesgSender, String loanPrefix) {
		this.value = value;
		this.host = host;
		this.mesgSender = mesgSender;
		this.loanPrefix = loanPrefix;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getMesgSender() {
		return mesgSender;
	}

	public void setMesgSender(String mesgSender) {
		this.mesgSender = mesgSender;
	}

	public String getLoanPrefix() {
		return loanPrefix;
	}

	public void setLoanPrefix(String loanPrefix) {
		this.loanPrefix = loanPrefix;
	}

	public static Map<String, CountryType> getCountryMap() {
		return COUNTRY_MAP;
	}

}

package com.acloudchina.coap.common;

public enum ProtypeEnum {
	DCP((String) "dcp"), CEP((String) "cep"), PMP((String) "pmp"), CMP((String) "cmp")
	, RAWDATA((String) "rawdata");

	private String val;

	ProtypeEnum(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}

	public static ProtypeEnum convertEnum(String val) {
		if (DCP.getVal().equals(val)) {
			return DCP;
		} else if (CEP.getVal().equals(val)) {
			return CEP;
		} else if (PMP.getVal().equals(val)) {
			return PMP;
		} else if (CMP.getVal().equals(val)) {
			return CMP;
		} else if (RAWDATA.getVal().equals(val)) {
			return RAWDATA;
		} else {
			throw new IllegalArgumentException("请求类型非法");
		}
	}

}

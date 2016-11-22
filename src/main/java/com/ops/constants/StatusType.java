/**
 * 
 */
package com.ops.constants;


public enum StatusType {
	
	STATUS_INACTIVE("STATUS","INACTIVE"), STATUS_BLOCKED("STATUS","BLOCKED");

	private final String statusType;
	private final String statusKey;

	private StatusType(String statusType, String statusKey) {
		this.statusType = statusType;
		this.statusKey = statusKey;
	}

	public String getStatusType() {
		return statusType;
	}

	public String getStatusKey() {
		return statusKey;
	}
}

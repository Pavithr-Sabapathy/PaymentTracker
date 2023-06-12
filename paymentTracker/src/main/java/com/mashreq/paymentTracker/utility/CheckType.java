package com.mashreq.paymentTracker.utility;

public enum CheckType {
	   NO ("N"), YES ("Y");

	   private String value;

	   CheckType (String value) {
	      this.value = value;
	   }

	   public String getValue () {
	      return this.value;
	   }

	   public static CheckType getCheckType (String value) {
	      if (CheckType.NO.getValue().equals(value)) {
	         return CheckType.NO;
	      } else if (CheckType.YES.getValue().equals(value)) {
	         return CheckType.YES;
	      }
	      return null;
	   }
}

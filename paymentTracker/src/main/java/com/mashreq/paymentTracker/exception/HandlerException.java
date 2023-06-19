package com.mashreq.paymentTracker.exception;

public class HandlerException extends BaseException {

   private static final long serialVersionUID = 1L;

   public HandlerException (int exceptionCode, String message, Throwable cause) {
      super(exceptionCode, message, cause);
   }

   public HandlerException (int exceptionCode, String message) {
      super(exceptionCode, message);
   }

   public HandlerException (int exceptionCode, Throwable cause) {
      super(exceptionCode, cause);
   }
}

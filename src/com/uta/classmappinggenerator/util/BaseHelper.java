package com.uta.classmappinggenerator.util;

import org.apache.commons.lang3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseHelper {

	protected void checkValidValue(String value, String purpose) {
		if (StringUtils.isBlank(value)) {
			throw new HelperException(purpose + " cannot be null");
		}
	}
	
	protected void logErrorAndThrowException(String endUserMsg, Exception e) {
		
		getLog().error(endUserMsg, e);
		throw new HelperException(endUserMsg, e);
	}

	public void logErrorAndThrowException(String endUserMsg) {
		
		getLog().error(endUserMsg);
		throw new HelperException(endUserMsg);
	}
	
	protected void logError(String endUserMsg, Exception e) {
		
		getLog().error(endUserMsg, e);
	}
	
	protected Logger getLog() {
		return LoggerFactory.getLogger(this.getClass());
	}
}


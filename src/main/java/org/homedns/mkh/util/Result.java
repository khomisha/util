/* 
 * Copyright 2019-2020 Mikhail Khodonov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.homedns.mkh.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Result object
 * 
 * @author Mikhail Khodonov
 *
 */
public class Result {
	public static final Integer SUCCESS = 1;
	public static final Integer FAILURE = 0;

	private String id;
	private Integer returnCode;
	private String[][] dataTable;
	private String message;
	private List< Serializable > returnValues;
	
	public Result( ) {
		setReturnCode( SUCCESS );
		returnValues = new ArrayList< >( );
	}

	/**
	 * Returns data table
	 * 
	 * @return the data table
	 */
	public String[][] getDataTable( ) {
		return( dataTable );
	}
	
	/**
	 * Sets data table
	 * 
	 * @param data the data table to set
	 */
	public void setDataTable( String[][] dataTable ) {
		this.dataTable = dataTable;
	}
	
	/**
	 * Returns return code
	 * 
	 * @return the return code
	 */
	public Integer getReturnCode( ) {
		return( returnCode );		
	}
	
	/**
	 * Sets return code
	 * 
	 * @param iReturnCode the return code to set
	 */
	public void setReturnCode( Integer returnCode ) {
		this.returnCode = returnCode;
	}
	
	/**
	 * Returns message
	 * 
	 * @return the message
	 */
	public String getMessage( ) {
		return( message );
	}
	
	/**
	 * Sets message
	 * 
	 * @param sMessage the message to set
	 */
	public void setMessage( String message ) {
		this.message = message;
	}

	/**
	 * Returns returns values
	 * 
	 * @return the returnValues
	 */
	public List< Serializable > getReturnValues( ) {
		return( returnValues );
	}
	
	/**
	 * Sets returns values
	 * 
	 * @param returnValues the returnValues to set
	 */
	public void setReturnValues( List< Serializable > returnValues ) {
		this.returnValues = returnValues;
	}

	/**
	 * Returns id
	 * 
	 * @return the id
	 */
	public String getId( ) {
		return( id );
	}

	/**
	 * Sets id
	 * 
	 * @param id the id to set
	 */
	public void setId( String id ) {
		this.id = id;
	}
	
	/**
	 * Sets failure result
	 * 
	 * @param e the exception
	 */
	public void setFailure( Exception e ) {
		setReturnCode( Result.FAILURE );
		setMessage( e.getMessage( ) + "\n" + Util.getStackTrace( e ) );		
	}
}

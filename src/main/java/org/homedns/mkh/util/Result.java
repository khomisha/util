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
	public static final String SUCCESS = "Success";
	public static final String FAILURE = "Failure";

	private String[][] data;
	private List< Serializable > returnValues;
	private String id;
	
	public Result( ) {
		returnValues = new ArrayList< >( );
		returnValues.add( SUCCESS );
	}

	/**
	 * Returns data
	 * 
	 * @return the data
	 */
	public String[][] getData( ) {
		return data;
	}
	
	/**
	 * Sets data
	 * 
	 * @param data the data to set
	 */
	public void setData( String[][] data ) {
		this.data = data;
	}
	
	/**
	 * Returns returns values
	 * 
	 * @return the returnValues
	 */
	public List< Serializable > getReturnValues( ) {
		return returnValues;
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
}

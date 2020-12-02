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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result object
 * 
 * @author Mikhail Khodonov
 *
 */
public class Result {
	public static final Integer SUCCESS = 1;
	public static final Integer FAILURE = -1;
	private static final String DATA_TABLE 		= "data";
	private static final String RETURN_CODE 	= "return_code";
	private static final String MESSAGE 		= "message";
	private static final String RETURN_VALUES 	= "return_values";

	private String id;
	private AttributeMap< String, Serializable > attributes;
	private Map< String, Type > types;
	
	public Result( ) {
		attributes = new AttributeMap< >( );
		types = new HashMap< >( );
		setReturnCode( SUCCESS );
	}

	/**
	 * Returns data table
	 * 
	 * @return the data table
	 */
	public String[][] getDataTable( ) {
		return( getAttribute( String[][].class, DATA_TABLE ) );
	}
	
	/**
	 * Sets data table
	 * 
	 * @param data the data table to set
	 */
	public void setDataTable( String[][] dataTable ) {
		setAttribute( DATA_TABLE, dataTable );
	}
	
	/**
	 * Returns return code
	 * 
	 * @return the return code
	 */
	public int getReturnCode( ) {
		return( getAttribute( Integer.class, RETURN_CODE ) );		
	}
	
	/**
	 * Sets return code
	 * 
	 * @param iReturnCode the return code to set
	 */
	public void setReturnCode( int iReturnCode ) {
		setAttribute( RETURN_CODE, iReturnCode );
	}
	
	/**
	 * Returns message
	 * 
	 * @return the message
	 */
	public String getMessage( ) {
		return( getAttribute( String.class, MESSAGE ) );
	}
	
	/**
	 * Sets message
	 * 
	 * @param sMessage the message to set
	 */
	public void setMessage( String sMessage ) {
		setAttribute( MESSAGE, sMessage );
	}
	
	/**
	 * Returns specified arbitrary data object
	 * 
	 * @param sKey the key 
	 * 
	 * @return the data list or null
	 */
	public < T extends Serializable > T getData( String sKey ) {
		return( getAttribute( types.get( sKey ), sKey ) );
	}

	/**
	 * Sets arbitrary data object
	 * 
	 * @param sKey the key
	 * @param data the data
	 */
	public void setData( String sKey, Serializable data ) {
		types.put( sKey, data.getClass( ) );
		setAttribute( sKey, data );
	}

	/**
	 * Returns returns values
	 * 
	 * @return the returnValues
	 */
	public List< Serializable > getReturnValues( ) {
		List< Serializable > values = getData( RETURN_VALUES );
		if( values == null ) {
			values = new ArrayList< >( );
			setReturnValues( values );
		}
		return( values );
	}
	
	/**
	 * Sets returns values
	 * 
	 * @param returnValues the returnValues to set
	 */
	public void setReturnValues( List< Serializable > returnValues ) {
		setData( RETURN_VALUES, ( Serializable )returnValues );
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
	* Returns attribute value.
	*
	* @param type the expected attribute type
	* @param sKey the attribute key
	*
	* @return attribute value or null
	*/
	@SuppressWarnings("unchecked")
	private < T extends Serializable > T getAttribute( Type type, String sKey ) {
		Serializable value = attributes.getAttribute( sKey );
		if( value == null ) {
			return( null );
		}
		if( type == value.getClass( ) ) {
			return( ( T )value );
		} else {
			throw new IllegalArgumentException( type.getTypeName( ) );
		}
	}

	/**
	 * Sets attribute value
	 * 
	 * @param sKey the attribute key
	 * @param value the attribute value
	 */
	private void setAttribute( String sKey, Serializable value ) {
		attributes.setAttribute( sKey, value );
	}
}

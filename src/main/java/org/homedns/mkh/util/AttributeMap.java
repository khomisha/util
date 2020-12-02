/*
 * Copyright 2013-2014 Mikhail Khodonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package org.homedns.mkh.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Attribute map
 *
 */
public class AttributeMap< E, T > implements Serializable {
	private static final long serialVersionUID = -8011964670329907128L;
	
	private Map< E, T > _attributes = new HashMap< E, T >( );
	
	/**
	 * Sets attribute value
	 * 
	 * @param key
	 *            the attribute key
	 * @param value
	 *            the attribute value
	 */
	public void setAttribute( E key, T value ) {
		_attributes.put( key, value );		
	}
	
	/**
	 * Returns attribute value
	 * 
	 * @param key
	 *            the attribute key
	 * 
	 * @return the attribute value
	 */
	public T getAttribute( E key ) {
		return ( _attributes.get( key ) );
	}
}

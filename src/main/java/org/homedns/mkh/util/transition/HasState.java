/*
 * Copyright 2014 Mikhail Khodonov
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

package org.homedns.mkh.util.transition;

/**
 * Object with states interface 
 *
 */
public interface HasState {
	
	/**
	 * Sets state
	 * 
	 * @param state the state to set
	 */
	public void setState( int iState );
	
	/**
	 * Returns current state
	 * 
	 * @return the current state
	 */
	public int getState( );
	
	/**
	 * Changes object state to new one
	 * 
	 * @param newState the state to change
	 */
	public void changeStateTo( int iNewState );
	
	/**
	 * Returns transition parameter
	 * 
	 * @return the transition parameter
	 */
	public default Object getTransitionParam( ) {
		return( null );
	}
	
	/**
	 * Sets transition parameter
	 * 
	 * @param param the transition parameter to set
	 */
	public default void setTransitionParam( Object param ) {
	}
}

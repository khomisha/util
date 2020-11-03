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
 * Command which executes when transition from one state to another is going on 
 *
 */
public interface TransitionCommand {
	
	/**
	 * Executes some action before transition from one state to another is going on
	 * 
	 * @param target
	 *            the transition target object
	 */
	public default void executeBefore( HasState target ) throws Exception {
	}
	
	/**
	 * Executes some action after transition from one state to another is completed
	 * 
	 * @param target
	 *            the transition target object
	 */
	public default void executeAfter( HasState target ) throws Exception {
	}
}

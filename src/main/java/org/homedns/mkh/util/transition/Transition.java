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

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

/**
 * State transition object
 *
 */
public abstract class Transition {
	private static final Logger LOG = Logger.getLogger( Transition.class );

	// row indicates current state e.g. (NO_STATE, READONLY, ADD, UPDATE)
	// column indicates new state e.g. (NO_STATE, READONLY, ADD, UPDATE)
	// row/column intersection gives transition command which should be executed
	private TransitionCommand[][] transitionTable;

	public Transition( ) {
	}

	/**
	 * @param transitionTable the state transition table
	 */
	public Transition( TransitionCommand[][] transitionTable ) {
		setTransitionTable( transitionTable );
	}
	
	/**
	 * Do transition to the new state
	 * 
	 * @param iNewState
	 *            the new state
	 * @param target
	 *            the target object
	 *            
	 * @throws Exception 
	 */
	public void doTransition( int iNewState, HasState target ) throws Exception {
		try {
			TransitionCommand tc = getTransitionCommand( iNewState, target.getState( ) );
			tc.executeBefore( target );
			target.setState( iNewState );
			tc.executeAfter( target );
			LOG.info( "state: " + iNewState );
		}
		catch( UnsupportedOperationException | IllegalArgumentException e ) {
			LOG.error( e.getMessage( ) + " failure to change state: " + iNewState );
		}
		catch( FileNotFoundException e ) {
		}
	}
	
	/**
	 * Makes transition table
	 * 
	 * @return the transition table
	 */
	protected TransitionCommand[][] doTransitionTable( ) {
		return( null );
	}
	
	/**
	 * Sets state transition table
	 * 
	 * @param transitionTable the state transition table to set
	 */
	public void setTransitionTable( TransitionCommand[][] transitionTable ) {
		this.transitionTable = transitionTable;
	}

	/**
	 * Returns transition command for specified current state and new
	 * state
	 * 
	 * @param iNewState
	 *            the new state
	 * @param iCurrentState
	 *            the current state
	 * 
	 * @return the transition command
	 */
	protected TransitionCommand getTransitionCommand( int iNewState, int iCurrentState ) {
		return( transitionTable[ iCurrentState ][ iNewState ] );
	}
	
	public class NoCommand implements TransitionCommand {
		
		public NoCommand( ) {
		}

		@Override
		public void executeBefore( HasState target ) {
		}
	}
	
	public class WrongCommand implements TransitionCommand {
		
		public WrongCommand( ) {
		}

		@Override
		public void executeBefore( HasState target ) {
			throw new UnsupportedOperationException( "Wrong transition" );
		}	
	}
}

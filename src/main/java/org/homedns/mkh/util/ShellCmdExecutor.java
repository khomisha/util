/* 
 * Copyright 2021 Mikhail Khodonov.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Shell commands executor
 *
 */
public class ShellCmdExecutor {
	private static final Logger LOG = Logger.getLogger( ShellCmdExecutor.class );

	private File workingDir;
	
	public ShellCmdExecutor( ) {
		// default working directory
		setWorkingDir( Paths.get( System.getProperty( "user.home" ) ) );
	}
	
	/**
	 * @param workingDir the working directory
	 */
	public ShellCmdExecutor( Path workingDir ) {
		setWorkingDir( workingDir );
	}

	/**
	 * Executes specified shell command
	 * 
	 * @param command the command to execute
	 * @param bBuiltin the built in command flag, only applicable for linux
	 * 
	 * @return the result (if any)
	 * 
	 * @throws Exception
	 */
	public List< String > execute( List< String > command, boolean bBuiltin ) throws Exception {
		if( Util.isWindows( ) ) {
			command.add( 0, "cmd.exe" );	// ??? it may not work when command is fixed-size list backed by the array.
			command.add( 1, "/c" );
		} else {
			if( !bBuiltin ) {
				command.set( 0, "./" + command.get( 0 ) );
			}
		}
		ProcessBuilder builder = new ProcessBuilder( );
		builder.directory( workingDir );
		builder.command( command );
		LOG.debug( builder.command( ) );
	    List< String > output = new ArrayList< >( );
		Process process = builder.start( );
		try( BufferedReader in = new BufferedReader( new InputStreamReader( process.getInputStream( ) ) ) ) {
			in.lines( ).forEach( output::add );
		}
		int iExit = process.waitFor( );
		if( iExit != 0 ) {
			throw new IllegalArgumentException( "Failure executing command: " + command );
		}
		return( output );
	}

	/**
	 * Sets working directory
	 * 
	 * @param workingDir the working directory to set
	 */
	public void setWorkingDir( Path workingDir ) {
		this.workingDir = workingDir.toFile( );
	}
}

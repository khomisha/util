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
import java.util.Arrays;
import java.util.List;

/**
 * Shell commands executor
 *
 */
public class ShellCmdExecutor {
	private File workingDir;
	
	public ShellCmdExecutor( ) {
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
	 * 
	 * @return the result (if any)
	 * 
	 * @throws Exception
	 */
	public String execute( String ...command ) throws Exception {
		return( execute( new ArrayList< >( Arrays.asList( command ) ) ) );
	}

	/**
	 * Executes specified shell command
	 * 
	 * @param command the command to execute
	 * 
	 * @return the result (if any)
	 * 
	 * @throws Exception
	 */
	public String execute( List< String > command ) throws Exception {
		if( Util.isWindows( ) ) {
			command.add( 0, "cmd.exe /c" );
		} else {
			command.add( 0, "./" );			
		}
		ProcessBuilder builder = new ProcessBuilder( );
		builder.directory( workingDir );
		builder.command( command );
	    StringBuilder output = new StringBuilder( );
		Process process = builder.start( );
		try( BufferedReader in = new BufferedReader( new InputStreamReader( process.getInputStream( ) ) ) ) {
			in.lines( ).forEach( output::append );
		}
		int iExit = process.waitFor( );
		if( iExit != 0 ) {
			throw new IllegalArgumentException( "Failure executing command: " + command );
		}
		return( output.toString( ) );
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

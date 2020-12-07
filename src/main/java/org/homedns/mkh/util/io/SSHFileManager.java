/* 
 * Copyright 2020 Mikhail Khodonov.
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

package org.homedns.mkh.util.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * SSH file manager
 *
 */
public class SSHFileManager implements FileManager {
	private static final Logger LOG = Logger.getLogger( SSHFileManager.class );

	private String sUserName;
	private String sPassword;
	private String sHost;
	private int iPort = 22;
	private String sPath;
	private Channel channel;
	private Map< Integer, String > templates;
	
	/**
	 * @param sUserName the user name
	 * @param sPassword the password
	 * @param sHost the host
	 * 
	 * @throws Exception 
	 */
	public SSHFileManager( String sUserName, String sPassword, String sHost ) throws Exception {
		this.sUserName = sUserName;
		this.sPassword = sPassword;
		this.sHost = sHost;
		setCommandTemplate( );
	}

	/**
	 * @param sUserName the user name
	 * @param sPassword the password
	 * @param sHost the host
	 * @param iPort the port
	 * 
	 * @throws Exception 
	 */
	public SSHFileManager( String sUserName, String sPassword, String sHost, int iPort ) throws Exception {
		this.sUserName = sUserName;
		this.sPassword = sPassword;
		this.sHost = sHost;
		this.iPort = iPort;
		setCommandTemplate( );
	}

	/**
	 * @see org.homedns.mkh.util.io.FileManager#setRootPath(java.lang.String)
	 */
	@Override
	public void setRootPath( String sPath ) {
		this.sPath = sPath;
	}

	/**
	 * @see org.homedns.mkh.util.io.FileManager#getRootPath()
	 */
	@Override
	public String getRootPath( ) {
		return( sPath );
	}

	/**
	 * @see org.homedns.mkh.util.io.FileManager#read(java.lang.String)
	 */
	@Override
	public String read( String sPath ) throws Exception {
		openChannel( "sftp" );
		channel.connect( );
		StringBuffer sb = new StringBuffer( );
		try( BufferedReader in = new BufferedReader( new InputStreamReader( ( ( ChannelSftp )channel ).get( sPath ) ) ) ) {
			String sLine;
			while( ( sLine = in.readLine( ) ) != null ) {
				sb.append( sLine );
			}
		}
		finally {
			close( );
		}
		return( sb.toString( ) );
	}
	
	/**
	 * @see org.homedns.mkh.util.io.FileManager#write(java.lang.String, java.lang.String)
	 */
	@Override
	public void write( String sContent, String sPath ) throws Exception {
		openChannel( "sftp" );
		channel.connect( );
		try( PrintWriter out = new PrintWriter( ( ( ChannelSftp )channel ).put( sPath ) ) ) {
			out.print( sContent );
		}
		finally {
			close( );
		}
	}
	
	/**
	 * @see org.homedns.mkh.util.io.FileManager#execCommand(int, java.lang.String)
	 */
	@Override
	public Object execCommand( int iCommand, String sParams ) throws Exception {
		String sCommand = templates.get( iCommand );
		if( sCommand == null ) {
			throw new IllegalArgumentException( iCommand + ": " + sParams );
		}
		if( iCommand == FileManager.LS ) {
			return( Arrays.asList( execCommand( sCommand + sParams ).split( "\n" ) ) );
		} else {
			return( execCommand( sCommand + sParams ) );
		}
	}

	/**
	 * @see org.homedns.mkh.util.io.FileManager#close()
	 */
	@Override
	public void close( ) {
		if( channel != null ) {
			try {
				Session session = channel.getSession( );
				channel.disconnect( );
				session.disconnect( );
			}
			catch( JSchException e ) {
				LOG.error( e.getMessage( ), e );
			}
		}					
	}

	/**
	 * Executes specified command and returns command execution result if any
	 * 
	 * @param sCommand the command
	 * 
	 * @return the result or empty string
	 * 
	 * @throws Exception
	 */
	protected String execCommand( String sCommand ) throws Exception {
		openChannel( "exec" );
		ChannelExec exec = ( ChannelExec )channel;
		exec.setCommand( sCommand );
	    StringBuilder output = new StringBuilder( );
		try( BufferedReader in = new BufferedReader( new InputStreamReader( exec.getInputStream( ) ) ) ) {
			exec.connect( );
			int iChar;
	        while( ( iChar = in.read( ) ) != -1 ) {
	        	output.append( ( char )iChar );
	        }
		}
		finally {
			close( );
		}
		return( output.toString( ) );
	}
	
	/**
	 * Returns true if target server OS is windows and false if linux
	 * 
	 * @return true or false
	 * 
	 * @throws Exception
	 */
	private boolean isWindows( ) throws Exception {
		return( !execCommand( "cmd.exe /c systeminfo" ).isEmpty( ) );
	}
	
	/**
	 * Opens channel to the ssh server
	 * 
	 * @param sType the channel type
	 * 
	 * @throws Exception
	 */
	private void openChannel( String sType ) throws Exception {
		Session session = new JSch( ).getSession( sUserName, sHost, iPort );
        session.setPassword( sPassword );
        session.setConfig( "StrictHostKeyChecking", "no" );
        session.connect( 10000 );
		channel = session.openChannel( sType );
	}
	
	/**
	 * Sets commands templates
	 * 
	 * @throws Exception
	 */
	private void setCommandTemplate( ) throws Exception {
		templates = new HashMap< >( );
		if( isWindows( ) ) {
			templates.put( FileManager.LS, "cmd.exe /c dir /B /OD " );
			templates.put( FileManager.CP, "cmd.exe /c copy /Y " );
			templates.put( FileManager.MV, "cmd.exe /c move /Y " );
			templates.put( FileManager.RM, "cmd.exe /c erase /Q " );
		} else {
			templates.put( FileManager.LS, "ls -tr " );
			templates.put( FileManager.CP, "cp " );
			templates.put( FileManager.MV, "mv -f " );
			templates.put( FileManager.RM, "rm -f " );			
		}
	}
}

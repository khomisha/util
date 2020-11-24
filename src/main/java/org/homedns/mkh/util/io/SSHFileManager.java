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
	
	/**
	 * @param sUserName the user name
	 * @param sPassword the password
	 * @param sHost the host
	 */
	public SSHFileManager( String sUserName, String sPassword, String sHost ) {
		this.sUserName = sUserName;
		this.sPassword = sPassword;
		this.sHost = sHost;
	}

	/**
	 * @param sUserName the user name
	 * @param sPassword the password
	 * @param sHost the host
	 * @param iPort the port
	 */
	public SSHFileManager( String sUserName, String sPassword, String sHost, int iPort ) {
		this.sUserName = sUserName;
		this.sPassword = sPassword;
		this.sHost = sHost;
		this.iPort = iPort;
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
	public String execCommand( int iCommand, String sParams ) throws Exception {
		String sResult = null;
		switch( iCommand ) {
			case COPY:
				sResult = execCommand( "cp " + sParams );
				break;
			case REMOVE:
				sResult = execCommand( "rm " + sParams );
				break;
			case LS:
				sResult = execCommand( "ls " + sParams );
				break;
			case CD:
				sResult = execCommand( "cd " + sParams );
				break;
			default:
				throw new IllegalArgumentException( iCommand + ": " + sParams );
		}
		return( sResult );
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
}

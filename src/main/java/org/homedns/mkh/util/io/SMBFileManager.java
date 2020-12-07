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
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/**
 * SMB file manager
 *
 */
public class SMBFileManager implements FileManager {
	private static final Logger LOG = Logger.getLogger( SMBFileManager.class );

	private String sPath;
	private NtlmPasswordAuthentication auth;
	private String sHost;
	
	/**
	 * @param sHost the host
	 */
	public SMBFileManager( String sHost ) {
		auth = NtlmPasswordAuthentication.ANONYMOUS;
		this.sHost = sHost;
	}
	
	/**
	 * @param sUserName the user
	 * @param sPassword the password
	 * @param sHost the host
	 */
	public SMBFileManager( String sUserName, String sPassword, String sHost ) {
		auth = new NtlmPasswordAuthentication( null, sUserName, sPassword );
		this.sHost = sHost;
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
		SmbFile source = new SmbFile( "smb://" + sHost + sPath, auth );
		StringBuffer sb = new StringBuffer( );
		try( BufferedReader in = new BufferedReader( new InputStreamReader( new SmbFileInputStream( source ) ) ) ) {
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
		SmbFile source = new SmbFile( "smb://" + sHost + sPath, auth );
		try( PrintWriter out = new PrintWriter( new SmbFileOutputStream( source ) ) ) {
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
		String sResult = null;
		String[] as = null;
		SmbFile source = null;
		switch( iCommand ) {
			case CP:
				as = sParams.split( " " );
				source = new SmbFile( "smb://" + sHost + as[ 0 ], auth );
				source.copyTo( new SmbFile( "smb://" + sHost + as[ 1 ], auth ) );
				break;
			case RM:
				source = new SmbFile( "smb://" + sHost + sParams, auth );
				source.delete( );
				break;
			case LS:
				String sPath = "smb://" + sHost + sParams;
				LOG.debug( sPath );
				SmbFile dir = new SmbFile( "smb://" + sHost + sParams, auth );
				List< SmbFile > files = Arrays.asList( dir.listFiles( ) );
				files.sort( this::compare );
				return( files.stream( ).map( f -> f.getName( ) ).collect( Collectors.toList( ) ) );
			case MV:
				as = sParams.split( " " );
				source = new SmbFile( "smb://" + sHost + as[ 0 ], auth );
				source.renameTo( new SmbFile( "smb://" + sHost + as[ 1 ], auth ) );
				break;
			default:
				throw new IllegalArgumentException( iCommand + ": " + sParams );
		}
		return( sResult );
	}
	
	/**
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	private int compare( SmbFile file1, SmbFile file2 ) {
		return( 			
			( file1.getLastModified( ) == file2.getLastModified( ) ) ? 
			0 : 
			( file1.getLastModified( ) > file2.getLastModified( ) ) ? 1 : -1
		);
	}
}

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

/**
 * The file manager
 *
 */
public class SMBFileManager implements FileManager {
	private String sPath;	
	
	/**
	 * @param sPath the root path
	 */
	public SMBFileManager( String sPath ) {
		setRootPath( sPath );
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.homedns.mkh.util.io.FileManager#write(java.lang.String, java.lang.String)
	 */
	@Override
	public void write( String sContent, String sPath ) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.homedns.mkh.util.io.FileManager#execCommand(int, java.lang.String)
	 */
	@Override
	public String execCommand( int iCommand, String sParams ) throws Exception {
		String sResult = null;
		switch( iCommand ) {
			case COPY:
				break;
			case REMOVE:
				break;
			case LS:
				break;
			case MV:
				break;
			default:
				throw new IllegalArgumentException( iCommand + ": " + sParams );
		}
		return( sResult );
	}
}

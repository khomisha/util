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
 * File system provider
 *
 */
public interface FileManager {
	public static final int CP = 0;
	public static final int RM = 2;
	public static final int LS = 3;
	public static final int MV = 4;
	
	/**
	 * Sets root path
	 * 
	 * @param sPath the root path to set  
	 */
	public default void setRootPath( String sPath ) {
	}
	
	/**
	 * Returns root path
	 * 
	 * @return the root path
	 */
	public default String getRootPath( ) {
		return( null );
	}

	/**
	 * Reads specified file
	 * 
	 * @param sPath the file relative path from root directory
	 * 
	 * @return file content
	 * 
	 * @throws Exception 
	 */
	public String read( String sPath ) throws Exception;
	
	/**
	 * Writes content to the specified file
	 * 
	 * @param sContent the content
	 * @param sPath the file relative path from root directory
	 * 
	 * @throws Exception
	 */
	public void write( String sContent, String sPath ) throws Exception;

	/**
	 * Executes specified command and returns command execution result if any
	 * 
	 * @param iCommand the command
	 * @param sParams the parameters string
	 * 
	 * @return the result or null
	 * 
	 * @throws Exception
	 */
	public Object execCommand( int iCommand, String sParams ) throws Exception;
	
	/**
	 * Closes file manager and it's resources
	 */
	public default void close( ) {
	}
}

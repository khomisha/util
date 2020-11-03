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

package org.homedns.mkh.util;

import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Writer UTF-8 text into the file 
 *
 */
public class TextWriter {
	private Path path;
	
	/**
	 * @param path the path to target file
	 */
	public TextWriter( Path path ) {
		setPath( path );
	}

	/**
	 * Sets path to file
	 * 
	 * @param path the path to file to set
	 */
	public void setPath( Path path ) {
		this.path = path;
	}
	
	/**
	 * Writes string into the file
	 * 
	 * @param s
	 * 
	 * @throws Exception
	 */
	public void write( String s ) throws Exception {
		try( PrintWriter out = new PrintWriter( path.toFile( ), "UTF-8" ) ) {
			out.print( s );
		}
	}
}

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

import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Writer UTF-8 text into the file 
 *
 */
public class TextWriter {
	
	/**
	 * Writes string into the file in UTF-8 encoding
	 * 
	 * @param path the file path
	 * @param sContent the content to write
	 * 
	 * @throws Exception
	 */
	public static void write( Path path, String sContent ) throws Exception {
		try( PrintWriter out = new PrintWriter( path.toFile( ), "UTF-8" ) ) {
			out.print( sContent );
		}
	}
}

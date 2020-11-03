/*
 * Copyright 2014-2020 Mikhail Khodonov
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

package org.homedns.mkh.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * The application configuration parameters object
 *
 */
public class Parameters {
	private static final Logger LOG = Logger.getLogger( Parameters.class );

	private Properties parameters = new Properties( );
	private Path path;

	/**
	 * @param path
	 *            the parameters file path 
	 * 
	 * @throws IOException, FileNotFoundException
	 */
	public Parameters( Path path ) throws FileNotFoundException, IOException {
		this.path = path;
		readConfig( );
	}

	/**
	 * Returns configuration parameters.
	 * 
	 * @return configuration parameters
	 */
	public Properties getParameters( ) {
		return( parameters );
	}

	/**
	 * Saves parameters
	 * 
	 * @throws IOException
	 */
	public void save( ) throws IOException {
		try( FileOutputStream stream = new FileOutputStream( path.toFile( ) ) ) {
			parameters.store( stream, null );
			LOG.debug( "Properties file " + path.getFileName( ) + " is successfully saved" );
		}
	}
	
	/**
	 * Reads configuration parameters file.
	 * 
	 * @throws FileNotFoundException, IOException
	 */
	private void readConfig( ) throws FileNotFoundException, IOException {
		try( FileInputStream stream = new FileInputStream( path.toFile( ) ) ) {
			parameters.load( stream );
			LOG.debug( "Properties file " + path.getFileName( ) + " is successfully loaded" );
		}
	}
}

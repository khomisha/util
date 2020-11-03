/*
 * Copyright 2013-2014 Mikhail Khodonov
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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Data object to transfer data from client to server and vice versa
 *
 */
public class Data extends ArrayList< ArrayList< Serializable > > implements Serializable {
	private static final long serialVersionUID = -6272834019384174268L;

	public Data( ) { 
	}

	/**
	 * Adds row
	 * 
	 * @return the row index to add
	 */
	public int addRow( ) {
		ArrayList< Serializable > row = new ArrayList< Serializable >( );
		add( row );
		return( size( ) - 1 );
	}
	
	/**
	 * Appends value to the last row. If data object is empty, it adds row first
	 * 
	 * @param value
	 *            the value to add
	 */
	public < T extends Serializable > void addValue( T value ) {
		if( isEmpty( ) ) {
			addRow( );
		}
		ArrayList< Serializable > row = get( size( ) - 1 );
		row.add( value );
	}
	
	/**
	 * Returns specified cell value
	 * 
	 * @param iRow
	 *            the cell row index
	 * @param iCol
	 *            the cell column index
	 * @return the data cell value
	 */
	public Object getValue( int iRow, int iCol ) {
		ArrayList< Serializable > row = get( iRow );
		return( row.get( iCol ) );
	}

	/**
	 * Returns data row
	 * 
	 * @param iRow
	 *            the row index
	 * 
	 * @return the data row
	 */
	public ArrayList< Serializable > getRow( int iRow ) {
		return( get( iRow ) );
	}
	
	/**
	 * Removes row
	 * 
	 * @param iRow
	 *            the row index to remove
	 */
	public void removeRow( int iRow ) {
		remove( iRow );
	}

	/**
	 * Removes value from specified row, column
	 * 
	 * @param iRow
	 *            the row index
	 * @param iCol
	 *            the column index
	 */
	public void removeValue( int iRow, int iCol ) {
		ArrayList< Serializable > row = get( iRow );
		row.remove( iCol );
	}
	
	/**
	 * Sets value to the specified cell
	 * 
	 * @param iRow
	 *            the cell row index where value to set
	 * @param iCol
	 *            the cell column index where value to set
	 * @param value
	 *            the value to set
	 */
	public < T extends Serializable > void setValue( int iRow, int iCol, T value ) {
		ArrayList< Serializable > row = get( iRow );
		row.set( iCol, value );
	}
}
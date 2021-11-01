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

package org.homedns.mkh.util.smtpclient;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;

/**
 * The email factory
 *
 */
public class EmailFactory {

	/**
	 * Returns email
	 * 
	 * @param sSender the sender name
	 * @param sSenderAddress the sender address
	 * @param sReceiver the receiver name
	 * @param sReceiverAddress the receiver address
	 * @param sSubject the subject
	 * @param sText the main text
	 * 
	 * @return the email
	 */
	public static Email getEmail( 
		String sSender, 
		String sSenderAddress, 
		String sReceiver, 
		String sReceiverAddress, 
		String sSubject, 
		String sText 
	) {
		Email email = EmailBuilder.startingBlank( )
			.from( sSender, sSenderAddress )
			.to( sReceiver, sReceiverAddress )
			.withSubject( sSubject )
			.withPlainText( sText )
			.withHeader( "X-Priority", 3 )
			.buildEmail( );
		return( email );
	}
}

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

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;

/**
 * The mailer factory
 *
 */
public class MailerFactory {
	private static final int SESSION_TIMEOUT = 50000;
	private static final TransportStrategy TRANSPORT_STRATEGY = TransportStrategy.SMTP_TLS;

	/**
	 * Returns mailer for specified smtp server, port, account and password
	 * 
	 * @param sSMTPServer the smtp server
	 * @param iPort the port
	 * @param sAccount the account
	 * @param sPass the password
	 * 
	 * @return the mailer
	 */
	public static Mailer getMailer( String sSMTPServer, int iPort, String sAccount, String sPass ) {
		Mailer mailer = MailerBuilder
			.withSMTPServer( sSMTPServer, iPort, sAccount, sPass )
			.withTransportStrategy( TRANSPORT_STRATEGY )
			.withSessionTimeout( SESSION_TIMEOUT )
			.withProperty( "mail.smtp.sendpartial", true )
			.withDebugLogging( true )
			.buildMailer( );
		return( mailer );
	}
}

/*******************************************************************************
 * Copyright (c) 2010  Samuel Lampa <samuel.lampa@rilnet.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.smw.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.rdf.business.IRDFStore;
import net.bioclipse.rdf.business.RDFManager;
import net.bioclipse.rdf.model.StringMatrix;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

public class SmwManager implements IBioclipseManager {
	protected String m_wikiURL;

	private static final Logger logger = Logger.getLogger(SmwManager.class);

	/**
	 * Gives a short one word name of the manager used as variable name when
	 * scripting.
	 */
	public String getManagerName() {
		return "smw";
	}

	public String getTriples( String wikiURL, String format, int limit ) {
		String sparqlQuery = "";
		String resultRDFXML = "";

		if ( limit == 0 ) {
			sparqlQuery = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";    		
		} else {
			sparqlQuery = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }" +
			"LIMIT " + Integer.toString( limit );    		
		}

		resultRDFXML = sparql( sparqlQuery, wikiURL, format );
		return resultRDFXML;
	}

	public String getTriples( String wikiURL, String format ) {
		return getTriples( wikiURL, format, 0 );
	}

	public String getTriples( String wikiURL ) {
		return getTriples( wikiURL, "RDF/XML" );
	}

	public String addTriple( String subject, String predicate, String object, String wikiURL ) {
		String result = null;
		String action = "INSERT";
		try {
			result = updateTriple( subject, predicate, object, wikiURL, action );
		} catch (BioclipseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String removeTriple( String subject, String predicate, String object, String wikiURL ) {
		String result = null;
		String action = "DELETE";
		try {
			result = updateTriple( subject, predicate, object, wikiURL, action );
		} catch (BioclipseException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String updateTriple( String subject, String predicate, String object, String wikiURL, String action ) throws BioclipseException {
		String result = null;
		String sparqlQuery = null;
		String sparqlGetQueryURL = null;
		String subjectPart = null;
		String predicatePart = null;
		String objectPart = null;
		boolean delete = false;
		String actionPart = null;

		if ( action.equals("DELETE") ) {
			delete = true;
		} else if ( action.equals("INSERT") ) {
			delete = false;
		} else {
			throw new BioclipseException("No action set in SmwManager.updateTriple method");
		}

		wikiURL = ensureTrailingSlash( wikiURL );

		if ( subject.contains("http://") ) {
			subjectPart = urlencode("<") + subject + urlencode("> ");
		} else {
			subjectPart = subject + urlencode(" ");
		}

		if ( predicate.contains("http://") ) {
			predicatePart = urlencode("<") + predicate + urlencode("> ");
		} else {
			predicatePart = predicate + urlencode(" ");
		}

		if ( object.contains("http://") ) {
			objectPart = urlencode("<") + object + urlencode("> ");
		} else {
			objectPart = object + urlencode(" ");
		}

		if ( delete ) {
			actionPart = urlencode( "DELETE " );    		
		} else {
			actionPart = urlencode( "INSERT INTO <> " );
		}

		sparqlQuery = urlencode("@PREFIX w : <" + wikiURL + "Special:URIResolver/> . ") + 
		actionPart +
		urlencode("{ ") +
		subjectPart + predicatePart + objectPart +
		urlencode("}");

		sparqlGetQueryURL = wikiURL + "Special:SPARQLEndpoint?query=" + sparqlQuery;
		result = downloadURL( sparqlGetQueryURL );
		return result;
	}

	public String sparql( String sparqlQuery, String wikiURL ) {
		StringMatrix result = new StringMatrix();
		String resultString = null; 
		RDFManager myRdfManager = new RDFManager();

		// Make some configurations
		String serviceURL = wikiURL + "Special:SPARQLEndpoint";

		if ( sparqlQuery.contains("INSERT") ) {
			result = myRdfManager.sparqlRemote(serviceURL, sparqlQuery, null );
			resultString = result.toString();
		} else {
			result = myRdfManager.sparqlRemote(serviceURL, sparqlQuery, null );
			resultString = result.toString();
		}
		// Convert and return results
		return resultString;
	}

	public String sparql( String sparqlQuery, String wikiURL, String format ) {
		if ( format == null )
			format = "RDF/XML";
		String resultString = null; 
		RDFManager myRdfManager = new RDFManager();
		IRDFStore rdfModel = null;

		// Make some configurations
		String serviceURL = wikiURL + "Special:SPARQLEndpoint";

		try {
			rdfModel = myRdfManager.sparqlConstructRemote(serviceURL, sparqlQuery, null);
		} catch (BioclipseException e) {
			e.printStackTrace();
		}    
		resultString = rdfModel.toString();
		return resultString;
	}

	public String downloadURL(String url) {
		String resultString = null;
		try {
			try {
				URL page = new URL(url);
				String line;
				StringBuffer stringBuff = new StringBuffer();

				HttpURLConnection conn = (HttpURLConnection) page.openConnection();
				String userAgent = "Bioclipse SMW Connector Plugin";
				conn.setRequestProperty( "User-Agent", userAgent );
				System.out.println("Setting User-Agent to: " + userAgent);
				conn.connect();

				// Create Input stream reader object (default charset: Unicode)
				InputStreamReader inStreamReader = new InputStreamReader(
						// Get content, typecasted to InputStream
						(InputStream) conn.getContent()); 

				// Create a buffer that can store the data until we use it
				BufferedReader buffReader = new BufferedReader(inStreamReader);
				do {
					line = buffReader.readLine();
					if(line != null) {
						stringBuff.append(line + "\n");
					}
				} while (line != null);
				conn.disconnect();
				resultString = stringBuff.toString();
			} catch (MalformedURLException mue) {
				System.out.println("MalformedURLException: " + mue);
			}
		} catch (IOException ioe) {
			System.out.println("IO Error: " + ioe.getMessage());
		} 
		return resultString;
	}


	private String ensureTrailingSlash( String url ) {
		if ( !url.endsWith("/") ) 
			url = url + "/";
		return url;
	}


	private String urlencode( String urlString ) {
		String resultString = null;
		try {
			resultString = URLEncoder.encode( urlString, "UTF-8" );
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultString;
	}
}

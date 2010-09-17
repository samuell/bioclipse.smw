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

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.rdf.business.IRDFStore;
import net.bioclipse.rdf.model.StringMatrix;

@PublishedClass(
		value="Connects Bioclipse to a remote Semantic MediaWiki installation"
)
public interface ISmwManager extends IBioclipseManager {

	@Recorded
	@PublishedMethod(
			params="String wikiURL, int limit", 
			methodSummary="Returns all triples from an RDFIO powered wiki (limited by limit) as an IRDFStore. limit = 0 means \"no limit\""
	)
	public IRDFStore getRDF( String wikiURL, int limit );
	
	@Recorded
	@PublishedMethod(
			params="String wikiURL", 
			methodSummary="Returns all triples from an RDFIO powered wiki as an IRDFStore"
	)
	public IRDFStore getRDF( String wikiURL );

	@Recorded
	@PublishedMethod(
			params="String subject, String predicate, String object, String wikiURL", 
			methodSummary="Adds a triple to the specified wiki, where the triple is" +
					"specified by subject, predicate and object. Wiki article titles" +
					"can be used, by prepending them with the \"w:\" prefix."
	)
	public String addTriple( String subject, String predicate, String object, String wikiURL );
	
	@Recorded
	@PublishedMethod(
			params="String subject, String predicate, String object, String wikiURL", 
			methodSummary="Removes a triple to the specified wiki, where the triple is" +
					"specified by subject, predicate and object. Wiki article titles" +
					"can be used, by prepending them with the \"w:\" prefix."
	)
	public String removeTriple( String subject, String predicate, String object, String wikiURL );
	
	@Recorded
	@PublishedMethod(
			params="String sparqlQuery, String wikiURL", 
			methodSummary="Executes a SPARQL query against the remote wikis SPARQL Endpoint"
	)
	public String sparql( String sparqlQuery, String wikiURL );
	
	@Recorded
	@PublishedMethod(
			params="String sparqlQuery, String wikiURL, String outputFormat", 
			methodSummary="This is for executing a SPARQL CONSTRUCT query against the remote " +
					"wikis SPARQL Endpoint, and receiving RDF data in the specified format"
	)
	public String sparql( String sparqlQuery, String wikiURL, String ouputFormat );

	public String DownloadURL(String url);
	
}

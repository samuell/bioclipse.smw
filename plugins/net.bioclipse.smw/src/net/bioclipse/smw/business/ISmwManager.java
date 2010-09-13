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
import net.bioclipse.rdf.model.StringMatrix;

@PublishedClass(
		value="Connects Bioclipse to a remote Semantic MediaWiki installation"
)
public interface ISmwManager extends IBioclipseManager {

	@Recorded
	@PublishedMethod(
			params="String wikiURL, String sparqlQuery", 
			methodSummary="Executes a SPARQL query against the remote wikis SPARQL Endpoint"
	)
	public String queryWithSparql( String wikiURL, String sparqlQuery );

}

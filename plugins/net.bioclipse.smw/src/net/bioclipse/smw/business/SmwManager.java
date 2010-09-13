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

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.rdf.business.RDFManager;
import net.bioclipse.rdf.model.StringMatrix;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;

public class SmwManager implements IBioclipseManager {

    private static final Logger logger = Logger.getLogger(SmwManager.class);

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "smw";
    }
    
    public String queryWithSparql( String wikiURL, String sparqlQuery ) {
    	StringMatrix result = new StringMatrix();
    	String resultString = ""; 
    	RDFManager myRdfManager = new RDFManager();
    	
        // Make some configurations
    	String serviceURL = wikiURL + "Special:SPARQLEndpoint";

    	// Execute SPARQL
    	result = myRdfManager.sparqlRemote(serviceURL, sparqlQuery, null );

    	// Convert and return results
    	resultString = result.toString();
    	return resultString;
    }
}

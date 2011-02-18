/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.metamatrix.core.index.CompositeIndexSelector;
import com.metamatrix.core.index.RuntimeIndexSelector;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.transformation.metadata.ServerMetadataFactory;
import org.teiid.query.metadata.QueryMetadataInterface;

public class VDBMetadataFactory {
	
	public static QueryMetadataInterface getVDBMetadata(String vdbFile) {
        IndexSelector selector = new RuntimeIndexSelector(vdbFile);
        return ServerMetadataFactory.getInstance().createCachingServerMetadata(selector); 
    }
	
	public static QueryMetadataInterface getVDBMetadata(URL vdbURL) throws IOException {
        IndexSelector selector = new RuntimeIndexSelector(vdbURL);
        return ServerMetadataFactory.getInstance().createCachingServerMetadata(selector); 
    }	
	
	public static QueryMetadataInterface getVDBMetadata(String[] vdbFile) {
		
        List selectors = new ArrayList();
        for (int i = 0; i < vdbFile.length; i++){
	        selectors.add(new RuntimeIndexSelector(vdbFile[i]));        
        }
        
        IndexSelector composite = new CompositeIndexSelector(selectors);
        return ServerMetadataFactory.getInstance().createCachingServerMetadata(composite);
    }	
}

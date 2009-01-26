/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import java.io.PrintStream;

import junit.framework.Assert;

import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.data.Request;
import com.metamatrix.modeler.jdbc.data.Results;

/**
 * This JdbcNodeVisitor implementation 
 */
public class TableLoadingVisitor implements JdbcNodeVisitor {
    
    private final Stopwatch sw;
    private final PrintStream stream;
    private final String prefix;

    /**
     * Construct an instance of TableLoadingVisitor.
     * 
     */
    public TableLoadingVisitor( final Stopwatch sw, final PrintStream stream, final String prefix ) {
        super();
        this.sw = (sw != null ? sw : new Stopwatch());
        this.stream = stream;
        this.prefix = prefix != null ? prefix : ""; //$NON-NLS-1$
    }
    
    public Stopwatch getStopwatch() {
        return sw;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor#visit(com.metamatrix.modeler.jdbc.metadata.JdbcNode)
     */
    public boolean visit(JdbcNode node) throws JdbcException {
        if ( node instanceof JdbcTable ) {
            final JdbcTable table = (JdbcTable)node;

            if ( stream != null ) {
                stream.println(prefix + table.getPath() + " information:"); //$NON-NLS-1$
            }
            
            // Load the table information (e.g., columns, pks, etc.)
            final String[] namedResults = table.getNamesOfResults();
            for (int i = 0; i < namedResults.length; ++i) {
                // Start the time segment ...
                sw.start();
                
                final String resultName = namedResults[i];
                final Request request = table.getRequest(resultName);
                Assert.assertNotNull(request);  // should never be null when passing in result from 'getNamesOfResult'
                if ( stream != null ) {
                    if ( request.hasResults() ) {
                        final Results results = request.getResults();
                        stream.println(prefix + "  # " + resultName + " records = " + results.getRowCount()); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        stream.println(prefix + request.getProblems() );
                    }
                }
                // Stop the time segment
                sw.stop();
            }
            
            return false;   // no need to visit children - there never are children
        }
        // Nothing to do
        return true;
    }

}

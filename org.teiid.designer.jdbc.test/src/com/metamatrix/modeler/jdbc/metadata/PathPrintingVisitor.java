/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import java.io.PrintStream;
import com.metamatrix.core.util.Assertion;

/**
 * PathPrintingVisitor
 */
public class PathPrintingVisitor implements JdbcNodeVisitor {

    private final PrintStream stream;
    private final String prefix;

    /**
     * Construct an instance of PathPrintingVisitor.
     */
    public PathPrintingVisitor( final PrintStream stream ) {
        this(stream, null);
    }

    /**
     * Construct an instance of PathPrintingVisitor.
     */
    public PathPrintingVisitor( final PrintStream stream,
                                final String prefix ) {
        super();
        Assertion.isNotNull(stream);
        this.stream = stream;
        this.prefix = prefix != null ? prefix : ""; //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor#visit(com.metamatrix.modeler.jdbc.metadata.JdbcNode)
     */
    public boolean visit( JdbcNode node ) {
        stream.println(this.prefix + node.getPath().toString());
        return true;
    }

}

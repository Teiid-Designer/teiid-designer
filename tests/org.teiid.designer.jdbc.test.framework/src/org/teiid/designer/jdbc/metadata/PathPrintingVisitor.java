/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata;

import java.io.PrintStream;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcNodeVisitor;


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
        CoreArgCheck.isNotNull(stream);
        this.stream = stream;
        this.prefix = prefix != null ? prefix : ""; //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.JdbcNodeVisitor#visit(org.teiid.designer.jdbc.metadata.JdbcNode)
     */
    @Override
	public boolean visit( JdbcNode node ) {
        stream.println(this.prefix + node.getPath().toString());
        return true;
    }

}

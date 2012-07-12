/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata;

import org.teiid.core.util.Stopwatch;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcNodeVisitor;


/**
 * PerformanceVisitor
 */
public class PerformanceVisitor implements JdbcNodeVisitor {

    private int numNodes;
    private Stopwatch sw;

    /**
     * Construct an instance of PerformanceVisitor.
     */
    public PerformanceVisitor() {
        super();
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.JdbcNodeVisitor#visit(org.teiid.designer.jdbc.metadata.JdbcNode)
     */
    public boolean visit( JdbcNode node ) {
        if (sw == null) {
            sw = new Stopwatch();
            sw.start();
        }
        ++numNodes;
        return true;
    }

    public void stop() {
        if (sw != null) {
            sw.stop();
        }
    }

    public double getAverageTimeInMillis() {
        final long totalTimeInMillis = sw.getTotalDuration();
        return totalTimeInMillis / numNodes;
    }

    /**
     * @return
     */
    protected long getTotalTimeInMillis() {
        return sw.getTotalDuration();
    }

    /**
     * @return
     */
    protected int getNumberOfNodes() {
        return numNodes;
    }

}

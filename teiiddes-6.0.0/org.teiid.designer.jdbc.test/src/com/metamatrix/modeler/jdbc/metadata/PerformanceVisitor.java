/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import com.metamatrix.core.util.Stopwatch;

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
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor#visit(com.metamatrix.modeler.jdbc.metadata.JdbcNode)
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

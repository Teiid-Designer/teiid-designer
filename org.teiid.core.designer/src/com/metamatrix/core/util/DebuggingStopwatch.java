/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 5.0.2
 */
public class DebuggingStopwatch {
    private static final String NEW_LINE = "\n"; //$NON-NLS-1$
    private static final String SPACER = "   -- > "; //$NON-NLS-1$
    private static final String DELTA_TIME = " Delta Time (ms) = "; //$NON-NLS-1$
    private static final String TOTAL_TIME = " Total Time (ms) = "; //$NON-NLS-1$
    private static final String STOPWATCH_STATS = " ===>> Stopwatch Statistics for: "; //$NON-NLS-1$
    private static final String END = " =========== END STATS ============="; //$NON-NLS-1$
    private String title;
    private long[] increments;
    private List messages;
    private int nValues = 0;

    private Stopwatch incStopwatch;
    private Stopwatch totalStopwatch;

    /**
     * @since 5.0.2
     */
    public DebuggingStopwatch( String title,
                               int maxStatistics,
                               boolean printStart ) {
        super();
        this.title = title;
        increments = new long[maxStatistics];
        messages = new ArrayList(maxStatistics);
        incStopwatch = new Stopwatch();
        totalStopwatch = new Stopwatch();
        if (printStart) {
            System.out.println(" *** Stopwatch Statistics intiated for: " + title); //$NON-NLS-1$
        }
    }

    public void stopStats() {
        incStopwatch.stop();
        incStopwatch.reset();
    }

    public void startStats() {
        incStopwatch.start(true);
    }

    public void start() {
        totalStopwatch.start();
    }

    public void stop() {
        totalStopwatch.stop();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(STOPWATCH_STATS + title + NEW_LINE);
        for (int i = 0; i < nValues; i++) {
            sb.append(SPACER + messages.get(i) + DELTA_TIME + increments[i] + NEW_LINE);
        }
        sb.append(TOTAL_TIME + totalStopwatch.getTotalDuration() + NEW_LINE);
        sb.append(END);
        return sb.toString();
    }
}

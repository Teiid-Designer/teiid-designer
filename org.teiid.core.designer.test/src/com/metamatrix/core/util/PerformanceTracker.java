/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Utility class to aid in profiling classes, methods and operations during development
 * 
 * @since 5.0
 */
public class PerformanceTracker {

    private static final char RETURN_CHAR = StringUtil.Constants.NEW_LINE_CHAR;
    static final char SPACE_CHAR = StringUtil.Constants.SPACE_CHAR;

    private String idOrTitle;
    private HashMap timingResultsMap = new HashMap(10);
    private HashMap watchMap = new HashMap(10);
    boolean doTrackTime = true;

    /**
     * @since 5.0
     */
    public PerformanceTracker( String id ) {
        this(id, true);
        this.idOrTitle = id;
    }

    /**
     * @since 5.0
     */
    public PerformanceTracker( String id,
                               boolean doTime ) {
        super();
        this.idOrTitle = id;
        this.doTrackTime = doTime;
    }

    public void reset() {
        timingResultsMap.clear();
        watchMap.clear();
    }

    public void start( String id ) {
        if (doTrackTime) {
            Stopwatch newWatch = new Stopwatch();
            watchMap.put(id, newWatch);
            newWatch.start(true);
        }
    }

    public void stop( String id ) {
        if (doTrackTime) {
            Stopwatch existingWatch = (Stopwatch)watchMap.get(id);
            if (existingWatch != null) {
                existingWatch.stop();
                track(id, existingWatch.getTotalDuration());
                watchMap.remove(id);
            }
        } else {
            track(id, 0);
        }
    }

    public void setDoTrackTime( boolean doTime ) {
        this.doTrackTime = doTime;
    }

    public String getID() {
        return this.idOrTitle;
    }

    private void track( String id,
                        long increment ) {
        TimingResult existingResult = (TimingResult)timingResultsMap.get(id);
        if (existingResult == null) {
            existingResult = new TimingResult(id);
            timingResultsMap.put(id, existingResult);
        }
        existingResult.add(increment);
    }

    public void print() {
        int maxLengthIDName = 0;
        for (Iterator iter = timingResultsMap.keySet().iterator(); iter.hasNext();) {
            String nextKey = (String)iter.next();
            maxLengthIDName = Math.max(maxLengthIDName, nextKey.length());
        }
        maxLengthIDName += 1;

        StringBuffer buffer = new StringBuffer(1000);
        buffer.append(" >>> PERFORMANCE TRACKER FOR: " + idOrTitle); //$NON-NLS-1$
        for (Iterator iter = timingResultsMap.keySet().iterator(); iter.hasNext();) {
            TimingResult existingResult = (TimingResult)timingResultsMap.get(iter.next());
            buffer.append(RETURN_CHAR + existingResult.getSummary(maxLengthIDName));
        }

        System.out.println(buffer);
    }

    class TimingResult {
        private String id;
        private long totalTime;
        private int nCalls;
        private double average;

        public TimingResult( String id ) {
            super();
            this.id = id;
        }

        public void add( long deltaTime ) {
            totalTime += deltaTime;
            nCalls++;
            average = ((double)totalTime) / nCalls;
        }

        public long getTotalTime() {
            return totalTime;
        }

        private String getConstantLengthString( String theStr,
                                                int targetLength,
                                                boolean prepend ) {
            int nSpaces = targetLength - theStr.length();
            if (nSpaces > 0) {
                StringBuffer spaces = new StringBuffer(nSpaces);
                for (int i = 0; i < nSpaces; i++) {
                    spaces.append(SPACE_CHAR);
                }
                if (prepend) {
                    return spaces.toString() + theStr;
                }
                return theStr + spaces;
            }

            return theStr;
        }

        public String getSummary( int maxNameLength ) {
            String nCallsStr = "" + nCalls; //$NON-NLS-1$
            String totalTimeStr = "" + totalTime; //$NON-NLS-1$
            String averageStr = "" + average; //$NON-NLS-1$

            StringBuffer buffer = new StringBuffer(150);
            buffer.append("    ID = "); //$NON-NLS-1$
            buffer.append(getConstantLengthString(id, maxNameLength, false));
            buffer.append(" COUNT = ["); //$NON-NLS-1$
            buffer.append(getConstantLengthString(nCallsStr, 11, true));
            buffer.append(']');
            if (doTrackTime) {
                buffer.append("   TOTAL TIME (ms) = ["); //$NON-NLS-1$
                buffer.append(getConstantLengthString(totalTimeStr, 11, true));
                buffer.append("]   AVERAGE TIME (ms) = ["); //$NON-NLS-1$
                buffer.append(averageStr + "]"); //$NON-NLS-1$
            }
            return buffer.toString();

        }
    }
}

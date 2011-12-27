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
 * @since 5.0
 */
public class PerformanceTrackerManager {
    private HashMap trackers = new HashMap(10);
    
    private static final PerformanceTrackerManager instance = new PerformanceTrackerManager();
    
    public static final PerformanceTrackerManager getInstance() {
        return instance;
    }
    
    /** 
     * 
     * @since 5.0
     */
    public PerformanceTrackerManager() {
        super();
    }
    
    /**
     * Returns a 
     * @param id
     * @return
     * @since 5.0
     */
    public PerformanceTracker getTracker(String id) {
        return getTracker(id, true);
    }
    
    /**
     * Returns a PerformanceTracker with the given String ID. If one does not exists, it is created.
     * @param id
     * @param doTime
     * @return
     * @since 5.0
     */
    public PerformanceTracker getTracker(String id, boolean doTime) {
        PerformanceTracker tracker = null;
        boolean createTracker = false;
        if( trackers.isEmpty() ) {
            createTracker = true;
        } else {
            tracker = (PerformanceTracker)trackers.get(id);
            if( tracker == null ) {
                createTracker = true;
            }
        }
        
        if( createTracker ) {
            tracker = new PerformanceTracker(id, doTime);
            trackers.put(id, tracker);
        }
        
        return tracker;
    }
    
    // -----------------------------------
    // Tracker Cache Management
    // -----------------------------------
    /**
     * Add an existing tracker to the manager 
     * @param tracker
     * @since 5.0
     */
    public void addTracker(PerformanceTracker tracker) {
        trackers.put(tracker.getID(), tracker);
    }
    
    /**
     * Remove an existing tracker from the manager 
     * @param tracker
     * @since 5.0
     */
    public void removeTracker(PerformanceTracker tracker) {
        trackers.remove(tracker.getID());
    }
    
    /**
     * Remove an existing tracker with the given ID from the manager 
     * @param tracker
     * @since 5.0
     */
    public void removeTracker(String trackerID) {
        trackers.remove(trackerID);
    }
    
    /**
     * Reset's the count and timers for the tracker with the given ID 
     * @param trackerID
     * @since 5.0
     */
    public void reset(String trackerID) {
        PerformanceTracker tracker = getTracker(trackerID);
        if( tracker != null ) {
            tracker.reset();
        }
    }
    
    public void resetAll() {
        for( Iterator iter = trackers.values().iterator(); iter.hasNext(); ) {
            PerformanceTracker tracker = (PerformanceTracker)iter.next();
            tracker.reset();
        }
    }
    
    public void clear() {
        trackers.clear();
    }
    
    // -----------------------------------
    // Individual Tracker utility method calls
    // -----------------------------------
    
    /** 
     * @param trackerID
     * @param methodID
     * @since 5.0
     */
    public void start(String trackerID, String methodID) {
        PerformanceTracker tracker = getTracker(trackerID);
        if( tracker != null ) {
            tracker.start(methodID);
        }
    }
    
    /**
     *  
     * @param trackerID
     * @param methodID
     * @since 5.0
     */
    public void stop(String trackerID, String methodID) {
        PerformanceTracker tracker = getTracker(trackerID);
        if( tracker != null ) {
            tracker.start(methodID);
        }
    }
    
    /**
     *  
     * @param trackerID
     * @since 5.0
     */
    public void print(String trackerID) {
        PerformanceTracker tracker = getTracker(trackerID);
        if( tracker != null ) {
            tracker.print();
        }
    }
    
    public void print(boolean clear) {
        for( Iterator iter = trackers.keySet().iterator(); iter.hasNext(); ) {
            String trackerID = (String)iter.next();
            print(trackerID);
        }
        if( clear ) {
            resetAll();
        }
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Keeps track of all TempDirectories created so that they can be removed.
 * This class exists so that when test suites are run there is a simple way to cleanup.
 * Ideally the application and tests will clean up after themselves without the need for this class.
 */
public class TempDirectoryMonitor {
    private static List instances = new ArrayList();
    private static boolean on = false;
    
    protected void createdTempDirectory(TempDirectory tempDirectoryToCreate) {
        instances.add(tempDirectoryToCreate);
    }
    
    public static void turnOn() {
        if (on) {
        } else {
            on = true;
            TempDirectory.setMonitor(new TempDirectoryMonitor());
        }
    }
    
    public static void removeAll() {
        for (Iterator iterator = instances.iterator(); iterator.hasNext(); ) {
            TempDirectory instance = (TempDirectory) iterator.next();
            instance.remove();
        }
        instances = new ArrayList();
    }
    
    public static boolean hasTempDirectoryToRemove() {
        return instances.size() > 0;
    }
}

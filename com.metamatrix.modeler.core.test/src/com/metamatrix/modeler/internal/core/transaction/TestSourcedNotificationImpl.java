/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.transaction;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;

import com.metamatrix.modeler.core.transaction.SourcedNotification;

/**
 * @author Lance Phillips
 *
 * @since 3.1
 */
public class TestSourcedNotificationImpl extends TestCase {
    
    //###################################################################################
    //# Main
    //###################################################################################
    /**
     * @since 3.1
     */
    public static void main(final String[] arguments) {
        TestRunner.run(suite());
    }
    
    //###################################################################################
    //# Test Suite
    //###################################################################################
    /**
     * @since 3.1
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();
        suite.addTestSuite(TestSourcedNotificationImpl.class);
        return suite;
    }
    
    //###################################################################################
    //# Constructors 
    //###################################################################################
    /**
     * Constructor for TestUnitOfWorkProviderImpl.
     * @param name
     */
    public TestSourcedNotificationImpl(String name) {
        super(name);
    }
    
    /**
     * Constructor for TestUnitOfWorkProviderImpl.
     * @param name
     */
    public TestSourcedNotificationImpl() {
        this("TestUnitOfWorkProviderImpl"); //$NON-NLS-1$
    }
    
    //###################################################################################
    //# Helper Methods
    //###################################################################################
    private Notification helpCreateNotification(){
        return new NotificationImpl(Notification.ADD, true, false);
    }
    //###################################################################################
    //# Actual Tests
    //###################################################################################
    
    /**
     * Ensure that SimpleEmfUnitOfWorkProvider can't be created with NULL
     * resource set.
     */
    public void testCreationArgs() {
        //Verify object creation args may both be null
        try {
            new SourcedNotificationImpl(null, null);
        } catch (Exception e) {
            fail("Unexpected object creation error : " + e.getMessage() ); //$NON-NLS-1$
        }
    }
 
    public void testGetNotifications(){
        try {
            final SourcedNotification sn = new SourcedNotificationImpl(null, null);
            assertNotNull(sn);
            assertNotNull(sn.getNotifications() );            
        } catch (Exception e) {
            fail("Unexpected object creation error : " + e.getMessage() ); //$NON-NLS-1$
        }
    }  
    
    /**
     * Test all getters when notification is null
     *
     */
    public void testNullNotificationGetters(){
        StringBuffer failures = new StringBuffer();
        try {
            final SourcedNotification sn = new SourcedNotificationImpl(null, null);
            assertNotNull(sn);
            
            if(sn.getEventType() != 0){
                failures.append("\nError getting eventType with null notification"); //$NON-NLS-1$
            }
            
            if(sn.getFeature() != null){
                failures.append("\nError getting feature with null notification"); //$NON-NLS-1$
            }

            if(sn.getFeatureID(String.class) != 0){
                failures.append("\nError getting featureID with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewBooleanValue() != false){
                failures.append("\nError getting newBooleanValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewByteValue() != 0){
                failures.append("\nError getting newByteValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewCharValue() != ' '){
                failures.append("\nError getting newCharValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewDoubleValue() != 0){
                failures.append("\nError getting newDoubleValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewFloatValue() != 0){
                failures.append("\nError getting newFloatValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewIntValue() != 0){
                failures.append("\nError getting newIntValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewLongValue() != 0){
                failures.append("\nError getting newLongValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewShortValue() != 0){
                failures.append("\nError getting newShortValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewStringValue() != null){
                failures.append("\nError getting newStringValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getNewValue() != null){
                failures.append("\nError getting newValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldBooleanValue() != false){
                failures.append("\nError getting oldBooleanValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldByteValue() != 0){
                failures.append("\nError getting oldByteValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldCharValue() != ' '){
                failures.append("\nError getting oldCharValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldDoubleValue() != 0){
                failures.append("\nError getting oldDoubleValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldFloatValue() != 0){
                failures.append("\nError getting oldFloatValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldIntValue() != 0){
                failures.append("\nError getting oldIntValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldLongValue() != 0){
                failures.append("\nError getting oldLongValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldShortValue() != 0){
                failures.append("\nError getting oldShortValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldStringValue() != null){
                failures.append("\nError getting oldStringValue with null notification"); //$NON-NLS-1$
            }

            if(sn.getOldValue() != null){
                failures.append("\nError getting oldValue with null notification"); //$NON-NLS-1$
            }
            
            if(sn.getPosition() != 0){
                failures.append("\nError getting position with null notification"); //$NON-NLS-1$
            }
            
        } catch (Exception e) {
            failures.append("\nUnexpected error : " + e.getMessage() ); //$NON-NLS-1$
        }
        
        if(failures.length() > 0){
            fail(failures.toString() );
        }
    } 
    
    public void testAddingNullNotification(){
        try {
            final SourcedNotification sn = new SourcedNotificationImpl(null, null);
            assertNotNull(sn);
            assertNotNull(sn.getNotifications() );   
            sn.add(null);
            Collection chain = sn.getNotifications();
            assertTrue("Expected 0 notifications but got " + chain.size(), chain.size() == 0 );         //$NON-NLS-1$
        } catch (Exception e) {
            fail("Unexpected error : " + e.getMessage() ); //$NON-NLS-1$
        }

    }
    
    public void testAddingNotification(){
        try {
            final SourcedNotification sn = new SourcedNotificationImpl(null, null);
            assertNotNull(sn);
            assertNotNull(sn.getNotifications() );   
            sn.add(helpCreateNotification() );
            sn.add(helpCreateNotification() );
            Collection chain = sn.getNotifications();
            assertTrue("Expected 2 notifications but got " + chain.size(), chain.size() == 2 );         //$NON-NLS-1$
        } catch (Exception e) {
            fail("Unexpected error : " + e.getMessage() ); //$NON-NLS-1$
        }

    }

    public void testAddingNotification2(){
        try {
            final SourcedNotification sn = new SourcedNotificationImpl(null, null);
            assertNotNull(sn);
            assertNotNull(sn.getNotifications() );   
            sn.add(helpCreateNotification() );
            Collection chain = sn.getNotifications();
            assertTrue("Expected 1 notification but got " + chain.size(), chain.size() == 1 ); //$NON-NLS-1$
            sn.add(helpCreateNotification() );
            chain = sn.getNotifications();
            assertTrue("Expected 2 notifications but got " + chain.size(), chain.size() == 2 );         //$NON-NLS-1$
        } catch (Exception e) {
            fail("Unexpected error : " + e.getMessage() ); //$NON-NLS-1$
        }

    }

}

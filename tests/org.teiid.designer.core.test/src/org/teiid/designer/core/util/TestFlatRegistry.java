/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.util;


import static org.mockito.Mockito.mock;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.teiid.designer.core.PropertyChangePublisher;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.container.ContainerImpl;


/**
 */
public class TestFlatRegistry extends TestCase {

	private class TestBean implements PropertyChangePublisher {
		
		public static final String NAME_PROPERTY = "name"; //$NON-NLS-1$
		
		private final Map<String, List<PropertyChangeListener>> listenerMap = new HashMap<String, List<PropertyChangeListener>>();
		
		private String name;
		
		/**
		 * @param name
		 */
		public TestBean(String name) {
			this.name = name;
		}

		public void setName(String name) {
			String oldName = this.name;
			String newName = name;
			
			this.name = name;
			
			List<PropertyChangeListener> listenerList = listenerMap.get(NAME_PROPERTY);
			
			if (listenerList != null) {
				listenerList = new ArrayList(listenerList);
				for (PropertyChangeListener listener : listenerList) {
					listener.propertyChange(new PropertyChangeEvent(this, NAME_PROPERTY, oldName, newName));
				}
			}
		}
		
		public String getName() {
			return name;
		}
		
		public List<PropertyChangeListener> getPropertyChangeListeners(String propertyName) {
			return listenerMap.get(propertyName);
		}
		
		@Override
		public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			List<PropertyChangeListener> list = listenerMap.get(propertyName);
			if (list == null) {
				return;
			}
			
			list.remove(listener);
		}
		
		@Override
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			// Not implemented
		}
		
		@Override
		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			List<PropertyChangeListener> list = listenerMap.get(propertyName);
			if (list == null) {
				list = new ArrayList<PropertyChangeListener>();
				listenerMap.put(propertyName, list);
			}
			
			list.add(listener);
		}
		
		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			// Not implemented
		}
	}
	
    private FlatRegistry registry;

    /**
     * Constructor for TestFlatRegistry.
     * @param arg0
     */
    public TestFlatRegistry(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestFlatRegistry.class);
    }

    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.registry = new FlatRegistry();
    }

    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.registry = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestFlatRegistry"); //$NON-NLS-1$
        suite.addTestSuite(TestFlatRegistry.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }
            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================

    public Object helpTestRegister( final Object obj, final String name, final boolean unregister ) {
        registry.register(name, obj);
        if ( registry.lookup(name) != obj ) {
            fail("Unable to find registered object (" + obj + ") with name \"" + name + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        Object registeredObj = registry.unregister(name);
        if ( registeredObj != obj ) {
            fail("Result from unregister did not match registered object (" + obj + ") with name \"" + name + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        if ( !unregister ) {
            registry.register(name, obj);
        }
        return obj;
    }

    public void helpCheckSize( final int expectedSize ) {
        if ( registry.size() != expectedSize ) {
            fail("The register has " + registry.size() + " entries; expected " + expectedSize); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testRegister() {
        helpTestRegister("Object 1","obj1",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpTestRegister("Object 2","obj2",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(2);
        helpTestRegister("Object 3","obj2",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(2);
        helpTestRegister("Object 4","obj4",true); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(2);
        helpTestRegister("Object 4","obj4",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(3);
        helpTestRegister("Object 3","obj3",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(4);
        
        try {
        	// but cannot register an object with a null name
        	helpTestRegister("Object X", null, false); //$NON-NLS-1$
        	fail("Should not be able to register object with a null name"); //$NON-NLS-1$
        }
        catch (IllegalArgumentException ex) {
        	// Should throw this exception when trying to register the container with a null name
        }
    }

    public void testNameAndClassLookup() {
        Container container1 = mock(Container.class);
        Container container2 = mock(ContainerImpl.class);
        
        String CONTAINER_1 = "container1"; //$NON-NLS-1$
        String CONTAINER_2 = "container2"; //$NON-NLS-1$
        
        registry.register(CONTAINER_1, container1);
        registry.register(CONTAINER_2, container2);
        
        Container c1 = registry.lookup(CONTAINER_1, Container.class);
        assertSame(container1, c1);
        
        Object cNull = registry.lookup(CONTAINER_1, String.class);
        assertNull(cNull);
        
        Container c2 = registry.lookup(CONTAINER_2, ContainerImpl.class);
        assertSame(container2, c2);
        
        // container c2 is an instance of Container
        c2 = registry.lookup(CONTAINER_2, Container.class);
        assertSame(container2, c2);
    }
    
    public void testPropertyChangePublisherRegistrationWithNullName() {
    	String name = null;
    	TestBean testBean = new TestBean(name);
    	try {
    		Object o = registry.register(name, testBean, TestBean.NAME_PROPERTY);
    		fail("Should not be able to register object with a null name"); //$NON-NLS-1$
    	}
    	catch (IllegalArgumentException ex) {
    		// Should throw this exception when trying to register the container with a null name
    	}
    }
    
    public void testPropertyChangePubisherRegistration() {
    	String name = "My Name"; //$NON-NLS-1$
    	TestBean testBean = new TestBean(name);
        Object o = registry.register(name, testBean, TestBean.NAME_PROPERTY);
        
        assertSame(testBean, o);
        assertEquals(1, registry.size());
        
        List<PropertyChangeListener> propertyChangeListeners = testBean.getPropertyChangeListeners(TestBean.NAME_PROPERTY);
		assertEquals(1, propertyChangeListeners.size());
        assertEquals(registry, propertyChangeListeners.get(0));
    }
    
    public void testPropertyChangePublisherLookup() {
    	String name = "My Name"; //$NON-NLS-1$
    	String newName = "My New Name"; //$NON-NLS-1$
    	TestBean testBean = new TestBean(name);
    	
    	// Registery the test bean against its name
        Object o = registry.register(testBean.getName(), testBean, TestBean.NAME_PROPERTY);
        
        // Lookup the test bean by its name
        PropertyChangePublisher p1 = registry.lookup(name, TestBean.class);
        assertSame(o, p1);
    }
    
    public void testPropertyChangePublisherSetName() {
    	String name = "My Name"; //$NON-NLS-1$
    	String newName = "My New Name"; //$NON-NLS-1$
    	TestBean testBean = new TestBean(name);
    	
    	// Registery the test bean against its name
        Object o = registry.register(testBean.getName(), testBean, TestBean.NAME_PROPERTY);
        
        // Lookup the test bean by its name
        PropertyChangePublisher p1 = registry.lookup(name, TestBean.class);
        
        // Set the test bean's name to something else
        testBean.setName(newName);
        assertEquals(newName, testBean.getName());
        
        // Test bean now registered under new name
        PropertyChangePublisher p2 = registry.lookup(newName, TestBean.class);
        assertSame(o, p2);
        assertSame(p1, p2);
        
        // Test bean no longer registered under old name
        assertNull(registry.lookup(name, TestBean.class));
    }
    
    public void testPropertyChangePublisherSetNameToNull() {
    	String name = "My Name"; //$NON-NLS-1$
    	String newName = null;
    	TestBean testBean = new TestBean(name);
    	
    	// Registery the test bean against its name
        registry.register(testBean.getName(), testBean, TestBean.NAME_PROPERTY);
        
        // Set the test bean's name to something else
        try {
        	testBean.setName(newName);
        	fail("Cannot register a null name to the registry"); //$NON-NLS-1$
        }
        catch (IllegalArgumentException ex) {
        	// Cannot see the name to null as cannot register a null key
        }
    }

}

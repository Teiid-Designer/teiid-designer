/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.eventsupport;

import java.util.ArrayList;
import junit.framework.TestCase;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * TestSelectionProvider
 */
public class TestSelectionProvider extends TestCase {

    /**
     * Constructor for TestSelectionProvider.
     * @param name
     */
    public TestSelectionProvider(String name) {
        super(name);
    }

    // ========================================================
    // utility methods

    private ISelection createSelection() {
        Object[] array = new Object[] { new Object(), new Object(), new Object() };
        return new StructuredSelection(array); 
    }

    class InstrumentedSelectionChangedListener implements ISelectionChangedListener {
        public ISelection selection;
        public Object source;
        public SelectionChangedEvent event;
        public void selectionChanged(SelectionChangedEvent e) {
            this.event = e;
            this.source = e.getSource();
            this.selection = e.getSelection();
        }
    }

    // ========================================================
    // test cases

    /*
     * Test for void SelectionProvider()
     */
    public void testSelectionProvider() {
        SelectionProvider p = new SelectionProvider();
        assertNotNull(p);
        assertNotNull(p.getListenerList());
        assertNull(p.getSelection());
    }

    public void testAddSelectionChangedListener() {
        SelectionProvider p = new SelectionProvider();
        p.addSelectionChangedListener(null);
        assertTrue(p.getListenerList().size() == 0);

        ISelectionChangedListener l = new InstrumentedSelectionChangedListener();
        p.addSelectionChangedListener(l);
        assertTrue(p.getListenerList().size() == 1);
        p.addSelectionChangedListener(l);
        assertTrue(p.getListenerList().size() == 1);
    }

    public void testRemoveSelectionChangedListener() {
        SelectionProvider p = new SelectionProvider();
        p.removeSelectionChangedListener(null);
        assertTrue(p.getListenerList().size() == 0);

        ISelectionChangedListener l = new InstrumentedSelectionChangedListener();
        p.removeSelectionChangedListener(l);
        assertTrue(p.getListenerList().size() == 0);
        p.addSelectionChangedListener(l);
        assertTrue(p.getListenerList().size() == 1);
        p.removeSelectionChangedListener(l);
        assertTrue(p.getListenerList().size() == 0);
        p.removeSelectionChangedListener(l);
        assertTrue(p.getListenerList().size() == 0);
    }

    /*
     * Test for void setSelection(SelectionChangedEvent)
     */
    public void testSetSelectionSelectionChangedEvent() {
        SelectionProvider source = new SelectionProvider();
        SelectionProvider p = new SelectionProvider();
        
        InstrumentedSelectionChangedListener l = new InstrumentedSelectionChangedListener();
        p.addSelectionChangedListener(l);
        assertNull(l.selection);
        
        ISelection selection = createSelection();
        p.setSelection(new SelectionChangedEvent(source, selection));
        
        assertTrue(l.selection == selection);
        assertTrue(l.source == source);
        assertTrue(p.getSelection() == selection);
        
        p.setSelection((SelectionChangedEvent) null);
        assertNull(p.getSelection());
    }

    /*
     * Test for void setSelection(List)
     */
    public void testSetSelectionList() {
        ArrayList list = new ArrayList(2);
        list.add(new Object());
        list.add(new Object());
        SelectionProvider p = new SelectionProvider();
        InstrumentedSelectionChangedListener l = new InstrumentedSelectionChangedListener();
        p.addSelectionChangedListener(l);
        p.setSelection(list);
        assertNotNull(l.selection);

        StructuredSelection selection = new StructuredSelection(list);
        assertEquals(l.selection, selection);
    }

    /*
     * Test for void setSelection(ISelection)
     */
    public void testSetSelectionISelection() {
        SelectionProvider p = new SelectionProvider();
        InstrumentedSelectionChangedListener l = new InstrumentedSelectionChangedListener();
        ISelection selection = createSelection();
        p.addSelectionChangedListener(l);
        p.setSelection(selection);
        assertNotNull(l.selection);
        assertTrue(l.selection == selection);
    }

    /*
     * Test for void setSelection(ISelection, boolean)
     */
    public void testSetSelectionISelectionboolean() {
        SelectionProvider p = new SelectionProvider();
        InstrumentedSelectionChangedListener l = new InstrumentedSelectionChangedListener();
        ISelection selection = createSelection();
        p.addSelectionChangedListener(l);
        p.setSelection(selection, false);
        assertNull(l.selection);
        p.setSelection(selection, true);
        assertNotNull(l.selection);
        assertTrue(l.selection == selection);
    }

    /*
     * Test for void setSelection(ISelection, boolean, ISelectionProvider)
     */
    public void testSetSelectionISelectionbooleanISelectionProvider() {
        SelectionProvider p = new SelectionProvider();
        InstrumentedSelectionChangedListener l = new InstrumentedSelectionChangedListener();
        ISelection selection = createSelection();
        p.addSelectionChangedListener(l);
        p.setSelection(selection, false, null);
        assertNull(l.selection);
        p.setSelection(selection, true, null);
        assertNotNull(l.selection);
        assertTrue(l.selection == selection);
        SelectionProvider source = new SelectionProvider();
        p.setSelection(selection, true, source);
        assertNotNull(l.selection);
        assertTrue(l.selection == selection);
        assertTrue(l.source == source);
    }

    public void testFireSelectionChangedEvent() {
        SelectionProvider p = new SelectionProvider();
        InstrumentedSelectionChangedListener l = new InstrumentedSelectionChangedListener();
        p.addSelectionChangedListener(l);
        ISelection selection = createSelection();
        SelectionProvider source = new SelectionProvider();
        p.fireSelectionChangedEvent(new SelectionChangedEvent(source, selection));
        assertNotNull(l.selection);
        assertTrue(l.selection == selection);
        assertTrue(l.source == source);

        // calling this method should NOT set the internal selection - that's what setSelection(event) is for
        assertNull(p.getSelection());
    }

}

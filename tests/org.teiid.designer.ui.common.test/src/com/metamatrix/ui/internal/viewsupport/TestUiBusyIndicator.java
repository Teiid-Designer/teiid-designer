/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import junit.framework.TestCase;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Test {@link UiBusyIndicator}
 */
public class TestUiBusyIndicator extends TestCase {

    /*
     * This is set to a value that should not impact the testing time overall.
     * However, this is probably a little quick for manual inspection of the
     * 'busy icon' so increase as appropriate.
     * 
     * The doWork() has a thread sleep in it to extend the time taken for the
     * work this is 1/200th of this value.
     */
    private static final int LONG_TIME = 1000000;

    private class TestShell extends Shell {

        private Text text;
        private String value;

        public TestShell(Display display) {
            super(display);
            GridLayoutFactory.fillDefaults().applyTo(this);

            text = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(text);
        }

        @Override
        protected void checkSubclass() {
        }

        @Override
        public void setText(String value) {
            this.value = value;
            text.setText(value);
        }

        @Override
        public String getText() {
            if (!text.isDisposed())
                return text.getText();

            return value;
        }
    }

    private Display display;
    private Runnable nonUIRunnable;
    private Runnable uIRunnable;

    private int runnableFinalValue;
    private TestShell testShell;

    @Override
    protected void setUp() throws Exception {
        display = Display.getDefault() != null ? Display.getDefault()
                : new Display();

        runnableFinalValue = 0;

        nonUIRunnable = new Runnable() {
            @Override
            public void run() {
                checkCursor(SWT.CURSOR_WAIT);
                doWork();
                checkCursor(SWT.CURSOR_WAIT);
            }
        };
        

        uIRunnable = new Runnable() {
            @Override
            public void run() {
                if (testShell == null || testShell.isDisposed())
                    return;

                checkCursor(SWT.CURSOR_WAIT);

                doWork();

                testShell.setText(new Integer(runnableFinalValue).toString());

                checkCursor(SWT.CURSOR_WAIT);
            } 
        };
    }

    private void doWork() {
        try {
            Thread.sleep(LONG_TIME / 200);
        }
        catch (InterruptedException ex) {
        }

        for (int i = 0; i <= LONG_TIME; ++i) {
            runnableFinalValue = i;
        }
    }

    private void checkCursor(int style) {
        Shell[] shells = display.getShells();
        for (int i = 0; i < shells.length; i++) {
            Cursor cursor = shells[i].getCursor();
            if (style == 0) {
                assertEquals(null, cursor);
            }
            else {
                assertEquals(display.getSystemCursor(style), cursor);
            }
        }
    }

    public void testNonUIRunnable() {
        UiBusyIndicator.showWhile(display, nonUIRunnable);
        assertEquals(LONG_TIME, runnableFinalValue);
        checkCursor(SWT.CURSOR_ARROW);
    }

    public void testUIRunnable() {
        testShell = new TestShell(display);
        testShell.setBounds(200, 200, 400, 400);
        testShell.open();

        UiBusyIndicator.showWhile(display, uIRunnable);
        assertEquals(LONG_TIME, runnableFinalValue);
        assertEquals(LONG_TIME, Integer.parseInt(testShell.getText()));
        checkCursor(SWT.CURSOR_ARROW);

        testShell.dispose();
    }

    public void testUIRunnableThrowingException() {
        final RuntimeException testException = new RuntimeException(
                "Test exception that should be thrown"); //$NON-NLS-1$
        
        Runnable exceptionRunnable = new Runnable() {
            @Override
            public void run() {
                throw testException;
            }
        };

        try {
            UiBusyIndicator.showWhile(display, exceptionRunnable);
            fail("An SWTError should be thrown encapsulating the real exception thrown from the runnable"); //$NON-NLS-1$
        }
        catch (SWTException ex) {
            assertEquals(testException, ex.getCause());
        }
        
        // Test that despite the exception being thrown
        // the default cursor is still returned
        checkCursor(SWT.CURSOR_ARROW);
    }
}

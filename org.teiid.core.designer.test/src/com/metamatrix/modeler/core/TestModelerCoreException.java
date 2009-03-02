/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.util.ExternalizeUtil;

/**
 */
public class TestModelerCoreException extends TestCase {

    /**
	 * Assert that the two exceptions have the same message and status.
	 */
	public static void assertEqualThrowables( Throwable e1,
	                                          Throwable e2 ) {
		assertEquals(e1.getClass(), e2.getClass());
		assertEquals(e1.getMessage(), e2.getMessage());

		StackTraceElement[] stack1 = e1.getStackTrace();
		StackTraceElement[] stack2 = e2.getStackTrace();
		assertEquals(stack1.length, stack2.length);
		for (int i = 0; i < stack1.length; i++) {
			assertEquals(stack1[i], stack2[i]);
		}
	}

	public static Object helpSerializeRoundtrip( Object testObject ) throws Exception {
		Object result = null;
		// build a buffer to use for the output
		ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(testObject);
		oout.close();

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		ObjectInputStream oin = new ObjectInputStream(bin);
		result = oin.readObject();
		return result;
	}

    private ByteArrayOutputStream bout;
    private ObjectOutputStream oout;

    private ByteArrayInputStream bin;
    private ObjectInputStream oin;

    /**
     * Constructor for TestModelerCoreException.
     * @param name
     */
    public TestModelerCoreException(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        bout = new ByteArrayOutputStream(4096);
        oout = new ObjectOutputStream(bout);
    }

    @Override
    public void tearDown() throws Exception {
        oout.close();
        bout.close();

        if (oin!=null) {
            oin.close();
        }
        if (bin!=null) {
            bin.close();
        }
    }

    public void testGetNonNullMessage_nonNullMessage() {
        String msg = "xyz"; //$NON-NLS-1$
        String out = ModelerCoreException.getNonNullMessage(msg);
        assertEquals("Did not get expected message", msg, out); //$NON-NLS-1$
    }

    public void testGetNonNullMessage_nullMessage() {
        String out = ModelerCoreException.getNonNullMessage(null);
        assertEquals("Did not get expected message", "", out); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testGetNonNullMessage_emptyMessage() {
        String msg = ""; //$NON-NLS-1$
        String out = ModelerCoreException.getNonNullMessage(msg);
        assertEquals("Did not get expected message", msg, out); //$NON-NLS-1$
    }

    public void testGetNonNullThrowableMessage_nullThrowable() {
        String out = ModelerCoreException.getNonNullMessageFromThrowable(null);
        assertEquals("Did not get expected message", "", out);         //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testGetNonNullThrowableMessage_nullMessage() {
        Throwable t = new NullPointerException();
        String out = ModelerCoreException.getNonNullMessageFromThrowable(t);
        assertEquals("Did not get expected message", "", out);         //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testGetNonNullThrowableMessage_hasMessage() {
        String msg = "xyz"; //$NON-NLS-1$
        Throwable t = new NullPointerException(msg);
        String out = ModelerCoreException.getNonNullMessageFromThrowable(t);
        assertEquals("Did not get expected message", msg, out);         //$NON-NLS-1$
    }

    public void testConstructor1_null() {
        try {
            new ModelerCoreException((CoreException)null);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor1() {
        String msg = "msg"; //$NON-NLS-1$
        NullPointerException npe = new NullPointerException();
        IStatus status = new Status(2, "xyz", 10, msg, npe); //$NON-NLS-1$
        CoreException e1 = new CoreException(status);
        ModelerCoreException e2 = new ModelerCoreException(e1);
        assertEquals("Did not get real exception", e1, e2.getException());         //$NON-NLS-1$
        assertEquals("Did not get real status", status, e2.getStatus()); //$NON-NLS-1$
        assertEquals("Did not get real message", msg, e2.getMessage()); //$NON-NLS-1$

    }

    public void testConstructor2_null() {
        try {
            new ModelerCoreException((IStatus)null);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor2() {
        String msg = "msg"; //$NON-NLS-1$
        NullPointerException npe = new NullPointerException();
        IStatus status = new Status(2, "xyz", 10, msg, npe); //$NON-NLS-1$
        ModelerCoreException e2 = new ModelerCoreException(status);
        assertEquals("Did not get real exception", npe, e2.getException());         //$NON-NLS-1$
        assertEquals("Did not get real status", status, e2.getStatus()); //$NON-NLS-1$
        assertEquals("Did not get real message", msg, e2.getMessage()); //$NON-NLS-1$

    }

    public void testConstructor3_null() {
        try {
            new ModelerCoreException(0, null);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor3() {
        String msg = "msg"; //$NON-NLS-1$
        int code = 10;
        ModelerCoreException e2 = new ModelerCoreException(code, msg);
        assertEquals("Did not get real status", code, e2.getStatus().getCode()); //$NON-NLS-1$
        assertEquals("Did not get real message", msg, e2.getMessage()); //$NON-NLS-1$

    }

    public void testConstructor4_null() {
        try {
            new ModelerCoreException((String)null);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor4() {
        String msg = "msg"; //$NON-NLS-1$
        ModelerCoreException e2 = new ModelerCoreException(msg);
        assertEquals("Did not get real message", msg, e2.getMessage()); //$NON-NLS-1$

    }

    public void testConstructor5_null() {
        try {
            new ModelerCoreException((Throwable)null);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor5() {
        NullPointerException npe = new NullPointerException();
        ModelerCoreException e2 = new ModelerCoreException(npe);
        assertEquals("Did not get real exception", npe, e2.getException()); //$NON-NLS-1$

    }

    public void testConstructor6_null() {
        try {
            new ModelerCoreException((Throwable)null, 0);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor6() {
        NullPointerException npe = new NullPointerException();
        int code = 10;
        ModelerCoreException e2 = new ModelerCoreException(npe, code);
        assertEquals("Did not get real message", npe, e2.getException()); //$NON-NLS-1$
        assertEquals("Did not get real status", code, e2.getStatus().getCode()); //$NON-NLS-1$

    }

    public void testConstructor7_null() {
        try {
            new ModelerCoreException((Throwable)null, 0, null);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor7() {
        NullPointerException npe = new NullPointerException();
        int code = 10;
        String msg = "msg"; //$NON-NLS-1$
        ModelerCoreException e2 = new ModelerCoreException(npe, code, msg);
        assertEquals("Did not get real exception", npe, e2.getException()); //$NON-NLS-1$
        assertEquals("Did not get real status", code, e2.getStatus().getCode()); //$NON-NLS-1$
        assertEquals("Did not get real message", msg, e2.getMessage()); //$NON-NLS-1$

    }

    public void testConstructor8_null() {
        try {
            new ModelerCoreException((Throwable)null, null);
        } catch(Throwable e) {
            fail(e.getMessage());
        }
    }

    public void testConstructor8() {
        NullPointerException npe = new NullPointerException();
        String msg = "msg"; //$NON-NLS-1$
        ModelerCoreException e2 = new ModelerCoreException(npe, msg);
        assertEquals("Did not get real exception", npe, e2.getException()); //$NON-NLS-1$
        assertEquals("Did not get real message", msg, e2.getMessage()); //$NON-NLS-1$

    }

    public void testSerialization() throws Exception {
		NullPointerException npe = new NullPointerException();
		int code = 10;
		String msg = "msg"; //$NON-NLS-1$
		ModelerCoreException e = new ModelerCoreException(npe, code, msg);
		// Serialize once
		Object s1 = helpSerializeRoundtrip(e);
		assertNotNull(s1);
		assertTrue(s1 instanceof ModelerCoreException);
		ModelerCoreException e1 = (ModelerCoreException)s1;
		assertEquals(code, e1.getStatus().getCode());

		// Serialize a second time
		Object s2 = helpSerializeRoundtrip(s1);
		assertNotNull(s2);
		assertTrue(s2 instanceof ModelerCoreException);
		ModelerCoreException e2 = (ModelerCoreException)s2;
		assertEquals(code, e2.getStatus().getCode());
	}

    public static IStatus example() {
        Status child = new Status(IStatus.OK, "PluginID", 4, "Hello", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
        MultiStatus multi =  new MultiStatus("PluginID", IStatus.ERROR, new IStatus[] {child}, "MyMessage", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
        return new ModelerCoreException.StatusImpl(multi);
    }

    public void testSerializeIStatus() throws Exception {
		IStatus example = example();
		Object serialized = helpSerializeRoundtrip(example);
		assertNotNull(serialized);
		assertTrue(serialized instanceof ModelerCoreException.StatusImpl);
		assertTrue(serialized.equals(example));

	}

    /**
     * Test ExternalizeUtil writeThrowable() and readThrowable() on CoreExceptions.
     * @throws Exception
     */
    public void testWriteThrowableCoreException() throws Exception {
        Status status1 = new Status(IStatus.WARNING, "plugin1", 1, "message1", null); //$NON-NLS-1$//$NON-NLS-2$
        ModelerCoreException t1 = new ModelerCoreException(status1);

        ExternalizeUtil.writeThrowable(oout, t1);
        oout.flush();
        bin = new ByteArrayInputStream(bout.toByteArray());
        oin = new ObjectInputStream(bin);

        Throwable result1 = ExternalizeUtil.readThrowable(oin);
        assertEqualThrowables(t1, result1);
    }

    /**
     * Test ExternalizeUtil writeThrowable() and readThrowable() on a mix of types.
     * @throws Exception
     */
    public void testWriteThrowableMixed() throws Exception {
        Status status3 = new Status(IStatus.WARNING, "plugin3", 3, "message3", null); //$NON-NLS-1$//$NON-NLS-2$
        ModelerCoreException t3 = new ModelerCoreException(status3);
        MetaMatrixCoreException t2 = new MetaMatrixCoreException(t3);
        Throwable t1 = new Throwable(t2);

        ExternalizeUtil.writeThrowable(oout, t1);
        oout.flush();
        bin = new ByteArrayInputStream(bout.toByteArray());
        oin = new ObjectInputStream(bin);

        Throwable result1 = ExternalizeUtil.readThrowable(oin);
        assertEqualThrowables(t1, result1);

        MetaMatrixCoreException result2 = (MetaMatrixCoreException) result1.getCause();
        assertEqualThrowables(t2, result2);
    }
}

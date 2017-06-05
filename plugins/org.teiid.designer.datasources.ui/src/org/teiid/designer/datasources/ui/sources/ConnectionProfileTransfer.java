/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class ConnectionProfileTransfer extends ByteArrayTransfer {

    private Object object;
    private long startTime;

    private static final ConnectionProfileTransfer INSTANCE = new ConnectionProfileTransfer();
    private static final String TYPE_NAME = "Connection Profile Transfer"//$NON-NLS-1$
        + System.currentTimeMillis()
        + ":" + INSTANCE.hashCode();//$NON-NLS-1$
    private static final int TYPEID = registerType(TYPE_NAME);

    /**
     * Returns the singleton instance.
     * @return The singleton instance
     */
    public static ConnectionProfileTransfer getInstance() {
        return INSTANCE;
    }

    private ConnectionProfileTransfer() { }

    /**
     * @see Transfer#getTypeIds()
     */
    @Override
    protected int[] getTypeIds() {
        return new int[] {TYPEID};
    }

    /**
     * @see Transfer#getTypeNames()
     */
    @Override
    protected String[] getTypeNames() {
        return new String[] {TYPE_NAME};
    }
    /**
     * Returns the Object.
     * 
     * @return The Object
     */
    public Object getObject() {
        return object;
    }

    /**
     * The data object is not converted to bytes. It is held onto in a field. Instead, a checksum is written out to prevent
     * unwanted drags across mulitple running copies of Eclipse.
     * 
     * @see org.eclipse.swt.dnd.Transfer#javaToNative(Object, TransferData)
     */
    @Override
    public void javaToNative(Object object,
                             TransferData transferData) {
        startTime = System.currentTimeMillis();
        if (transferData != null)
            super.javaToNative(String.valueOf(startTime).getBytes(), transferData);
    }

    /**
     * The data object is not converted to bytes. It is held onto in a field. Instead, a checksum is written out to prevent
     * unwanted drags across mulitple running. copies of Eclipse.
     * 
     * @see org.eclipse.swt.dnd.Transfer#nativeToJava(TransferData)
     */
    @Override
    public Object nativeToJava(TransferData transferData) {
        byte bytes[] = (byte[])super.nativeToJava(transferData);
        if( bytes != null ) {
            long startTime = Long.parseLong(new String(bytes));
            return (this.startTime == startTime) ? getObject() : null;
        }
        return getObject();
    }

    /**
     * Sets the Object.
     * 
     * @param obj
     *            The Object
     */
    public void setObject(Object obj) {
        object = obj;
    }
}

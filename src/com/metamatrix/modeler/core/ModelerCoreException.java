/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.util.EquivalenceUtil;
import com.metamatrix.core.util.ExternalizeUtil;

public class ModelerCoreException extends CoreException implements Externalizable {
    private static final String NO_MESSAGE = "No Error Message"; //$NON-NLS-1$

    CoreException nestedCoreException;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public ModelerCoreException() {
        // Set the status to some non-null value for now.
        super(new StatusHolder(new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, getNonNullMessage(NO_MESSAGE), null)));
    }

    /**
     * Construct an instance of ModelerCoreException.
     * 
     * @param e
     * @param code
     */
    public ModelerCoreException( Throwable e,
                                 int code ) {
        super(new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, code, "", e)); //$NON-NLS-1$
    }

    /**
     * Construct an instance of ModelerCoreException.
     * 
     * @param exception
     */
    public ModelerCoreException( CoreException exception ) {
        super(exception == null ? new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, "", null) : exception.getStatus()); //$NON-NLS-1$
        if (exception != null) {
            this.nestedCoreException = exception;
        }
    }

    /**
     * Construct an instance of ModelerCoreException.
     * 
     * @param status
     */
    public ModelerCoreException( IStatus status ) {
        super(status == null ? new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, "", null) : status); //$NON-NLS-1$
    }

    /**
     * Construct an instance of ModelerCoreException.
     * 
     * @param message
     */
    public ModelerCoreException( String message ) {
        super(new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, getNonNullMessage(message), null));
    }

    public ModelerCoreException( int code,
                                 String message ) {
        super(new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, code, getNonNullMessage(message), null));
    }

    public ModelerCoreException( Throwable e,
                                 int code,
                                 String message ) {
        super(new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, code, getNonNullMessage(message), e));
    }

    /**
     * Construct an instance of ModelerCoreException.
     * 
     * @param e
     */
    public ModelerCoreException( Throwable e ) {
        super(new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, getNonNullMessageFromThrowable(e), e));
    }

    /**
     * Construct an instance of ModelerCoreException.
     * 
     * @param e
     * @param message
     */
    public ModelerCoreException( Throwable e,
                                 String message ) {
        super(new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, getNonNullMessage(message), e));
    }

    /**
     * Subclasses may override this method, which is used by {@link #toString()} to obtain the "type" for the exception class.
     * 
     * @return the type; defaults to "Modeler Core Exception"
     */
    protected String getToStringType() {
        return "Modeler Core Exception"; //$NON-NLS-1$
    }

    /**
     * Get non-null version of message.
     * 
     * @param message Message
     * @return Original message if non-null, otherwise ""
     */
    static String getNonNullMessage( String message ) {
        if (message == null) {
            return ""; //$NON-NLS-1$
        }
        return message;
    }

    /**
     * Get non-null version of message.
     * 
     * @param message Message
     * @return Original message if non-null, otherwise ""
     */
    static String getNonNullMessageFromThrowable( Throwable t ) {
        if (t == null) {
            return ""; //$NON-NLS-1$
        }
        return getNonNullMessage(t.getMessage());
    }

    /**
     * Returns a printable representation of this exception suitable for debugging purposes only.
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getToStringType());
        buffer.append(":"); //$NON-NLS-1$
        if (getException() != null) {
            if (getException() instanceof CoreException) {
                CoreException c = (CoreException)getException();
                buffer.append(" Core Exception [code "); //$NON-NLS-1$
                buffer.append(c.getStatus().getCode());
                buffer.append("] "); //$NON-NLS-1$
                buffer.append(c.getStatus().getMessage());
            } else {
                buffer.append(" "); //$NON-NLS-1$
                buffer.append(getException().toString());
            }
        } else {
            buffer.append(" "); //$NON-NLS-1$
            buffer.append(getStatus().toString());
        }
        return buffer.toString();
    }

    /**
     * Overrides the default implementation to return the message contained within the status. This is useful because when this
     * object is deserialized, the deserialized message may be different from the default message (i.e. the message created by the
     * no-arg constructor).
     * 
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return getStatus().getMessage();
    }

    public Throwable getException() {
        if (this.nestedCoreException == null) {
            return getStatus().getException();
        }
        return this.nestedCoreException;
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        final IStatus status = readIStatus(in);
        final IStatus currentStatus = getStatus();
        if (currentStatus instanceof StatusHolder && status != null) {
            // Assuming the no-arg constructor of this class was called by the
            // deserialization, we need to set the holder's actual IStatus
            // to the object we just deserialized.
            ((StatusHolder)currentStatus).setStatus(status);
        }

        StackTraceElement[] stackTrace = (StackTraceElement[])in.readObject();
        setStackTrace(stackTrace);

        nestedCoreException = (CoreException)ExternalizeUtil.readThrowable(in);
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal( ObjectOutput out ) throws IOException {
        writeIStatus(out, this.getStatus());
        out.writeObject(this.getStackTrace());

        ExternalizeUtil.writeThrowable(out, nestedCoreException);
    }

    /**
     * Holder that forwards all calls to the contained IStatus instance.
     */
    private static class StatusHolder implements IStatus, Serializable {

        private static final long serialVersionUID = 1L;
        private IStatus status = null;

        /** No-arg constructor to fulfill the Serializable contract */
        public StatusHolder() {
        }

        StatusHolder( IStatus status ) {
            setStatus(status);
        }

        void setStatus( IStatus status ) {
            this.status = status;
        }

        public IStatus[] getChildren() {
            return status.getChildren();
        }

        public int getCode() {
            return status.getCode();
        }

        public Throwable getException() {
            return status.getException();
        }

        public String getMessage() {
            return status.getMessage();
        }

        public String getPlugin() {
            return status.getPlugin();
        }

        public int getSeverity() {
            return status.getSeverity();
        }

        public boolean isMultiStatus() {
            return status.isMultiStatus();
        }

        public boolean isOK() {
            return status.isOK();
        }

        public boolean matches( int severityMask ) {
            return status.matches(severityMask);
        }
    }

    /*
     * Serializing CoreException and subclasses.
     */
    public static void writeThrowable( ObjectOutput out,
                                       Throwable t ) throws IOException {
        if (t == null || !(t instanceof CoreException) || (t instanceof MetaMatrixCoreException)) {
            out.writeBoolean(false);
            out.writeObject(t);
        } else {
            out.writeBoolean(true);
            writeCoreException(out, (CoreException)t);
        }
    }

    public static void writeCoreException( ObjectOutput out,
                                           CoreException e ) throws IOException {
        writeIStatus(out, e.getStatus());
        out.writeObject(e.getStackTrace());
    }

    public static void writeIStatus( ObjectOutput out,
                                     IStatus status ) throws IOException {
        StatusImpl serializableStatus = new StatusImpl(status);
        out.writeObject(serializableStatus);
    }

    public static Throwable readThrowable( ObjectInput in ) throws IOException, ClassNotFoundException {
        final boolean isCoreException = in.readBoolean();
        if (isCoreException) {
            return readCoreException(in);
        }

        return (Throwable)in.readObject();
    }

    public static CoreException readCoreException( ObjectInput in ) throws IOException, ClassNotFoundException {
        IStatus status = readIStatus(in);
        CoreException exception = new CoreException(status);

        StackTraceElement[] stackTrace = (StackTraceElement[])in.readObject();
        exception.setStackTrace(stackTrace);
        return exception;
    }

    public static IStatus readIStatus( ObjectInput in ) throws IOException, ClassNotFoundException {
        IStatus status = (IStatus)in.readObject();
        return status;
    }

    /**
     * Serializable implementation of IStatus used to serialize instances of CoreException and MetaMatrixCoreException
     */
    public static class StatusImpl implements IStatus, Externalizable {
        public static final long serialVersionUID = 0;

        private StatusImpl[] children = null;
        private int code = 0;
        private Throwable exception = null;
        private String message = null;
        private String plugin = null;
        private int severity = 0;
        private boolean multiStatus = false;
        private boolean ok = false;

        /**
         * Constructor required by Externalizable constract
         */
        public StatusImpl() {
        }

        /**
         * Create a StatusImpl if you know you're shipping across the wire.
         * 
         * @since 4.2
         */
        public StatusImpl( int severity,
                           String pluginId,
                           int code,
                           String message,
                           Throwable exception ) {
            this.severity = severity;
            this.plugin = pluginId;
            this.code = code;
            this.message = message;
            this.exception = exception;
        }

        /**
         * Copy constructor that converts all instances of IStatus in the original tree to an instance of StatusImpl to guarantee
         * serialization.
         * 
         * @param original
         * @throws NullPointerException if the original IStatus is null
         */
        public StatusImpl( IStatus original ) {
            if (original.getChildren() != null) {
                final int length = original.getChildren().length;
                this.children = new StatusImpl[length];
                for (int i = 0; i < length; i++) {
                    if (original.getChildren()[i] != null) {
                        this.children[i] = new StatusImpl(original.getChildren()[i]);
                    }
                }
            }
            this.code = original.getCode();
            this.exception = original.getException();
            this.message = original.getMessage();
            this.plugin = original.getPlugin();
            this.severity = original.getSeverity();
            this.multiStatus = original.isMultiStatus();
            this.ok = original.isOK();
        }

        /**
         * Constructs a StatusImpl. Converts all the IStatus instances into StatusImpls to guarantee serialization.
         */
        public StatusImpl( IStatus[] children,
                           int code,
                           Throwable exception,
                           String message,
                           String plugin,
                           int severity,
                           boolean multiStatus,
                           boolean ok ) {
            if (children != null) {
                final int length = children.length;
                this.children = new StatusImpl[length];
                for (int i = 0; i < length; i++) {
                    if (children[i] != null) {
                        this.children[i] = new StatusImpl(children[i]);
                    }
                }
            }
            this.code = code;
            this.exception = exception;
            this.message = message;
            this.plugin = plugin;
            this.severity = severity;
            this.multiStatus = multiStatus;
            this.ok = ok;
        }

        public IStatus[] getChildren() {
            return children;
        }

        public int getCode() {
            return code;
        }

        public Throwable getException() {
            return exception;
        }

        public String getMessage() {
            return message;
        }

        public String getPlugin() {
            return plugin;
        }

        public int getSeverity() {
            return severity;
        }

        public boolean isMultiStatus() {
            return multiStatus;
        }

        public boolean isOK() {
            return ok;
        }

        public boolean matches( int severityMask ) {
            return (severity & severityMask) != 0;
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof IStatus)) return false;
            IStatus other = (IStatus)obj;
            return EquivalenceUtil.areEquivalent(this.children, other.getChildren()) && this.code == other.getCode()
                   && EquivalenceUtil.areEqual(this.message, other.getMessage())
                   && EquivalenceUtil.areEqual(this.plugin, other.getPlugin()) && this.severity == other.getSeverity()
                   && this.multiStatus == other.isMultiStatus() && this.ok == other.isOK();
            // both have a throwable, or neither has a throwable
            // (this.exception == null ^ other.getException() != null)

        }

        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            final int length = in.readInt();
            children = new StatusImpl[length];
            for (int i = 0; i < length; i++) {
                children[i] = (StatusImpl)in.readObject();
            }
            code = in.readInt();
            exception = readThrowable(in);
            message = (String)in.readObject();
            plugin = (String)in.readObject();
            severity = in.readInt();
            multiStatus = in.readBoolean();
            ok = in.readBoolean();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            ExternalizeUtil.writeArray(out, children);
            out.writeInt(code);
            writeThrowable(out, exception);
            out.writeObject(message);
            out.writeObject(plugin);
            out.writeInt(severity);
            out.writeBoolean(multiStatus);
            out.writeBoolean(ok);
        }
    }

}

package com.metamatrix.modeler.transformation.udf;

import java.net.URL;

public class UdfModelEvent {

    // ===========================================================================================================================
    // Enums
    // ===========================================================================================================================

    public enum Type {
        NEW,
        CHANGED,
        DELETED;
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private final Type type;

    private final URL url;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    public UdfModelEvent( URL url ) {
        this.type = Type.CHANGED;
        this.url = url;
    }

    public UdfModelEvent( URL url,
                                 Type type ) {
        this.type = Type.CHANGED;
        this.url = url;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @return the <code>URL</code> of the function model that was changed
     */
    public URL getUrl() {
        return this.url;
    }

    public boolean isChanged() {
        return (this.type == Type.CHANGED);
    }

    public boolean isDeleted() {
        return (this.type == Type.DELETED);
    }

    public boolean isNew() {
        return (this.type == Type.NEW);
    }
}

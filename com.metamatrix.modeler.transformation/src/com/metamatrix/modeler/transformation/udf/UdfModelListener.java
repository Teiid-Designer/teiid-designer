package com.metamatrix.modeler.transformation.udf;

public interface UdfModelListener {

    /**
     * @param event the event being processed (never <code>null</code>
     * @since 6.0.0
     */
    void processEvent(UdfModelEvent event);
}

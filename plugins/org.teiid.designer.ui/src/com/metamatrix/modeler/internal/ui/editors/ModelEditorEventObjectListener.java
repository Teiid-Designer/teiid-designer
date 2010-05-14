/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import java.util.EventObject;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;

/**
 * @since 4.2
 */
public class ModelEditorEventObjectListener implements EventObjectListener {

    ModelEditor editor;

    /**
     * @since 4.2
     */
    public ModelEditorEventObjectListener( ModelEditor modelEditor ) {
        super();
        this.editor = modelEditor;
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;
        // All the editor needs to do is take care of the object editor. If it's open, close it if the event
        // resource is same as editor's resource.
        if (event.getType() == ModelResourceEvent.RELOADED) {
            final ModelResource editorMR = editor.getModelResource();
            final ModelResource evResource = event.getModelResource();
            if (evResource != null && evResource.equals(editorMR)) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        editor.closeObjectEditor();
                    }
                });
            }
        }
    }
}

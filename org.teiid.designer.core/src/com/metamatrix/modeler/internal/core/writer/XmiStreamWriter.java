/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.writer.StreamWriter;

/**
 * @author dfuglsang To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class XmiStreamWriter implements StreamWriter {

    /**
     * Constructor for XmiStreamWriter.
     */
    public XmiStreamWriter() {
        super();
        // System.err.println("Created instance of the XmiStreamWriter");
    }

    /**
     * @see com.metamatrix.api.mtk.core.writer.MtkStreamWriter#write(java.io.OutputStream, java.util.Map, java.util.Collection)
     *      Warning: The objects collection input parameter will be added to a new Resource within the execution of this method,
     *      and thus will lose its relationship to any other resource that it is contained in.
     */
    public void write( OutputStream outputstream,
                       Map options,
                       Collection objects ) throws IOException {
        if (outputstream == null) {
            ArgCheck.isNotNull(outputstream,
                               ModelerCore.Util.getString("XmiStreamWriter.The_OutputStream_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        if (options == null) {
            ArgCheck.isNotNull(options, ModelerCore.Util.getString("XmiStreamWriter.The_Map_reference_may_not_be_null_2")); //$NON-NLS-1$
        }
        if (objects == null) {
            ArgCheck.isNotNull(objects, ModelerCore.Util.getString("XmiStreamWriter.The_Collection_reference_may_not_be_null_3")); //$NON-NLS-1$
        }
        Resource temp = new XMIResourceImpl();
        temp.getContents().addAll(objects);
        temp.save(outputstream, options);
    }

    public void write( OutputStream outputstream,
                       Map options,
                       Resource resource ) {
        throw new UnsupportedOperationException(ModelerCore.Util.getString("XmiStreamWriter.Can_not_perform_operation_4")); //$NON-NLS-1$
    }

}

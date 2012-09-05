/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import java.util.Iterator;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * PushdownFunctionUtil
 */
public class PushdownFunctionUtil {

    private PushdownFunctionUtil() {
        super();
    }

    public static String getSignature( final Procedure proc ) {
        final String name = proc.getName();
        final StringBuffer sb = new StringBuffer();
        sb.append(name);

        // Add the parameters ...
        sb.append('(');
        boolean isFirst = true;
        Iterator iter = proc.getParameters().iterator();
        while (iter.hasNext()) {
            final ProcedureParameter param = (ProcedureParameter)iter.next();
            DirectionKind direction = param.getDirection();
            int directionKind = direction.getValue();
            if (directionKind == DirectionKind.IN || directionKind == DirectionKind.INOUT) {
                if (!isFirst) {
                    sb.append(',');
                }
                final String paramSig = getSignature(param);
                sb.append(paramSig);
                isFirst = false;
            }
        }
        sb.append(')');

        // Add the return parameter ...
        iter = proc.getParameters().iterator();
        while (iter.hasNext()) {
            final ProcedureParameter param = (ProcedureParameter)iter.next();
            DirectionKind direction = param.getDirection();
            int directionKind = direction.getValue();
            if (directionKind == DirectionKind.RETURN) {
                sb.append(':');
                final String paramSig = getSignature(param);
                sb.append(paramSig);
            }
        }

        return sb.toString();
    }

    public static String getSignature( final ProcedureParameter param ) {
        EObject type = param.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param, true);
        final String typeName = dtMgr.getName(type);
        return typeName;
    }

}

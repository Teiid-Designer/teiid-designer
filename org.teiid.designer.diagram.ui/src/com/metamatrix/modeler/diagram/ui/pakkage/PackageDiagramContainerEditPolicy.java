/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.pakkage;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

/**
 * PackageDiagramContainerEditPolicy
 */
public class PackageDiagramContainerEditPolicy extends ContainerEditPolicy {

    @Override
    protected Command getCreateCommand(CreateRequest request) {
System.out.println(" -->> PackageDiagramContainerEditPolicy.getCreateCommand()]" ); //$NON-NLS-1$

        return null;
    }

    @Override
    public Command getOrphanChildrenCommand(GroupRequest request) {
System.out.println(" -->> PackageDiagramContainerEditPolicy.getOrphanChildrenCommand()]" ); //$NON-NLS-1$
        return null;
    }
}

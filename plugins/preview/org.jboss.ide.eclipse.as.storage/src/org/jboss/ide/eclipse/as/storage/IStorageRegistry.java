/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.ide.eclipse.as.storage;

import java.util.Collection;

/**
 * A registry that initialises and hold all instances of
 * {@link IStorageUnit} in the application
 */
public interface IStorageRegistry {

    /**
     * @return all registered storage units
     */
    Collection<IStorageUnit> getRegisteredUnits();
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.Map;

/**
 * GeneratorManagerOptions Interface
 */
public interface IGeneratorManagerOptions {

    public void setCustomPropsMap(Map customPropsMap);

    public boolean hasValidOptions();
  
    public String getOptionsStatusMessage();
  
}

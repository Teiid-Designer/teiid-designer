/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.text.Position;

/**
 * Execution Plan Element Object
 */
public class PlanElement {
    private final String SEPARATOR = "/"; //$NON-NLS-1$
    private List<PlanElement> elementChildren = new ArrayList<PlanElement>();
    private List attributeChildren = new ArrayList();

    private String name;
    private PlanElement parent;
    private Position position;
    private String value;
    private boolean isRoot = false;

    public PlanElement( String name ) {
        super();
        this.name = name;
    }

    public Object[] getChildElements() {
        return elementChildren.toArray(new Object[0]);
    }

    public PlanElement addChildElement( PlanElement element ) {
        elementChildren.add(element);
        element.setParent(this);
        return this;
    }

    public void setParent( PlanElement element ) {
        this.parent = element;
    }

    public PlanElement getParent() {
        return parent;
    }

    public PlanElement addChildAttribute( PlanAttribute attribute ) {
        attributeChildren.add(attribute);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getAttributeValue( String localName ) {
        for (Iterator iter = attributeChildren.iterator(); iter.hasNext();) {
            PlanAttribute attribute = (PlanAttribute)iter.next();
            if (attribute.getName().equals(localName)) return attribute.getValue();
        }
        return null;
    }

    public void clear() {
        elementChildren.clear();
        attributeChildren.clear();
    }

    public void setPosition( Position position ) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setRoot( boolean isRoot ) {
        this.isRoot = isRoot;
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public String getFullPath() {
        String path = SEPARATOR + this.getName();
        PlanElement thisElement = this;

        while (thisElement.getParent() != null) {
            path = SEPARATOR + thisElement.getParent().getName() + path;
            thisElement = thisElement.getParent();
        }

        return path;
    }
}

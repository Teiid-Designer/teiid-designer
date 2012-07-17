/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.choice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.mapping.choice.IChoiceObject;
import org.teiid.designer.metamodels.xml.ChoiceErrorMode;
import org.teiid.designer.metamodels.xml.ChoiceOption;
import org.teiid.designer.metamodels.xml.XmlAll;
import org.teiid.designer.metamodels.xml.XmlChoice;
import org.teiid.designer.metamodels.xml.XmlContainerNode;
import org.teiid.designer.metamodels.xml.XmlDocumentNode;
import org.teiid.designer.metamodels.xml.XmlFragment;
import org.teiid.designer.metamodels.xml.XmlSequence;


/**
 * XmlChoiceObject
 */
public class XmlChoiceObject implements IChoiceObject {

    // =======================
    //  instance Variables
    // =======================
    private XmlChoice xcChoice;
    private Map optionSqlCriteriaMap = new HashMap();

    /**
     * Construct an instance of XmlChoiceObject.
     * 
     */
    public XmlChoiceObject( XmlChoice xcChoice ) {
        super();
        this.xcChoice = xcChoice;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#getOrderedOptions()
     */
    @Override
	public List getOrderedOptions() {
        List contents = xcChoice.getOrderedChoiceOptions();

        return contents;
    }

    @Override
	public void setOrderedOptions( List lst ) {
        xcChoice.setOrderedChoiceOptions( lst );
    }


    /* (non-Javadoc)
     * swjTODO: Design problem: There should not be a getName down here.  The options should be Objects
     * that are rendered by an ILabelProvider up in the ui ChoicePanel.
     * 
     * @See org.teiid.designer.mapping.choice.IChoiceObject#getName()
     */
    @Override
	public String getName( Object option ) {
        String result = null;
        if ( option instanceof EObject ) {
            result = ModelerCore.getModelEditor().getName((EObject) option);
            if ( result == null ) {
                if ( option instanceof XmlSequence ) {
                    result = "sequence"; //$NON-NLS-1$  hack because we can't get a label provider down in this non-ui class
                } else if ( option instanceof XmlChoice ) {
                    result = "choice"; //$NON-NLS-1$
                } else if ( option instanceof XmlAll ) {
                    result = "all"; //$NON-NLS-1$
                }
            }
        }
        
        if ( result == null ) {
            result = option.toString();
        }
        
        return result;
    }

    /**
     * @see org.teiid.designer.mapping.choice.IChoiceObject#getCriteria(java.lang.Object)
     */
    @Override
	public String getCriteria(final Object option) {
        return ((ChoiceOption)option).getChoiceCriteria();
    }

    /**
     * @see org.teiid.designer.mapping.choice.IChoiceObject#setCriteria(java.lang.Object, java.lang.String)
     */
    @Override
	public void setCriteria(final Object option,
                            final String criteria) {
        ((ChoiceOption)option).setChoiceCriteria(criteria);
    }

    /** 
     * @see org.teiid.designer.mapping.choice.IChoiceObject#getSqlCriteria(java.lang.Object)
     * @since 4.3
     */
    @Override
	public String getSqlCriteria(final Object option) {
        return (String)this.optionSqlCriteriaMap.get(option);
    }
    
    /** 
     * @see org.teiid.designer.mapping.choice.IChoiceObject#setSqlCriteria(java.lang.Object, java.lang.String)
     * @since 4.3
     */
    @Override
	public void setSqlCriteria(final Object option,
                               final String criteria) {
        this.optionSqlCriteriaMap.put(option, criteria);
    }
    
    /* (non-Javadoc) 
     * @See org.teiid.designer.mapping.choice.IChoiceObject#isIncluded(java.lang.Object)
     */
    @Override
	public boolean isIncluded( Object option ) {
        if ( option instanceof XmlDocumentNode ) {
            return !((XmlDocumentNode)option).isExcludeFromDocument();        
        } 
        else
        if ( option instanceof XmlContainerNode ) {        
            return !((XmlContainerNode)option).isExcludeFromDocument();        
        }                 
        else {
            return true;
        }
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#setIncluded(java.lang.Object, boolean)
     */
    @Override
	public void setIncluded( Object option, boolean b ) {
        if ( option instanceof XmlDocumentNode ) {        
            ((XmlDocumentNode)option).setExcludeFromDocument( !b );
        } 
        if ( option instanceof XmlContainerNode ) {        
            ((XmlContainerNode)option).setExcludeFromDocument( !b );
        } 
    }


    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#move(int, Object)
     */    
    @Override
	public void move( int iNewPosition, Object object ) {
        /*
         * this method has been replaced by getOrderedOptions/setOrderedOptions.
         */
        
        /*
         * jh note: 
         *  xcChoice.getElements().move(,,,) did not work because the
         *          set of elements does not include any containers like
         *          sequence, choice or all.
         *  ModelerCore.getModelEditor().move(...) works, but not all the time.  Will submit a defect
         *          for metadata team.
         */
        
//        try {        
//            ModelerCore.getModelEditor().move( getChoice(), (EObject)object, iNewPosition );
//        } catch ( ModelerCoreException mce ) {
//            ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());                                        
//        }
        throw new UnsupportedOperationException();        
    }
        
    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#move(int, Object)
     */    
    @Override
	public void move( int iNewPosition, int iOldPosition ) {        
        /*
         * jh note:  Since we now must use 'ModelerCore.getModelEditor().move(...)',
         *           which takes an object and a destination index, this move is no 
         *           longer possible. 
         */
        throw new UnsupportedOperationException();        
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#getMinOccurs()
     */    
    @Override
	public int getMinOccurs() {
        return xcChoice.getMinOccurs();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#getDefaultErrorMode()
     */    
    @Override
	public String getDefaultErrorMode() {
        return xcChoice.getDefaultErrorMode().getName();  
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#setDefaultErrorMode()
     */    
    @Override
	public void setDefaultErrorMode( String value ) {                
        xcChoice.setDefaultErrorMode( ChoiceErrorMode.get( value ) );
    }

    @Override
	public String[] getValidErrorModeValues() {
        List lstValues = ChoiceErrorMode.VALUES;
                
        String[] saValues = new String[ lstValues.size() ];
        int iCounter = 0;
        Iterator it = lstValues.iterator();
        
        while ( it.hasNext() ) {
            ChoiceErrorMode cemTemp = (ChoiceErrorMode)it.next();
            saValues[ iCounter++ ] = cemTemp.getName();
        }
        
        return saValues;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#getDefaultOption()
     */    
    @Override
	public Object getDefaultOption() {
        return xcChoice.getDefaultOption();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.choice.IChoiceObject#setDefaultOption()
     */    
    @Override
	public void setDefaultOption( Object value ) {
        xcChoice.setDefaultOption( (ChoiceOption)value );
    }

    @Override
	public EObject getRoot() {
        // get the choice object's root by walking upward until we find a XmlFragment
        EObject eoRoot = null;
        int iCounter = 0;

        EObject eoTemp = xcChoice.getParent();
        
        while( true ) {
            
            if ( eoTemp instanceof XmlFragment ) {
                eoRoot = eoTemp;
                break;    
            }
            
            iCounter++;
            if ( iCounter > 100 ) {
//                System.out.println("[XmlChoiceObject.getRoot()] Quitting after 100 tries"); //$NON-NLS-1$
                break;
            }
            
            // get the next parent
            eoTemp = eoTemp.eContainer();            
        }
        
        return eoRoot;
    }

    @Override
	public EObject getChoice() {
        return xcChoice;
    }

    @Override
	public EObject getParent() {
        return xcChoice.getParent();
    }
}

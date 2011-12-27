/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.RelationalObjectBuilder;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;


/** 
 * Class to externalize the logic for building Virtual Relational from XSD
 */
public class GenerateVirtualFromXsdHelper {
    private static final String SPACER = "_";//$NON-NLS-1$
    private static final String NAME = "name";//$NON-NLS-1$
    private static final String ANY = "Any";//$NON-NLS-1$
    private static final String DEFAULT_SQL = "Select * FROM";//$NON-NLS-1$
        
    private final RelationalObjectBuilder builder;
    private final StringNameValidator nameValidator = new StringNameValidator();
    private final DatatypeManager dtMgr; 
    private final MultiStatus status;
    private final Resource resource;
    private final Collection types;
    private final Stack recursionStack = new Stack();
    private final Stack elementStack = new Stack();
    
    private String currentRootName = null;
    private IProgressMonitor monitor;
    
    public static boolean HEADLESS = false;//Flag to allow for JUnit testing
    
    public GenerateVirtualFromXsdHelper(final MultiStatus status, Resource resource, Collection types) {
        CoreArgCheck.isNotNull(status);
        CoreArgCheck.isNotNull(resource);
        this.status = status;
        this.resource = resource;
        this.builder = new RelationalObjectBuilder(resource);
        this.types = types;
        if(HEADLESS) {
            this.dtMgr = null;
        }else {
            this.dtMgr = ModelerCore.getBuiltInTypesManager();
        }
    }

    public void execute(IProgressMonitor monitor) {

        boolean requiredStart = ModelerCore.startTxn(false, false, getString("CreateVirtualModelFromSchemaWizard.undoTitle"), this); //$NON-NLS-1$
        try {
            if( resource != null && !types.isEmpty() ) {
                int nTables = 0;
                String sSize = Integer.toString(types.size());

                for(Iterator iter = types.iterator(); iter.hasNext(); ) {
                    nTables++;
                    final XSDConcreteComponent next = (XSDConcreteComponent)iter.next();
                    final String tableName = createTableFromContent(next);
                    if(!elementStack.isEmpty() ) {
                        elementStack.clear();
                    }
                    
                    if( monitor != null ) {
                        monitor.worked(1);
                        monitor.subTask( getString("CreateVirtualModelFromSchemaWizard.incrementalProgress", new Object[] {Integer.toString(nTables), sSize, tableName} )); //$NON-NLS-1$
                    }
                }
                
            }
        } catch (ModelerCoreException exc) {
            addStatus(IStatus.ERROR, exc.getMessage(), exc);
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(!monitor.isCanceled() ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }        
    }
    
    public void doBuild(final IProgressMonitor monitor) {
        this.monitor = monitor == null ? new NullProgressMonitor() : monitor;
        final String message = getString("CreateVirtualModelFromSchemaWizard.msg");         //$NON-NLS-1$
        try {
            this.monitor.beginTask(message, types.size()*10); 
            
            if (! this.monitor.isCanceled()) {
                execute(this.monitor);
            }            
            this.monitor.done();
        } catch (Exception e) {
            addStatus(IStatus.ERROR, getString("CreateVirtualModelFromSchemaWizard.createError"), e);//$NON-NLS-1$
        }
    } 
    
    //***************************************************************************************************
    //                                  PRIVATE METHODS
    //***************************************************************************************************
    private String getString(final String key, final Object[] args) {
        if(HEADLESS) {
            //If we are testing, just return the key
            return key;
        }
        
        return ModelerXsdUiConstants.Util.getString(key, args);
    }
    
    private String getString(final String key) {
        return getString(key, new Object[] {} );
    }
    
    private String createTableFromContent(XSDConcreteComponent content) throws ModelerCoreException{        
        final String tableDesc = XsdUtil.getDescription(content);
        String tableName = ModelerCore.getModelEditor().getName(content);
        while( tableName.indexOf('.') > -1) {
            tableName = tableName.replace('.', '_');
        }
        
        final String tmp = nameValidator.createValidUniqueName(tableName);
        if(tmp != null) {
            tableName = tmp;
        }
        
        final EObject vTable = builder.createBaseTable(tableName, resource, false, tableDesc);
        builder.createXPathNIS(vTable, content);
        
        Collection columnsList = new ArrayList();
        XSDComplexTypeDefinition ctd = content instanceof XSDComplexTypeDefinition ? (XSDComplexTypeDefinition)content : null;
        if(content instanceof XSDElementDeclaration) {
            elementStack.push(content);
            final XSDTypeDefinition type = ((XSDElementDeclaration)content).getTypeDefinition();
            if(type instanceof XSDComplexTypeDefinition) {
                ctd = (XSDComplexTypeDefinition)type;
            }else {
                processContent(type, resource, vTable, columnsList);
            }
        }
        
        XSDModelGroup group = XsdUtil.getCompositor(ctd);
        if(group != null ) {
            Iterator  particles = group.getParticles().iterator();
            while(particles != null && particles.hasNext() ) {
                XSDParticle nextPart = (XSDParticle)particles.next();
                processContent(nextPart.getContent(), resource, vTable, columnsList);
            }
        }else if(ctd != null && ctd.getContent() instanceof XSDTypeDefinition) {
            processContent(ctd.getContent(), resource, vTable, columnsList);
        }else if(ctd != null && ctd.getContent() instanceof XSDAttributeDeclaration) {
            processContent(ctd.getContent(), resource, vTable, columnsList);
        }else {
            final String contentType = (ctd == null || ctd.getContent() == null) ? "null" : ctd.getContent().getClass().getName(); //$NON-NLS-1$
            final String msg = getString("CreateVirtualModelFromSchemaWizard.invalidContent", new Object[] {contentType}); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
        }
        
        if( !columnsList.isEmpty() ) {
            builder.addColumns(vTable, columnsList);
        }
        
        builder.createTransformation(vTable, DEFAULT_SQL);
        
        return tableName;
        
    }
    
    private void processContent(final XSDConcreteComponent content, final Resource resource, final EObject vTable, final Collection columnList) throws ModelerCoreException{
        if(content instanceof XSDElementDeclaration) {            
            final XSDElementDeclaration element = resolveElement( (XSDElementDeclaration)content);
            final XSDElementDeclaration parentElement = elementStack.isEmpty() ? null : (XSDElementDeclaration)elementStack.peek();
            currentRootName = parentElement == null ? element.getName() : parentElement.getName() + SPACER + element.getName();
            elementStack.push(element);
            final XSDTypeDefinition typeDefn = element.getType();
            if(typeDefn instanceof XSDComplexTypeDefinition) {
                addColumnsForComplexType((XSDComplexTypeDefinition)typeDefn, resource, vTable, columnList);
            }else {
                processContent(typeDefn, resource, vTable, columnList);
            }
            
            if(!elementStack.isEmpty() ) {
                elementStack.pop();
            }
        }else if(content instanceof XSDComplexTypeDefinition) {
            final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)content;
            if(complexType.getName() != null) {
                currentRootName = complexType.getName();
            }
            addColumnsForComplexType(complexType, resource, vTable, columnList);
        }else if(content instanceof XSDSimpleTypeDefinition) {
            XSDSimpleTypeDefinition simpleTypeDefn = (XSDSimpleTypeDefinition)content;
                        
            createColumn(columnList, vTable, simpleTypeDefn);
        }else if(content instanceof XSDAttributeDeclaration) {
            final XSDAttributeDeclaration attr = resolveAttribute( (XSDAttributeDeclaration)content);
            XSDSimpleTypeDefinition simpleTypeDefn = attr.getTypeDefinition();
            final XSDElementDeclaration parentElement = elementStack.isEmpty() ? null : (XSDElementDeclaration)elementStack.peek();
            currentRootName = parentElement == null ? attr.getName() : parentElement.getName() + SPACER + attr.getName();            
            
            createColumn(columnList, vTable, simpleTypeDefn);
        }else if(content instanceof XSDAttributeUse){
            final XSDAttributeDeclaration attr = resolveAttribute( ((XSDAttributeUse)content).getAttributeDeclaration() );
            XSDSimpleTypeDefinition simpleTypeDefn = attr.getTypeDefinition();
            final XSDElementDeclaration parentElement = elementStack.isEmpty() ? null : (XSDElementDeclaration)elementStack.peek();
            currentRootName = parentElement == null ? attr.getName() : parentElement.getName() + SPACER + attr.getName();            
            
            createColumn(columnList, vTable, simpleTypeDefn);        
        }else if(content instanceof XSDAttributeGroupDefinition) {
            final XSDAttributeGroupDefinition attGroup = (XSDAttributeGroupDefinition)content;
            final Iterator atts = attGroup.getAttributeUses().iterator();
            while(atts.hasNext() ) {
                processContent( (XSDConcreteComponent)atts.next(), resource, vTable, columnList);
            }
        }else if(content instanceof XSDWildcard){
            final String tableName = getName(vTable);
            currentRootName = tableName == null ? ANY : tableName + SPACER + ANY;  
            final String tmp = nameValidator.createValidUniqueName(currentRootName);
            if(tmp != null) {
                currentRootName = tmp;
            }

            createColumn(columnList, vTable, null);
        }else {
            final String contentType = content == null ? "null" : content.getClass().getName(); //$NON-NLS-1$
            final String msg = getString("CreateVirtualModelFromSchemaWizard.unexpectedContent", new Object[] {contentType}); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
        }    
    }
    
    private String getName(final EObject eObj) {
        if(eObj == null) {
            return null;
        }
        
        final EStructuralFeature name = eObj.eClass().getEStructuralFeature(NAME);
        if(name != null) {
            return (String)eObj.eGet(name);
        }
        
        return null;
    }
    
    private void createColumn(final Collection columnList, final EObject vTable, XSDSimpleTypeDefinition simpleTypeDefn) throws ModelerCoreException{
        //Use the passed in type to capture property values, use the builtIn type for that type
        //to calculate name and to use as the dataType for the column
        boolean isAny = simpleTypeDefn == null;
        XSDSimpleTypeDefinition builtInType = simpleTypeDefn;
        
        if(HEADLESS) {
            //If testing, can't use DTMgr, so just climb to the first type below ANY.
            boolean done = false;
            while(!done && builtInType != null && builtInType != builtInType.getBaseTypeDefinition() ) {
                final XSDSimpleTypeDefinition tmp = builtInType.getBaseTypeDefinition();
                if(tmp == null || (tmp.getName() != null && tmp.getName().equals("anySimpleType") ) ) {  //$NON-NLS-1$
                    done = true;
                }else {
                    builtInType = builtInType.getBaseTypeDefinition();
                }
            }            
        }else {
            while(builtInType != null && !dtMgr.isEnterpriseDatatype(builtInType) && builtInType != builtInType.getBaseTypeDefinition() ) {
                builtInType = builtInType.getBaseTypeDefinition();
            }
        }
        
        if(isAny && !HEADLESS) {
            final String msg = getString("CreateVirtualModelFromSchemaWizard.wildcard", new Object[] {currentRootName}); //$NON-NLS-1$
            addStatus(IStatus.INFO, msg, null);
            builtInType = (XSDSimpleTypeDefinition)dtMgr.getAnySimpleType();
        }
        
        if(builtInType == null) {
            final String msg = getString("CreateVirtualModelFromSchemaWizard.noDt", new Object[] {currentRootName}); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
            return;
        }
        
        if(currentRootName == null) {
            currentRootName = builtInType.getName();
        }

        final String tmp = nameValidator.createValidUniqueName(currentRootName);
        if(tmp != null) {
            currentRootName = tmp;
        }
        
        
                          
        String columnDesc = XsdUtil.getDescription(simpleTypeDefn);
        final EObject nextCol = builder.createColumn(currentRootName, vTable, columnDesc, builtInType, simpleTypeDefn);
        if(nextCol != null) {
            builder.createColXPathNIS(nextCol, elementStack);
            columnList.add(nextCol);
        }        
    }
    
    private void addColumnsForComplexType(XSDComplexTypeDefinition complexTypeDefn, Resource resource, EObject vTable, Collection columnsList) throws ModelerCoreException {
        if(recursionStack.contains(complexTypeDefn) ) {
            return;
        }
        
        recursionStack.push(complexTypeDefn);
        
        XSDModelGroup group = XsdUtil.getCompositor(complexTypeDefn); 
        final XSDComplexTypeContent content = complexTypeDefn.getContent();
        if(group != null) {
            processModelGroup(group, resource, vTable, columnsList);
        }else if(content instanceof XSDComplexTypeDefinition) {
            addColumnsForComplexType( (XSDComplexTypeDefinition)content, resource, vTable, columnsList);
        }else if(content instanceof XSDSimpleTypeDefinition) {
            processContent(content, resource, vTable, columnsList);
        }else if(content instanceof XSDWildcard){
            processContent(content, resource, vTable, columnsList);
        }else if(content != null) {
            final String contentType = content.getClass().getName(); 
            final String msg = getString("CreateVirtualModelFromSchemaWizard.unexpectedContent", new Object[] {contentType}); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
        }
        
        final Iterator atts = complexTypeDefn.getAttributeContents().iterator();
        while(atts.hasNext() ) {
            final XSDConcreteComponent next = (XSDConcreteComponent)atts.next();
            processContent(next, resource, vTable, columnsList);
        }
        
        if(!recursionStack.isEmpty() ) {
            recursionStack.pop();
        }

    }
    
    private void processModelGroup(final XSDModelGroup group, final Resource resource, final EObject vTable, final Collection columnsList) throws ModelerCoreException {
        if(group == null) {
            return;
        }
        
        Iterator particles = group.getParticles().iterator();
        while( particles.hasNext() ) {
            XSDParticle nextPart = (XSDParticle)particles.next();
            XSDParticleContent partContent = nextPart.getContent();
            if( partContent instanceof XSDElementDeclaration ) {
                final XSDElementDeclaration elementContent = resolveElement( (XSDElementDeclaration)partContent );
                processContent(elementContent, resource, vTable, columnsList);
            }else if(partContent instanceof XSDAttributeDeclaration) {
                final XSDAttributeDeclaration attr = (XSDAttributeDeclaration)partContent;
                currentRootName = attr.getName();
                processContent(attr, resource, vTable, columnsList);                                       
            }else if(partContent instanceof XSDModelGroupDefinition) {
                XSDModelGroup childGroup = ((XSDModelGroupDefinition)partContent).getModelGroup();
                processModelGroup(childGroup, resource, vTable, columnsList);
            }else if(partContent instanceof XSDWildcard){
                processContent(partContent, resource, vTable, columnsList);
            }else {
                final String contentType = partContent == null ? "null" : partContent.getClass().getName(); //$NON-NLS-1$
                final String msg = getString("CreateVirtualModelFromSchemaWizard.unexpectedContent", new Object[] {contentType}); //$NON-NLS-1$
                addStatus(IStatus.ERROR, msg, null);
            }
        }        
    }
    
    private XSDElementDeclaration resolveElement(final XSDElementDeclaration element) {
        if(element == null || element.getResolvedElementDeclaration() == element) {
            return element;
        }
        
        return resolveElement(element.getResolvedElementDeclaration() );
    }
    
    private XSDAttributeDeclaration resolveAttribute(final XSDAttributeDeclaration attr) {
        if(attr == null || attr.getResolvedAttributeDeclaration() == attr) {
            return attr;
        }
        
        return resolveAttribute(attr.getResolvedAttributeDeclaration() );
    }    
    
    private void addStatus(final int severity, final String message, final Throwable ex) {
        final Status sts = new Status(severity, ModelerXsdUiConstants.PLUGIN_ID, 0, message, ex);
        status.add(sts);
    }        
}

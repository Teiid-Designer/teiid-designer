/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.aspects.validation.rules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.xerces.util.XMLChar;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.WebServiceMetamodelPlugin;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.webservice.util.WebServiceUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * @since 4.2
 */
public class WebServiceComponentNameRule extends StringNameRule {

    /**
     * @param invalidChars
     * @param featureID
     * @since 4.2
     */
    public WebServiceComponentNameRule( final char[] invalidChars, // NO_UCD
                                        final int featureID ) {
        super(invalidChars, featureID);
    }

    /**
     * @param featureID
     * @since 4.2
     */
    public WebServiceComponentNameRule( final int featureID ) {
        super(featureID);
    }

    /**
     * @see com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature,
     *      org.eclipse.emf.ecore.EObject, java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
    public void validate( final EStructuralFeature eStructuralFeature,
                          final EObject eObject,
                          final Object value,
                          final ValidationContext context ) {
        // check if the feature matches the given feature
        if (super.getFeatureID() != eStructuralFeature.getFeatureID()) {
            return;
        }
        // apply the default name validation checks
        super.validate(eStructuralFeature, eObject, value, context);
        // get results for the eObject
        final Collection results = context.getTargetResults(eObject);
        if (results != null && !results.isEmpty()) {
            return;
        }

        // validate the name
        final String name = (String)value;
        // check if this a valid xml name
        InvalidCharacter invalidCharInName = null;
        if (name != null) {
            // check if this a valid xml name
            invalidCharInName = isValidNCName(name);
        }
        final ValidationResult result = new ValidationResultImpl(eObject);
        if (invalidCharInName != null) {
            // create validation problem and addit to the results
            String msg = null;
            if (invalidCharInName.index == 0) {
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.NameHasInvalidFirstCharacter", invalidCharInName.character); //$NON-NLS-1$
            } else if (invalidCharInName.index == 1) {
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.NameHasInvalidSecondCharacter", invalidCharInName.character); //$NON-NLS-1$
            } else if (invalidCharInName.index == 2) {
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.NameHasInvalidThirdCharacter", invalidCharInName.character); //$NON-NLS-1$
            } else {
                final Object[] params = new Object[] {new Integer(invalidCharInName.index + 1),
                    new Character(invalidCharInName.character)};
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.NameHasInvalidNthCharacter", params); //$NON-NLS-1$
            }
            final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
            return;
        }

        // collect all the siblings whose names need to be checked aginst the name of the
        // current
        final List siblings = getSiblingsForUniquenessCheck(eObject, context);
        if (siblings.isEmpty()) {
            return;
        }

        // validate the uniqueness of the name among the siblings
        validateUniqueness(context, siblings, eStructuralFeature.getFeatureID());

        return;
    }

    class InvalidCharacter {
        public final char character;
        public final int index;

        public InvalidCharacter( final char character,
                                 final int index ) {
            this.character = character;
            this.index = index;
        }
    }

    /**
     * Check to see if a string is a valid NCName according to [4] from the XML Namespaces 1.0 Recommendation
     * 
     * @param name string to check
     * @return the first invalid character that was found, or null if all the characters were valid
     * @see XMLChar#isValidNCName(java.lang.String)
     */
    protected InvalidCharacter isValidNCName( final String ncName ) {
        if (ncName.length() == 0) {
            return null;
        }
        char ch = ncName.charAt(0);
        if (XMLChar.isNCNameStart(ch) == false) {
            return new InvalidCharacter(ch, 0);
        }
        for (int i = 1; i < ncName.length(); i++) {
            ch = ncName.charAt(i);
            if (XMLChar.isNCName(ch) == false) {
                return new InvalidCharacter(ch, i);
            }
        }
        return null;
    }

    /**
     * Uniqueness check is handled in this rule rather than use the existing framework.
     * 
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#validateUniqueness()
     * @since 4.2
     */
    @Override
    protected boolean validateUniqueness() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    protected List getSiblingsForUniquenessCheck( final EObject eObject,
                                                  final ValidationContext context ) {

        final String objType = eObject.eClass().getName();
        final String ruleName = getRuleName() + objType;

        if (eObject instanceof Interface) {
            // Find all Interface instances in all of the models in the validation context,
            // but only run this rule once for Interfaces ...
            Container container = context.getResourceContainer();
            boolean modelContainer = ModelerCore.isModelContainer(container);
            String containerInfo = (modelContainer) ? eObject.eResource().getURI().toString() : Interface.class.getName();

            if (!context.hasRunRule(containerInfo, ruleName)) {
                List resources = new ArrayList();

                if (modelContainer) {
                    resources.add(eObject.eResource());
                } else {
                    List containerResources = container.getResources();

                    for (int size = containerResources.size(), i = 0; i < size; ++i) {
                        Resource resource = (Resource)containerResources.get(i);

                        if (isWebServiceModel(resource)) {
                            resources.add(resource);
                        }
                    }
                }

                context.recordRuleRun(containerInfo, ruleName);

                // Find all of the interfaces in all the models in the context
                final List results = WebServiceUtil.findInterfaces(resources, ModelVisitorProcessor.DEPTH_ONE);
                return results;
            }

        } else if (eObject instanceof Operation) {
            // type of object this rule is being run on.
            // this rule is being run once per object type per parent
            if (!context.hasRunRule(eObject, ruleName)) {
                context.recordRuleRun(eObject, ruleName);

                // Operation names must be unique within an Interface (per WS-I).
                final Interface parent = ((Operation)eObject).getInterface();
                final List results = WebServiceUtil.findOperations(parent, ModelVisitorProcessor.DEPTH_INFINITE);
                return results;
            }

        } else if (eObject instanceof Input) {
            // type of object this rule is being run on.
            // this rule is being run once per object type per parent
            if (!context.hasRunRule(eObject, ruleName)) {
                context.recordRuleRun(eObject, ruleName);

                // output name should be unique among the messages
                // of the same operation
                Object parent = eObject.eContainer();
                return WebServiceUtil.findMessages(parent);
            }
        } else if (eObject instanceof Output) {
            // type of object this rule is being run on.
            // this rule is being run once per object type per parent
            if (!context.hasRunRule(eObject, ruleName)) {
                context.recordRuleRun(eObject, ruleName);

                // operation name should be unique among the operations
                // of the same operation
                Object parent = eObject.eContainer();
                return WebServiceUtil.findMessages(parent);
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Indicates if the specified <code>Resource</code> is a web service model.
     * 
     * @param theResource the resource being checked
     * @return <code>true</code>if a web service model; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean isWebServiceModel( Resource theResource ) {
        boolean result = false;
        String metamodelUri = null;

        // get the metamodel URI from the loaded resource
        if (theResource instanceof EmfResource) {
            ModelAnnotation annotation = ((EmfResource)theResource).getModelAnnotation();

            if (annotation != null) {
                metamodelUri = annotation.getPrimaryMetamodelUri();
            }
        } else if (theResource instanceof XSDResourceImpl) {
            return false;
        }

        // if necessary get metamodel URI by reading the header
        if (metamodelUri == null) {
            URI uri = theResource.getURI();

            if (uri != null) {
                File file = new File(uri.toFileString());

                if (file.exists()) {
                    final XMIHeader header = ModelUtil.getXmiHeader(file);

                    if (header != null) {
                        metamodelUri = header.getPrimaryMetamodelURI();
                    }
                }
            }
        }

        if (metamodelUri != null && WebServicePackage.eNS_URI.equals(metamodelUri)) result = true;

        return result;
    }

    /**
     * Recursively check the names of the siblings against each other names and create validation errors if we find siblings with
     * same case insensitive name.
     * 
     * @param context The validation context to which we add results
     * @param siblings The siblings EObjects
     * @param nameFeatureID the ID of the feature that represents the name feature
     */
    public void validateUniqueness( final ValidationContext context,
                                    final List siblings,
                                    final int nameFeatureID ) {
        final StringNameValidator validator = new StringNameValidator();
        Map objectCountMap = validator.getDuplicateNamesMap(siblings, nameFeatureID);

        // if there is at least one match, create a problem
        if (objectCountMap.size() > 0) {
            Iterator keyIter = objectCountMap.keySet().iterator();
            while (keyIter.hasNext()) {
                EObject eObject = (EObject)keyIter.next();
                final EStructuralFeature eFeature = eObject.eClass().getEStructuralFeature(nameFeatureID);
                final String name = (String)eObject.eGet(eFeature);
                final Integer count = (Integer)objectCountMap.get(eObject);
                final ValidationResult result = new ValidationResultImpl(eObject);
                final String message = getUniquenessMessage(context, eObject, name, count);
                if (message != null) {
                    final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, message);
                    result.addProblem(problem);
                    context.addResult(result);
                }
            }
        }
    }

    protected String getUniquenessMessage( final ValidationContext context,
                                           final EObject eObject,
                                           final String name,
                                           final Integer count ) {
        // Determine the number of models being validated in this context ...
        final int numModels = context.getResourcesToValidate().length;

        // create validation problem and addit to the results
        String msg = null;
        if (eObject instanceof Interface) {
            // interface name should be unique among the interfaces
            // of in the context of a given set of the same webservice metamodels
            if (numModels > 1) {
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.InterfaceNameMustBeUniqueInVdb", name, count); //$NON-NLS-1$
            } else {
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.InterfaceNameMustBeUniqueInModel", name, count); //$NON-NLS-1$
            }
        } else if (eObject instanceof Operation) {
            // operation name should be unique among the operations
            // of in the context of a given set of the same webservice metamodels
            if (numModels > 1) {
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.OperationNameMustBeUniqueInVdb", name, count); //$NON-NLS-1$
            } else {
                msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.OperationNameMustBeUniqueInModel", name, count); //$NON-NLS-1$
            }
        } else if (eObject instanceof Message) {
            // output name should be unique among the messages
            // of the same operation
            msg = WebServiceMetamodelPlugin.Util.getString("WebServiceComponentNameRule.InputAndOutputNamesMustBeUnique", name, count); //$NON-NLS-1$
        }
        return msg;
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.uml2.uml.UMLPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationalValidationAspectFactoryImpl
 */
public class Uml2ValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            /*
             * Entities that are considered 'named' are checked for sibling name conflicts:
             */
            case UMLPackage.CLASS:                 return new Uml2NamedElementAspect(entity);
            case UMLPackage.PROPERTY:              return new Uml2NamedElementAspect(entity);
            case UMLPackage.PACKAGE:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.MODEL:                 return new Uml2NamedElementAspect(entity);
            case UMLPackage.OPERATION:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.PARAMETER:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.ENUMERATION:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.ENUMERATION_LITERAL:   return new Uml2NamedElementAspect(entity);
            case UMLPackage.PRIMITIVE_TYPE:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.ASSOCIATION:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.ABSTRACTION:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.REALIZATION:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXTENSION:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXTENSION_END:         return new Uml2NamedElementAspect(entity);
            case UMLPackage.ASSOCIATION_CLASS:     return new Uml2NamedElementAspect(entity);
            case UMLPackage.GENERALIZATION_SET:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.OPAQUE_EXPRESSION:     return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXPRESSION:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.CONSTRAINT:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.LITERAL_BOOLEAN:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.LITERAL_STRING:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.LITERAL_NULL:          return new Uml2NamedElementAspect(entity);
            case UMLPackage.LITERAL_INTEGER:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.LITERAL_UNLIMITED_NATURAL: return new Uml2NamedElementAspect(entity);
            case UMLPackage.INSTANCE_SPECIFICATION:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.INSTANCE_VALUE:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.STEREOTYPE:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.PROFILE:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.USAGE:                 return new Uml2NamedElementAspect(entity);
            case UMLPackage.SUBSTITUTION:          return new Uml2NamedElementAspect(entity);
            case UMLPackage.INFORMATION_ITEM:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.INFORMATION_FLOW:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.ACTIVITY:              return new Uml2NamedElementAspect(entity);
            case UMLPackage.ARTIFACT:              return new Uml2NamedElementAspect(entity);
            case UMLPackage.MANIFESTATION:         return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERFACE:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERFACE_REALIZATION:
				return new Uml2NamedElementAspect(entity);
            case UMLPackage.CONTROL_FLOW:          return new Uml2NamedElementAspect(entity);
            case UMLPackage.OBJECT_FLOW:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.INITIAL_NODE:          return new Uml2NamedElementAspect(entity);
            case UMLPackage.ACTIVITY_FINAL_NODE:   return new Uml2NamedElementAspect(entity);
            case UMLPackage.DECISION_NODE:         return new Uml2NamedElementAspect(entity);
            case UMLPackage.MERGE_NODE:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXECUTABLE_NODE:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.OUTPUT_PIN:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.INPUT_PIN:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.ACTIVITY_PARAMETER_NODE:   return new Uml2NamedElementAspect(entity);
            case UMLPackage.VALUE_PIN:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.ACTOR:                 return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXTEND:                return new Uml2NamedElementAspect(entity);
            case UMLPackage.USE_CASE:              return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXTENSION_POINT:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.INCLUDE:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.CALL_EVENT:
				return new Uml2NamedElementAspect(entity);
			case UMLPackage.CHANGE_EVENT:
				return new Uml2NamedElementAspect(entity);
            case UMLPackage.RECEPTION:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.SIGNAL:                return new Uml2NamedElementAspect(entity);
            case UMLPackage.SIGNAL_EVENT:
				return new Uml2NamedElementAspect(entity);
			case UMLPackage.TIME_EVENT:
				return new Uml2NamedElementAspect(entity);
			case UMLPackage.ANY_RECEIVE_EVENT:
				return new Uml2NamedElementAspect(entity);
            case UMLPackage.CONNECTOR:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.VARIABLE:              return new Uml2NamedElementAspect(entity);
            case UMLPackage.STRUCTURED_ACTIVITY_NODE:  return new Uml2NamedElementAspect(entity);
            case UMLPackage.CONDITIONAL_NODE:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.LOOP_NODE:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERACTION:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.LIFELINE:              return new Uml2NamedElementAspect(entity);
            case UMLPackage.MESSAGE:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.GENERAL_ORDERING:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXECUTION_OCCURRENCE_SPECIFICATION:
				return new Uml2NamedElementAspect(entity);
			case UMLPackage.EXECUTION_SPECIFICATION:
				return new Uml2NamedElementAspect(entity);
            case UMLPackage.STATE_INVARIANT:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.CREATE_OBJECT_ACTION:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.DESTROY_OBJECT_ACTION:     return new Uml2NamedElementAspect(entity);
            case UMLPackage.TEST_IDENTITY_ACTION:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_SELF_ACTION:          return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_STRUCTURAL_FEATURE_ACTION:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.CLEAR_STRUCTURAL_FEATURE_ACTION:   return new Uml2NamedElementAspect(entity);
            case UMLPackage.REMOVE_STRUCTURAL_FEATURE_VALUE_ACTION:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.ADD_STRUCTURAL_FEATURE_VALUE_ACTION:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_LINK_ACTION:          return new Uml2NamedElementAspect(entity);
            case UMLPackage.CREATE_LINK_ACTION:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.DESTROY_LINK_ACTION:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.CLEAR_ASSOCIATION_ACTION:  return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_VARIABLE_ACTION:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.CLEAR_VARIABLE_ACTION:     return new Uml2NamedElementAspect(entity);
            case UMLPackage.ADD_VARIABLE_VALUE_ACTION:     return new Uml2NamedElementAspect(entity);
            case UMLPackage.REMOVE_VARIABLE_VALUE_ACTION:  return new Uml2NamedElementAspect(entity);
            case UMLPackage.SEND_SIGNAL_ACTION:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.BROADCAST_SIGNAL_ACTION:   return new Uml2NamedElementAspect(entity);
            case UMLPackage.SEND_OBJECT_ACTION:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.CALL_OPERATION_ACTION:     return new Uml2NamedElementAspect(entity);
            case UMLPackage.CALL_BEHAVIOR_ACTION:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.PORT:                      return new Uml2NamedElementAspect(entity);
            case UMLPackage.COLLABORATION_USE:
				return new Uml2NamedElementAspect(entity);
            case UMLPackage.COLLABORATION:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.STATE_MACHINE:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.REGION:                    return new Uml2NamedElementAspect(entity);
            case UMLPackage.PSEUDOSTATE:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.STATE:                     return new Uml2NamedElementAspect(entity);
            case UMLPackage.CONNECTION_POINT_REFERENCE:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.TRANSITION:                    return new Uml2NamedElementAspect(entity);
            case UMLPackage.FINAL_STATE:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.FORK_NODE:                 return new Uml2NamedElementAspect(entity);
            case UMLPackage.JOIN_NODE:                 return new Uml2NamedElementAspect(entity);
            case UMLPackage.FLOW_FINAL_NODE:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.CENTRAL_BUFFER_NODE:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.ACTIVITY_PARTITION:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.REDEFINABLE_TEMPLATE_SIGNATURE:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXPANSION_NODE:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXPANSION_REGION:          return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERACTION_USE:
				return new Uml2NamedElementAspect(entity);
            case UMLPackage.GATE:                      return new Uml2NamedElementAspect(entity);
            case UMLPackage.PART_DECOMPOSITION:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERACTION_OPERAND:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERACTION_CONSTRAINT:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.COMBINED_FRAGMENT:         return new Uml2NamedElementAspect(entity);
            case UMLPackage.CONTINUATION:              return new Uml2NamedElementAspect(entity);
            case UMLPackage.PROTOCOL_STATE_MACHINE:    return new Uml2NamedElementAspect(entity);
            case UMLPackage.PROTOCOL_TRANSITION:       return new Uml2NamedElementAspect(entity);
            case UMLPackage.DATA_STORE_NODE:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.PARAMETER_SET:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_EXTENT_ACTION:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.RECLASSIFY_OBJECT_ACTION:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_IS_CLASSIFIED_OBJECT_ACTION:      return new Uml2NamedElementAspect(entity);
            case UMLPackage.START_CLASSIFIER_BEHAVIOR_ACTION:
				return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_LINK_OBJECT_END_ACTION:   return new Uml2NamedElementAspect(entity);
            case UMLPackage.READ_LINK_OBJECT_END_QUALIFIER_ACTION: return new Uml2NamedElementAspect(entity);
            case UMLPackage.CREATE_LINK_OBJECT_ACTION:     return new Uml2NamedElementAspect(entity);
            case UMLPackage.ACCEPT_EVENT_ACTION:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.ACCEPT_CALL_ACTION:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.REPLY_ACTION:                  return new Uml2NamedElementAspect(entity);
            case UMLPackage.RAISE_EXCEPTION_ACTION:        return new Uml2NamedElementAspect(entity);
            case UMLPackage.TIME_EXPRESSION:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.DURATION:                      return new Uml2NamedElementAspect(entity);
            case UMLPackage.DURATION_INTERVAL:             return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERVAL:                      return new Uml2NamedElementAspect(entity);
            case UMLPackage.TIME_CONSTRAINT:               return new Uml2NamedElementAspect(entity);
            case UMLPackage.INTERVAL_CONSTRAINT:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.TIME_INTERVAL:                 return new Uml2NamedElementAspect(entity);
            case UMLPackage.DURATION_CONSTRAINT:           return new Uml2NamedElementAspect(entity);
            case UMLPackage.DEPLOYMENT:                    return new Uml2NamedElementAspect(entity);
            case UMLPackage.NODE:                          return new Uml2NamedElementAspect(entity);
            case UMLPackage.DEVICE:                        return new Uml2NamedElementAspect(entity);
            case UMLPackage.EXECUTION_ENVIRONMENT:         return new Uml2NamedElementAspect(entity);
            case UMLPackage.COMMUNICATION_PATH:            return new Uml2NamedElementAspect(entity);
            case UMLPackage.COMPONENT:                     return new Uml2NamedElementAspect(entity);
            case UMLPackage.DEPLOYMENT_SPECIFICATION:      return new Uml2NamedElementAspect(entity);
            default:
                return null;
            }
    }


}

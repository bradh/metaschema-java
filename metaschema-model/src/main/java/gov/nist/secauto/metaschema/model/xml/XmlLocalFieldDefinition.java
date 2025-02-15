/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.model.xml;

import gov.nist.secauto.metaschema.datatypes.DataTypes;
import gov.nist.secauto.metaschema.datatypes.markup.MarkupLine;
import gov.nist.secauto.metaschema.datatypes.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.Defaults;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IValueConstraintSupport;
import gov.nist.secauto.metaschema.model.common.instance.IFlagInstance;
import gov.nist.secauto.metaschema.model.common.instance.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.model.common.instance.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.model.definitions.AbstractInfoElementDefinition;
import gov.nist.secauto.metaschema.model.definitions.AssemblyDefinition;
import gov.nist.secauto.metaschema.model.definitions.FieldDefinition;
import gov.nist.secauto.metaschema.model.definitions.LocalInfoElementDefinition;
import gov.nist.secauto.metaschema.model.definitions.ModuleScopeEnum;
import gov.nist.secauto.metaschema.model.instances.AbstractFieldInstance;
import gov.nist.secauto.metaschema.model.instances.FlagInstance;
import gov.nist.secauto.metaschema.model.xml.XmlLocalFieldDefinition.InternalFieldDefinition;
import gov.nist.secauto.metaschema.model.xml.constraint.ValueConstraintSupport;
import gov.nist.secauto.metaschema.model.xmlbeans.xml.FlagDocument;
import gov.nist.secauto.metaschema.model.xmlbeans.xml.LocalFieldDefinitionType;
import gov.nist.secauto.metaschema.model.xmlbeans.xml.LocalFlagDefinitionType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmlLocalFieldDefinition
    extends AbstractFieldInstance<InternalFieldDefinition> {
  private final LocalFieldDefinitionType xmlField;
  private final InternalFieldDefinition fieldDefinition;
  private IValueConstraintSupport constraints;

  /**
   * Constructs a new Metaschema field definition from an XML representation bound to Java objects.
   * 
   * @param xmlField
   *          the XML representation bound to Java objects
   * @param parent
   *          the parent assembly definition
   */
  public XmlLocalFieldDefinition(LocalFieldDefinitionType xmlField, AssemblyDefinition parent) {
    super(parent);
    this.xmlField = xmlField;
    this.fieldDefinition = new InternalFieldDefinition();
  }

  /**
   * Get the underlying XML model.
   * 
   * @return the XML model
   */
  protected LocalFieldDefinitionType getXmlField() {
    return xmlField;
  }

  /**
   * Used to generate the instances for the constraints in a lazy fashion when the constraints are
   * first accessed.
   */
  protected synchronized void checkModelConstraints() {
    if (constraints == null) {
      if (getXmlField().isSetConstraint()) {
        constraints = new ValueConstraintSupport(getXmlField().getConstraint());
      } else {
        constraints = IValueConstraintSupport.NULL_CONSTRAINT;
      }
    }
  }

  @Override
  public InternalFieldDefinition getDefinition() {
    return fieldDefinition;
  }

  @Override
  public boolean isInXmlWrapped() {
    boolean retval;
    if (DataTypes.MARKUP_MULTILINE.equals(getDefinition().getDatatype())) {
      // default value
      retval = Defaults.DEFAULT_FIELD_IN_XML_WRAPPED;
      if (getXmlField().isSetInXml()) {
        retval = getXmlField().getInXml();
      }
    } else {
      // All other data types get "wrapped"
      retval = true;
    }
    return retval;
  }

  @Override
  public String getName() {
    return getXmlField().getName();
  }

  @Override
  public String getUseName() {
    return getXmlField().isSetUseName() ? getXmlField().getUseName() : getDefinition().getUseName();
  }

  @Override
  public String getXmlNamespace() {
    return getContainingDefinition().getXmlNamespace();
  }

  @Override
  public String getGroupAsName() {
    return getXmlField().isSetGroupAs() ? getXmlField().getGroupAs().getName() : null;
  }

  @Override
  public String getGroupAsXmlNamespace() {
    return getContainingDefinition().getXmlNamespace();
  }

  @Override
  public int getMinOccurs() {
    int retval = Defaults.DEFAULT_GROUP_AS_MIN_OCCURS;
    if (getXmlField().isSetMinOccurs()) {
      retval = getXmlField().getMinOccurs().intValueExact();
    }
    return retval;
  }

  @Override
  public int getMaxOccurs() {
    int retval = Defaults.DEFAULT_GROUP_AS_MAX_OCCURS;
    if (getXmlField().isSetMaxOccurs()) {
      Object value = getXmlField().getMaxOccurs();
      if (value instanceof String) {
        // unbounded
        retval = -1;
      } else if (value instanceof BigInteger) {
        retval = ((BigInteger) value).intValueExact();
      }
    }
    return retval;
  }

  @Override
  public JsonGroupAsBehavior getJsonGroupAsBehavior() {
    JsonGroupAsBehavior retval = Defaults.DEFAULT_JSON_GROUP_AS_BEHAVIOR;
    if (getXmlField().isSetGroupAs() && getXmlField().getGroupAs().isSetInJson()) {
      retval = getXmlField().getGroupAs().getInJson();
    }
    return retval;
  }

  @Override
  public XmlGroupAsBehavior getXmlGroupAsBehavior() {
    XmlGroupAsBehavior retval = Defaults.DEFAULT_XML_GROUP_AS_BEHAVIOR;
    if (getXmlField().isSetGroupAs() && getXmlField().getGroupAs().isSetInXml()) {
      retval = getXmlField().getGroupAs().getInXml();
    }
    return retval;
  }

  @Override
  public MarkupMultiline getRemarks() {
    return getXmlField().isSetRemarks() ? MarkupStringConverter.toMarkupString(getXmlField().getRemarks()) : null;
  }

  public class InternalFieldDefinition
      extends AbstractInfoElementDefinition
      implements FieldDefinition, LocalInfoElementDefinition<XmlLocalFieldDefinition> {
    private final Map<String, FlagInstance<?>> flagInstances;

    /**
     * Create the corresponding definition for the local flag instance.
     */
    public InternalFieldDefinition() {
      super(XmlLocalFieldDefinition.this.getContainingDefinition().getContainingMetaschema());

      // handle flags
      if (getXmlField().getFlagList().size() > 0 || getXmlField().getDefineFlagList().size() > 0) {
        XmlCursor cursor = getXmlField().newCursor();
        cursor.selectPath(
            "declare namespace m='http://csrc.nist.gov/ns/oscal/metaschema/1.0';" + "$this/m:flag|$this/m:define-flag");

        Map<String, FlagInstance<?>> flagInstances = new LinkedHashMap<>();
        while (cursor.toNextSelection()) {
          XmlObject obj = cursor.getObject();
          if (obj instanceof FlagDocument.Flag) {
            FlagInstance<?> flagInstance = new XmlFlagInstance((FlagDocument.Flag) obj, this);
            flagInstances.put(flagInstance.getEffectiveName(), flagInstance);
          } else if (obj instanceof LocalFlagDefinitionType) {
            FlagInstance<?> flagInstance = new XmlLocalFlagDefinition((LocalFlagDefinitionType) obj, this);
            flagInstances.put(flagInstance.getEffectiveName(), flagInstance);
          }
        }
        this.flagInstances = Collections.unmodifiableMap(flagInstances);
      } else {
        this.flagInstances = Collections.emptyMap();
      }

    }

    @Override
    public String getFormalName() {
      return getXmlField().getFormalName();
    }

    @Override
    public MarkupLine getDescription() {
      return MarkupStringConverter.toMarkupString(getXmlField().getDescription());
    }

    @Override
    public ModuleScopeEnum getModuleScope() {
      return ModuleScopeEnum.LOCAL;
    }

    @Override
    public String getName() {
      return XmlLocalFieldDefinition.this.getName();
    }

    @Override
    public String getUseName() {
      return getName();
    }

    @Override
    public String getXmlNamespace() {
      return XmlLocalFieldDefinition.this.getXmlNamespace();
    }

    @Override
    public DataTypes getDatatype() {
      DataTypes retval;
      if (getXmlField().isSetAsType()) {
        retval = getXmlField().getAsType();
      } else {
        // the default
        retval = DataTypes.DEFAULT_DATA_TYPE;
      }
      return retval;
    }

    @Override
    public boolean hasJsonValueKeyFlagInstance() {
      return getXmlField().isSetJsonValueKey() && getXmlField().getJsonValueKey().isSetFlagName();
    }

    @Override
    public IFlagInstance getJsonValueKeyFlagInstance() {
      IFlagInstance retval = null;
      if (getXmlField().isSetJsonValueKey() && getXmlField().getJsonValueKey().isSetFlagName()) {
        retval = getFlagInstanceByName(getXmlField().getJsonValueKey().getFlagName());
      }
      return retval;
    }

    @Override
    public String getJsonValueKeyName() {
      String retval = null;

      if (getXmlField().isSetJsonValueKey()) {
        retval = getXmlField().getJsonValueKey().getStringValue();
      }

      if (retval == null || retval.isEmpty()) {
        retval = getDatatype().getJavaTypeAdapter().getDefaultJsonValueKey();
      }
      return retval;
    }

    @Override
    public boolean isCollapsible() {
      return getXmlField().isSetCollapsible() ? getXmlField().getCollapsible() : Defaults.DEFAULT_FIELD_COLLAPSIBLE;
    }

    @Override
    public XmlLocalFieldDefinition getDefiningInstance() {
      return XmlLocalFieldDefinition.this;
    }

    @Override
    public Map<String, FlagInstance<?>> getFlagInstances() {
      return flagInstances;
    }

    @Override
    public FlagInstance<?> getFlagInstanceByName(String name) {
      return getFlagInstances().get(name);
    }

    @Override
    public boolean hasJsonKey() {
      return getXmlField().isSetJsonKey();
    }

    @Override
    public FlagInstance<?> getJsonKeyFlagInstance() {
      FlagInstance<?> retval = null;
      if (hasJsonKey()) {
        retval = getFlagInstanceByName(getXmlField().getJsonKey().getFlagName());
      }
      return retval;
    }

    @Override
    public List<? extends IConstraint> getConstraints() {
      checkModelConstraints();
      return constraints.getConstraints();
    }

    @Override
    public List<? extends IAllowedValuesConstraint> getAllowedValuesContraints() {
      checkModelConstraints();
      return constraints.getAllowedValuesContraints();
    }

    @Override
    public List<? extends IMatchesConstraint> getMatchesConstraints() {
      checkModelConstraints();
      return constraints.getMatchesConstraints();
    }

    @Override
    public List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
      checkModelConstraints();
      return constraints.getIndexHasKeyConstraints();
    }

    @Override
    public List<? extends IExpectConstraint> getExpectConstraints() {
      checkModelConstraints();
      return constraints.getExpectConstraints();
    }

    @Override
    public MarkupMultiline getRemarks() {
      return XmlLocalFieldDefinition.this.getRemarks();
    }
  }
}

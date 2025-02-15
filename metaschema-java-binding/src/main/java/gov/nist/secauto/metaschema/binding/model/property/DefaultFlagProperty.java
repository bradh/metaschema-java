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

package gov.nist.secauto.metaschema.binding.model.property;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.binding.BindingContext;
import gov.nist.secauto.metaschema.binding.io.BindingException;
import gov.nist.secauto.metaschema.binding.io.context.ParsingContext;
import gov.nist.secauto.metaschema.binding.io.context.PathBuilder;
import gov.nist.secauto.metaschema.binding.io.json.JsonParsingContext;
import gov.nist.secauto.metaschema.binding.io.json.JsonWritingContext;
import gov.nist.secauto.metaschema.binding.io.xml.XmlParsingContext;
import gov.nist.secauto.metaschema.binding.io.xml.XmlWritingContext;
import gov.nist.secauto.metaschema.binding.model.ClassBinding;
import gov.nist.secauto.metaschema.binding.model.ModelUtil;
import gov.nist.secauto.metaschema.binding.model.annotations.Flag;
import gov.nist.secauto.metaschema.binding.model.constraint.ValueConstraintSupport;
import gov.nist.secauto.metaschema.binding.model.property.info.PropertyCollector;
import gov.nist.secauto.metaschema.binding.model.property.info.SingletonPropertyCollector;
import gov.nist.secauto.metaschema.datatypes.DataTypes;
import gov.nist.secauto.metaschema.datatypes.adapter.JavaTypeAdapter;
import gov.nist.secauto.metaschema.datatypes.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IValueConstraintSupport;
import gov.nist.secauto.metaschema.model.common.definition.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.IInstanceSet;

import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public class DefaultFlagProperty
    extends AbstractNamedProperty<ClassBinding>
    implements FlagProperty {
  // private static final Logger logger = LogManager.getLogger(DefaultFlagProperty.class);

  private final Flag flag;
  private final JavaTypeAdapter<?> javaTypeAdapter;
  private InternalFlagDefinition definition;
  private IValueConstraintSupport constraints;

  public DefaultFlagProperty(ClassBinding parentClassBinding, Field field, BindingContext bindingContext) {
    super(field, parentClassBinding);
    this.flag = field.getAnnotation(Flag.class);
    this.javaTypeAdapter = bindingContext.getJavaTypeAdapterInstance(this.flag.typeAdapter());
  }

  public Flag getFlagAnnotation() {
    return flag;
  }

  // @Override
  // public boolean isJsonKey() {
  // return getField().isAnnotationPresent(JsonKey.class);
  // }

  // @Override
  // public boolean isJsonValueKey() {
  // return getField().isAnnotationPresent(JsonFieldValueKeyFlag.class);
  // }

  @Override
  public boolean isRequired() {
    return getFlagAnnotation().required();
  }

  public JavaTypeAdapter<?> getJavaTypeAdapter() {
    return javaTypeAdapter;
  }

  @Override
  public String getUseName() {
    return ModelUtil.resolveLocalName(getFlagAnnotation().useName(), getJavaPropertyName());
  }

  @Override
  public String getXmlNamespace() {
    return ModelUtil.resolveNamespace(getFlagAnnotation().namespace(), getParentClassBinding(), true);
  }

  /**
   * Used to generate the instances for the constraints in a lazy fashion when the constraints are
   * first accessed.
   */
  protected synchronized void checkModelConstraints() {
    if (constraints == null) {
      constraints = new ValueConstraintSupport(this);
    }
  }

  @Override
  public Object read(XmlParsingContext context) throws IOException, BindingException, XMLStreamException {
    throw new UnsupportedOperationException("use read(Object, StartElement, XmlParsingContext) instead");
  }

  @Override
  public boolean read(Object parentInstance, StartElement parent, XmlParsingContext context) throws IOException {
    PathBuilder pathBuilder = context.getPathBuilder();
    pathBuilder.pushInstance(this);

    // when reading an attribute:
    // - "parent" will contain the attributes to read
    // - the event reader "peek" will be on the end element or the next start element
    boolean handled = false;
    if (parent != null) {
      Attribute attribute = parent.getAttributeByName(getXmlQName());
      if (attribute != null) {
        // get the attribute value
        Object value = getJavaTypeAdapter().parse(attribute.getValue());
        // apply the value to the parentObject
        setValue(parentInstance, value);

        pathBuilder.pushItem();

        // validate the flag value
        if (context.isValidating()) {
          validateValue(value, context);
        }
        pathBuilder.popItem();
        handled = true;
      }
    }
    pathBuilder.popInstance();
    return handled;
  }

  @Override
  public PropertyCollector newPropertyCollector() {
    return new SingletonPropertyCollector();
  }

  @Override
  protected Object readInternal(Object parentInstance, JsonParsingContext context) throws IOException {

    PathBuilder pathBuilder = context.getPathBuilder();
    pathBuilder.pushInstance(this);

    JsonParser parser = context.getReader();
    // advance past the property name
    parser.nextFieldName();
    // parse the value
    Object retval = readValueAndSupply(context).get();

    pathBuilder.pushItem();

    // validate the flag value
    if (context.isValidating()) {
      validateValue(retval, context);
    }

    pathBuilder.popItem();
    pathBuilder.popInstance();
    return retval;
  }

  // TODO: implement collector?
  @Override
  public Object readValueFromString(String value) throws IOException {
    return getJavaTypeAdapter().parse(value);
  }

  @Override
  public Supplier<?> readValueAndSupply(String value) throws IOException {
    return getJavaTypeAdapter().parseAndSupply(value);
  }

  @Override
  public Supplier<?> readValueAndSupply(JsonParsingContext context) throws IOException {
    return getJavaTypeAdapter().parseAndSupply(context.getReader());
  }

  @Override
  public boolean write(Object instance, QName parentName, XmlWritingContext context)
      throws XMLStreamException, IOException {
    QName name = getXmlQName();
    String value;

    Object objectValue = getValue(instance);
    if (objectValue != null) {
      value = objectValue.toString();
    } else {
      value = null;
    }

    if (value != null) {
      XMLStreamWriter2 writer = context.getWriter();
      if (name.getNamespaceURI().isEmpty()) {
        writer.writeAttribute(name.getLocalPart(), value);
      } else {
        writer.writeAttribute(name.getNamespaceURI(), name.getLocalPart(), value);
      }
    }
    return true;
  }

  @Override
  public void write(Object instance, JsonWritingContext context) throws IOException {
    JsonGenerator writer = context.getWriter();

    Object value = getValue(instance);
    if (value != null) {
      // write the field name
      writer.writeFieldName(getJsonName());

      // write the value
      writeValue(value, context);
    }
  }

  @Override
  public String getValueAsString(Object value) throws IOException {
    return getJavaTypeAdapter().asString(getValue(value));
  }

  @Override
  public void writeValue(Object value, JsonWritingContext context) throws IOException {
    getJavaTypeAdapter().writeJsonValue(value, context.getWriter());
  }

  @Override
  public ClassBinding getContainingDefinition() {
    return getParentClassBinding();
  }

  @Override
  public String toCoordinates() {
    return String.format("%s Instance(%s): %s:%s", getModelType().name().toLowerCase(), getName(),
        getParentClassBinding().getBoundClass().getName(), getField().getName());
  }

  @Override
  public MarkupMultiline getRemarks() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IFlagDefinition getDefinition() {
    synchronized (this) {
      if (definition == null) {
        definition = new InternalFlagDefinition();
      }
    }
    return definition;
  }

  private class InternalFlagDefinition implements IFlagDefinition {
    @Override
    public DataTypes getDatatype() {
      return DataTypes.getDataTypeForAdapter(getJavaTypeAdapter());
    }

    @Override
    public String getName() {
      return DefaultFlagProperty.this.getName();
    }

    @Override
    public String getUseName() {
      return null;
    }

    @Override
    public String getXmlNamespace() {
      return DefaultFlagProperty.this.getXmlNamespace();
    }

    @Override
    public MarkupMultiline getRemarks() {
      return DefaultFlagProperty.this.getRemarks();
    }

    @Override
    public String toCoordinates() {
      return DefaultFlagProperty.this.toCoordinates();
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
  }

  @Override
  public void validateValue(Object value, ParsingContext<?, ?> context) {
    // TODO Auto-generated method stub

  }

  @Override
  public IInstanceSet evaluateMetapathInstances(MetapathExpression target) {
    // TODO Auto-generated method stub
    return null;
  }
}

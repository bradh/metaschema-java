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

package gov.nist.secauto.metaschema.binding.model.property.info;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import gov.nist.secauto.metaschema.binding.io.BindingException;
import gov.nist.secauto.metaschema.binding.io.context.PathBuilder;
import gov.nist.secauto.metaschema.binding.io.json.JsonParsingContext;
import gov.nist.secauto.metaschema.binding.io.json.JsonUtil;
import gov.nist.secauto.metaschema.binding.io.json.JsonWritingContext;
import gov.nist.secauto.metaschema.binding.io.xml.XmlParsingContext;
import gov.nist.secauto.metaschema.binding.io.xml.XmlWritingContext;
import gov.nist.secauto.metaschema.binding.model.property.NamedModelProperty;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class SingletonPropertyInfo
    extends AbstractModelPropertyInfo<Type>
    implements ModelPropertyInfo {

  public SingletonPropertyInfo(NamedModelProperty property) {
    super(property);
  }

  @Override
  public void readValue(PropertyCollector collector, Object parentInstance, JsonParsingContext context)
      throws IOException, BindingException {
    NamedModelProperty property = getProperty();

    JsonParser parser = context.getReader();

    boolean isObject = JsonToken.START_OBJECT.equals(parser.currentToken());
    if (isObject) {
      // read the object's START_OBJECT
      JsonUtil.assertAndAdvance(parser, JsonToken.START_OBJECT);
    }
    PathBuilder pathBuilder = context.getPathBuilder();
    pathBuilder.pushItem();

    List<Object> values = property.readItem(parentInstance, context);
    collector.addAll(values);

    for (Object value : values) {

      if (context.isValidating()) {
        getProperty().validateItem(value, context);
      }
    }
    pathBuilder.popItem();

    if (isObject) {
      // read the object's END_OBJECT
      JsonUtil.assertAndAdvance(context.getReader(), JsonToken.END_OBJECT);
    }
  }

  @Override
  public boolean readValue(PropertyCollector collector, Object parentInstance, StartElement start,
      XmlParsingContext context)
      throws IOException, BindingException, XMLStreamException {

    PathBuilder pathBuilder = context.getPathBuilder();
    pathBuilder.pushItem();

    boolean handled = true;
    Object value = getProperty().readItem(parentInstance, start, context);
    if (value != null) {
      collector.add(value);
      handled = true;

      if (context.isValidating()) {
        getProperty().validateItem(value, context);
      }
    }
    pathBuilder.popItem();
    return handled;
  }

  @Override
  public Class<?> getItemType() {
    return (Class<?>) getType();
  }

  @Override
  public PropertyCollector newPropertyCollector() {
    return new SingletonPropertyCollector();
  }

  @Override
  public boolean writeValue(Object parentInstance, QName parentName, XmlWritingContext context)
      throws XMLStreamException, IOException {
    NamedModelProperty property = getProperty();
    return property.writeItem(property.getValue(parentInstance), parentName, context);
  }

  @Override
  public void writeValue(Object parentInstance, JsonWritingContext context) throws IOException {
    NamedModelProperty property = getProperty();
    getProperty().getDataTypeHandler().writeItems(Collections.singleton(property.getValue(parentInstance)), true,
        context);
  }

  @Override
  public boolean isValueSet(Object parentInstance) throws IOException {
    return getProperty().getValue(parentInstance) != null;
  }

}

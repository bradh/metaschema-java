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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import gov.nist.secauto.metaschema.binding.io.BindingException;
import gov.nist.secauto.metaschema.binding.io.json.JsonParsingContext;
import gov.nist.secauto.metaschema.binding.io.json.JsonUtil;
import gov.nist.secauto.metaschema.binding.model.ClassBinding;
import gov.nist.secauto.metaschema.model.common.instance.INamedInstance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;

public abstract class AbstractNamedProperty<CLASS_BINDING extends ClassBinding>
    extends AbstractProperty<CLASS_BINDING>
    implements NamedProperty, INamedInstance {
  private static final Logger logger = LogManager.getLogger(AbstractNamedProperty.class);

  public AbstractNamedProperty(Field field, CLASS_BINDING parentClassBinding) {
    super(field, parentClassBinding);
  }

  @Override
  public String getName() {
    return getJavaPropertyName();
  }

  public boolean isNextProperty(JsonParsingContext context) throws IOException {
    JsonParser parser = context.getReader();

    // the parser's current token should be the JSON field name
    JsonUtil.assertCurrent(parser, JsonToken.FIELD_NAME);

    String propertyName = parser.currentName();
    if (logger.isTraceEnabled()) {
      logger.trace("reading property {}", propertyName);
    }

    return getJsonName().equals(propertyName);
  }

  @Override
  public Object read(JsonParsingContext context) throws IOException, BindingException {
    Object retval = null;
    if (isNextProperty(context)) {
      retval = readInternal(null, context);
    }
    return retval;
  }

  @Override
  public boolean read(Object parentInstance, JsonParsingContext context) throws IOException, BindingException {
    boolean handled = isNextProperty(context);
    if (handled) {
      Object value = readInternal(parentInstance, context);
      setValue(parentInstance, value);
    }
    return handled;
  }

  protected abstract Object readInternal(Object parentInstance, JsonParsingContext context)
      throws IOException, BindingException;
  //
  // protected Object readInternal(Object parentInstance, JsonParsingContext context)
  // throws IOException, BindingException {
  // JsonParser parser = context.getReader();
  // // advance past the property name
  // parser.nextFieldName();
  // // parse the value
  // PropertyCollector collector = newPropertyCollector();
  // ModelPropertyInfo info = getPropertyInfo();
  // info.readValue(collector, parentInstance, context);
  // readValue(collector, parentInstance, context);
  //
  // Object retval = collector.getValue();
  //
  // // validate the flag value
  // if (context.isValidating()) {
  // validateValue(retval, context);
  // }
  // return retval;
  // }
}

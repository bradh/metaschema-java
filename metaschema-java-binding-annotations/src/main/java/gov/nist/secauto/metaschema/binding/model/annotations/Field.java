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

package gov.nist.secauto.metaschema.binding.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.binding.model.annotations.constraint.AllowedValues;
import gov.nist.secauto.metaschema.binding.model.annotations.constraint.Expect;
import gov.nist.secauto.metaschema.binding.model.annotations.constraint.IndexHasKey;
import gov.nist.secauto.metaschema.binding.model.annotations.constraint.Matches;
import gov.nist.secauto.metaschema.datatypes.adapter.JavaTypeAdapter;
import gov.nist.secauto.metaschema.model.common.Defaults;
import gov.nist.secauto.metaschema.model.common.instance.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.model.common.instance.XmlGroupAsBehavior;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface Field {
  /**
   * The model name to use for singleton values. This name will be used for associated XML elements.
   * <p>
   * If the value is "##default", then element name is derived from the JavaBean property name.
   * 
   * @return the name
   */
  String useName() default "##default";

  /**
   */
  /**
   * The namespace to use for associated XML elements.
   * <p>
   * If the value is "##default", then element name is derived from the namespace provided in the
   * package-info.
   * 
   * @return the namespace
   */
  String namespace() default "##default";

  /**
   * If the data type allows it, determines if the field's value must be wrapped with an element
   * having the specified {@link #useName()} and {@link #namespace()}.
   * 
   * @return {@code true} if the field must be wrapped, or {@code false} otherwise
   */
  boolean inXmlWrapped() default Defaults.DEFAULT_FIELD_IN_XML_WRAPPED;

  /**
   * The Metaschema data type adapter for the field's value.
   * 
   * @return the data type adapter
   */
  Class<? extends JavaTypeAdapter<?>> typeAdapter() default NullJavaTypeAdapter.class;

  /**
   * The name of the JSON property that contains the field's value. If this value is provided, the
   * name will be used as the property name. Use of this annotation is mutually exclusive with the
   * {@link JsonFieldValueKeyFlag} annotation.
   * 
   * @return the name
   */
  String valueName() default "##none";

  /**
   * The name to use for an XML element wrapper or a JSON/YAML property.
   * 
   * @return the name
   */
  String groupName() default "##none";

  /**
   * XML target namespace of the XML Schema element.
   * <p>
   * If the value is "##default", then element name is derived from the namespace provided in the
   * package-info.
   * 
   * @return the namespace
   */
  String groupNamespace() default "##default";

  /**
   * A non-negative number that indicates the minimum occurrence of the element.
   * 
   * @return a non-negative number
   */
  int minOccurs() default Defaults.DEFAULT_GROUP_AS_MIN_OCCURS;

  /**
   * A number that indicates the maximum occurrence of the element.
   * 
   * @return a positive number or {@code -1} to indicate "unbounded"
   */
  int maxOccurs() default Defaults.DEFAULT_GROUP_AS_MAX_OCCURS;

  /**
   * Describes how to handle collections in JSON/YAML.
   * 
   * @return the JSON collection strategy
   */
  JsonGroupAsBehavior inJson() default JsonGroupAsBehavior.NONE;

  /**
   * Describes how to handle collections in XML.
   * 
   * @return the XML collection strategy
   */
  XmlGroupAsBehavior inXml() default XmlGroupAsBehavior.UNGROUPED;

  /**
   * Get the allowed value constraints for this field.
   * 
   * @return the allowed values or an empty array if no allowed values constraints are defined
   */
  AllowedValues[] allowedValues() default {};

  /**
   * Get the matches constraints for this field.
   * 
   * @return the allowed values or an empty array if no allowed values constraints are defined
   */
  Matches[] matches() default {};

  /**
   * Get the index-has-key constraints for this field.
   * 
   * @return the allowed values or an empty array if no allowed values constraints are defined
   */
  IndexHasKey[] indexHasKey() default {};

  /**
   * Get the expect constraints for this field.
   * 
   * @return the expected constraints or an empty array if no expected constraints are defined
   */
  Expect[] expect() default {};
}

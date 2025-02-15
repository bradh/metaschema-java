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

package gov.nist.secauto.metaschema.codegen.property;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import gov.nist.secauto.metaschema.binding.model.annotations.Flag;
import gov.nist.secauto.metaschema.binding.model.annotations.JsonFieldValueKeyFlag;
import gov.nist.secauto.metaschema.binding.model.annotations.JsonKey;
import gov.nist.secauto.metaschema.codegen.AbstractJavaClassGenerator;
import gov.nist.secauto.metaschema.codegen.support.AnnotationUtils;
import gov.nist.secauto.metaschema.datatypes.DataTypes;
import gov.nist.secauto.metaschema.datatypes.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.definition.IFieldDefinition;
import gov.nist.secauto.metaschema.model.common.definition.IFlagDefinition;
import gov.nist.secauto.metaschema.model.common.definition.IFlaggedDefinition;
import gov.nist.secauto.metaschema.model.common.instance.IFlagInstance;
import gov.nist.secauto.metaschema.model.definitions.MetaschemaFlaggedDefinition;
import gov.nist.secauto.metaschema.model.instances.FlagInstance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Set;

public class FlagPropertyGenerator
    extends AbstractPropertyGenerator<AbstractJavaClassGenerator<?>> {
  private static final Logger logger = LogManager.getLogger(FlagPropertyGenerator.class);

  private final FlagInstance<?> instance;
  private final DataTypes dataType;

  public FlagPropertyGenerator(FlagInstance<?> instance, AbstractJavaClassGenerator<?> classGenerator) {
    super(classGenerator);
    this.instance = instance;

    IFlagDefinition definition = instance.getDefinition();
    DataTypes type = definition.getDatatype();
    if (type == null) {
      logger.warn("Unsupported datatype '{}', using {}", type, DataTypes.DEFAULT_DATA_TYPE);
      type = DataTypes.DEFAULT_DATA_TYPE;
    }
    this.dataType = type;
  }

  protected IFlagInstance getInstance() {
    return instance;
  }

  public DataTypes getDataType() {
    return dataType;
  }

  @Override
  protected String getInstanceName() {
    return instance.getUseName();
  }

  @Override
  protected TypeName getJavaType() {
    return ClassName.get(getDataType().getJavaTypeAdapter().getJavaClass());
  }

  @Override
  public MarkupLine getDescription() {
    return instance.getDefinition().getDescription();
  }

  @Override
  protected Set<MetaschemaFlaggedDefinition> buildField(FieldSpec.Builder builder) {
    AnnotationSpec.Builder annotation = AnnotationSpec.builder(Flag.class)
        .addMember("useName", "$S", instance.getEffectiveName());

    if (getInstance().isRequired()) {
      annotation.addMember("required", "$L", true);
    }

    DataTypes valueDataType = getDataType();
    annotation.addMember("typeAdapter", "$T.class",
        valueDataType.getJavaTypeAdapter().getClass());

    AnnotationUtils.applyAllowedValuesConstraints(annotation,
        getInstance().getDefinition().getAllowedValuesContraints());
    AnnotationUtils.applyIndexHasKeyConstraints(annotation, getInstance().getDefinition().getIndexHasKeyConstraints());
    AnnotationUtils.applyMatchesConstraints(annotation, getInstance().getDefinition().getMatchesConstraints());
    AnnotationUtils.applyExpectConstraints(annotation, getInstance().getDefinition().getExpectConstraints());

    builder.addAnnotation(annotation.build());

    IFlagInstance instance = getInstance();
    IFlaggedDefinition parent = instance.getContainingDefinition();
    if (parent.hasJsonKey() && instance.equals(parent.getJsonKeyFlagInstance())) {
      builder.addAnnotation(JsonKey.class);
    }

    if (parent instanceof IFieldDefinition) {
      IFieldDefinition parentField = (IFieldDefinition) parent;

      if (parentField.hasJsonValueKeyFlagInstance() && instance.equals(parentField.getJsonValueKeyFlagInstance())) {
        builder.addAnnotation(JsonFieldValueKeyFlag.class);
      }
    }
    return Collections.emptySet();
  }
}

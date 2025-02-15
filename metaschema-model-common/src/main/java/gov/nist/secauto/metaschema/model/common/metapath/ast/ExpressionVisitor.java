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

package gov.nist.secauto.metaschema.model.common.metapath.ast;

public interface ExpressionVisitor<RESULT, CONTEXT> {

  RESULT visitAddition(Addition expr, CONTEXT context);

  RESULT visitAnd(And expr, CONTEXT context);

  RESULT visitStep(Step expr, CONTEXT context);

  RESULT visitComparison(Comparison expr, CONTEXT context);

  RESULT visitContextItem(ContextItem expr, CONTEXT context);

  RESULT visitDecimalLiteral(DecimalLiteral expr, CONTEXT context);

  RESULT visitDivision(Division expr, CONTEXT context);

  RESULT visitFlag(Flag expr, CONTEXT context);

  RESULT visitFunctionCall(FunctionCall expr, CONTEXT context);

  RESULT visitIntegerDivision(IntegerDivision expr, CONTEXT context);

  RESULT visitIntegerLiteral(IntegerLiteral expr, CONTEXT context);

  RESULT visitMetapath(Metapath expr, CONTEXT context);

  RESULT visitMod(Mod expr, CONTEXT context);

  RESULT visitModelInstance(ModelInstance modelInstance, CONTEXT context);

  RESULT visitMultiplication(Multiplication expr, CONTEXT context);

  RESULT visitName(Name expr, CONTEXT context);

  RESULT visitNegate(Negate expr, CONTEXT context);

  RESULT visitOr(OrNode expr, CONTEXT context);

  RESULT visitParenthesizedExpression(ParenthesizedExpression expr, CONTEXT context);

  RESULT visitRelativeDoubleSlashPath(RelativeDoubleSlashPath relativeDoubleSlashPath, CONTEXT context);

  RESULT visitRelativeSlashPath(RelativeSlashPath relativeSlashPath, CONTEXT context);

  RESULT visitRootDoubleSlashPath(RootDoubleSlashPath rootDoubleSlashPath, CONTEXT context);

  RESULT visitRootSlashOnlyPath(RootSlashOnlyPath rootSlashOnlyPath, CONTEXT context);

  RESULT visitRootSlashPath(RootSlashPath rootSlashPath, CONTEXT context);

  RESULT visitStringConcat(StringConcat expr, CONTEXT context);

  RESULT visitStringLiteral(StringLiteral expr, CONTEXT context);

  RESULT visitSubtraction(Subtraction expr, CONTEXT context);

  RESULT visitUnion(Union expr, CONTEXT context);

  RESULT visitWildcard(Wildcard expr, CONTEXT context);
}

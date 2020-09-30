/*
 * MIT License
 *
 * Copyright (c) 2020 Alen Turkovic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.alturkovic.rule.engine.reader;

class RuleDefinitionValidator {

  public static void validate(final RuleDefinition definition) {
    checkIfDefined(definition.getName(), "Must define 'name': " + definition);

    if (isDefined(definition.getWhen())) {
      checkIfSimpleRuleIsValid(definition);
    } else {
      checkIfCompositeRuleIsValid(definition);
    }
  }

  private static void checkIfSimpleRuleIsValid(final RuleDefinition definition) {
    checkIfRulesAreEmpty(definition);
    checkIfThenIsValid(definition);
  }

  private static void checkIfCompositeRuleIsValid(final RuleDefinition definition) {
    checkIfDefined(definition.getType(), "Must define 'type' with 'rules': " + definition);

    checkIfThenIsEmpty(definition);
    checkIfRulesAreValid(definition);
  }

  private static void checkIfRulesAreEmpty(final RuleDefinition definition) {
    if (!definition.getRules().isEmpty()) {
      throw new IllegalArgumentException("Cannot define both 'when' and 'rules': " + definition);
    }
  }

  private static void checkIfThenIsValid(final RuleDefinition definition) {
    if (definition.getThen().isEmpty()) {
      throw new IllegalArgumentException("Must define at least one 'then' with 'when': " + definition);
    }

    definition.getThen().forEach(then -> checkIfDefined(then, "Must define non-blank 'then' with 'when': " + definition));
  }

  private static void checkIfThenIsEmpty(final RuleDefinition definition) {
    if (!definition.getThen().isEmpty()) {
      throw new IllegalArgumentException("Cannot define both 'then' and 'rules': " + definition);
    }
  }

  private static void checkIfRulesAreValid(final RuleDefinition definition) {
    if (definition.getRules().isEmpty()) {
      throw new IllegalArgumentException("Must define at least one 'rules' without 'when': " + definition);
    }

    definition.getRules().forEach(RuleDefinitionValidator::validate);
  }

  private static void checkIfDefined(final String value, final String message) {
    if (!isDefined(value)) {
      throw new IllegalArgumentException(message);
    }
  }

  private static boolean isDefined(final String value) {
    return value != null && !value.isBlank();
  }
}

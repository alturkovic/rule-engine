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

package com.github.alturkovic.rule.engine.proxy;

import com.github.alturkovic.rule.engine.aop.Description;
import com.github.alturkovic.rule.engine.aop.Given;
import com.github.alturkovic.rule.engine.aop.Name;
import com.github.alturkovic.rule.engine.aop.Priority;
import com.github.alturkovic.rule.engine.aop.Then;
import com.github.alturkovic.rule.engine.aop.When;
import com.github.alturkovic.rule.engine.api.Facts;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

class RuleProxyValidator {

  public static void validateRuleDefinition(final Object rule) {
    checkIfRuleClassIsWellAnnotated(rule);
    checkIfWhenMethodIsWellDefined(rule);
    checkIfThenMethodsAreWellDefined(rule);
    checkIfRuleMethodsAreWellDefined(rule);
  }

  private static void checkIfRuleClassIsWellAnnotated(final Object rule) {
    if (!rule.getClass().isAnnotationPresent(com.github.alturkovic.rule.engine.aop.Rule.class)) {
      throw new IllegalArgumentException(format("Rule '%s' is not annotated with '@Rule'", rule.getClass().getName()));
    }
  }

  private static void checkIfWhenMethodIsWellDefined(final Object rule) {
    final List<Method> whenMethods = getMethodsAnnotatedWith(When.class, rule);
    if (whenMethods.size() != 1) {
      throw new IllegalArgumentException(format("Rule '%s' must have exactly one method annotated with '@When'", rule.getClass().getName()));
    }

    final Method whenMethod = whenMethods.get(0);
    if (!isWhenWellDefined(whenMethod)) {
      throw new IllegalArgumentException(format("@When method '%s' in rule '%s' must be public with boolean return type and with @Given or Facts parameters", whenMethod, rule.getClass().getName()));
    }
  }

  private static void checkIfThenMethodsAreWellDefined(final Object rule) {
    final List<Method> thenMethods = getMethodsAnnotatedWith(Then.class, rule);
    if (thenMethods.isEmpty()) {
      throw new IllegalArgumentException(format("Rule '%s' must have at least one public method annotated with '@Then'", rule.getClass().getName()));
    }

    for (final Method thenMethod : thenMethods) {
      if (!isThenWellDefined(thenMethod)) {
        throw new IllegalArgumentException(format("@Then method '%s' in rule '%s' must be public with @Given or Facts parameters", thenMethod, rule.getClass().getName()));
      }
    }
  }

  private static void checkIfRuleMethodsAreWellDefined(final Object rule) {
    checkIfNameMethodIsWellDefined(rule);
    checkIfDescriptionMethodIsWellDefined(rule);
    checkIfPriorityMethodIsWellDefined(rule);
  }

  private static void checkIfNameMethodIsWellDefined(final Object rule) {
    final var nameMethods = getMethodsAnnotatedWith(Name.class, rule);
    if (nameMethods.size() > 1) {
      throw new IllegalArgumentException(format("Rule '%s' can have at most one method annotated with '@Name'", rule.getClass().getName()));
    }

    if (!nameMethods.isEmpty()) {
      final var nameMethod = nameMethods.get(0);
      if (!isNameWellDefined(nameMethod)) {
        throw new IllegalArgumentException(format("@Name method '%s' in rule '%s' must be public with String return type and no parameters", nameMethod, rule.getClass().getName()));
      }
    }
  }

  private static void checkIfDescriptionMethodIsWellDefined(final Object rule) {
    final var descriptionMethods = getMethodsAnnotatedWith(Description.class, rule);
    if (descriptionMethods.size() > 1) {
      throw new IllegalArgumentException(format("Rule '%s' can have at most one method annotated with '@Description'", rule.getClass().getName()));
    }

    if (!descriptionMethods.isEmpty()) {
      final var descriptionMethod = descriptionMethods.get(0);
      if (!isDescriptionWellDefined(descriptionMethod)) {
        throw new IllegalArgumentException(format("@Description method '%s' in rule '%s' must be public with String return type and no parameters", descriptionMethod, rule.getClass().getName()));
      }
    }
  }

  private static void checkIfPriorityMethodIsWellDefined(final Object rule) {
    final var priorityMethods = getMethodsAnnotatedWith(Priority.class, rule);
    if (priorityMethods.size() > 1) {
      throw new IllegalArgumentException(format("Rule '%s' can have at most one method annotated with '@Priority'", rule.getClass().getName()));
    }

    if (!priorityMethods.isEmpty()) {
      final var priorityMethod = priorityMethods.get(0);
      if (!isPriorityWellDefined(priorityMethod)) {
        throw new IllegalArgumentException(format("@Priority method '%s' in rule '%s' must be public with int return type and no parameters", priorityMethod, rule.getClass().getName()));
      }
    }
  }

  private static List<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation, final Object rule) {
    return Arrays.stream(rule.getClass().getMethods())
        .filter(m -> m.isAnnotationPresent(annotation))
        .collect(Collectors.toList());
  }

  private static boolean isWhenWellDefined(final Method method) {
    return Modifier.isPublic(method.getModifiers()) &&
        method.getReturnType().equals(Boolean.TYPE) &&
        areParametersValid(method);
  }

  private static boolean isThenWellDefined(final Method method) {
    return Modifier.isPublic(method.getModifiers()) &&
        areParametersValid(method);
  }

  private static boolean isNameWellDefined(final Method method) {
    return Modifier.isPublic(method.getModifiers()) &&
        method.getReturnType().equals(String.class) &&
        hasNoParameters(method);
  }

  private static boolean isDescriptionWellDefined(final Method method) {
    return Modifier.isPublic(method.getModifiers()) &&
        method.getReturnType().equals(String.class) &&
        hasNoParameters(method);
  }

  private static boolean isPriorityWellDefined(final Method method) {
    return Modifier.isPublic(method.getModifiers()) &&
        method.getReturnType().equals(Integer.TYPE) &&
        hasNoParameters(method);
  }

  private static boolean areParametersValid(final Method method) {
    for (final var parameter : method.getParameters()) {
      final var given = parameter.getAnnotation(Given.class);
      if (given == null && !Facts.class.isAssignableFrom(parameter.getType())) {
        return false;
      }
    }
    return true;
  }

  private static boolean hasNoParameters(final Method method) {
    final var parameters = method.getParameters();
    return parameters == null || parameters.length == 0;
  }
}

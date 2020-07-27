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

package com.github.alturkovic.rule.engine.aop;

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class RuleProxy implements InvocationHandler {
  private final Object target;
  private com.github.alturkovic.rule.engine.aop.Rule annotation;
  private String name;
  private String description;
  private Method[] methods;
  private Method whenMethod;
  private Set<OrderedAction> thenMethods;
  private Method compareToMethod;
  private Method toStringMethod;

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    final var methodName = method.getName();
    switch (methodName) {
      case "getName":
        return getRuleName();
      case "getDescription":
        return getRuleDescription();
      case "getPriority":
        return getRuleAnnotation().priority();
      case "accept":
        return whenMethod(args);
      case "execute":
        return thenMethods(args);
      case "compareTo":
        return compareToMethod(args);
      case "equals":
        return equalsMethod(args);
      case "hashCode":
        return hashCodeMethod();
      case "toString":
        return toStringMethod();
      default:
        throw new IllegalStateException(String.format("Method '%s' execution not supported on rule '%s'", method, proxy));
    }
  }

  private com.github.alturkovic.rule.engine.aop.Rule getRuleAnnotation() {
    if (this.annotation == null) {
      this.annotation = target.getClass().getAnnotation(com.github.alturkovic.rule.engine.aop.Rule.class);
    }
    return this.annotation;
  }

  private String getRuleName() {
    if (this.name == null) {
      final var name = getRuleAnnotation().name();
      this.name = name.isEmpty() ? target.getClass().getSimpleName() : name;
    }
    return this.name;
  }

  private String getRuleDescription() {
    if (this.description == null) {
      final var description = getRuleAnnotation().description();
      this.description = description.isEmpty() ? buildDefaultDescription() : description;
    }
    return this.description;
  }

  private String buildDefaultDescription() {
    final var description = new StringBuilder("When ");
    description.append(getWhenMethod().getName());
    description.append(" then ");

    final var iterator = getThenMethods().iterator();
    while (iterator.hasNext()) {
      description.append(iterator.next().method.getName());
      if (iterator.hasNext()) {
        description.append(", ");
      }
    }

    return description.toString();
  }

  private Object whenMethod(final Object[] args) {
    final var facts = (Facts) args[0];
    final var whenMethod = getWhenMethod();
    try {
      final var parameters = extractGivenParameters(whenMethod, facts);
      return whenMethod.invoke(target, parameters.toArray());
    } catch (final Exception e) {
      log.warn("Rule evaluation failed", e);
      return false;
    }
  }

  private Method getWhenMethod() {
    if (this.whenMethod == null) {
      this.whenMethod = Arrays.stream(getMethods())
          .filter(m -> m.isAnnotationPresent(When.class))
          .findFirst()
          .orElse(null);
    }
    return this.whenMethod;
  }

  private Object thenMethods(final Object[] args) {
    final var facts = (Facts) args[0];
    for (final var orderedAction : getThenMethods()) {
      try {
        final var parameters = extractGivenParameters(orderedAction.method, facts);
        orderedAction.method.invoke(target, parameters.toArray());
      } catch (final Exception e) {
        log.warn("Rule execution failed", e);
        return null;
      }
    }
    return null;
  }

  private Set<OrderedAction> getThenMethods() {
    if (this.thenMethods == null) {
      this.thenMethods = Arrays.stream(getMethods())
          .filter(m -> m.isAnnotationPresent(Then.class))
          .map(m -> new OrderedAction(m, m.getAnnotation(Then.class).value()))
          .collect(Collectors.toCollection(TreeSet::new));
    }
    return this.thenMethods;
  }

  private Method[] getMethods() {
    if (this.methods == null) {
      this.methods = target.getClass().getMethods();
    }
    return this.methods;
  }

  private List<Object> extractGivenParameters(final Method method, final Facts facts) {
    final var actualParameters = new ArrayList<>();

    for (final var parameter : method.getParameters()) {
      final var given = parameter.getAnnotation(Given.class);
      if (given != null) {
        if (!facts.isDeclared(given.value())) {
          throw new IllegalArgumentException(String.format("Requested @Given(\"%s\") fact was not declared in facts: %s", given.value(), facts));
        }
        actualParameters.add(facts.get(given.value()));
      } else {
        actualParameters.add(facts);
      }
    }

    return actualParameters;
  }

  private Object compareToMethod(final Object[] args) throws Exception {
    final var otherRule = args[0];
    final var compareToMethod = getCompareToMethod();
    if (compareToMethod != null && Proxy.isProxyClass(otherRule.getClass())) {
      return compareToMethod.invoke(target, ((RuleProxy) Proxy.getInvocationHandler(otherRule)).target);
    }
    return compareTo((Rule) otherRule);
  }

  private Method getCompareToMethod() {
    if (this.compareToMethod == null) {
      Arrays.stream(getMethods())
          .filter(m -> "compareTo".equals(m.getName()))
          .findFirst()
          .ifPresent(m -> this.compareToMethod = m);
    }
    return this.compareToMethod;
  }

  private int compareTo(final Rule otherRule) {
    final var priority = getRuleAnnotation().priority();
    final var otherPriority = otherRule.getPriority();
    final var priorityComparison = Integer.compare(priority, otherPriority);
    if (priorityComparison != 0) {
      return priorityComparison;
    }
    return getRuleName().compareTo(otherRule.getName());
  }

  private boolean equalsMethod(final Object[] args) {
    if (!(args[0] instanceof Rule)) {
      return false;
    }

    final var otherRule = (Rule) args[0];
    final var priority = getRuleAnnotation().priority();
    final var otherPriority = otherRule.getPriority();
    if (priority != otherPriority) {
      return false;
    }

    return Objects.equals(getRuleName(), otherRule.getName());
  }

  private int hashCodeMethod() {
    var result = getRuleName().hashCode();
    result = 31 * result + getRuleAnnotation().priority();
    return result;
  }

  private String toStringMethod() throws Exception {
    final var toStringMethod = getToStringMethod();
    return toStringMethod == null ? getRuleName() : (String) toStringMethod.invoke(target);
  }

  private Method getToStringMethod() {
    if (this.toStringMethod == null) {
      Arrays.stream(getMethods())
          .filter(m -> m.getName().equals("toString"))
          .findFirst()
          .ifPresent(m -> this.toStringMethod = m);
    }
    return this.toStringMethod;
  }

  public static Rule asRule(final Object rule) {
    Validator.validateRuleDefinition(rule);
    return (Rule) Proxy.newProxyInstance(
        Rule.class.getClassLoader(),
        new Class[]{Rule.class, Comparable.class},
        new RuleProxy(rule));
  }

  @AllArgsConstructor
  private static class OrderedAction implements Comparable<OrderedAction> {
    private final Method method;
    private final int order;

    @Override
    public int compareTo(final OrderedAction orderedAction) {
      if (order < orderedAction.order) {
        return -1;
      } else if (order > orderedAction.order) {
        return 1;
      } else {
        return method.equals(orderedAction.method) ? 0 : 1;
      }
    }
  }

  private static class Validator {

    private static void validateRuleDefinition(final Object rule) {
      checkRuleClass(rule);
      checkWhenMethod(rule);
      checkThenMethods(rule);
    }

    private static void checkRuleClass(final Object rule) {
      if (!rule.getClass().isAnnotationPresent(com.github.alturkovic.rule.engine.aop.Rule.class)) {
        throw new IllegalArgumentException(format("Rule '%s' is not annotated with '%s'", rule.getClass().getName(), com.github.alturkovic.rule.engine.aop.Rule.class.getName()));
      }
    }

    private static void checkWhenMethod(final Object rule) {
      final List<Method> whenMethods = getMethodsAnnotatedWith(When.class, rule);
      if (whenMethods.size() != 1) {
        throw new IllegalArgumentException(format("Rule '%s' must have exactly one method annotated with '%s'", rule.getClass().getName(), When.class.getName()));
      }

      final Method whenMethod = whenMethods.get(0);
      if (!isWhenWellDefined(whenMethod)) {
        throw new IllegalArgumentException(format("When method '%s' in rule '%s' must be public with boolean return type and with @Given or Facts parameters", whenMethod, rule.getClass().getName()));
      }
    }

    private static void checkThenMethods(final Object rule) {
      final List<Method> thenMethods = getMethodsAnnotatedWith(Then.class, rule);
      if (thenMethods.isEmpty()) {
        throw new IllegalArgumentException(format("Rule '%s' must have at least one public method annotated with '%s'", rule.getClass().getName(), Then.class.getName()));
      }

      for (final Method thenMethod : thenMethods) {
        if (!isThenWellDefined(thenMethod)) {
          throw new IllegalArgumentException(format("Then method '%s' in rule '%s' must be public with @Given or Facts parameters", thenMethod, rule.getClass().getName()));
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
          validParameters(method);
    }

    private static boolean isThenWellDefined(final Method method) {
      return Modifier.isPublic(method.getModifiers()) &&
          validParameters(method);
    }

    private static boolean validParameters(final Method method) {
      for (final var parameter : method.getParameters()) {
        final var given = parameter.getAnnotation(Given.class);
        if (given == null && !parameter.getType().isAssignableFrom(Facts.class)) {
          return false;
        }
      }
      return true;
    }
  }
}
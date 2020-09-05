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

import com.github.alturkovic.rule.engine.aop.Rule;
import com.github.alturkovic.rule.engine.aop.Then;
import com.github.alturkovic.rule.engine.aop.When;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class RuleProxyDefinition {
  private final Class<?> targetClass;
  private Rule annotation;
  private String name;
  private String description;
  private List<Method> methods;
  private Method whenMethod;
  private List<Method> thenMethods;
  private Method compareToMethod;
  private Method toStringMethod;

  public String getName() {
    if (name == null) {
      name = Optional.of(getAnnotation().name())
          .filter(n -> !n.isBlank())
          .orElseGet(targetClass::getSimpleName);
    }
    return name;
  }

  public String getDescription() {
    if (description == null) {
      description = Optional.of(getAnnotation().description())
          .filter(d -> !d.isBlank())
          .orElseGet(this::buildDefaultDescription);
    }
    return description;
  }

  public Method getWhenMethod() {
    if (whenMethod == null) {
      whenMethod = getMethods().stream()
          .filter(m -> m.isAnnotationPresent(When.class))
          .findFirst()
          .orElse(null);
    }
    return whenMethod;
  }

  public List<Method> getThenMethods() {
    if (thenMethods == null) {
      thenMethods = getMethods().stream()
          .filter(m -> m.isAnnotationPresent(Then.class))
          .sorted(Comparator.comparingInt(m -> m.getAnnotation(Then.class).value()))
          .collect(Collectors.toList());
    }
    return thenMethods;
  }

  public Method getCompareToMethod() {
    if (compareToMethod == null) {
      compareToMethod = getNamedMethod("compareTo");
    }
    return compareToMethod;
  }

  public Method getToStringMethod() {
    if (toStringMethod == null) {
      toStringMethod = getNamedMethod("toString");
    }
    return toStringMethod;
  }

  public int getPriority() {
    return getAnnotation().priority();
  }

  private Rule getAnnotation() {
    if (annotation == null) {
      annotation = targetClass.getAnnotation(com.github.alturkovic.rule.engine.aop.Rule.class);
    }
    return annotation;
  }

  private List<Method> getMethods() {
    if (methods == null) {
      methods = List.of(targetClass.getMethods());
    }
    return methods;
  }

  private Method getNamedMethod(final String methodName) {
    return getMethods().stream()
        .filter(m -> methodName.equals(m.getName()))
        .findFirst()
        .orElse(null);
  }

  private String buildDefaultDescription() {
    final var description = new StringBuilder("When ");
    description.append(getWhenMethod().getName());
    description.append(" then ");

    final var iterator = getThenMethods().iterator();
    while (iterator.hasNext()) {
      description.append(iterator.next().getName());
      if (iterator.hasNext()) {
        description.append(", ");
      }
    }

    return description.toString();
  }
}

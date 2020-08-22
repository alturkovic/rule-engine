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

import com.github.alturkovic.rule.engine.aop.Given;
import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class RuleProxyMethodInvoker {
  private final RuleProxyDefinition definition;

  public Object when(final Object target, final Facts facts) {
    final var when = definition.getWhenMethod();
    try {
      final var requestedWhenParameters = extractGivenParameters(when, facts);
      return when.invoke(target, requestedWhenParameters);
    } catch (final Exception e) {
      return false;
    }
  }

  public Object then(final Object target, final Facts facts) throws Exception {
    for (final var thenMethod : definition.getThenMethods()) {
      final var requestedFactParameters = extractGivenParameters(thenMethod, facts);
      thenMethod.invoke(target, requestedFactParameters);
    }
    return null;
  }

  public Object compareToProxy(final Object target, final Object other) throws Exception {
    final var compareTo = definition.getCompareToMethod();
    if (compareTo != null && Proxy.isProxyClass(other.getClass())) {
      final var otherTarget = ((RuleProxy) Proxy.getInvocationHandler(other)).getTarget();
      return compareTo.invoke(target, otherTarget);
    }
    return compareTo((Rule) other);
  }

  public boolean equalsProxy(final Object other) {
    if (!(other instanceof Rule)) {
      return false;
    }

    final var otherRule = (Rule) other;
    final var priority = definition.getPriority();
    final var otherPriority = otherRule.getPriority();
    if (priority != otherPriority) {
      return false;
    }

    return Objects.equals(definition.getName(), otherRule.getName());
  }

  public int hashCodeProxy() {
    var result = definition.getName().hashCode();
    result = 31 * result + definition.getPriority();
    return result;
  }

  public String toStringProxy(final Object target) throws Exception {
    final var toString = definition.getToStringMethod();
    return (String) toString.invoke(target);
  }

  private Object[] extractGivenParameters(final Method method, final Facts facts) {
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

    return actualParameters.toArray();
  }

  private int compareTo(final Rule otherRule) {
    final var priority = definition.getPriority();
    final var otherPriority = otherRule.getPriority();
    final var priorityComparison = Integer.compare(priority, otherPriority);
    if (priorityComparison != 0) {
      return priorityComparison;
    }
    return definition.getName().compareTo(otherRule.getName());
  }
}

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

class RuleProxyMethodInvoker {
  private final Object target;
  private final RuleProxyDefinition definition;

  public RuleProxyMethodInvoker(final Object target) {
    this.target = target;
    this.definition = new RuleProxyDefinition(target.getClass());
  }

  public String name() throws Exception {
    final var nameMethod = definition.getNameMethod();
    if (nameMethod != null) {
      return (String) nameMethod.invoke(target);
    }
    return definition.getName();
  }

  public String description() throws Exception {
    final var descriptionMethod = definition.getDescriptionMethod();
    if (descriptionMethod != null) {
      return (String) descriptionMethod.invoke(target);
    }
    return definition.getDescription();
  }

  public int priority() throws Exception {
    final var priorityMethod = definition.getPriorityMethod();
    if (priorityMethod != null) {
      return (int) priorityMethod.invoke(target);
    }
    return definition.getPriority();
  }

  public boolean when(final Facts facts) {
    final var whenMethod = definition.getWhenMethod();
    try {
      final var requestedWhenParameters = extractGivenParameters(whenMethod, facts);
      return (boolean) whenMethod.invoke(target, requestedWhenParameters);
    } catch (final Exception e) {
      return false;
    }
  }

  public Object then(final Facts facts) throws Exception {
    for (final var thenMethod : definition.getThenMethods()) {
      final var requestedFactParameters = extractGivenParameters(thenMethod, facts);
      thenMethod.invoke(target, requestedFactParameters);
    }
    return null;
  }

  public Object compareToProxy(final Object other) throws Exception {
    final var compareToMethod = definition.getCompareToMethod();
    if (compareToMethod != null && Proxy.isProxyClass(other.getClass())) {
      final var otherTarget = ((RuleProxy) Proxy.getInvocationHandler(other)).getTarget();
      return compareToMethod.invoke(target, otherTarget);
    }
    return compareTo((Rule) other);
  }

  public boolean equalsProxy(final Object other) throws Exception {
    if (!(other instanceof Rule)) {
      return false;
    }

    final var otherRule = (Rule) other;
    final var priority = priority();
    final var otherPriority = otherRule.getPriority();
    if (priority != otherPriority) {
      return false;
    }

    return Objects.equals(name(), otherRule.getName());
  }

  public int hashCodeProxy() throws Exception {
    var result = name().hashCode();
    result = 31 * result + priority();
    return result;
  }

  public String toStringProxy() throws Exception {
    return (String) definition.getToStringMethod().invoke(target);
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

  private int compareTo(final Rule otherRule) throws Exception {
    final var priority = priority();
    final var otherPriority = otherRule.getPriority();
    final var priorityComparison = Integer.compare(priority, otherPriority);
    if (priorityComparison != 0) {
      return priorityComparison;
    }
    return name().compareTo(otherRule.getName());
  }
}

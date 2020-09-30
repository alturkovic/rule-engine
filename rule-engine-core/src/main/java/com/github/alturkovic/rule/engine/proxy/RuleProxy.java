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

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A proxy around an annotation based POJO rule.
 */
public class RuleProxy implements InvocationHandler {
  private final Object target;
  private final RuleProxyMethodInvoker invoker;

  private RuleProxy(final Object target) {
    this.target = target;
    this.invoker = new RuleProxyMethodInvoker(target);
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    final var methodName = method.getName();
    switch (methodName) {
      case "getName":
        return invoker.name();
      case "getDescription":
        return invoker.description();
      case "getPriority":
        return invoker.priority();
      case "accept":
        return invoker.when((Facts) args[0]);
      case "execute":
        return invoker.then((Facts) args[0]);
      case "compareTo":
        return invoker.compareToProxy(args[0]);
      case "equals":
        return invoker.equalsProxy(args[0]);
      case "hashCode":
        return invoker.hashCodeProxy();
      case "toString":
        return invoker.toStringProxy();
      default:
        throw new IllegalStateException(String.format("Method '%s' execution not supported on rule '%s'", method, proxy));
    }
  }

  public Object getTarget() {
    return target;
  }

  public static Rule asRule(final Object rule) {
    RuleProxyValidator.validateRuleDefinition(rule);
    return (Rule) Proxy.newProxyInstance(
        Rule.class.getClassLoader(),
        new Class[]{Rule.class, Comparable.class},
        new RuleProxy(rule));
  }
}
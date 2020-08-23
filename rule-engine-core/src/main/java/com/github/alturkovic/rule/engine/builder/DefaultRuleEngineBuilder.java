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

package com.github.alturkovic.rule.engine.builder;

import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.RuleEngine;
import com.github.alturkovic.rule.engine.api.RuleEngineListener;
import com.github.alturkovic.rule.engine.api.Rules;
import com.github.alturkovic.rule.engine.core.DefaultRuleEngine;
import com.github.alturkovic.rule.engine.core.SimpleOrderedRules;
import com.github.alturkovic.rule.engine.proxy.RuleProxy;

public class DefaultRuleEngineBuilder {
  private final Rules rules = new SimpleOrderedRules();
  private RuleEngineListener listener = RuleEngineListener.NO_OP;

  public DefaultRuleEngineBuilder listener(final RuleEngineListener listener) {
    this.listener = listener;
    return this;
  }

  public DefaultRuleEngineBuilder rule(final Rule rule) {
    this.rules.register(rule);
    return this;
  }

  public DefaultRuleEngineBuilder rule(final Object rule) {
    this.rules.register(RuleProxy.asRule(rule));
    return this;
  }

  public RuleEngine build() {
    return new DefaultRuleEngine(listener, rules);
  }
}
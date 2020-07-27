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

import com.github.alturkovic.rule.engine.api.Action;
import com.github.alturkovic.rule.engine.api.Condition;
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.core.SimpleRule;
import com.github.alturkovic.rule.engine.support.MultiAction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RuleBuilder {
  private final String name;
  private String description;
  private Condition condition = Condition.ALWAYS;
  private Action action = Action.NO_OP;
  private int priority = Rule.DEFAULT_PRIORITY;

  public RuleBuilder priority(final int priority) {
    this.priority = priority;
    return this;
  }

  public RuleBuilder description(final String description) {
    this.description = description;
    return this;
  }

  public RuleBuilder when(final Condition condition) {
    this.condition = condition;
    return this;
  }

  public RuleBuilder then(final Action action) {
    if (this.action == Action.NO_OP) {
      this.action = action;
    } else {
      if (!(this.action instanceof MultiAction)) {
        this.action = new MultiAction(this.action);
      }
      ((MultiAction) this.action).add(action);
    }
    return this;
  }

  public Rule build() {
    return new SimpleRule(name, description, priority, condition, action);
  }
}

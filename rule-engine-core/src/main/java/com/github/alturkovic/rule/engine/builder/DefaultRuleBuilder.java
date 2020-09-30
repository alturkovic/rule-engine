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
import com.github.alturkovic.rule.engine.composite.CompositeAction;
import com.github.alturkovic.rule.engine.core.DefaultRule;
import java.util.ArrayList;
import java.util.function.Predicate;

public class DefaultRuleBuilder extends AbstractRuleBuilder<DefaultRuleBuilder> {
  private Condition condition = Condition.ALWAYS;
  private Action action = Action.NO_OP;

  public DefaultRuleBuilder(final String name) {
    super(name);
  }

  public DefaultRuleBuilder when(final Condition condition) {
    this.condition = condition;
    return this;
  }

  public <T> DefaultRuleBuilder when(final String fact, final Predicate<T> predicate) {
    this.condition = facts -> predicate.test(facts.get(fact));
    return this;
  }

  public DefaultRuleBuilder then(final Action action) {
    if (this.action == Action.NO_OP) {
      this.action = action;
    } else {
      this.action = new CompositeAction(accumulateActions(action));
    }
    return this;
  }

  public Rule build() {
    return new DefaultRule(name, description, priority, condition, action);
  }

  private ArrayList<Action> accumulateActions(final Action current) {
    final var actions = new ArrayList<Action>();
    if (this.action instanceof CompositeAction) {
      actions.addAll(((CompositeAction) this.action).getActions());
    } else {
      actions.add(this.action);
    }
    actions.add(current);
    return actions;
  }

  public static DefaultRuleBuilder newRule(final String name) {
    return new DefaultRuleBuilder(name);
  }
}

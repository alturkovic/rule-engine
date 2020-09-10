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

package com.github.alturkovic.rule.engine.mvel;

import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.builder.AbstractRuleBuilder;
import com.github.alturkovic.rule.engine.composite.CompositeAction;
import com.github.alturkovic.rule.engine.core.DefaultRule;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public class MVELRuleBuilder extends AbstractRuleBuilder<MVELRuleBuilder> {
  private ParserContext context = new ParserContext();
  private String condition;
  private List<String> actions = new ArrayList<>();

  public MVELRuleBuilder(final String name) {
    super(name);
  }

  public MVELRuleBuilder context(final ParserContext context) {
    this.context = context;
    return this;
  }

  public MVELRuleBuilder when(final String expression) {
    this.condition = expression;
    return this;
  }

  public MVELRuleBuilder then(final String expression) {
    this.actions.add(expression);
    return this;
  }

  public MVELRuleBuilder then(final List<String> expressions) {
    this.actions.addAll(expressions);
    return this;
  }

  public Rule build() {
    final var mvelCondition = new MVELCondition(MVEL.compileExpression(condition, context));
    final var mvelActions = new CompositeAction(parseActions());
    return new DefaultRule(name, description, priority, mvelCondition, mvelActions);
  }

  private List<MVELAction> parseActions() {
    return actions.stream()
        .map(action -> new MVELAction(MVEL.compileExpression(action, context)))
        .collect(Collectors.toList());
  }

  public static MVELRuleBuilder newMVELRule(final String name) {
    return new MVELRuleBuilder(name);
  }
}

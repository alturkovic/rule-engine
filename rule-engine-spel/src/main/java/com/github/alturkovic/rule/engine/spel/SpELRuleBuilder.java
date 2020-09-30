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

package com.github.alturkovic.rule.engine.spel;

import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.builder.AbstractRuleBuilder;
import com.github.alturkovic.rule.engine.composite.CompositeAction;
import com.github.alturkovic.rule.engine.core.DefaultRule;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ParserContext;

import static com.github.alturkovic.rule.engine.spel.util.SpELUtils.parse;

public class SpELRuleBuilder extends AbstractRuleBuilder<SpELRuleBuilder> {
  private ParserContext context = ParserContext.TEMPLATE_EXPRESSION;
  private BeanResolver beanResolver;
  private String condition;
  private List<String> actions = new ArrayList<>();

  public SpELRuleBuilder(final String name) {
    super(name);
  }

  public SpELRuleBuilder context(final ParserContext context) {
    this.context = context;
    return this;
  }

  public SpELRuleBuilder beanResolver(final BeanResolver beanResolver) {
    this.beanResolver = beanResolver;
    return this;
  }

  public SpELRuleBuilder when(final String expression) {
    this.condition = expression;
    return this;
  }

  public SpELRuleBuilder then(final String expression) {
    this.actions.add(expression);
    return this;
  }

  public SpELRuleBuilder then(final List<String> expressions) {
    this.actions.addAll(expressions);
    return this;
  }

  public Rule build() {
    final var spELCondition = new SpELCondition(parse(condition, context), beanResolver);
    final var spELActions = new CompositeAction(parseActions());
    return new DefaultRule(name, description, priority, spELCondition, spELActions);
  }

  private List<SpELAction> parseActions() {
    return actions.stream()
        .map(action -> new SpELAction(parse(action, context), beanResolver))
        .collect(Collectors.toList());
  }

  public static SpELRuleBuilder newSpELRule(final String name) {
    return new SpELRuleBuilder(name);
  }
}

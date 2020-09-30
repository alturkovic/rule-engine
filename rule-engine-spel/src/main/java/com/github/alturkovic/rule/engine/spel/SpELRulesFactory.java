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
import com.github.alturkovic.rule.engine.reader.RuleDefinition;
import com.github.alturkovic.rule.engine.reader.RuleDefinitionReader;
import com.github.alturkovic.rule.engine.reader.RulesFactory;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ParserContext;

import static com.github.alturkovic.rule.engine.spel.SpELRuleBuilder.newSpELRule;

public class SpELRulesFactory extends RulesFactory {
  private final ParserContext context;
  private final BeanResolver beanResolver;

  public SpELRulesFactory(final RuleDefinitionReader definitionReader) {
    this(definitionReader, ParserContext.TEMPLATE_EXPRESSION, null);
  }

  public SpELRulesFactory(final RuleDefinitionReader definitionReader, final ParserContext context, final BeanResolver beanResolver) {
    super(definitionReader);
    this.context = context;
    this.beanResolver = beanResolver;
  }

  @Override
  protected Rule toSimpleRule(final RuleDefinition ruleDefinition) {
    return newSpELRule(ruleDefinition.getName())
        .beanResolver(beanResolver)
        .context(context)
        .description(ruleDefinition.getDescription())
        .priority(ruleDefinition.getPriority())
        .when(ruleDefinition.getWhen())
        .then(ruleDefinition.getThen())
        .build();
  }
}

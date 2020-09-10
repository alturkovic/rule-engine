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

package com.github.alturkovic.rule.engine.reader;

import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.Rules;
import com.github.alturkovic.rule.engine.composite.AllCompositeRule;
import com.github.alturkovic.rule.engine.composite.AnyCompositeRule;
import com.github.alturkovic.rule.engine.core.SimpleOrderedRules;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class RulesFactory {
  private final RuleDefinitionReader definitionReader;

  public Rules create(final InputStream stream) {
    final var definitions = definitionReader.definitions(stream);
    return asRules(definitions);
  }

  protected Rule toRule(final RuleDefinition ruleDefinition) {
    return ruleDefinition.isComposite()
        ? toCompositeRule(ruleDefinition)
        : toSimpleRule(ruleDefinition);
  }

  protected Rule toCompositeRule(final RuleDefinition ruleDefinition) {
    switch (ruleDefinition.getType()) {
      case "any":
        return asAnyCompositeRule(ruleDefinition);
      case "all":
        return asAllCompositeRule(ruleDefinition);
      default:
        throw new IllegalArgumentException("Unsupported rules type: " + ruleDefinition.getType());
    }
  }

  protected abstract Rule toSimpleRule(final RuleDefinition ruleDefinition);

  protected Rules asRules(final List<RuleDefinition> definitions) {
    final var rules = definitions.stream()
        .map(this::toRule)
        .collect(Collectors.toSet());

    return new SimpleOrderedRules(rules);
  }

  private AllCompositeRule asAllCompositeRule(final RuleDefinition ruleDefinition) {
    return AllCompositeRule.builder()
        .name(ruleDefinition.getName())
        .description(ruleDefinition.getDescription())
        .priority(ruleDefinition.getPriority())
        .rules(asRules(ruleDefinition.getRules()))
        .build();
  }

  private AnyCompositeRule asAnyCompositeRule(final RuleDefinition ruleDefinition) {
    return AnyCompositeRule.builder()
        .name(ruleDefinition.getName())
        .description(ruleDefinition.getDescription())
        .priority(ruleDefinition.getPriority())
        .rules(asRules(ruleDefinition.getRules()))
        .build();
  }
}

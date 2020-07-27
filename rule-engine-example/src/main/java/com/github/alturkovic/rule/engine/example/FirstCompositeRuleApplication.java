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

package com.github.alturkovic.rule.engine.example;

import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.builder.FactsBuilder;
import com.github.alturkovic.rule.engine.builder.RuleBuilder;
import com.github.alturkovic.rule.engine.builder.RuleEngineBuilder;
import com.github.alturkovic.rule.engine.support.FirstCompositeRule;
import java.util.Set;

public class FirstCompositeRuleApplication {
  public static void main(final String[] args) {
    final var rule1 = new RuleBuilder("Rule1")
        .priority(1)
        .then(f -> System.out.print("1"))
        .build();

    final var rule2 = new RuleBuilder("Rule2")
        .priority(2)
        .then(f -> System.out.print("2"))
        .build();

    final var engine = new RuleEngineBuilder()
        .rule(new FirstCompositeRule("FirstCompositeRule", null, Rule.DEFAULT_PRIORITY, Set.of(rule1, rule2)))
        .build();

    engine.evaluate(new FactsBuilder()
        .build());
  }
}

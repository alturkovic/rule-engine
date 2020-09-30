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

package com.github.alturkovic.rule.engine.core;

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.RuleEngine;
import com.github.alturkovic.rule.engine.api.RuleEngineListener;
import com.github.alturkovic.rule.engine.api.Rules;
import java.util.Set;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * This implementation will keep firing rules that {@link Rule#accept(Facts) accept} the given {@link Facts} until no rules accept them.
 */
@Slf4j
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class InferenceRuleEngine implements RuleEngine {
  private final RuleEngineListener listener;
  private final Rules rules;

  @Override
  public void evaluate(final Facts facts) {
    Set<Rule> selectedRules;
    do {
      log.debug("Selecting candidate rules using: {}", facts);
      selectedRules = selectCandidates(facts);
      if (!selectedRules.isEmpty()) {
        final var engine = new DefaultRuleEngine(listener, new SimpleOrderedRules(selectedRules));
        engine.evaluate(facts);
      } else {
        log.debug("No candidate rules found using: {}", facts);
      }
    } while (!selectedRules.isEmpty());
  }

  private Set<Rule> selectCandidates(final Facts facts) {
    final var candidates = new TreeSet<Rule>();
    for (final var rule : rules) {
      if (rule.accept(facts)) {
        candidates.add(rule);
      }
    }
    return candidates;
  }
}

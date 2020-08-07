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
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class DefaultRuleEngine implements RuleEngine {
  private final RuleEngineListener listener;
  private final Set<Rule> rules;

  @Override
  public void evaluate(final Facts facts) {
    log.debug("Rule engine evaluating: {}", facts);
    for (final var rule : rules) {
      if (listener.shouldStopBeforeEvaluation(rule, facts)) {
        log.debug("Stopping further rule evaluation before '{}' was executed", rule);
        break;
      }

      final boolean accepted = isRuleConditionAccepted(facts, rule);
      Exception exception = null;
      if (accepted) {
        log.debug("Executing rule '{}' action using: {}", rule, facts);
        exception = executeRule(facts, rule);
      } else {
        log.debug("Rule '{}' was not accepted by the condition using: {}", rule, facts);
      }

      if (listener.shouldStopAfterEvaluation(rule, facts, accepted, exception)) {
        log.debug("Stopping further rule evaluation after '{}' was executed", rule);
        break;
      }
    }
  }

  private boolean isRuleConditionAccepted(final Facts facts, final Rule rule) {
    try {
      listener.beforeCondition(rule, facts);
      final var accepted = rule.accept(facts);
      listener.afterCondition(rule, facts, accepted);
      return accepted;
    } catch (final Exception e) {
      log.error(String.format("Rule '%s' failed condition check using: %s", rule, facts), e);
      listener.onConditionError(rule, facts, e);
      return false;
    }
  }

  private Exception executeRule(final Facts facts, final Rule rule) {
    try {
      listener.beforeAction(rule, facts);
      rule.execute(facts);
      listener.afterAction(rule, facts);
    } catch (final Exception e) {
      log.error(String.format("Rule '%s' failed execution using: %s", rule, facts), e);
      listener.onActionError(rule, facts, e);
      return e;
    }
    return null;
  }
}

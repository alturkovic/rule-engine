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

package com.github.alturkovic.rule.engine.listener;

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.RuleEngineListener;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SpecificRuleListener implements RuleEngineListener {
  private final Rule rule;
  private final RuleEngineListener listener;

  @Override
  public boolean shouldStopBeforeEvaluation(final Rule rule, final Facts facts) {
    if (this.rule.equals(rule)) {
      return listener.shouldStopBeforeEvaluation(rule, facts);
    }
    return false;
  }

  @Override
  public void beforeCondition(final Rule rule, final Facts facts) {
    if (this.rule.equals(rule)) {
      listener.beforeCondition(rule, facts);
    }
  }

  @Override
  public void afterCondition(final Rule rule, final Facts facts, final boolean accepted) {
    if (this.rule.equals(rule)) {
      listener.afterCondition(rule, facts, accepted);
    }
  }

  @Override
  public void onConditionError(final Rule rule, final Facts facts, final Exception e) {
    if (this.rule.equals(rule)) {
      listener.onConditionError(rule, facts, e);
    }
  }

  @Override
  public void beforeAction(final Rule rule, final Facts facts) {
    if (this.rule.equals(rule)) {
      listener.beforeAction(rule, facts);
    }
  }

  @Override
  public void afterAction(final Rule rule, final Facts facts) {
    if (this.rule.equals(rule)) {
      listener.afterAction(rule, facts);
    }
  }

  @Override
  public void onActionError(final Rule rule, final Facts facts, final Exception e) {
    if (this.rule.equals(rule)) {
      listener.onActionError(rule, facts, e);
    }
  }

  @Override
  public boolean shouldStopAfterEvaluation(final Rule rule, final Facts facts, final boolean accepted, final Exception e) {
    if (this.rule.equals(rule)) {
      return listener.shouldStopAfterEvaluation(rule, facts, accepted, e);
    }
    return false;
  }
}

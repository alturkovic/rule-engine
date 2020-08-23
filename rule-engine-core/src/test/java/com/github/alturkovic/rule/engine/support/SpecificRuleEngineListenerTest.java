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

package com.github.alturkovic.rule.engine.support;

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.RuleEngineListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecificRuleEngineListenerTest {

  @Mock
  private Rule rule;

  @Mock
  private Rule anotherRule;

  @Mock
  private Facts facts;

  @Mock
  private RuleEngineListener listener;

  private SpecificRuleEngineListener specificRuleEngineListener;

  @BeforeEach
  public void setup() {
    specificRuleEngineListener = new SpecificRuleEngineListener(rule, listener);
  }

  @Test
  void shouldStopBeforeEvaluationOnSpecificRule() {
    when(listener.shouldStopBeforeEvaluation(rule, facts)).thenReturn(true);

    assertThat(specificRuleEngineListener.shouldStopBeforeEvaluation(rule, facts)).isTrue();
    assertThat(specificRuleEngineListener.shouldStopBeforeEvaluation(anotherRule, facts)).isFalse();
  }

  @Test
  void shouldCallBeforeConditionOnSpecificRule() {
    specificRuleEngineListener.beforeCondition(rule, facts);
    specificRuleEngineListener.beforeCondition(anotherRule, facts);

    verify(listener, times(1)).beforeCondition(any(), eq(facts));
  }

  @Test
  void shouldCallAfterConditionOnSpecificRule() {
    specificRuleEngineListener.afterCondition(rule, facts, true);
    specificRuleEngineListener.afterCondition(anotherRule, facts, true);

    verify(listener, times(1)).afterCondition(any(), eq(facts), eq(true));
  }

  @Test
  void shouldCallOnConditionErrorOnSpecificRule() {
    final var exception = new IllegalStateException();
    specificRuleEngineListener.onConditionError(rule, facts, exception);
    specificRuleEngineListener.onConditionError(anotherRule, facts, exception);

    verify(listener, times(1)).onConditionError(any(), eq(facts), eq(exception));
  }

  @Test
  void shouldCallBeforeActionOnSpecificRule() {
    specificRuleEngineListener.beforeAction(rule, facts);
    specificRuleEngineListener.beforeAction(anotherRule, facts);

    verify(listener, times(1)).beforeAction(any(), eq(facts));
  }

  @Test
  void shouldCallAfterActionOnSpecificRule() {
    specificRuleEngineListener.afterAction(rule, facts);
    specificRuleEngineListener.afterAction(anotherRule, facts);

    verify(listener, times(1)).afterAction(any(), eq(facts));
  }

  @Test
  void shouldCallOnActionErrorOnSpecificRule() {
    final var exception = new IllegalStateException();
    specificRuleEngineListener.onActionError(rule, facts, exception);
    specificRuleEngineListener.onActionError(anotherRule, facts, exception);

    verify(listener, times(1)).onActionError(any(), eq(facts), eq(exception));
  }

  @Test
  void shouldStopAfterEvaluationOnSpecificRule() {
    when(listener.shouldStopAfterEvaluation(eq(rule), eq(facts), eq(true), isNull())).thenReturn(true);

    assertThat(specificRuleEngineListener.shouldStopAfterEvaluation(rule, facts, true, null)).isTrue();
    assertThat(specificRuleEngineListener.shouldStopAfterEvaluation(anotherRule, facts, true, null)).isFalse();
  }
}
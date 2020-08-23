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
class SpecificRuleListenerTest {

  @Mock
  private Rule rule;

  @Mock
  private Rule anotherRule;

  @Mock
  private Facts facts;

  @Mock
  private RuleEngineListener listener;

  private SpecificRuleListener specificRuleListener;

  @BeforeEach
  public void setup() {
    specificRuleListener = new SpecificRuleListener(rule, listener);
  }

  @Test
  void shouldStopBeforeEvaluationOnSpecificRule() {
    when(listener.shouldStopBeforeEvaluation(rule, facts)).thenReturn(true);

    assertThat(specificRuleListener.shouldStopBeforeEvaluation(rule, facts)).isTrue();
    assertThat(specificRuleListener.shouldStopBeforeEvaluation(anotherRule, facts)).isFalse();
  }

  @Test
  void shouldCallBeforeConditionOnSpecificRule() {
    specificRuleListener.beforeCondition(rule, facts);
    specificRuleListener.beforeCondition(anotherRule, facts);

    verify(listener, times(1)).beforeCondition(any(), eq(facts));
  }

  @Test
  void shouldCallAfterConditionOnSpecificRule() {
    specificRuleListener.afterCondition(rule, facts, true);
    specificRuleListener.afterCondition(anotherRule, facts, true);

    verify(listener, times(1)).afterCondition(any(), eq(facts), eq(true));
  }

  @Test
  void shouldCallOnConditionErrorOnSpecificRule() {
    final var exception = new IllegalStateException();
    specificRuleListener.onConditionError(rule, facts, exception);
    specificRuleListener.onConditionError(anotherRule, facts, exception);

    verify(listener, times(1)).onConditionError(any(), eq(facts), eq(exception));
  }

  @Test
  void shouldCallBeforeActionOnSpecificRule() {
    specificRuleListener.beforeAction(rule, facts);
    specificRuleListener.beforeAction(anotherRule, facts);

    verify(listener, times(1)).beforeAction(any(), eq(facts));
  }

  @Test
  void shouldCallAfterActionOnSpecificRule() {
    specificRuleListener.afterAction(rule, facts);
    specificRuleListener.afterAction(anotherRule, facts);

    verify(listener, times(1)).afterAction(any(), eq(facts));
  }

  @Test
  void shouldCallOnActionErrorOnSpecificRule() {
    final var exception = new IllegalStateException();
    specificRuleListener.onActionError(rule, facts, exception);
    specificRuleListener.onActionError(anotherRule, facts, exception);

    verify(listener, times(1)).onActionError(any(), eq(facts), eq(exception));
  }

  @Test
  void shouldStopAfterEvaluationOnSpecificRule() {
    when(listener.shouldStopAfterEvaluation(eq(rule), eq(facts), eq(true), isNull())).thenReturn(true);

    assertThat(specificRuleListener.shouldStopAfterEvaluation(rule, facts, true, null)).isTrue();
    assertThat(specificRuleListener.shouldStopAfterEvaluation(anotherRule, facts, true, null)).isFalse();
  }
}
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompositeRuleEngineListenerTest {

  @Mock
  private Rule rule;

  @Mock
  private Facts facts;

  @Mock
  private RuleEngineListener listener1, listener2;

  private CompositeRuleEngineListener compositeRuleEngineListener;

  @BeforeEach
  public void setup() {
    compositeRuleEngineListener = new CompositeRuleEngineListener(List.of(listener1, listener2));
  }

  @Test
  void shouldStopBeforeEvaluationOnSpecificRuleWhenAllReturnTrue() {
    when(listener1.shouldStopBeforeEvaluation(rule, facts)).thenReturn(true);
    when(listener2.shouldStopBeforeEvaluation(rule, facts)).thenReturn(true);

    assertThat(compositeRuleEngineListener.shouldStopBeforeEvaluation(rule, facts)).isTrue();
  }

  @Test
  void shouldNotStopBeforeEvaluationOnSpecificRuleWhenAnyReturnsFalse() {
    when(listener1.shouldStopBeforeEvaluation(rule, facts)).thenReturn(true);
    when(listener2.shouldStopBeforeEvaluation(rule, facts)).thenReturn(false);

    assertThat(compositeRuleEngineListener.shouldStopBeforeEvaluation(rule, facts)).isFalse();
  }

  @Test
  void shouldCallBeforeConditionOnSpecificRule() {
    compositeRuleEngineListener.beforeCondition(rule, facts);

    verify(listener1).beforeCondition(eq(rule), eq(facts));
    verify(listener2).beforeCondition(eq(rule), eq(facts));
  }

  @Test
  void shouldCallAfterConditionOnSpecificRule() {
    compositeRuleEngineListener.afterCondition(rule, facts, true);

    verify(listener1).afterCondition(eq(rule), eq(facts), eq(true));
    verify(listener2).afterCondition(eq(rule), eq(facts), eq(true));
  }

  @Test
  void shouldCallOnConditionErrorOnSpecificRule() {
    final var exception = new IllegalStateException();
    compositeRuleEngineListener.onConditionError(rule, facts, exception);

    verify(listener1).onConditionError(eq(rule), eq(facts), eq(exception));
    verify(listener2).onConditionError(eq(rule), eq(facts), eq(exception));
  }

  @Test
  void shouldCallBeforeActionOnSpecificRule() {
    compositeRuleEngineListener.beforeAction(rule, facts);

    verify(listener1).beforeAction(eq(rule), eq(facts));
    verify(listener2).beforeAction(eq(rule), eq(facts));
  }

  @Test
  void shouldCallAfterActionOnSpecificRule() {
    compositeRuleEngineListener.afterAction(rule, facts);

    verify(listener1).afterAction(eq(rule), eq(facts));
  }

  @Test
  void shouldCallOnActionErrorOnSpecificRule() {
    final var exception = new IllegalStateException();
    compositeRuleEngineListener.onActionError(rule, facts, exception);

    verify(listener1).onActionError(eq(rule), eq(facts), eq(exception));
    verify(listener2).onActionError(eq(rule), eq(facts), eq(exception));
  }

  @Test
  void shouldStopAfterEvaluationOnSpecificRuleWhenAllReturnTrue() {
    when(listener1.shouldStopAfterEvaluation(eq(rule), eq(facts), eq(true), isNull())).thenReturn(true);
    when(listener2.shouldStopAfterEvaluation(eq(rule), eq(facts), eq(true), isNull())).thenReturn(true);

    assertThat(compositeRuleEngineListener.shouldStopAfterEvaluation(rule, facts, true, null)).isTrue();
  }

  @Test
  void shouldNotStopAfterEvaluationOnSpecificRuleWhenAnyReturnsFalse() {
    when(listener1.shouldStopAfterEvaluation(eq(rule), eq(facts), eq(true), isNull())).thenReturn(true);
    when(listener2.shouldStopAfterEvaluation(eq(rule), eq(facts), eq(true), isNull())).thenReturn(false);

    assertThat(compositeRuleEngineListener.shouldStopAfterEvaluation(rule, facts, true, null)).isFalse();
  }
}
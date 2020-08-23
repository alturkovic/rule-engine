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

import com.github.alturkovic.rule.engine.BaseTest;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultRuleEngineTest extends BaseTest {

  @Test
  void shouldExecuteAcceptedRules() {
    when(rule1.accept(facts)).thenReturn(true);
    when(rule2.accept(facts)).thenReturn(false);

    engine.evaluate(facts);

    verify(rule1).execute(facts);
    verify(rule2, never()).execute(facts);
  }

  @Test
  void shouldStopFurtherEvaluationBefore() {
    when(listener.shouldStopBeforeEvaluation(rule1, facts)).thenReturn(false);
    when(listener.shouldStopBeforeEvaluation(rule2, facts)).thenReturn(true);

    engine.evaluate(facts);

    verify(rule1).accept(facts);
    verify(rule2, never()).accept(facts);
  }

  @Test
  void shouldListenBeforeCondition() {
    engine.evaluate(facts);

    final var inOrder = inOrder(rule1, rule2, listener);
    inOrder.verify(listener).beforeCondition(rule1, facts);
    inOrder.verify(rule1).accept(facts);
    inOrder.verify(listener).beforeCondition(rule2, facts);
    inOrder.verify(rule2).accept(facts);
  }

  @Test
  void shouldListenAfterCondition() {
    when(rule1.accept(facts)).thenReturn(true);
    when(rule2.accept(facts)).thenReturn(false);

    engine.evaluate(facts);

    final var inOrder = inOrder(rule1, rule2, listener);
    inOrder.verify(rule1).accept(facts);
    inOrder.verify(listener).afterCondition(rule1, facts, true);
    inOrder.verify(rule2).accept(facts);
    inOrder.verify(listener).afterCondition(rule2, facts, false);
  }

  @Test
  void shouldListenOnConditionError() {
    final var exception = new IllegalStateException();
    when(rule2.accept(facts)).thenThrow(exception);

    engine.evaluate(facts);

    final var inOrder = inOrder(rule1, rule2, listener);
    inOrder.verify(rule1).accept(facts);
    inOrder.verify(rule2).accept(facts);
    listener.onConditionError(rule2, facts, exception);
  }

  @Test
  void shouldListenBeforeAction() {
    when(rule1.accept(facts)).thenReturn(true);
    when(rule2.accept(facts)).thenReturn(true);

    engine.evaluate(facts);

    final var inOrder = inOrder(rule1, rule2, listener);
    inOrder.verify(listener).beforeAction(rule1, facts);
    inOrder.verify(rule1).execute(facts);
    inOrder.verify(listener).beforeAction(rule2, facts);
    inOrder.verify(rule2).execute(facts);
  }

  @Test
  void shouldListenAfterAction() {
    when(rule1.accept(facts)).thenReturn(true);
    when(rule2.accept(facts)).thenReturn(true);

    engine.evaluate(facts);

    final var inOrder = inOrder(rule1, rule2, listener);
    inOrder.verify(rule1).execute(facts);
    inOrder.verify(listener).afterAction(rule1, facts);
    inOrder.verify(rule2).execute(facts);
    inOrder.verify(listener).afterAction(rule2, facts);
  }

  @Test
  void shouldListenOnActionError() {
    final var exception = new IllegalStateException();
    when(rule1.accept(facts)).thenReturn(true);
    when(rule2.accept(facts)).thenReturn(true);
    doThrow(exception).when(rule2).execute(facts);

    engine.evaluate(facts);

    final var inOrder = inOrder(rule1, rule2, listener);
    inOrder.verify(rule1).execute(facts);
    inOrder.verify(rule2).execute(facts);
    listener.onActionError(rule2, facts, exception);
  }

  @Test
  void shouldStopFurtherEvaluationAfter() {
    when(listener.shouldStopAfterEvaluation(eq(rule1), eq(facts), anyBoolean(), any())).thenReturn(true);

    engine.evaluate(facts);

    verify(rule1).accept(facts);
    verify(rule2, never()).accept(facts);
  }
}
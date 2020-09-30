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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriorityThresholdListenerTest {

  private final RuleEngineListener priority2ThresholdListener = new PriorityThresholdListener(2);

  @Mock
  private Rule rule;

  @Mock
  private Facts facts;

  @Test
  void shouldNotStopBeforeEvaluationWhenPriorityIsLower() {
    when(rule.getPriority()).thenReturn(1);
    assertThat(priority2ThresholdListener.shouldStopBeforeEvaluation(rule, facts)).isFalse();
  }

  @Test
  void shouldNotStopBeforeEvaluationWhenPriorityIsSame() {
    when(rule.getPriority()).thenReturn(2);
    assertThat(priority2ThresholdListener.shouldStopBeforeEvaluation(rule, facts)).isFalse();
  }

  @Test
  void shouldStopBeforeEvaluationWhenPriorityIsHigher() {
    when(rule.getPriority()).thenReturn(3);
    assertThat(priority2ThresholdListener.shouldStopBeforeEvaluation(rule, facts)).isTrue();
  }
}
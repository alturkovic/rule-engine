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

@ExtendWith(MockitoExtension.class)
class SkipAfterFailedRuleListenerTest {

  private final RuleEngineListener skipAfterFailedRuleListener = new SkipAfterFailedRuleListener();

  @Mock
  private Rule rule;

  @Mock
  private Facts facts;

  @Test
  void shouldStopAfterEvaluationWhenRuleHasFailed() {
    assertThat(skipAfterFailedRuleListener.shouldStopAfterEvaluation(rule, facts, true, new RuntimeException())).isTrue();
    assertThat(skipAfterFailedRuleListener.shouldStopAfterEvaluation(rule, facts, false, new RuntimeException())).isTrue();
  }

  @Test
  void shouldNotStopAfterEvaluationWhenRuleHasExecuted() {
    assertThat(skipAfterFailedRuleListener.shouldStopAfterEvaluation(rule, facts, true, null)).isFalse();
    assertThat(skipAfterFailedRuleListener.shouldStopAfterEvaluation(rule, facts, false, null)).isFalse();
  }
}
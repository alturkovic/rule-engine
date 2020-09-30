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
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InferenceRuleEngineTest {
  private final int WANTED_TEMPERATURE = 24;

  @Mock
  protected Rule incrementByTwoTemperatureRule, decrementByOneTemperatureRule;

  @Mock
  protected Facts facts;

  protected RuleEngine engine;

  private int currentTemperature;

  @BeforeEach
  public void setup() {
    currentTemperature = 0;

    final var rules = new TreeSet<Rule>();
    rules.add(incrementByTwoTemperatureRule);
    rules.add(decrementByOneTemperatureRule);

    mockIncrementRule();
    mockDecrementRule();

    engine = new InferenceRuleEngine(RuleEngineListener.NO_OP, new SimpleOrderedRules(rules));
  }

  private void mockIncrementRule() {
    lenient().when(incrementByTwoTemperatureRule.accept(facts)).then(invocation -> currentTemperature < WANTED_TEMPERATURE);

    lenient().doAnswer(invocation -> {
      currentTemperature += 2;
      return null;
    }).when(incrementByTwoTemperatureRule).execute(facts);
  }

  private void mockDecrementRule() {
    lenient().when(decrementByOneTemperatureRule.accept(facts)).then(invocation -> currentTemperature > WANTED_TEMPERATURE);

    lenient().doAnswer(invocation -> {
      currentTemperature--;
      return null;
    }).when(decrementByOneTemperatureRule).execute(facts);
  }

  @Test
  void shouldKeepCallingValidCandidate() {
    currentTemperature = 21;

    engine.evaluate(facts);

    assertThat(currentTemperature).isEqualTo(WANTED_TEMPERATURE);
    final var inOrder = inOrder(incrementByTwoTemperatureRule, decrementByOneTemperatureRule);
    inOrder.verify(incrementByTwoTemperatureRule, times(2)).execute(facts);
    inOrder.verify(decrementByOneTemperatureRule, times(1)).execute(facts);
  }

  @Test
  void shouldNotCallAnyRulesWithoutValidCandidates() {
    currentTemperature = WANTED_TEMPERATURE;

    engine.evaluate(facts);

    assertThat(currentTemperature).isEqualTo(WANTED_TEMPERATURE);
    verify(incrementByTwoTemperatureRule, never()).execute(facts);
    verify(decrementByOneTemperatureRule, never()).execute(facts);
  }
}
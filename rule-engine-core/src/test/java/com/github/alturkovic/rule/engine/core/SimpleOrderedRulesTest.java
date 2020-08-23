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

import com.github.alturkovic.rule.engine.api.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleOrderedRulesTest {

  @Mock
  private Rule rule1, rule2, rule3;

  @Test
  void shouldOrderByPriority() {
    doNotMockCompareMethods();

    when(rule1.getPriority()).thenReturn(1);
    when(rule2.getPriority()).thenReturn(2);
    when(rule3.getPriority()).thenReturn(3);

    assertRuleOrder();
  }

  @Test
  void shouldOrderByPriorityUsingExtremes() {
    doNotMockCompareMethods();

    when(rule1.getPriority()).thenReturn(Integer.MIN_VALUE);
    when(rule3.getPriority()).thenReturn(Integer.MAX_VALUE);

    assertRuleOrder();
  }

  @Test
  void shouldOrderByNameWhenPriorityIsSame() {
    doNotMockCompareMethods();

    when(rule1.getPriority()).thenReturn(1);
    when(rule2.getPriority()).thenReturn(2);
    when(rule3.getPriority()).thenReturn(2);

    when(rule2.getName()).thenReturn("Test rule 2");
    when(rule3.getName()).thenReturn("Test rule 3");

    assertRuleOrder();
  }

  private void doNotMockCompareMethods() {
    when(rule1.compareTo(any())).thenCallRealMethod();
    when(rule2.compareTo(any())).thenCallRealMethod();
    when(rule3.compareTo(any())).thenCallRealMethod();
  }

  private void assertRuleOrder() {
    final var rules = new SimpleOrderedRules(rule3, rule1, rule2);
    assertThat(rules).containsExactly(rule1, rule2, rule3);
  }
}
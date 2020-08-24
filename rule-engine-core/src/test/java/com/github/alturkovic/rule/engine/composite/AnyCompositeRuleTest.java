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

package com.github.alturkovic.rule.engine.composite;

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.core.SimpleOrderedRules;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnyCompositeRuleTest {
  private AnyCompositeRule anyCompositeRule;

  @Mock
  private Rule rule1, rule2;

  @Mock
  private Facts facts;

  @BeforeEach
  public void setupCompositeRule() {
    final var rules = new TreeSet<Rule>();
    rules.add(rule1);
    rules.add(rule2);

    this.anyCompositeRule = AnyCompositeRule.builder()
        .rules(new SimpleOrderedRules(rules))
        .build();
  }

  @Test
  void shouldAcceptWhenAnyAccepts() {
    when(rule1.accept(facts)).thenReturn(false);
    when(rule2.accept(facts)).thenReturn(true);

    assertThat(anyCompositeRule.accept(facts)).isTrue();
  }

  @Test
  void shouldNotAcceptWhenAllDecline() {
    when(rule1.accept(eq(facts))).thenReturn(false);
    when(rule2.accept(eq(facts))).thenReturn(false);

    assertThat(anyCompositeRule.accept(facts)).isFalse();
  }

  @Test
  void shouldExecuteLastAcceptedRule() {
    when(rule1.accept(facts)).thenReturn(false);
    when(rule2.accept(facts)).thenReturn(true);

    anyCompositeRule.accept(facts);
    anyCompositeRule.execute(facts);

    verify(rule1, never()).execute(facts);
    verify(rule2).execute(facts);
  }

  @Test
  void shouldNotExecuteIfThereWasNoAcceptedRule() {
    anyCompositeRule.execute(facts);

    verify(rule1, never()).execute(facts);
    verify(rule2, never()).execute(facts);
  }

  @Test
  void shouldNotExecuteLastAcceptedRuleMultipleTimes() {
    when(rule1.accept(facts)).thenReturn(false);
    when(rule2.accept(facts)).thenReturn(true);

    anyCompositeRule.accept(facts);
    anyCompositeRule.execute(facts);
    anyCompositeRule.execute(facts);

    verify(rule1, never()).execute(facts);
    verify(rule2, times(1)).execute(facts);
  }
}
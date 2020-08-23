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

import com.github.alturkovic.rule.engine.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AllCompositeRuleTest extends BaseTest {
  private AllCompositeRule allCompositeRule;

  @BeforeEach
  public void setupCompositeRule() {
    this.allCompositeRule = AllCompositeRule.builder()
        .rules(rules)
        .build();
  }

  @Test
  void shouldAcceptWhenAllAccept() {
    when(rule1.accept(facts)).thenReturn(true);
    when(rule2.accept(facts)).thenReturn(true);

    assertThat(allCompositeRule.accept(facts)).isTrue();
  }

  @Test
  void shouldNotAcceptWhenAnyDeclines() {
    when(rule1.accept(facts)).thenReturn(true);
    when(rule2.accept(facts)).thenReturn(false);

    assertThat(allCompositeRule.accept(facts)).isFalse();
  }

  @Test
  void shouldExecuteAllRules() {
    allCompositeRule.execute(facts);

    verify(rule1).execute(facts);
    verify(rule2).execute(facts);
  }
}
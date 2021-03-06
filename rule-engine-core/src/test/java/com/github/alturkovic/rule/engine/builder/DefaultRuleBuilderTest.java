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

package com.github.alturkovic.rule.engine.builder;

import com.github.alturkovic.rule.engine.api.Action;
import com.github.alturkovic.rule.engine.api.Condition;
import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.core.DefaultRule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class DefaultRuleBuilderTest {

  @Test
  void shouldBuild() {
    final var name = "Rule";
    final var description = "Description";
    final var priority = 1;

    final var facts = mock(Facts.class);
    final var when = mock(Condition.class);
    final var then1 = mock(Action.class);
    final var then2 = mock(Action.class);

    final var rule = new DefaultRuleBuilder(name)
        .description(description)
        .priority(priority)
        .when(when)
        .then(then1)
        .then(then2)
        .build();

    assertThat(rule).isInstanceOf(DefaultRule.class);
    assertThat(rule.getName()).isEqualTo(name);
    assertThat(rule.getDescription()).isEqualTo(description);
    assertThat(rule.getPriority()).isEqualTo(priority);

    rule.accept(facts);
    rule.execute(facts);

    final var inOrder = inOrder(when, then1, then2);

    inOrder.verify(when).accept(facts);
    inOrder.verify(then1).execute(facts);
    inOrder.verify(then2).execute(facts);
  }
}
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

package com.github.alturkovic.rule.engine.mvel;

import com.github.alturkovic.rule.engine.api.Facts;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.PropertyAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MVELConditionTest {

  @Mock
  private Facts facts;

  @Test
  void shouldAcceptSimpleCondition() {
    final var adultCondition = new MVELCondition(MVEL.compileExpression("age > 18"));
    when(facts.asMap()).thenReturn(Map.of("age", 20));
    assertThat(adultCondition.accept(facts)).isTrue();
  }

  @Test
  void shouldAcceptWithCustomContext() {
    final ParserContext context = new ParserContext();
    context.addPackageImport("java.util");

    final var randomCondition = new MVELCondition(MVEL.compileExpression("return new java.util.Random(1).nextBoolean();", context));
    assertThat(randomCondition.accept(facts)).isTrue();
  }

  @Test
  void shouldFailWithInvalidMVEL() {
    final var adultCondition = new MVELCondition(MVEL.compileExpression("age > 18"));

    assertThatThrownBy(() -> adultCondition.accept(facts))
        .isInstanceOf(PropertyAccessException.class);
  }
}
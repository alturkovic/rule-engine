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

package com.github.alturkovic.rule.engine.spel;

import com.github.alturkovic.rule.engine.api.Facts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelEvaluationException;

import static com.github.alturkovic.rule.engine.spel.util.SpELUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SpELActionTest {

  public static String value;

  @Mock
  private Facts facts;

  @AfterEach
  public void cleanup() {
    value = null;
  }

  @Test
  void shouldExecuteSimpleAction() {
    final var fooAction = new SpELAction(parse("#{T(com.github.alturkovic.rule.engine.spel.SpELActionTest).value = 'foo'}"));
    fooAction.execute(facts);
    assertThat(value).isEqualTo("foo");
  }

  @Test
  void shouldExecuteWithCustomContext() {
    final ParserContext context = new TemplateParserContext("%{", "}");

    final var fooAction = new SpELAction(parse("%{T(com.github.alturkovic.rule.engine.spel.SpELActionTest).value = 'foo'}", context));
    fooAction.execute(facts);
    assertThat(value).isEqualTo("foo");
  }

  @Test
  void shouldFailWithInvalidSpEL() {
    final var action = new SpELAction(parse("#{T(com.github.alturkovic.rule.engine.spel.SpELActionTest).foo = 'foo'}"));

    assertThatThrownBy(() -> action.execute(facts))
        .isInstanceOf(SpelEvaluationException.class);
  }
}
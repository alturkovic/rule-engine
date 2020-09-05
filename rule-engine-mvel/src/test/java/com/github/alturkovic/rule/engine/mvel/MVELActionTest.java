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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.PropertyAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MVELActionTest {

  @Mock
  private Facts facts;

  @AfterEach
  public void cleanup() {
    System.clearProperty("foo");
  }

  @Test
  public void shouldExecuteSimpleAction() {
    final var fooAction = new MVELAction(MVEL.compileExpression("System.setProperty(\"foo\", \"1\")"));
    fooAction.execute(facts);
    assertThat(System.getProperty("foo")).isEqualTo("1");
  }

  @Test
  public void shouldExecuteDefinedAction() {
    final var fooAction = new MVELAction(MVEL.compileExpression("def setProperty() { System.setProperty(\"foo\", \"1\") }; setProperty();"));
    fooAction.execute(facts);
    assertThat(System.getProperty("foo")).isEqualTo("1");
  }

  @Test
  public void shouldExecuteWithCustomContext() {
    final ParserContext context = new ParserContext();
    context.addPackageImport("java.util");

    final var randomAction = new MVELAction(MVEL.compileExpression("def random() { System.setProperty(\"foo\", new java.util.Random(1).nextInt(2)); }; random();", context));
    randomAction.execute(facts);
    assertThat(System.getProperty("foo")).isEqualTo("1");
  }

  @Test
  public void shouldFailWithInvalidMVEL() {
    final var action = new MVELAction(MVEL.compileExpression("unknown()"));

    assertThatThrownBy(() -> action.execute(facts))
        .isInstanceOf(PropertyAccessException.class);
  }
}
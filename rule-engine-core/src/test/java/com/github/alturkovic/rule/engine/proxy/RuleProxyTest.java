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

package com.github.alturkovic.rule.engine.proxy;

import com.github.alturkovic.rule.engine.aop.Rule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleProxyTest {

  @Test
  public void shouldUseDefinedValues() {
    final var NAME = "My name";
    final var DESCRIPTION = "My description";
    final var PRIORITY = 7;

    @Rule(name = NAME, description = DESCRIPTION, priority = PRIORITY)
    class DefinedProxy extends BaseProxy {
    }

    final var rule = RuleProxy.asRule(new DefinedProxy());
    assertThat(rule.getName()).isEqualTo(NAME);
    assertThat(rule.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(rule.getPriority()).isEqualTo(PRIORITY);
  }

  @Test
  public void shouldUseGeneratedDefaultsWhenUndefined() {

    @Rule
    class UndefinedProxy extends BaseProxy {
    }

    final var rule = RuleProxy.asRule(new UndefinedProxy());
    final var className = UndefinedProxy.class.getSimpleName();

    assertThat(rule.getName()).isEqualTo(className);
    assertThat(rule.getDescription()).isNotBlank();
    assertThat(rule.getPriority()).isEqualTo(com.github.alturkovic.rule.engine.api.Rule.DEFAULT_PRIORITY);
  }

  @Test
  public void shouldNotGenerateRuleForPojo() {
    assertThrows(IllegalArgumentException.class, () -> RuleProxy.asRule(new Object()));
  }
}
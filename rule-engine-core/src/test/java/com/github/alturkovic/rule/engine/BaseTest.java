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

package com.github.alturkovic.rule.engine;

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.RuleEngineListener;
import com.github.alturkovic.rule.engine.api.Rules;
import com.github.alturkovic.rule.engine.core.DefaultRuleEngine;
import com.github.alturkovic.rule.engine.core.SimpleOrderedRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseTest {

  @Mock
  protected Rule rule1, rule2;

  @Mock
  protected Facts facts;

  @Mock
  protected RuleEngineListener listener;

  protected DefaultRuleEngine engine;

  protected Rules rules;

  @BeforeEach
  public void setup() {
    rules = new SimpleOrderedRules();
    rules.register(rule1);
    rules.register(rule2);

    engine = new DefaultRuleEngine(listener, rules);
  }
}

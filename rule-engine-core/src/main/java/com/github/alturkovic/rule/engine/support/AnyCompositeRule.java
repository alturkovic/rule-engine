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

package com.github.alturkovic.rule.engine.support;

import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.Rules;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnyCompositeRule extends CompositeRule {
  private static final ThreadLocal<Rule> LAST_ACCEPTED_RULE = new ThreadLocal<>();

  @Builder
  public AnyCompositeRule(final String name, final String description, final int priority, final Rules rules) {
    super(name, description, priority, rules);
  }

  @Override
  public boolean accept(final Facts facts) {
    for (final Rule rule : rules) {
      if (rule.accept(facts)) {
        LAST_ACCEPTED_RULE.set(rule);
        return true;
      }
    }
    return false;
  }

  @Override
  public void execute(final Facts facts) {
    final var rule = LAST_ACCEPTED_RULE.get();
    if (rule != null) {
      LAST_ACCEPTED_RULE.remove();
      rule.execute(facts);
    }
  }
}

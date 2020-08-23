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

import com.github.alturkovic.rule.engine.aop.Given;
import com.github.alturkovic.rule.engine.aop.Rule;
import com.github.alturkovic.rule.engine.aop.Then;
import com.github.alturkovic.rule.engine.aop.When;
import com.github.alturkovic.rule.engine.api.Facts;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleProxyTest {

  @Test
  void shouldNotCreateProxyWithoutRuleAnnotation() {
    assertThrows(IllegalArgumentException.class, () -> RuleProxy.asRule(new Object()));
  }

  @Test
  void shouldNotCreateProxyWithoutWellMethod() {

    @Rule
    class NoWhenProxy {
      @Then
      public void then() {
      }
    }

    assertThrows(IllegalArgumentException.class, () -> RuleProxy.asRule(new NoWhenProxy()));
  }

  @Test
  void shouldNotCreateProxyWithWhenMethodThatDoesNotReturnBoolean() {

    @Rule
    class InvalidReturnWhenProxy {
      @When
      public void when() {
      }

      @Then
      public void then() {
      }
    }

    assertThrows(IllegalArgumentException.class, () -> RuleProxy.asRule(new InvalidReturnWhenProxy()));
  }

  @Test
  void shouldNotCreateProxyWithWhenMethodThatExpectsNonFactArguments() {

    @Rule
    class InvalidArgumentsWhenProxy {
      @When
      public boolean when(final Object o) {
        return true;
      }

      @Then
      public void then() {
      }
    }

    assertThrows(IllegalArgumentException.class, () -> RuleProxy.asRule(new InvalidArgumentsWhenProxy()));
  }

  @Test
  void shouldNotCreateProxyWithoutThenMethod() {

    @Rule
    class UnvalidatedProxy {
      @When
      public boolean when() {
        return true;
      }
    }

    assertThrows(IllegalArgumentException.class, () -> RuleProxy.asRule(new UnvalidatedProxy()));
  }

  @Test
  void shouldNotCreateProxyWithThenMethodThatExpectsNonFactArguments() {

    @Rule
    class InvalidArgumentsThenProxy {
      @When
      public boolean when() {
        return true;
      }

      @Then
      public void then(final Object o) {
      }
    }

    assertThrows(IllegalArgumentException.class, () -> RuleProxy.asRule(new InvalidArgumentsThenProxy()));
  }

  @Test
  void shouldUseDefinedValues() {
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
  void shouldUseGeneratedDefaultsWhenUndefined() {

    @Rule
    class UndefinedProxy {
      @When
      public boolean shouldRun() {
        return true;
      }

      @Then(1)
      public void runFirst() {
      }

      @Then(2)
      public void runSecond() {
      }
    }

    final var rule = RuleProxy.asRule(new UndefinedProxy());
    assertThat(rule.getName()).isEqualTo(UndefinedProxy.class.getSimpleName());
    assertThat(rule.getDescription()).isEqualTo("When shouldRun then runFirst, runSecond");
    assertThat(rule.getPriority()).isEqualTo(com.github.alturkovic.rule.engine.api.Rule.DEFAULT_PRIORITY);
  }

  @Test
  void shouldProxyToString() {
    final var toString = "Custom toString";

    @Rule
    class ToStringProxy extends BaseProxy {
      @Override
      public String toString() {
        return toString;
      }
    }

    final var rule = RuleProxy.asRule(new ToStringProxy());
    assertThat(rule).hasToString(toString);
  }

  @Test
  void shouldProxyCompareTo() {

    @Rule
    @AllArgsConstructor
    class CompareToProxy extends BaseProxy implements Comparable<CompareToProxy> {
      private final int priority;

      @Override
      public int compareTo(final CompareToProxy other) {
        return Integer.compare(priority, other.priority);
      }
    }

    final var rule1 = RuleProxy.asRule(new CompareToProxy(1));
    final var rule2 = RuleProxy.asRule(new CompareToProxy(2));
    final var rule3 = RuleProxy.asRule(new CompareToProxy(2));

    assertThat(rule1.compareTo(rule2)).isEqualTo(-1);
    assertThat(rule2.compareTo(rule1)).isEqualTo(1);
    assertThat(rule2.compareTo(rule3)).isEqualTo(0);
  }

  @Test
  void shouldCompareToRule() {
    final var name = "Rule name";
    final var priority = 7;

    @Rule(name = name, priority = priority)
    class CompareToProxy extends BaseProxy {
    }

    final var rule1 = RuleProxy.asRule(new CompareToProxy());
    final var rule2 = mock(com.github.alturkovic.rule.engine.api.Rule.class);
    when(rule2.getPriority()).thenReturn(8);
    final var rule3 = mock(com.github.alturkovic.rule.engine.api.Rule.class);
    when(rule3.getPriority()).thenReturn(priority);
    when(rule3.getName()).thenReturn(name);

    assertThat(rule1.compareTo(rule2)).isEqualTo(-1);
    assertThat(rule1.compareTo(rule3)).isEqualTo(0);
  }

  @Test
  void shouldProxyHashCode() {
    final var name = "HashCode rule";
    final var priority = 7;

    @Rule(name = name, priority = priority)
    class HashCodeProxy extends BaseProxy {
    }

    final var rule1 = RuleProxy.asRule(new HashCodeProxy());
    final var rule2 = RuleProxy.asRule(new HashCodeProxy());

    assertThat(rule1).hasSameHashCodeAs(rule1);
    assertThat(rule1).hasSameHashCodeAs(rule2);
  }

  @Test
  void shouldProxyEquals() {
    final var name = "Equals rule";
    final var priority = 7;

    @Rule(name = name, priority = priority)
    class EqualsProxy extends BaseProxy {
    }

    final var rule1 = RuleProxy.asRule(new EqualsProxy());

    final var rule2 = mock(com.github.alturkovic.rule.engine.api.Rule.class);
    when(rule2.getPriority()).thenReturn(5);
    assertThat(rule1).isNotEqualTo(rule2);

    final var rule3 = mock(com.github.alturkovic.rule.engine.api.Rule.class);
    when(rule3.getPriority()).thenReturn(priority);
    when(rule3.getName()).thenReturn(name);
    assertThat(rule1).isEqualTo(rule3);

    assertThat(rule1).isNotEqualTo(mock(Object.class));
  }

  @Test
  void shouldInjectFactsInProxiedMethods() {

    @Rule
    class InjectionProxy {
      private int fact1Value;
      private int fact2Value;
      private int fact3Value;
      private int fact4Value;

      @When
      public boolean accept(@Given("fact1") final int fact, final Facts facts) {
        fact1Value = fact;
        fact2Value = facts.get("fact2");
        return true;
      }

      @Then
      public void then1(@Given("fact3") final int fact) {
        fact3Value = fact;
      }

      @Then
      public void then2(final Facts facts) {
        fact4Value = facts.get("fact4");
      }
    }

    final var proxy = new InjectionProxy();
    final var rule = RuleProxy.asRule(proxy);
    final var facts = mock(Facts.class);
    when(facts.isDeclared(any())).thenReturn(true);
    when(facts.get("fact1")).thenReturn(1);
    when(facts.get("fact2")).thenReturn(2);
    when(facts.get("fact3")).thenReturn(3);
    when(facts.get("fact4")).thenReturn(4);

    if (rule.accept(facts)) {
      rule.execute(facts);
    }

    assertThat(proxy.fact1Value).isEqualTo(1);
    assertThat(proxy.fact2Value).isEqualTo(2);
    assertThat(proxy.fact3Value).isEqualTo(3);
    assertThat(proxy.fact4Value).isEqualTo(4);
  }

  @Test
  void shouldAccept() {

    @Rule
    class AcceptProxy {
      @When
      public boolean isHot(@Given("temp") final int temperature) {
        return temperature > 30;
      }

      @Then
      public void then() {
      }
    }

    final var rule = RuleProxy.asRule(new AcceptProxy());
    final var facts = mock(Facts.class);
    when(facts.isDeclared("temp")).thenReturn(true);
    when(facts.get("temp")).thenReturn(35);

    assertThat(rule.accept(facts)).isTrue();
  }

  @Test
  void shouldExecuteAllThenMethods() {

    @Rule
    class ExecuteProxy {
      private int fact1Value;
      private int fact2Value;

      @When
      public boolean accept() {
        return true;
      }

      @Then
      public void then1(@Given("fact1") final int fact) {
        fact1Value = fact;
      }

      @Then
      public void then2(@Given("fact2") final int fact) {
        fact2Value = fact;
      }
    }

    final var proxy = new ExecuteProxy();
    final var rule = RuleProxy.asRule(proxy);
    final var facts = mock(Facts.class);
    when(facts.isDeclared(any())).thenReturn(true);
    when(facts.get("fact1")).thenReturn(1);
    when(facts.get("fact2")).thenReturn(2);

    rule.execute(facts);

    assertThat(proxy.fact1Value).isEqualTo(1);
    assertThat(proxy.fact2Value).isEqualTo(2);
  }

  @Test
  void shouldNotAcceptIfWhenMethodFactIsUndeclared() {

    @Rule
    class UndeclaredWhenFactProxy {
      @When
      public boolean accept(@Given("undeclared") final Object o) {
        return true;
      }

      @Then
      public void run() {
      }
    }

    final var rule = RuleProxy.asRule(new UndeclaredWhenFactProxy());
    final var facts = mock(Facts.class);

    assertThat(rule.accept(facts)).isFalse();
  }

  @Test
  void shouldFailIfThenMethodFactIsUndeclared() {

    @Rule
    class UndeclaredThenFactProxy {
      @When
      public boolean accept() {
        return true;
      }

      @Then
      public void run(@Given("undeclared") final Object o) {
      }
    }

    final var rule = RuleProxy.asRule(new UndeclaredThenFactProxy());
    final var facts = mock(Facts.class);

    assertThrows(IllegalArgumentException.class, () -> rule.execute(facts));
  }
}
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
import com.github.alturkovic.rule.engine.api.Rule;
import com.github.alturkovic.rule.engine.api.Rules;
import com.github.alturkovic.rule.engine.composite.AllCompositeRule;
import com.github.alturkovic.rule.engine.composite.AnyCompositeRule;
import com.github.alturkovic.rule.engine.composite.CompositeRule;
import com.github.alturkovic.rule.engine.jackson.JacksonRuleDefinitionReader;
import com.github.alturkovic.rule.engine.reader.RulesFactory;
import java.util.Map;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MVELRulesFactoryTest {

  private final RulesFactory factory = new MVELRulesFactory(new JacksonRuleDefinitionReader());

  @AfterEach
  public void cleanup() {
    System.clearProperty("WeatherRuleResult");
  }

  private Rules loadRulesFromFile(final String file) {
    return factory.create(MVELRulesFactoryTest.class.getResourceAsStream(file));
  }

  private void assertWeatherRuleIsValid(final Rule weatherRule) {
    assertThat(weatherRule.getName()).isEqualTo("Weather rule");
    assertThat(weatherRule.getDescription()).isEqualTo("if it rains then take an umbrella");
    assertThat(weatherRule.getPriority()).isEqualTo(3);

    final var facts = mock(Facts.class);
    when(facts.asMap()).thenReturn(Map.of("rain", true));

    if (weatherRule.accept(facts)) {
      weatherRule.execute(facts);
    }

    assertThat(System.getProperty("WeatherRuleResult")).isEqualTo("Rains!");
  }

  private void assertAdultRuleIsValid(final Rule adultRule) {
    assertThat(adultRule.getName()).isEqualTo("Adult rule");
    assertThat(adultRule.getDescription()).isEqualTo("if person is over 18 then mark as adult");
    assertThat(adultRule.getPriority()).isEqualTo(1);

    final var facts = mock(Facts.class);
    final var person = new Person("John", 20);
    when(facts.asMap()).thenReturn(Map.of("person", person));

    if (adultRule.accept(facts)) {
      adultRule.execute(facts);
    }

    assertThat(person.isAdult()).isTrue();
  }

  private void assertCompositeRule(final Rule rule, final Class<? extends CompositeRule> expectedClass) {
    assertThat(rule).isInstanceOf(expectedClass);

    final var ruleIterator = (expectedClass.cast(rule)).getRules().iterator();
    assertAdultRuleIsValid(ruleIterator.next());
    assertWeatherRuleIsValid(ruleIterator.next());
  }

  @Data
  public static class Person {
    private String name;
    private int age;
    private boolean adult;

    public Person(final String name, final int age) {
      this.name = name;
      this.age = age;
    }
  }

  @Test
  void shouldCreateSingleRule() {
    final var rules = loadRulesFromFile("/weather-rule.json");
    assertThat(rules).hasSize(1);

    assertWeatherRuleIsValid(rules.iterator().next());
  }

  @Test
  void shouldCreateRules() {
    final var rules = loadRulesFromFile("/rules.json");
    assertThat(rules).hasSize(2);

    // must be ordered by default
    final var ruleIterator = rules.iterator();
    assertAdultRuleIsValid(ruleIterator.next());
    assertWeatherRuleIsValid(ruleIterator.next());
  }

  @Test
  void shouldCreateAnyCompositeRule() {
    final var rules = loadRulesFromFile("/any-rule.json");
    assertThat(rules).hasSize(1);

    final var anyCompositeRule = rules.iterator().next();
    assertCompositeRule(anyCompositeRule, AnyCompositeRule.class);
    assertThat(anyCompositeRule.getName()).isEqualTo("Any rule");
    assertThat(anyCompositeRule.getDescription()).isEqualTo("if any matches then execute it");
    assertThat(anyCompositeRule.getPriority()).isEqualTo(2);
  }

  @Test
  void shouldCreateAllCompositeRule() {
    final var rules = loadRulesFromFile("/all-rule.json");
    assertThat(rules).hasSize(1);

    final var allCompositeRule = rules.iterator().next();
    assertCompositeRule(allCompositeRule, AllCompositeRule.class);
    assertThat(allCompositeRule.getName()).isEqualTo("All rule");
    assertThat(allCompositeRule.getDescription()).isEqualTo("if all match then execute");
    assertThat(allCompositeRule.getPriority()).isEqualTo(2);
  }
}
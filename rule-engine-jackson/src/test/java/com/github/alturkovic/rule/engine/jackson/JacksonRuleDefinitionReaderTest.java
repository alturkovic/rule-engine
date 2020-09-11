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

package com.github.alturkovic.rule.engine.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.alturkovic.rule.engine.reader.RuleDefinition;
import com.github.alturkovic.rule.engine.reader.RuleDefinitionReader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonRuleDefinitionReaderTest {

  @Test
  public void shouldReadJson() {
    assertFileDefinitionsAreValid(new JacksonRuleDefinitionReader(), "/rules.json");
  }

  @Test
  public void shouldReadYml() {
    assertFileDefinitionsAreValid(new JacksonRuleDefinitionReader(new ObjectMapper(new YAMLFactory())), "/rules.yml");
  }

  public void assertFileDefinitionsAreValid(final RuleDefinitionReader reader, final String file) {
    final var definitions = reader.definitions(JacksonRuleDefinitionReaderTest.class.getResourceAsStream(file));
    assertThat(definitions).hasSize(1);
    assertAnyRuleDefinitionIsValid(definitions.iterator().next());
  }

  private void assertAnyRuleDefinitionIsValid(final RuleDefinition ruleDefinition) {
    assertThat(ruleDefinition.getName()).isEqualTo("Any rule");
    assertThat(ruleDefinition.getDescription()).isEqualTo("if any matches then execute it");
    assertThat(ruleDefinition.getPriority()).isEqualTo(2);
    assertThat(ruleDefinition.getType()).isEqualTo("any");
    assertThat(ruleDefinition.getRules()).hasSize(2);
    assertThat(ruleDefinition.isComposite()).isTrue();

    assertFirstRuleDefinitionIsValid(ruleDefinition.getRules().get(0));
    assertSecondRuleDefinitionIsValid(ruleDefinition.getRules().get(1));
  }

  private void assertFirstRuleDefinitionIsValid(final RuleDefinition ruleDefinition) {
    assertThat(ruleDefinition.getName()).isEqualTo("First rule");
    assertThat(ruleDefinition.getDescription()).isEqualTo("First rule description");
    assertThat(ruleDefinition.getPriority()).isEqualTo(1);
    assertThat(ruleDefinition.getWhen()).isEqualTo("first == true");
    assertThat(ruleDefinition.getThen()).containsExactly("print", "delete");
    assertThat(ruleDefinition.getType()).isNullOrEmpty();
    assertThat(ruleDefinition.getRules()).isEmpty();
    assertThat(ruleDefinition.isComposite()).isFalse();
  }

  private void assertSecondRuleDefinitionIsValid(final RuleDefinition ruleDefinition) {
    assertThat(ruleDefinition.getName()).isEqualTo("Second rule");
    assertThat(ruleDefinition.getDescription()).isEqualTo("Second rule description");
    assertThat(ruleDefinition.getPriority()).isEqualTo(2);
    assertThat(ruleDefinition.getWhen()).isEqualTo("accepted()");
    assertThat(ruleDefinition.getThen()).containsExactly("execute()");
    assertThat(ruleDefinition.getType()).isNullOrEmpty();
    assertThat(ruleDefinition.getRules()).isEmpty();
    assertThat(ruleDefinition.isComposite()).isFalse();
  }
}
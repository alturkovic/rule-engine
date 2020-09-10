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

package com.github.alturkovic.rule.engine.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.alturkovic.rule.engine.builder.DefaultRuleEngineBuilder;
import com.github.alturkovic.rule.engine.core.SimpleFacts;
import com.github.alturkovic.rule.engine.jackson.JacksonRuleDefinitionReader;
import com.github.alturkovic.rule.engine.spel.SpELRulesFactory;

public class SpELFileApplication {
  public static void main(final String[] args) {
    final var factory = new SpELRulesFactory(new JacksonRuleDefinitionReader(new ObjectMapper(new YAMLFactory())));
    final var rules = factory.create(SpELFileApplication.class.getResourceAsStream("/spel-example.yml"));

    final var engine = new DefaultRuleEngineBuilder()
        .rules(rules)
        .build();

    engine.evaluate(SimpleFacts.builder()
        .fact("rain", true)
        .build());
  }
}

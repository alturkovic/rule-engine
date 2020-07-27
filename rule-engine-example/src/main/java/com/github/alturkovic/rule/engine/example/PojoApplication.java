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

import com.github.alturkovic.rule.engine.aop.Given;
import com.github.alturkovic.rule.engine.aop.Rule;
import com.github.alturkovic.rule.engine.aop.Then;
import com.github.alturkovic.rule.engine.aop.When;
import com.github.alturkovic.rule.engine.api.Facts;
import com.github.alturkovic.rule.engine.builder.FactsBuilder;
import com.github.alturkovic.rule.engine.builder.RuleEngineBuilder;
import java.time.LocalDateTime;
import lombok.Data;

public class PojoApplication {
  public static void main(final String[] args) {
    final var facts = new FactsBuilder()
        .fact("temp", 40)
        .fact("forecast", "Sunny")
        .fact("date", LocalDateTime.now())
        .build();

    final var engine = new RuleEngineBuilder()
        .rule(new Example())
        .build();

    engine.getRules().stream()
        .map(com.github.alturkovic.rule.engine.api.Rule::getDescription)
        .forEach(System.out::println);

    engine.evaluate(facts);
  }

  @Data
  @SuppressWarnings("unused")
  @Rule(name = "Example rule", description = "Print temperature if it is hot and then print the date the forecast was provided")
  public static class Example {

    @When
    public boolean isHot(@Given("temp") final int temp) {
      return temp > 30;
    }

    @Then(1)
    public void printForecast(final Facts facts, @Given("temp") final int temp) {
      System.out.println(String.format("%s - %d", facts.get("forecast"), temp));
    }

    @Then(2)
    public void printWhenForecastWasMeasured(@Given("date") final LocalDateTime dateTime) {
      System.out.println("Forecast was measured @ " + dateTime);
    }
  }
}

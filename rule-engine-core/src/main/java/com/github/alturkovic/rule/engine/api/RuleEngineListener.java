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

package com.github.alturkovic.rule.engine.api;

/**
 * Triggers on specific {@link RuleEngine} execution events.
 */
public interface RuleEngineListener {
  RuleEngineListener NO_OP = new RuleEngineListener() {};

  default boolean shouldStopBeforeEvaluation(Rule rule, Facts facts) {
    return false;
  }

  default void beforeCondition(Rule rule, Facts facts) {
  }

  default void afterCondition(Rule rule, Facts facts, boolean accepted) {
  }

  default void onConditionError(Rule rule, Facts facts, Exception e) {
  }

  default void beforeAction(Rule rule, Facts facts) {
  }

  default void afterAction(Rule rule, Facts facts) {
  }

  default void onActionError(Rule rule, Facts facts, Exception e) {
  }

  default boolean shouldStopAfterEvaluation(Rule rule, Facts facts, boolean accepted, Exception e) {
    return false;
  }
}

# Rule engine

This rule engine is heavily inspired by [Easy Rules](https://github.com/j-easy/easy-rules).

Some design decisions have been changed, such as:
 * Rules are provided to engines when they are built, not at runtime, use different engines for different rule namespaces 
 * Engines do not ensure rule ordering, `Rules` abstraction can, so they do not have to be ordered
 * Engines cannot be parametrized, `RuleEngineListener` can control when to stop further evaluation without adding such specifics to engines
 * The default `RuleEngine` uses a single listener, but the core library provides a `CompositeRuleEngineListener` to encapsulate multiple listeners

## Core features

 * Zero runtime dependencies for the core module
 * Easy to use and extend API
 * Different ways to define rules
 * Different rule engine implementations

## Example

### 1. Define rules

#### Annotations

```java
@Rule(name = "weather rule", description = "if it rains then take an umbrella")
public class WeatherRule {

    @When
    public boolean itRains(@Given("rain") boolean rain) {
        return rain;
    }
    
    @Then
    public void takeAnUmbrella() {
        System.out.println("It rains, take an umbrella!");
    }
}
```

#### Programmatic

```java
Rule weatherRule = newRule("weather rule")
        .description("if it rains then take an umbrella")
        .<Boolean>when("rain", rain -> rain == true)
        .then(facts -> System.out.println("It rains, take an umbrella!"))
        .build();
```

#### Expression language

##### MVEL
```java
Rule weatherRule = newMVELRule("weather rule")
        .when("rain == true")
        .then("System.out.println(\"It rains, take an umbrella!\");")
        .build();
```

##### SpEL
```java
Rule weatherRule = newSpELRule("weather rule")
        .when("#{['rain'] == true}")
        .then("#{T(java.lang.System).out.println('It rains, take an umbrella!')}")
        .build();
```

#### Files

// TODO

### 2. Evaluate rules

```java
public class ExampleApplication {
    public static void main(String[] args) {
       var engine = new DefaultRuleEngineBuilder()
               .rule(new WeatherRule())
               .build();

       var facts = SimpleFacts.builder()
               .fact("rain", true)
               .build();

       engine.evaluate(facts); 
    }
}
```
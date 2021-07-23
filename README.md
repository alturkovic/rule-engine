![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?style=for-the-badge&labelColor=ED8B00&logo=java&color=808080) ![JitPack](https://img.shields.io/jitpack/v/github/alturkovic/rule-engine?style=for-the-badge&labelColor=007ec5&color=808080&logo=Git&logoColor=white) ![License](https://img.shields.io/github/license/alturkovic/rule-engine?style=for-the-badge&color=808080&logo=Open%20Source%20Initiative&logoColor=white)

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

```java
Rule weatherRule = newMVELRule("weather rule")
        .description("if it rains then take an umbrella")
        .when("rain == true")
        .then("System.out.println(\"It rains, take an umbrella!\");")
        .build();
```

#### Files

`weather-rule.yaml` file:

```yaml
---
- name: Weather rule
  description: if it rains then take an umbrella
  when: "#{['rain'] == true}"
  then:
    - "#{T(java.lang.System).out.println('It rains, take an umbrella!')}"
```

```java
RulesFactory factory = new SpELRulesFactory(new JacksonRuleDefinitionReader(new ObjectMapper(new YAMLFactory())));
Rules rules = factory.create(new FileInputStream("/weather-rule.yaml"));
```

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

## Importing into your project using Maven

Add the JitPack repository to your `pom.xml`.

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the following under your `<dependencies>`:

```xml
<dependency>
    <groupId>com.github.alturkovic</groupId>
    <artifactId>rule-engine</artifactId>
    <version>1.0.0</version>
</dependency>
```

This is xfunction-jdk8 branch which contains xfunction library compatible with Java 8.
Maintaining multi-release jar was discarded since it does not work in situations when you want to create
a class which extends class from newer version of Java (for example XSubscriber extends Flow.Subscriber which
is not available in Java 8). This comes [from multi-release jar
requirements](https://docs.oracle.com/en/java/javase/11/docs/specs/jar/jar.html#multi-release-jar-files):
`The public API exported by the classes in a multi-release JAR file must be exactly the same across versions`.


**xfunction** - Java library which provides set of extensions for standard Java APIs:

- ThrowingRunnable, ThrowingSupplier, ThrowingFunction and others
- functions to convert lambdas which throw checked exceptions to unchecked ones
- currying for lambdas
- preconditions
- [prefix trie](https://en.wikipedia.org/wiki/Trie) collection
- XML query/update utilities using [XPath](https://en.wikipedia.org/wiki/XPath)
- functions to delete/copy folders recursively
- md5 function which returns String in HEX format (instead of byte[])
- extension for HttpClient.Builder which allows to make insecure connections
- ...

# Requirements

Java 8+

# Download

[Release versions](https://github.com/lambdaprime/xfunction/releases)

Or you can add dependency to it as follows:

Gradle:

```
dependencies {
    implementation 'io.github.lambdaprime:id.xfunction:20.0'
}
```

In case you still using Java 8:

```
dependencies {
    implementation 'io.github.lambdaprime:id.xfunction-jdk8:20.0'
}
```

# Documentation

[Documentation](http://portal2.atwebpages.com/xfunction)

# Contributors

lambdaprime <intid@protonmail.com>

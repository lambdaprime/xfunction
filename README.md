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

**xfunction** has zero dependencies on any other libraries.

# Requirements

Java 11+

# Download

[Release versions](xfunction/release/CHANGELOG.md)

Or you can add dependency to it as follows:

Gradle:

```
dependencies {
  implementation 'io.github.lambdaprime:id.xfunction:27.0'
}
```

In case you still using Java 8:

```
dependencies {
  implementation 'io.github.lambdaprime:id.xfunction-jdk8:15.0'
}
```

# Documentation

[Documentation](http://portal2.atwebpages.com/xfunction)

# Contacts

lambdaprime <intid@protonmail.com>

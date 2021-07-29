**xfunction** - Java library which provides set of extensions for standard Java APIs:

- ThrowingRunnable, ThrowingSupplier, ThrowingFunction and others
- functions to convert lambdas which throw checked exceptions to unchecked ones
- currying for lambdas
- XML query/update utilities using XPath
- md5 method which returns String
- extension for HttpClient.Builder which allows to make insecure connections
- prefix trie collection
- ...

lambdaprime <intid@protonmail.com>

# Requirements

Java 8

# Download

You can download **xfunction** from <https://github.com/lambdaprime/xfunction/releases>

Or you can add dependency to it as follows:

Gradle:

```
repositories {
    mavenCentral()
    maven {
        url 'http://portal2.atwebpages.com/repository/'
    }
}

dependencies {
    compile 'io.github.lambdaprime:id.xfunction:<VERSION>'
}
```

Where VERSION should be version of the library you want to use.

# Documentation

Javadoc is available here <http://portal2.atwebpages.com/xfunction>

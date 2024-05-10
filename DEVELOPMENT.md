# Build

Building  module locally and making changes to it (this is optional and not intended for users).

## With Eclipse

- Build eclipse projects:

``` bash
gradle eclipse
```

- Import them into Eclipse

# Release steps

- Checkout `android` branch:
  - `git rebase main`
  - run `gradle clean build`
- Checkout `main` branch
- Close version in gradle.properties
- Run `gradle clean build javadoc`
- Publish
- Open next SNAPSHOT version
- Update CHANGELOG.md with new release (for changelog generation use `git log --format=%s`)
- Commit changes
- Push
- Upload documentation to website
- Checkout `android` branch:
  - `git rebase main`
  - publish to local Maven repo
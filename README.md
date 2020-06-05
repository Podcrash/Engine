TODO: Fill this out

New System env variables
```
MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_PASSWORD=?
```

## Running collect.sh:
Use the:
- DBEngine-*.*-all.jar
- MCEngine-*.*.jar
- PacketWrapper-1.0.jar
## Code Conventions:
Braces: OTBR (including no brace on single line if, but do break the line)

Indents: 4 space tab, not \t

Spacing: space in between the control and bracket i.e. if ()

Naming: classes UpperCamelCase, variables and methods lowerCamelCase. Constants CAPITALS_WITH_UNDERSCORES, packages
 all lowercase.

Documentation: standard javadocs with proper descriptions + resolution, no need for package-info's


## READ THIS:

There a lot of things that still need to be changed that make this most comprehensible engine.

A couple things:
- Some of the code needs to be refactored to make more sense. (Game state functions)
- Database things need to be centralized and made easier.
- A lot of code still needs to be documented.
- A lot of API subsets need to be finished.


# kie-asset-library-poc
PoC for KIE asset library

## Description
This repository shows off the possibilities of usage of kie-asset-plugin.

It is a plugin to be used to transform any provided assets (DMN, BPMN,..) into a maven project.

This is achieved by configuring archetype to be used for a particular project-type.
Ultimate value is in using several archetypes across portfolio, when this
plugin allows to easily build various projects using a single asset group.

## Usage
### kie-assets-library-assets
* Assets are placed under `src/main/resources`. Those are then in pom.xml in kie-assets-plugin configuration
specified within `project-definitions->project-definition->copy-resources`.
  * Each `project-definition` instance is resulting in a new set of generated projects (multiplied by number of `project-structure`s below.).
  * See profile `dmn`.
* Archetypes are configured in kie-assets-library-assets/pom.xml as `project-structures->project-structure->archetype`,
the arguments passed to archetype:generate goal are taken from `project-definitions->project-definition` properties `groupId`,
`artifactId`, `packageName`.
* Each project defined as `project-definition` is generated in `kie-assets-library-assets/target`. Project name is composed
from `project-definition->artifactId` and `project-structure->id`, to allow Cartesian product project generation out of the box.
  * See profiles `quarkus`, `springboot`, `kjar`.

Example build command:
```shell
mvn clean package -Pdmn,quarkus
```

### kie-assets-plugin
Plugin defines 3 goals:
* generate-project
* clean-generated-resources
* copy-external-resources

executed in that exact order.

See [kie-assets-library-assets/pom.xml](./kie-assets-library-assets/pom.xml).


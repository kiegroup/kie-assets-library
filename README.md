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
#### Basics
* Two kind of elements `project-definition` and `project-structure`.
* **project-definition** describes the resulting project, its name, assets included and additional (re)sources needed.
  ```xml
  <project-definition>
    <groupId>org.kie</groupId>
    <artifactId>dmn-and-bpmn-generated</artifactId>
    <packageName>org.kie.generated.dmn</packageName>
    <copy-resources></copy-resources>
    <copy-sources></copy-sources>
    <config-set-references></config-set-references>
  </project-definition>
  ```
* **project-structure** defines the project structure to be generated. It allows specification of archetype, dependencies
  and sources to prepare the project.
  ```xml
  <project-structure>
    <id>quarkus</id>
    <archetype>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-quarkus-archetype</artifactId>
      <version>${version.archetype.kogito.quarkus}</version>
    </archetype>
    <delete-resources></delete-resources>
    <config-sets></config-sets>
  </project-structure>
  ```
* Each `project-definition` instance is resulting in a new set of generated projects (multiplied by number of `project-structure` elements.).
#### Generating projects
* **Archetypes** are configured in kie-assets-library-assets/pom.xml as `project-structures->project-structure->archetype`,
the arguments passed to archetype:generate goal are taken from `project-definitions->project-definition` properties `groupId`,
`artifactId`, `packageName`.
  ```xml
  <project-structure>
    <id>quarkus</id>
    <archetype>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-quarkus-archetype</artifactId>
      <version>1.3.0.Final</version>
    </archetype>
  </project-structure>
  ```
* Each project defined as `project-definition` is generated in `kie-assets-library-assets/target`. Project name is composed
    from `project-definition->artifactId` and `project-structure->id`, to allow Cartesian product project generation out of the box.

#### Adapting archetype project stub
* **delete-resources** convenient support for cleaning up projects right after they were generated. Useful to remove any example
  content that can typically be provided implicitly.
  ```xml
  <project-structure>
    <delete-resources>
      <resource>
        <directory>src/main/resources</directory>
        <includes>
          <include>*</include>
          <include>**/*</include>
        </includes>
        <excludes>
          <exclude>application.properties</exclude>
        </excludes>
      </resource>
      <resource>
        <directory>./</directory>
        <includes>
          <include>README.md</include>
        </includes>
      </resource>
    </delete-resources>
  </project-structure>
  ```
* **Config-sets** provide convenient way to define additional dependencies or source packages to be added to generated projects.
  These named sets are useful to implement common functionality across all project-structures covering implementing details
  (like adding scenario simulation to your project).
  * Defined in project-structure as:
  ```xml
  <project-structures>
    <project-structure>
      <id>quarkus</id>
      <config-sets>
        <config-set>
          <id>scesim</id>
          <dependencies>
            <dependency>
              <groupId>org.kie.kogito</groupId>
              <artifactId>kogito-scenario-simulation</artifactId>
              <scope>test</scope>
            </dependency>
          </dependencies>
          <copy-sources>
            <package>
              <name>scesim.kogito</name>
              <type>test</type>
              <language>java</language>
            </package>
          </copy-sources>
        </config-set>
      </config-sets>
    </project-structure>
  </project-structures>
  ```
  * And referenced explicitly from project-definition.
* See profiles `quarkus`, `springboot`, `kjar`.

#### Assets
* Assets are placed under `src/main/resources`. Those are then in pom.xml in kie-assets-plugin configuration
  specified.
* **copy-resources** enables copying specific Assets from src/main/resources into projects generated above.
  ```xml
  <project-definition>
    <copy-resources>
      <resource>
        <directory>${project.basedir}/src/main/resources/dmn</directory>
        <includes>
          <include>call centre drd.dmn</include>
          <include>invoke call centre drd model.bpmn</include>
        </includes>
      </resource>
    </copy-resources>
  </project-definition>
  ```
* **copy-sources** allows to attach any source code package to the resulting project. The target package is created
  in the same subtree as is in the library.
  ```xml
  <project-definition>
    <copy-sources>
      <package>
        <name>org.kie.traffic</name>
        <type>main</type>
        <language>java</language>
      </package>
    </copy-sources>
  </project-definition>
  ```

* **config-set-reference** - is a way to reference config-set defined across project-structure elements. Enables
  to do additional actions that are project-structure specific (e.g. target environment specific).
  ```xml
  <project-definition>
    <config-set-references>
      <config-set-reference>scesim</config-set-reference>
    </config-set-references>
  </project-definition>
  ```
* See profile `dmn`.

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


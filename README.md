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
    <id>dmn1</id>
    <groupId>org.kie</groupId>
    <artifactId>dmn-and-bpmn-generated</artifactId>
    <packageName>org.kie.generated.dmn</packageName>
    <config>
      <copy-resources></copy-resources>
      <copy-sources></copy-sources>
      <dependencies></dependencies>
    </config>
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
    <common-config></common-config>
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
  * Defined in project-structure in config-sets (needs to be activated explicitly, see below):
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
          <copy-resources>
            <directory></directory>
            <includes></includes>
            <excludes></excludes>
          </copy-resources>
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
  * Or as common-config:
  ```xml
  <project-structure>
    <common-config>
      <copy-resources></copy-resources>
      <copy-sources></copy-sources>
      <dependencies></dependencies>
    </common-config>
  </project-structure>
  ```
  * Or even in project-definition:
  ```xml
  <project-definition>
    <config>
      <copy-resources>
        <resource>
          <directory>${project.basedir}/src/main/resources/dmn</directory>
          <includes>
            <include>call centre drd.dmn</include>
            <include>invoke call centre drd model.bpmn</include>
          </includes>
        </resource>
      </copy-resources>
      <copy-sources>
        <package>
          <name>org.kie.my.package</name>
          <language>java</language>
          <type>main</type>
        </package>
      </copy-sources>
      <dependencies>
        <dependency>
          <groupId>org.kie.dataobjects</groupId>
          <artifactId>some-project</artifactId>
          <version>1.0.0-SNAPSHOT</version>
        </dependency>
      </dependencies>
    </config>
  </project-definition>
  ```
* See project-structures `quarkus`, `springboot`, `kjar` in [kie-assets-library-support](./kie-assets-library-support).
* See project-definitions in [kie-assets-library-assets](./kie-assets-library-assets).

#### Assets
* Assets are placed under `src/main/resources`. Those are then in pom.xml in kie-assets-plugin configuration
  specified.
* **copy-resources** enables copying specific Assets from src/main/resources into projects generated above.
  ```xml
  <project-definition>
    <config>
      <copy-resources>
        <resource>
          <directory>${project.basedir}/src/main/resources/dmn</directory>
          <includes>
            <include>call centre drd.dmn</include>
            <include>invoke call centre drd model.bpmn</include>
          </includes>
        </resource>
      </copy-resources>
    </config>
  </project-definition>
  ```
* **copy-sources** allows to attach any source code package to the resulting project. The target package is created
  in the same subtree as is in the library.
  ```xml
  <project-definition>
    <config>
      <copy-sources>
        <package>
          <name>org.kie.traffic</name>
          <type>main</type>
          <language>java</language>
        </package>
      </copy-sources>
    </config>
  </project-definition>
  ```
* See profile `dmn`.

#### Filtering what is generated
By default Cartesian product of configured `project-definition` : `projec-structure` pairs is generated.
This behavior can be altered using additional configuration.
* **active-definition-ids** contains a list of `project-definition` ids. These are then the only included.
  ```xml
  <active-definition-ids>
    <active-definition-id>first</active-definition-id>
  </active-definition-ids>
  ```
  * Exposed property `-Dactive.definitions=first,second`
* **active-structure-ids** contains a list of `project-structure` ids. These are then the only included.
  ```xml
  <active-structure-ids>
    <active-structure-id>quarkus</active-structure-id>
  </active-structure-ids>
  ```
  * Exposed property `-Dactive.structures=kjar,quarkus`
* **active-config-sets** contains a list of `config-set` ids. These are then added to execution together with
  other configuration.
  ```xml
  <active-config-sets>
    <active-config-set>scesim</active-config-set>
  </active-config-sets>
  ```
  * Exposed property `-Dactive.config.sets=scesim,sthelse`

#### Generate
The actual invocation of kie-assets-plugin is in [kie-assets-library-generate](./kie-assets-library-generate), which inherits
configurations from both [kie-assets-library-support](./kie-assets-library-support) and [kie-assets-library-assets](./kie-assets-library-assets).

Example build commands:
* Some configuration is possible to be provided/refined as properties as mentioned in [Filtering what is generated](#filtering-what-is-generated).
* Using exposed properties:
```shell
mvn clean package -Dactive.definitions=dmn-generated -Dactive.structures=quarkus,kjar -Dactive.config.sets=scesim
```
* Or combination with defined profiles in poms:
```shell
mvn clean package -Pdmn -Dactive.structures=quarkus -Dactive.config.sets=scesim
```

### kie-assets-plugin
Plugin defines 3 goals:
* generate-project
* clean-generated-resources
* copy-external-resources

executed in that exact order.

See [kie-assets-library-assets/pom.xml](./kie-assets-library-assets/pom.xml).


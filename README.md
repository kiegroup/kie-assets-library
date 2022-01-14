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
* Two kind of elements `projectDefinition` and `projectStructure`.
* **projectDefinition** describes the resulting project, its name, assets included and additional (re)sources needed.
  ```xml
  <projectDefinition>
    <id>dmn1</id>
    <groupId>org.kie</groupId>
    <artifactId>dmn-and-bpmn-generated</artifactId>
    <packageName>org.kie.generated.dmn</packageName>
    <config>
      <deleteResources></deleteResources>
      <copyResources></copyResources>
      <copySources></copySources>
      <dependencies></dependencies>
    </config>
  </projectDefinition>
  ```
* **projectStructure** defines the project structure to be generated. It allows specification of archetype, dependencies
  and sources to prepare the project.
  ```xml
  <projectStructure>
    <id>quarkus</id>
    <generate>
      <type>ARCHETYPE</type>
      <archetype>
        <groupId>org.kie.kogito</groupId>
        <artifactId>kogito-quarkus-archetype</artifactId>
        <version>${version.archetype.kogito.quarkus}</version>
      </archetype>
    </generate>
    <commonConfig></commonConfig>
    <configSets></configSets>
  </projectStructure>
  ```
* Each `projectDefinition` instance is resulting in a new set of generated projects (multiplied by number of `projectStructure` elements.).
#### Generating projects
* **Generating options** are configured in kie-assets-library-assets/pom.xml as `projectStructures->projectStructure->generate`. Supported
  generating methods are:
  * **Archetypes** - specified as:
    ```xml
    <generate>
      <type>ARCHETYPE</type>
      <archetype>
        <groupId>org.kie.kogito</groupId>
        <artifactId>${springboot.archetype.artifactId}</artifactId>
        <version>${version.kogito}</version>
      </archetype>
      <properties>...</properties>
    </generate>
    ```
  * **Maven Plugin** - specified as:
    ```xml
    <generate>
      <type>MAVEN_PLUGIN</type>
      <mavenPluginConfig>
        <groupId>${quarkus.platform.groupId}</groupId>
        <artifactId>quarkus-maven-plugin</artifactId>
        <version>${version.quarkus.platform}</version>
        <goal>create</goal>
      </mavenPluginConfig>
      <quarkusPlatformGav>
        <groupId>${quarkus.platform.groupId}</groupId>
        <artifactId>${quarkus.platform.artifactId}</artifactId>
        <version>${version.quarkus.platform}</version>
      </quarkusPlatformGav>
      <quarkusExtensions>${quarkus.extensions}</quarkusExtensions>
      <properties>...</properties>
    </generate>
    ```
  * **Quarkus CLI** -specified as:
    ```xml
    <generate>
      <type>QUARKUS_CLI</type>
      <quarkusPlatformGav>
        <groupId>${quarkus.platform.groupId}</groupId>
        <artifactId>${quarkus.platform.artifactId}</artifactId>
        <version>${version.quarkus.platform}</version>
      </quarkusPlatformGav>
      <quarkusExtensions>${quarkus.extensions}</quarkusExtensions>
      <properties>...</properties>
    </generate>
    ```
    * NOTE: Requires Jbang installed. By default the install is enabled, disable using `-Dskip.install.quarkus.cli`.
      Make sure `jbang` is then on path.

* Each project defined as `projectDefinition` is generated in `kie-assets-library-assets/target`. Project name is composed
    from `projectDefinition->artifactId` and `projectStructure->id`, to allow Cartesian product project generation out of the box.
* By default, **Archetype** and **Maven Plugin** project structures use the same Maven settings and local Maven repository location as the main build.
  This can be changed using the following configuration:
  ```xml
  <generate>
    <type>ARCHETYPE</type>
    <settingsFile>/custom/settings.xml</settingsFile>
    <useSeparateRepository>true</useSeparateRepository>
  </generate>
  ```
  This will make all projects generated with this project structure use the provided settings.xml file and this project structure
  will have a separate local Maven repository under the directory where the projects are generated.

#### Adapting archetype project stub
* **Config-sets** provide convenient way to define additional dependencies or source packages to be added to generated projects.
  These named sets are useful to implement common functionality across all projectStructures covering implementing details
  (like adding scenario simulation to your project).
  * Defined in `projectStructure` in `configSets` (needs to be activated explicitly, see below):
    ```xml
    <projectStructures>
      <projectStructure>
        <id>quarkus</id>
        <configSets>
          <configSet>
            <id>scesim</id>
            <dependencies>
              <dependency>
                <groupId>org.kie.kogito</groupId>
                <artifactId>kogito-scenario-simulation</artifactId>
                <scope>test</scope>
              </dependency>
            </dependencies>
            <copyResources>
              <directory></directory>
              <includes></includes>
              <excludes></excludes>
            </copyResources>
            <copySources>
              <package>
                <name>scesim.kogito</name>
                <type>test</type>
                <language>java</language>
              </package>
            </copySources>
          </configSet>
        </configSets>
      </projectStructure>
    </projectStructures>
    ```
  * Or as `commonConfig`:
    ```xml
    <projectStructure>
      <commonConfig>
        <deleteResources>
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
        </deleteResources>
        <copyResources></copyResources>
        <copySources></copySources>
        <dependencies></dependencies>
      </commonConfig>
    </projectStructure>
    ```
  * Or even in projectDefinition:
    ```xml
    <projectDefinition>
      <config>
        <copyResources>
          <resource>
            <directory>${project.basedir}/src/main/resources/dmn</directory>
            <includes>
              <include>call centre drd.dmn</include>
              <include>invoke call centre drd model.bpmn</include>
            </includes>
          </resource>
        </copyResources>
        <copySources>
          <package>
            <name>org.kie.my.package</name>
            <language>java</language>
            <type>main</type>
          </package>
        </copySources>
        <dependencies>
          <dependency>
            <groupId>org.kie.dataobjects</groupId>
            <artifactId>some-project</artifactId>
            <version>1.0.0-SNAPSHOT</version>
          </dependency>
        </dependencies>
      </config>
    </projectDefinition>
    ```
  * As a reusable configuration:
    ```xml
    <configuration>
      <reusableConfigSets>
        <configSet>
          <id>someId</id>
        </configSet>
      </reusableConfigSets>
    </configuration>
    ```
    and later referenced from an arbitrary ConfigSet (see options above, applicable for
    all configSet, config and commonConfig variants), as:
    ```xml
    <configSet>
      <id>someDifferentId</id> <!-- this will be used for activation using activeConfigSets --> 
      <reusableConfig>someId</reusableConfig> <!-- this points just to reusableConfigSets element -->
      <!-- rest of config is ignored if reusableConfig is specified, no merging done. -->
    </configSet>
    ```
* See projectStructures in [kie-assets-library-support](./kie-assets-library-support).
* See projectDefinitions in [kie-assets-library-assets](./kie-assets-library-assets).

#### Assets
* Assets are placed under `src/main/resources`. Those are then in pom.xml in kie-assets-plugin configuration
  specified.
* **copyResources** enables copying specific Assets from src/main/resources into projects generated above.
  ```xml
  <projectDefinition>
    <config>
      <copyResources>
        <resource>
          <directory>${project.basedir}/src/main/resources/dmn</directory>
          <includes>
            <include>call centre drd.dmn</include>
            <include>invoke call centre drd model.bpmn</include>
          </includes>
        </resource>
      </copyResources>
    </config>
  </projectDefinition>
  ```
* **copySources** allows to attach any source code package to the resulting project. The target package is created
  in the same subtree as is in the library.
  ```xml
  <projectDefinition>
    <config>
      <copySources>
        <package>
          <name>org.kie.traffic</name>
          <type>main</type>
          <language>java</language>
        </package>
      </copySources>
    </config>
  </projectDefinition>
  ```
* See profile `dmn`.

#### Filtering what is generated
By default Cartesian product of configured `projectDefinition` : `projecStructure` pairs is generated.
This behavior can be altered using additional configuration.
* **activeDefinitions** contains a list of `projectDefinition` ids. These are then the only included.
  ```xml
  <activeDefinitions>
    <activeDefinition>first</activeDefinition>
  </activeDefinitions>
  ```
  * Exposed property `-Dactive.definitions=first,second`
* **activeStructures** contains a list of `projectStructure` ids. These are then the only included.
  ```xml
  <activeStructures>
    <activeStructure>quarkus</activeStructure>
  </activeStructures>
  ```
  * Exposed property `-Dactive.structures=kjar,quarkus`
* **activeConfigSets** contains a list of `configSet` ids. These are then added to execution together with
  other configuration.
  ```xml
  <activeConfigSets>
    <activeConfigSet>scesim</activeConfigSet>
  </activeConfigSets>
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


import java.nio.file.Files
import java.nio.file.Path

def justTwo = ["test.dmn", "test3.dmn"]
def allThree = ["test.dmn", "test2.dmn", "test3.dmn"]
def applicationProperties = ["application.properties"]
checkProjectAndResources("test-generated-quarkus", allThree + applicationProperties)
checkProjectAndResources("test-generated-springboot", allThree + applicationProperties)
checkProjectAndResources("test-generated-kjar", allThree)
checkProjectAndResources("test-generated2-quarkus", justTwo + applicationProperties)
checkProjectAndResources("test-generated2-springboot", justTwo + applicationProperties)
checkProjectAndResources("test-generated2-kjar", justTwo)


def checkProjectAndResources(String projectName, List<String> resources){
    Path projectRoot = basedir.toPath().resolve("target/$projectName");
    assert Files.isDirectory(projectRoot) : "project root does not exist $projectRoot"
    assert Files.isRegularFile(projectRoot.resolve("pom.xml")) : "pom does not exist in $projectRoot"
    def srcMainResources = projectRoot.resolve("src/main/resources")
    assert Files.isDirectory(srcMainResources) : "direcory not exists $srcMainResources"
    resources.forEach {resource -> assert Files.isRegularFile(srcMainResources.resolve(resource)):"file does not exist $resource"}
}
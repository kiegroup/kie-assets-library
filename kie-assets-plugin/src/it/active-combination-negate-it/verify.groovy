import java.nio.file.Files
import java.nio.file.Path

def justTwo = ["test.dmn", "test3.dmn"]
assert Files.list(basedir.toPath().resolve("target")).filter({ Files.isDirectory(it) }).count() == 1
checkProjectAndResources("test-generated2-quarkus", justTwo)


def checkProjectAndResources(String projectName, List<String> resources) {
    Path projectRoot = basedir.toPath().resolve("target/$projectName");
    assert Files.isDirectory(projectRoot): "project root does not exist $projectRoot"
    assert Files.isRegularFile(projectRoot.resolve("pom.xml")): "pom does not exist in $projectRoot"
    def srcMainResources = projectRoot.resolve("src/main/resources")
    assert Files.isDirectory(srcMainResources): "direcory not exists $srcMainResources"
    resources.forEach { resource -> assert Files.isRegularFile(srcMainResources.resolve(resource)): "file does not exist $resource" }
}

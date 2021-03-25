package org.kie.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.model.Resource;

/**
 * Path-oriented methods to enable resource filtering.
 */
public class FileFilteringUtils {
    public static final String GLOB = "glob:";

    /**
     * Get a glob matching string with prefix "glob:", formatted from local path prefix and appended glob expression.
     * @param location path to where start the filtering
     * @param glob glob expression to match
     * @return String glob expression with absolute path context
     */
    private static String getGlobString(Path location, String glob) {
        return GLOB + (Paths.get(glob).startsWith(location) ? glob : location.resolve(glob));
    }

    /**
     * Traverses directory structure starting at given location and retrieves all the Path instances
     * matching the provided includes and excludes glob expressions.
     * @param includes glob expressions to include
     * @param excludes glob expressions to exclude
     * @param location path where to start the search
     * @return List of paths matching given filtering
     * @throws IOException on error while traversing file tree
     */
    public static List<Path> matchPathsUsingGlob(List<String> includes, List<String> excludes, Path location) throws IOException {

        List<Path> matched = new ArrayList<>();
        List<PathMatcher> matchers = includes.stream().map(glob -> FileSystems.getDefault().getPathMatcher(
                getGlobString(location, glob))).collect(Collectors.toList());
        List<PathMatcher> excludeMatchers = excludes.stream().map(glob -> FileSystems.getDefault().getPathMatcher(
                getGlobString(location, glob))).collect(Collectors.toList());

        Files.walkFileTree(location, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path,
                                             BasicFileAttributes attrs) {
                if (
                        matchers.stream().anyMatch(pathMatcher -> pathMatcher.matches(path))
                                && excludeMatchers.stream().noneMatch(pathMatcher -> pathMatcher.matches(path))) {
                    matched.add(path);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        return matched;
    }

    /**
     * Filter files located under the provided location by applying filters defined in given {@linkplain Resource} instance.
     * e.g.
     * <pre>
     *     &lt;resource&gt;
     *         &lt;includes&gt;
     *             &lt;include&gt;*&lt;/include&gt;
     *         &lt;/includes&gt;
     *         &lt;excludes&gt;
     *             &lt;exclude&gt;file.txt&lt;/exclude&gt;
     *             &lt;exclude&gt;**&#47;file.txt&lt;/exclude&gt;
     *         &lt;/excludes&gt;
     *     &lt;/resource&gt;
     * </pre>
     * @param parent path where to start filtering
     * @param resource resource specifying includes and excludes
     * @return files matching the filters
     * @throws IOException on error during file tree traversal
     */
    public static List<Path> filterFilesStartingAtPath(Path parent, Resource resource) throws IOException {
        List<Path> matching = matchPathsUsingGlob(resource.getIncludes(), resource.getExcludes(), parent);
        return matching;
    }
}

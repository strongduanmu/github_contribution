package com.strongduanmu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * GitHub dependency list compare.
 */
public final class GithubDependencyListCompare {
    
    private static final String ORIGINAL_PATH = "/Users/strongduanmu/IdeaProjects/apache-shardingsphere-5.1.2/shardingsphere-distribution/shardingsphere-proxy-distribution/target/apache-shardingsphere-5.1.2-shardingsphere-proxy-bin/lib";
    private static final String TARGET_PATH = "/Users/strongduanmu/shardingsphere/shardingsphere-distribution/shardingsphere-proxy-distribution/target/apache-shardingsphere-5.2.0-shardingsphere-proxy-bin/lib";
    private static final String LICENSE_PATH = "/Users/strongduanmu/shardingsphere/shardingsphere-distribution/shardingsphere-proxy-distribution/src/main/release-docs/LICENSE";
    
    public static void main(String[] args) throws IOException {
        Collection<String> originalDependencyList = getDependencyList(new File(ORIGINAL_PATH));
        Collection<String> targetDependencyList = getDependencyList(new File(TARGET_PATH));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(LICENSE_PATH));
        String licenseContent = getLicenseContent(bufferedReader);
        System.out.println("--------新增或变更依赖--------");
        for (String each : targetDependencyList) {
            if (originalDependencyList.contains(each) || each.contains("shardingsphere")) {
                continue;
            }
            if (!licenseContent.contains(each)) {
                System.out.println("License content not contains: " + each + ", please add or update it.");
            }
        }
        System.out.println("--------删除或变更依赖--------");
        for (String each : originalDependencyList) {
            if (targetDependencyList.contains(each) || each.contains("shardingsphere")) {
                continue;
            }
            if (licenseContent.contains(each)) {
                System.out.println("License content contains: " + each + ", please delete or update it.");
            }
        }
    }
    
    private static String getLicenseContent(final BufferedReader bufferedReader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        while (null != (line = bufferedReader.readLine())) {
            result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }
    
    private static Collection<String> getDependencyList(final File file) {
        Collection<String> result = new HashSet<>();
        for (File each : file.listFiles()) {
            int lastIndex = each.getName().lastIndexOf("-");
            result.add(each.getName().substring(0, lastIndex) + " " + each.getName().substring(lastIndex + 1).replace(".jar", ""));
        }
        return result;
    }
}

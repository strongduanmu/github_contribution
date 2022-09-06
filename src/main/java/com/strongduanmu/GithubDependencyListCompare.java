package com.strongduanmu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GitHub dependency list compare.
 */
public final class GithubDependencyListCompare {
    
    private static final String RELEASE_LIB_PATH = "/Users/strongduanmu/ss_svn/dev/shardingsphere/5.2.0/apache-shardingsphere-5.2.0-shardingsphere-proxy-bin/lib";
    private static final String RELEASE_LICENSE_PATH = "/Users/strongduanmu/ss_svn/dev/shardingsphere/5.2.0/apache-shardingsphere-5.2.0-shardingsphere-proxy-bin/LICENSE";
    private static final Pattern VERSION_PATTERN = Pattern.compile("-\\d+(\\.\\d+)*");
    private static final Collection<String> VALID_LICENSES = new HashSet<>();
    
    static {
        VALID_LICENSES.add("Apache 2.0");
        VALID_LICENSES.add("BSD-2-Clause");
        VALID_LICENSES.add("BSD-3-Clause");
        VALID_LICENSES.add("New BSD License");
        VALID_LICENSES.add("CDDL");
        VALID_LICENSES.add("EPL 1.0");
        VALID_LICENSES.add("MIT");
        VALID_LICENSES.add("CC0 1.0");
    }
    
    public static void main(String[] args) throws IOException {
        Collection<String> releaseLibs = getReleaseLibs(new File(RELEASE_LIB_PATH));
        String licenseContent = getLicenseContent(new BufferedReader(new FileReader(RELEASE_LICENSE_PATH)));
        System.out.println("--------新增或变更依赖--------");
        for (String each : releaseLibs) {
            if (!licenseContent.contains(each) && !each.contains("shardingsphere") && !each.contains("elasticjob")) {
                System.out.println("License content not contains: " + each + ", please add or update it.");
            }
        }
        Collection<String> licenseLibs = getLicenseLibs(new BufferedReader(new FileReader(RELEASE_LICENSE_PATH)));
        System.out.println("--------删除或变更依赖--------");
        for (String each : licenseLibs) {
            if (!releaseLibs.contains(each)) {
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
    
    private static Collection<String> getReleaseLibs(final File file) {
        Collection<String> result = new HashSet<>();
        for (File each : Objects.requireNonNull(file.listFiles())) {
            Matcher matcher = VERSION_PATTERN.matcher(each.getName());
            String version = matcher.find() ? matcher.group() : "";
            int lastIndex = each.getName().lastIndexOf(version);
            result.add(each.getName().substring(0, lastIndex) + " " + each.getName().substring(lastIndex + 1).replace(".jar", ""));
        }
        return result;
    }
    
    private static Collection<String> getLicenseLibs(final BufferedReader bufferedReader) throws IOException {
        Collection<String> result = new HashSet<>();
        String line;
        while (null != (line = bufferedReader.readLine())) {
            if (!containValidLicense(line) || !line.contains(":")) {
                continue;
            }
            result.add(line.trim().split(" ")[0] + " " + line.trim().split(" ")[1].replace(":", ""));
        }
        return result;
    }
    
    private static boolean containValidLicense(final String line) {
        for (String each : VALID_LICENSES) {
            if (line.contains(each)) {
                return true;
            }
        }
        return false;
    }
}

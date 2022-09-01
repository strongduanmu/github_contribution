package com.strongduanmu;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

/**
 * GitHub dependency list compare.
 */
public final class GithubDependencyListCompare {
    
    private static final String ORIGINAL_PATH = "/Users/strongduanmu/IdeaProjects/apache-shardingsphere-5.1.2/shardingsphere-distribution/shardingsphere-proxy-distribution/target/apache-shardingsphere-5.1.2-shardingsphere-proxy-bin/lib";
    private static final String TARGET_PATH = "/Users/strongduanmu/IdeaProjects/shardingsphere/shardingsphere-distribution/shardingsphere-proxy-distribution/target/apache-shardingsphere-5.1.3-SNAPSHOT-shardingsphere-proxy-bin/lib";
    
    public static void main(String[] args) {
        Collection<String> originalDependencyList = getDependencyList(new File(ORIGINAL_PATH));
        Collection<String> targetDependencyList = getDependencyList(new File(TARGET_PATH));
        System.out.println("新增或变更依赖：");
        for (String each : targetDependencyList) {
            if (originalDependencyList.contains(each) || each.contains("shardingsphere")) {
                continue;
            }
            System.out.println(each);
        }
        System.out.println("删除或变更依赖：");
        for (String each : originalDependencyList) {
            if (targetDependencyList.contains(each) || each.contains("shardingsphere")) {
                continue;
            }
            System.out.println(each);
        }
    }
    
    private static Collection<String> getDependencyList(final File file) {
        Collection<String> result = new HashSet<>();
        for (File each : file.listFiles()) {
            result.add(each.getName());
        }
        return result;
    }
}

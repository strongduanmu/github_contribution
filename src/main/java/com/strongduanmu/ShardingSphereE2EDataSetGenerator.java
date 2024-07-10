package com.strongduanmu;

import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.sql.parser.api.CacheOption;
import org.apache.shardingsphere.sql.parser.api.SQLParserEngine;
import org.apache.shardingsphere.sql.parser.api.SQLStatementVisitorEngine;
import org.apache.shardingsphere.sql.parser.core.ParseASTNode;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.column.ColumnDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.statement.mysql.ddl.MySQLCreateTableStatement;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

public final class ShardingSphereE2EDataSetGenerator {
    
    private static final Map<String, String> DATA_TYPE_MAPS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    
    static {
        DATA_TYPE_MAPS.put("date", "Date");
    }
    
    public static void main(String[] args) {
        Collection<String> handledMetadataTables = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String each : readSQLs()) {
            try {
                CacheOption cacheOption = new CacheOption(128, 1024L);
                SQLParserEngine parserEngine = new SQLParserEngine("MySQL", cacheOption);
                ParseASTNode parseASTNode = parserEngine.parse(each, false);
                SQLStatementVisitorEngine sqlVisitorEngine = new SQLStatementVisitorEngine(TypedSPILoader.getService(DatabaseType.class, "MySQL"));
                SQLStatement sqlStatement = sqlVisitorEngine.visit(parseASTNode);
                if (sqlStatement instanceof MySQLCreateTableStatement) {
                    String tableName = ((MySQLCreateTableStatement) sqlStatement).getTable().getTableName().getIdentifier().getValue();
                    if (handledMetadataTables.contains(tableName)) {
                        continue;
                    }
                    handledMetadataTables.add(tableName);
                    StringBuilder builder = new StringBuilder("<metadata data-nodes=\"expected_dataset." + tableName + "\">\n");
                    for (ColumnDefinitionSegment column : ((MySQLCreateTableStatement) sqlStatement).getColumnDefinitions()) {
                        String dataTypeName = DATA_TYPE_MAPS.getOrDefault(column.getDataType().getDataTypeName(), column.getDataType().getDataTypeName());
                        builder.append("    <column name=\"").append(column.getColumnName().getIdentifier().getValue()).append("\" type=\"").append(dataTypeName).append("\" />\n");
                    }
                    builder.append("</metadata>");
                    System.out.println(builder);
                }
            } catch (final Exception ignore) {
            }
        }
    }
    
    private static Collection<String> readSQLs() {
        Collection<String> result = new LinkedList<>();
        ScriptReader reader = new ScriptReader(new InputStreamReader(Objects.requireNonNull(ShardingSphereE2EDataSetGenerator.class.getClassLoader().getResourceAsStream("sql/test.sql"))));
        while (true) {
            String sql = reader.readStatement();
            if (null == sql) {
                break;
            }
            String replacedSQL = sql.replaceAll("--.*\n", "").replaceAll("/\\*![^`]*\\*/", "").trim();
            if (StringUtils.isWhitespaceOrEmpty(replacedSQL)) {
                continue;
            }
            result.add(replacedSQL);
        }
        return result;
    }
}

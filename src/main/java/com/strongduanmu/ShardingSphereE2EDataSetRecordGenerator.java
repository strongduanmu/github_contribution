package com.strongduanmu;

import com.google.common.base.Strings;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.sql.parser.api.CacheOption;
import org.apache.shardingsphere.sql.parser.api.SQLParserEngine;
import org.apache.shardingsphere.sql.parser.api.SQLStatementVisitorEngine;
import org.apache.shardingsphere.sql.parser.core.ParseASTNode;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.assignment.InsertValuesSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.statement.core.util.SQLUtils;
import org.apache.shardingsphere.sql.parser.statement.mysql.dal.MySQLUseStatement;
import org.apache.shardingsphere.sql.parser.statement.mysql.dml.MySQLInsertStatement;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public final class ShardingSphereE2EDataSetRecordGenerator {
    
    public static void main(String[] args) throws IOException {
        String currentDatabase = "";
        StringBuilder sqlBuilder = new StringBuilder();
        for (String each : readSQLs()) {
            try {
                CacheOption cacheOption = new CacheOption(128, 1024L);
                SQLParserEngine parserEngine = new SQLParserEngine("MySQL", cacheOption);
                ParseASTNode parseASTNode = parserEngine.parse(each, false);
                SQLStatementVisitorEngine sqlVisitorEngine = new SQLStatementVisitorEngine(TypedSPILoader.getService(DatabaseType.class, "MySQL"));
                SQLStatement sqlStatement = sqlVisitorEngine.visit(parseASTNode);
                if (sqlStatement instanceof MySQLUseStatement) {
                    currentDatabase = ((MySQLUseStatement) sqlStatement).getDatabase();
                }
                if (sqlStatement instanceof MySQLInsertStatement) {
                    if (Strings.isNullOrEmpty(currentDatabase)) {
                        throw new IllegalArgumentException("Can not find currentDatabase.");
                    }
                    MySQLInsertStatement insertStatement = (MySQLInsertStatement) sqlStatement;
                    String tableName = insertStatement.getTable().getTableName().getIdentifier().getValue();
                    for (InsertValuesSegment insertValues : insertStatement.getValues()) {
                        StringBuilder valueBuilder = new StringBuilder();
                        for (ExpressionSegment expression : insertValues.getValues()) {
                            String exactlyValue = SQLUtils.getExactlyValue(expression.getText());
                            valueBuilder.append(Strings.isNullOrEmpty(exactlyValue) ? exactlyValue : exactlyValue.replace(", ", "{SPEX_DELIMITER}")).append(", ");
                        }
                        sqlBuilder.append("<row data-node=\"").append(currentDatabase.toLowerCase()).append(".").append(tableName).append("\" values=\"").append(valueBuilder.substring(0, valueBuilder.length() - 2)).append("\" />").append("\n");
                    }
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dataset.xml"))) {
            writer.write(sqlBuilder.toString());
        }
    }
    
    private static Collection<String> readSQLs() {
        Collection<String> result = new LinkedList<>();
        ScriptReader reader = new ScriptReader(new InputStreamReader(Objects.requireNonNull(ShardingSphereE2EDataSetRecordGenerator.class.getClassLoader().getResourceAsStream("sql/test.sql"))));
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

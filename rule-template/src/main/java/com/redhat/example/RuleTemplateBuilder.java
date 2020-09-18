package com.redhat.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.drools.template.jdbc.ResultSetGenerator;

public class RuleTemplateBuilder {
    public static void main(String[] args) {

        InputStream template = RuleTemplateBuilder.class
                .getResourceAsStream("/template-dtable/customer-classification-db.drt");

        byte[] drl = loadFromDB(template);

        try {
            File file = new File("src/main/resources/ruletemplate/db");
            file.mkdirs();
            file = new File("src/main/resources/ruletemplate/db/ruleSet.drl");
            file.createNewFile();
            FileOutputStream fileOS = new FileOutputStream(file, false);
            fileOS.write(drl);
            fileOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private static byte[] loadFromDB(InputStream template) {
        String drl = "";
        // setup the HSQL database with our rules.
        try {

            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:drools-templates", "sa", "");

            try {
                executeInDB(
                        "CREATE TABLE ClassificationRules ( id INTEGER IDENTITY, minAge INTEGER, maxAge INTEGER, previousCategory VARCHAR(256), newCategory VARCHAR(256) )",
                        conn);

                executeInDB("INSERT INTO ClassificationRules VALUES (1, 18, 21, 'NA', 'NA')", conn);
                executeInDB("INSERT INTO ClassificationRules VALUES (2, 22, 30, 'NA', 'BRONZE')", conn);
                executeInDB("INSERT INTO ClassificationRules VALUES (3, 31, 40, 'NA', 'SILVER')", conn);
                executeInDB("INSERT INTO ClassificationRules VALUES (4, 41, 150, 'NA', 'GOLD')", conn);
            } catch (SQLException e) {
                throw new IllegalStateException("Could not initialize in memory database", e);
            }

            Statement sta = conn.createStatement();
            ResultSet rs = sta.executeQuery(
                    "SELECT minAge, maxAge, previousCategory, newCategory " + " FROM ClassificationRules");

            ResultSetGenerator converter = new ResultSetGenerator();
            drl = converter.compile(rs, template);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return drl.getBytes();
    }

    /**
     * Executes an update statement into a database.
     * 
     * @param expression the SQL expression to be executed.
     * @param conn       the connection the the database where the statement will be
     *                   executed.
     * @throws SQLException
     */
    private static void executeInDB(String expression, Connection conn) throws SQLException {
        Statement st;
        st = conn.createStatement();
        int i = st.executeUpdate(expression);
        if (i == -1) {
            System.out.println("db error : " + expression);
        }

        st.close();
    }
}

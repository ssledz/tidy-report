/*
 * Copyright 2013 Sławomir Śledź <slawomir.sledz@sof-tech.pl>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.softech.tidyreport.model.sql;

import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.softech.fluentdbunit.DBExporter;
import pl.softech.fluentdbunit.DBLoader;
import static pl.softech.fluentdbunit.DBLoader.*;
import pl.softech.fluentdbunit.DBUtils;
import pl.softech.tidyreport.model.format.CsvReportFormat;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SimpleJdbcSqlReportTableTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private DataSource createDs() {
        return JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "sa");
    }

    private void createTable(DataSource ds, String ddl) throws SQLException {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            Statement st = conn.createStatement();
            st.executeUpdate(ddl);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }

    @Test
    public void testSimpleJdbcSqlReportTable() throws Exception {

        syso("testSimpleJdbcSqlReportTable");

        DataSource ds = createDs();

        createTable(ds, "CREATE TABLE account (id int, login varchar)");
        
        DBLoader.forDB(ds).cleanInsert(dataSet(table("account", //
                cols("id", "login"), //
                row("1", "Login1"), //
                row("2", "Login2"), //
                row("3", "Login3") //
                )));
        
        syso(DBUtils.dataSet2String(DBExporter.forDB(ds).table("account").getDataSet()));
        
        SimpleJdbcSqlReportTable report = new SimpleJdbcSqlReportTable(ds,
                "SELECT id,login FROM account WHERE id in (?,?)", 1, 2);

        CsvReportFormat out = new CsvReportFormat(";");
        report.write(out);

        String result = out.build().trim();
        syso(result);
        StringBuilder expected = new StringBuilder();
        expected.append("id;login\n");
        expected.append("1;Login1\n");
        expected.append("2;Login2");
        Assert.assertEquals(expected.toString().toLowerCase(), result.toLowerCase());

        report = new SimpleJdbcSqlReportTable(ds, "SELECT id,login FROM account WHERE id in (:id1,:id2)");
        Map<String, Object> parameters = new ImmutableMap.Builder<String, Object>().put("id1", 1).put("id2", 3).build();
        report.setParameters(parameters);
        out = new CsvReportFormat(";");
        report.write(out);

        result = out.build().trim();

        expected = new StringBuilder();
        expected.append("id;login\n");
        expected.append("1;Login1\n");
        expected.append("3;Login3");
        Assert.assertEquals(expected.toString().toLowerCase(), result.toLowerCase());
    }

    private void syso(String result) {
        System.out.println(result);
    }

    @Test
    public void testGetPlaceholders() {

        syso("testGetPlaceholders");

        String query = "SELECT * FROM KONTO WHERE id = :id and instr(:sflags,:flags) = :string dd :ss :";

        String[] expectedPlaceHolders = { //
            //
            ":id", //
            ":sflags", //
            ":flags", //
            ":string", //
            ":ss" //
        };

        Assert.assertArrayEquals(expectedPlaceHolders, SimpleJdbcSqlReportTable.getPlaceholders(query));
    }

    @Test
    public void testRemovePlaceHolders() {
        syso("testRemovePlaceHolders");

        String query = "SELECT * FROM KONTO WHERE id = :id and instr(:sflags,:flags) = :string dd :ss :";
        String expectedQuery = "SELECT * FROM KONTO WHERE id = ? and instr(?,?) = ? dd ? :";

        Assert.assertEquals(expectedQuery,
                SimpleJdbcSqlReportTable.removePlaceHolders(query, SimpleJdbcSqlReportTable.getPlaceholders(query)));
    }

    @Test
    public void testGuessArgs() {
        syso("testGuessArgs");

        SimpleJdbcSqlReportTable report = new SimpleJdbcSqlReportTable(null,
                "SELECT id,login FROM konto WHERE ID in (:id1, :id2, :id1)");

        report.setParameters(new ImmutableMap.Builder<String, Object>() //
                .put("id1", 11) //
                .put("id2", "2222") //
                .put("id3", "333") //
                .build());

        Object[] expectedArgs = {
            11, "2222", 11
        };

        Assert.assertArrayEquals(expectedArgs, report.guessArgs());

    }
}

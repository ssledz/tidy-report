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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import pl.softech.tidyreport.model.IColumnDefinition;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SqlColumnBuilder {

    private class SqlColumn implements IColumnDefinition<SqlRow, String> {

        private final int index;
        private final String name;

        private SqlColumn(int index) throws SQLException {
            super();
            this.index = index;
            this.name = metaData.getColumnLabel(index);
        }

        @Override
        public String getName() {
            return name;

        }

        @Override
        public String getValue(SqlRow record) {
            return format(record.getValueAt(index));
        }
    }

    private String format(Object obj) {
        if (obj instanceof Number) {
            return nf.format(obj);
        }
        return obj.toString();
    }
    private final ResultSetMetaData metaData;
    private final NumberFormat nf;

    public SqlColumnBuilder(ResultSetMetaData metaData) {
        this.metaData = metaData;
        nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
    }

    public IColumnDefinition<SqlRow, String>[] build() throws SQLException {

        @SuppressWarnings("unchecked")
        IColumnDefinition<SqlRow, String>[] columns = new IColumnDefinition[metaData.getColumnCount()];

        for (int i = 0; i < metaData.getColumnCount(); i++) {

            columns[i] = new SqlColumn(i + 1);

        }
        return columns;

    }
}

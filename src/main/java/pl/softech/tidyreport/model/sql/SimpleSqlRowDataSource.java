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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import pl.softech.tidyreport.model.IRowDataSource;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SimpleSqlRowDataSource implements IRowDataSource<SqlRow> {

	private final ResultSet rs;
	private final List<SqlRow> rows;
	private Iterator<SqlRow> it;

	public SimpleSqlRowDataSource(ResultSet rs) {
		this.rs = rs;
		this.rows = new LinkedList<SqlRow>();

		try {
			init();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	private void init() throws SQLException {

		while (rs.next()) {
			rows.add(new SqlRow(getValues()));
		}

		it = rows.iterator();
	}

	private Object value(Object value) {
		return value == null ? NullValue.INSTANCE : value;
	}
	
	private Object[] getValues() throws SQLException {

		ResultSetMetaData md = rs.getMetaData();

		Object[] ret = new Object[md.getColumnCount()];

		for (int i = 0; i < md.getColumnCount(); i++) {
			ret[i] = value(rs.getObject(i + 1));
		}

		return ret;
	}

	@Override
	public SqlRow readNext() {

		if (!it.hasNext()) {
			return null;
		}

		return it.next();

	}
}

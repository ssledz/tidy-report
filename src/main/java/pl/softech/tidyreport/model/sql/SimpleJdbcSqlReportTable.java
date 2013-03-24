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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import pl.softech.tidyreport.model.IReportOutput;
import pl.softech.tidyreport.model.IReportTableBuilder;
import pl.softech.tidyreport.model.ReportTable;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SimpleJdbcSqlReportTable implements IReportTableBuilder<SqlRow, String> {

	private final DataSource ds;
	private final String sql;

	private Map<String, Object> parameters;

	public SimpleJdbcSqlReportTable(DataSource ds, String sql, Object... args) {
		this.ds = ds;
		this.sql = sql;

		if (args != null) {
			parameters = new LinkedHashMap<String, Object>();
			int index = 0;
			for (Object arg : args) {
				parameters.put("param" + index, arg);
				index++;
			}
		}
	}

	static String[] getPlaceholders(String sql) {

		List<String> phs = new LinkedList<String>();

		int start = 0;

		while ((start = sql.indexOf(":", start)) != -1) {

			int end = start;
			for (end++; end < sql.length(); end++) {

				char c = sql.charAt(end);

				if (!Character.isLetter(c) && !Character.isDigit(c)) {
					break;
				}

			}

			//Samych : nie chcemy
			if (end - 1 > start) {
				phs.add(sql.substring(start, end));
			}
			start++;

		}

		return phs.isEmpty() ? null : phs.toArray(new String[0]);
	}

	static String removePlaceHolders(String sql, String[] placeHolders) {

		if (placeHolders == null) {
			return sql;
		}

		for (String ph : placeHolders) {

			sql = sql.replace(ph, "?");
		}

		return sql;

	}

	Object[] guessArgs() {

		if (parameters == null) {
			return null;
		}

		String[] placeHolders = getPlaceholders(sql);

		if (placeHolders == null) {
			return parameters.values().toArray(new Object[0]);
		}

		Object[] args = new Object[placeHolders.length];

		for (int i = 0; i < placeHolders.length; i++) {
			// placeHolder ma postac ":" + jakis identyfikator a w parametrach
			// mamy same identyfikatory
			args[i] = parameters.get(placeHolders[i].substring(1));
		}

		return args;

	}

	@Override
	public ReportTable<SqlRow, String> createReportTable() {
		try {
			return internalCreateReportTable(removePlaceHolders(sql, getPlaceholders(sql)), guessArgs());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private ReportTable<SqlRow, String> internalCreateReportTable(String sql, Object[] args) throws SQLException {

		Connection conn = ds.getConnection();

		try {
			conn.setReadOnly(true);
			PreparedStatement pstm = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					pstm.setObject(i + 1, args[i]);
				}
			}
			ResultSet rs = pstm.executeQuery();
			return new ReportTable<SqlRow, String>(new SimpleSqlRowDataSource(rs), new SqlColumnBuilder(
					rs.getMetaData()).build());
		} finally {
			conn.setReadOnly(false);
			conn.close();
		}

	}

	public void write(IReportOutput reportOutput) {
		createReportTable().write(reportOutput);
	}

	@Override
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}
}

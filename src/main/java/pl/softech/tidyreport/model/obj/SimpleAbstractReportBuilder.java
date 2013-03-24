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
package pl.softech.tidyreport.model.obj;

import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pl.softech.tidyreport.model.IColumnDefinition;
import pl.softech.tidyreport.model.IReportOutput;
import pl.softech.tidyreport.model.IReportTableBuilder;
import pl.softech.tidyreport.model.ReportTable;
import pl.softech.tidyreport.model.format.CsvReportFormat;
import pl.softech.tidyreport.model.format.JSonReportFormat;


/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public abstract class SimpleAbstractReportBuilder implements IReportTableBuilder<SimpleRow, String> {

	private class ColumnNameProvider {

		private String[] names;

		public void setColumns(String... names) {
			this.names = names;
		}

		public String getColumnNameAt(int index) {
			if (names == null || names.length <= index) {
				return "Column" + index;
			}

			return names[index];
		}

	}

	private final List<SimpleRow> rows;
	private SimpleRow currentRow;
	private IColumnDefinition<SimpleRow, String>[] columns;
	private Map<String, Object> parameters;

	public SimpleAbstractReportBuilder() {
		rows = new LinkedList<SimpleRow>();
	}

	protected void addRow(Object... values) {
		addRow();
		for(int i = 0; i < values.length; i++) {
			setValueAtColumn(i, values[i]);
		}
	}
	
	protected void addRow() {
		int size = 1;

		if (currentRow != null) {
			size = currentRow.getColumnSize();
		}

		currentRow = new SimpleRow(size);
		rows.add(currentRow);
	}

	protected void setValueAtColumn(int column, Object value) {
		currentRow.setValueAt(column, value);
	}

	protected void setColumns(IColumnDefinition<SimpleRow, String>... columns) {
		this.columns = columns;
	}

	protected void bindAtColumn(int column, IColumnDefinition<SimpleRow, String> columnDefinition) {
		columns[column] = columnDefinition;
	}

	protected void setColumns(String... names) {
		ColumnNameProvider cnp = new ColumnNameProvider();
		cnp.setColumns(names);
		initColumns(cnp);
	}

	@SuppressWarnings("unchecked")
	private void initColumns(ColumnNameProvider columnNameProvider) {

		Preconditions.checkArgument(rows.size() > 0, "You have to add some rows before column init");

		SimpleRow row = rows.get(0);
		
		//Szukamy wiersza z najwieksza iloscia kolumn
		for(SimpleRow r : rows) {
			if(r.getColumnSize() > row.getColumnSize()) {
				row = r;
			}
		}

		ColumnForClassDefinitionFactory factory = new ColumnForClassDefinitionFactory();

		columns = new IColumnDefinition[row.getColumnSize()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = factory.create(row.getClassAt(i), columnNameProvider.getColumnNameAt(i), i);
		}

	}

	protected abstract void build() throws Exception;

	@Override
	public ReportTable<SimpleRow, String> createReportTable() {
		try {
			build();

			if (columns == null) {
				initColumns(new ColumnNameProvider());
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new ReportTable<SimpleRow, String>(new SimpleRowDataSource(rows), columns);
	}

	@Override
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;

	}

	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public static void main(String[] args) {
		IReportOutput out = new CsvReportFormat(";");
		out = new JSonReportFormat();

		SimpleAbstractReportBuilder builder = new SimpleAbstractReportBuilder() {

			@Override
			protected void build() throws Exception {

				addRow();

				setValueAtColumn(0, "Ala");
				setValueAtColumn(1, "ma");
				setValueAtColumn(2, "kota");
				setValueAtColumn(3, 1234);
				setValueAtColumn(4, 1234.34566);
				setValueAtColumn(5, 1234);

				addRow();

				setValueAtColumn(0, "Ala");
				setValueAtColumn(1, "ma 2");
				setValueAtColumn(2, "koty");
				setValueAtColumn(3, 1);
				setValueAtColumn(4, 1.3);
				setValueAtColumn(5, 1234);

				setColumns("A", "B", "C", "D", "E", "F");

			}

		};

		builder.createReportTable().write(out);
		System.out.println(out.build());
	}

}

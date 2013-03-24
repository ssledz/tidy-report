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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pl.softech.tidyreport.model.IColumnDefinition;
import pl.softech.tidyreport.model.IReportOutput;
import pl.softech.tidyreport.model.IReportTableBuilder;
import pl.softech.tidyreport.model.IRowDataSource;
import pl.softech.tidyreport.model.ReportTable;
import pl.softech.tidyreport.model.format.CsvReportFormat;
import pl.softech.tidyreport.model.format.JSonReportFormat;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public abstract class EntityAbstractReportBuilder<R> implements IReportTableBuilder<R, String> {

	private class ColumnImpl implements IColumnDefinition<R, String>, Comparable<ColumnImpl> {

		private final String name;
		private final int order;
		private final AccessibleObject ao;

		private ColumnImpl(String name, int order, AccessibleObject ao) {
			this.name = name;
			this.order = order;
			this.ao = ao;
			this.ao.setAccessible(true);
		}

		@Override
		public String getName() {
			return name;
		}

		private String format(Object obj) {
			return obj == null ? "-" : obj.toString();
		}

		@Override
		public String getValue(R record) {

			try {
				if (ao instanceof Field) {
					return format(((Field) ao).get(record));
				}

				if (ao instanceof Method) {
					return format(((Method) ao).invoke(record));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return null;
		}

		@Override
		public int compareTo(ColumnImpl o) {
			return order - o.order;
		}

	}

	private final List<R> rows;
	private IColumnDefinition<R, String>[] columns;
	private Map<String, Object> parameters;

	@SuppressWarnings("unchecked")
	public EntityAbstractReportBuilder() {
		this.rows = new LinkedList<R>();
		this.columns = new IColumnDefinition[0];
	}

	protected abstract void build() throws Exception;

	protected void addRow(R row) {
		rows.add(row);
	}

	@Override
	public ReportTable<R, String> createReportTable() {

		try {
			build();
			initColumns();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new ReportTable<R, String>(new IRowDataSource<R>() {

			private final Iterator<R> it = rows.iterator();

			@Override
			public R readNext() {
				if (!it.hasNext()) {
					return null;
				}

				return it.next();
			}
		}, columns);
	}

	@SuppressWarnings("unchecked")
	private void initColumns() {

		if (rows.isEmpty()) {
			return;
		}

		R row = rows.get(0);

		List<IColumnDefinition<R, String>> columns = new LinkedList<IColumnDefinition<R, String>>();

		for (Field f : row.getClass().getDeclaredFields()) {
			ReportColumn rc = f.getAnnotation(ReportColumn.class);
			if(rc == null) {
				continue;
			}
			columns.add(new ColumnImpl(rc.name(), rc.order(), f));
		}

		for (Method m : row.getClass().getDeclaredMethods()) {
			ReportColumn rc = m.getAnnotation(ReportColumn.class);
			if(rc == null) {
				continue;
			}
			columns.add(new ColumnImpl(rc.name(), rc.order(), m));
		}
		
		this.columns = columns.toArray(new IColumnDefinition[0]);
		Arrays.sort(this.columns);
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

		class SampleRecord {
			@SuppressWarnings("unused")
			@ReportColumn(name = "nazwa", order = 2)
			private String name;
			@SuppressWarnings("unused")
			@ReportColumn(name = "licznik", order=1)
			public int cnt;

			private SampleRecord(String name, int cnt) {
				this.name = name;
				this.cnt = cnt;
			}
		}

		EntityAbstractReportBuilder<SampleRecord> report = new EntityAbstractReportBuilder<SampleRecord>() {

			@Override
			protected void build() throws Exception {
				addRow(new SampleRecord(null, 1));
				addRow(new SampleRecord("Nazwa 2", 13));
				addRow(new SampleRecord("Nazwa 3", 11));
				addRow(new SampleRecord("Nazwa 4", 111));
			}
		};

		IReportOutput out1 = new CsvReportFormat(";");
		IReportOutput out2 = new JSonReportFormat();
		report.createReportTable().write(out1);
		report.createReportTable().write(out2);
		System.out.println(out1.build());
		System.out.println(out2.build());

	}

}

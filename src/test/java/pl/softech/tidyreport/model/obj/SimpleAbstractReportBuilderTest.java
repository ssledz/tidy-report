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

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import pl.softech.tidyreport.model.IColumnDefinition;
import pl.softech.tidyreport.model.ReportTable;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SimpleAbstractReportBuilderTest {

	private void assertBuilder(Object[][] expectedRows, SimpleAbstractReportBuilder builder) {
		assertBuilder(expectedRows, null, builder);
	}

	private void assertBuilder(Object[][] expectedRows, String[] expectedHeader, SimpleAbstractReportBuilder builder) {

		SimpleReportOutput out = new SimpleReportOutput(expectedRows.length, expectedRows[0].length);
		ReportTable<SimpleRow, String> table = builder.createReportTable();
		table.write(out);
		out.build();
		Assert.assertArrayEquals(expectedRows, out.getRows());
		if (expectedHeader != null) {
			Assert.assertArrayEquals(expectedHeader, out.getHeader());
		}
	}

	@Test
	public void testAddRowObjectArray() {

		final Object[][] expectedRows = { //
		//
				{ "Value1", "Value2", "Value3", "Value4" }, //
				{ "Value5", "Value6", "Value7", "Value8" }, //
				{ "Value9", "Value10", "Value11", "Value12" } //
		};

		SimpleAbstractReportBuilder builder = new SimpleAbstractReportBuilder() {
			@Override
			protected void build() throws Exception {
				for (Object[] row : expectedRows) {
					addRow(row);
				}
			}
		};

		assertBuilder(expectedRows, builder);
	}

	@Test
	public void testSetValueAtColumn() {

		SimpleAbstractReportBuilder builder = new SimpleAbstractReportBuilder() {
			@Override
			protected void build() throws Exception {
				addRow();
				setValueAtColumn(2, "ValueAtRow1Colum2");
				addRow();
				setValueAtColumn(4, "ValueAtRow2Colum4");
			}
		};

		Object[][] expectedRows = { //
		//
				{ "NULL", "NULL", "ValueAtRow1Colum2", "NULL", "NULL" }, //
				{ "NULL", "NULL", "NULL", "NULL", "ValueAtRow2Colum4" }, //
		};

		assertBuilder(expectedRows, builder);
	}

	@Test
	public void testSetColumns() {

		SimpleAbstractReportBuilder builder = new SimpleAbstractReportBuilder() {
			@Override
			protected void build() throws Exception {
				addRow();
				setValueAtColumn(2, "ValueAtRow1Colum2");
				addRow();
				setValueAtColumn(4, "ValueAtRow2Colum4");

				setColumns("MyColumn0", "MyColumn1", "MyColumn2");
			}
		};

		String[] expectedHeader = { //
		//
				"MyColumn0", "MyColumn1", "MyColumn2", "Column3", "Column4" //
		};

		Object[][] expectedRows = { //
		//
				{ "NULL", "NULL", "ValueAtRow1Colum2", "NULL", "NULL" }, //
				{ "NULL", "NULL", "NULL", "NULL", "ValueAtRow2Colum4" }, //
		};

		assertBuilder(expectedRows, expectedHeader, builder);

		builder = new SimpleAbstractReportBuilder() {
			@SuppressWarnings("unchecked")
			@Override
			protected void build() throws Exception {
				addRow();
				setValueAtColumn(2, "ValueAtRow1Colum2");
				addRow();
				setValueAtColumn(4, "ValueAtRow2Colum4");

				setColumns(new IColumnDefinition<SimpleRow, String>() {

					@Override
					public String getName() {
						return "MyCustomColumn2";
					}

					@Override
					public String getValue(SimpleRow record) {
						Object value = record.getValueAt(2);
						return value == null ? "NULL" : value.toString();
					}

				});
			}
		};

		String[] expectedHeader2 = { //
		//
		"MyCustomColumn2"//
		};

		Object[][] expectedRows2 = { //
		//
				{ "ValueAtRow1Colum2" }, //
				{ "NULL" }, //
		};

		assertBuilder(expectedRows2, expectedHeader2, builder);

	}

	@Test
	public void testBindAtColumn() {

		SimpleAbstractReportBuilder builder = new SimpleAbstractReportBuilder() {
			@Override
			protected void build() throws Exception {
				addRow();
				setValueAtColumn(2, "ValueAtRow1Colum2");
				addRow();
				setValueAtColumn(4, "ValueAtRow2Colum4");

				setColumns("MyColumn0", "MyColumn1", "MyColumn2");
				bindAtColumn(4, new IColumnDefinition<SimpleRow, String>() {

					@Override
					public String getName() {
						return "XXX: Column4";
					}

					@Override
					public String getValue(SimpleRow record) {
						Object value = record.getValueAt(4);
						return value == null ? "XXX: NULL" : "XXX: " + value.toString();
					}

				});
			}
		};

		String[] expectedHeader = { //
		//
				"MyColumn0", "MyColumn1", "MyColumn2", "Column3", "XXX: Column4" //
		};

		Object[][] expectedRows = { //
		//
				{ "NULL", "NULL", "ValueAtRow1Colum2", "NULL", "XXX: NULL" }, //
				{ "NULL", "NULL", "NULL", "NULL", "XXX: ValueAtRow2Colum4" }, //
		};

		assertBuilder(expectedRows, expectedHeader, builder);
	}

	@Test
	public void testSetParameters() {
		SimpleAbstractReportBuilder builder = new SimpleAbstractReportBuilder() {
			@Override
			protected void build() throws Exception {
				addRow();
				setValueAtColumn(2, "ValueAtRow1Colum2: arg1=" + getParameters().get("arg1"));
				addRow();
				setValueAtColumn(4, "ValueAtRow2Colum4: arg2=" + getParameters().get("arg2"));
			}
		};

		Object[][] expectedRows = { //
		//
				{ "NULL", "NULL", "ValueAtRow1Colum2: arg1=value1", "NULL", "NULL" }, //
				{ "NULL", "NULL", "NULL", "NULL", "ValueAtRow2Colum4: arg2=value2" }, //
		};

		builder.setParameters(new ImmutableMap.Builder<String, Object>() //
				.put("arg1", "value1") //
				.put("arg2", "value2") //
				.build());

		assertBuilder(expectedRows, builder);
	}

}

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
package pl.softech.tidyreport.model.format;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class CsvReportFormatTest {

	@Test
	public void test() {

		syso("testCsvReportFormat");

		String[] columnNames = { "Column1", "Column2", "Column3", "Column4", "Column5" };
		String[][] rows = {//
		//
				{ "Row1Column1", "Row1Column2", "Row1Column3", "Row1Column4", "Row1Column5" }, //
				{ "Row2Column1", "Row2Column2", "Row2Column3", "Row2Column4", "Row2Column5" }, //
				{ "Row3Column1", "Row3Column2", "Row3Column3", "Row3Column4", "Row3Column5" } //
		};

		CsvReportFormat csv = new CsvReportFormat(";");

		csv.header(columnNames);

		SimpleColumnDefinition[] columns = SimpleColumnDefinition.create(columnNames);
		for (int i = 0; i < rows.length; i++) {
			csv.nextRow();
			for (int j = 0; j < rows[i].length; j++) {
				csv.nextColumn(rows[i][j], columns[j]);
			}
		}

		StringBuilder expected = new StringBuilder();
		expected.append("Column1;Column2;Column3;Column4;Column5\n");
		expected.append("Row1Column1;Row1Column2;Row1Column3;Row1Column4;Row1Column5\n");
		expected.append("Row2Column1;Row2Column2;Row2Column3;Row2Column4;Row2Column5\n");
		expected.append("Row3Column1;Row3Column2;Row3Column3;Row3Column4;Row3Column5");

		String report = csv.build();

		syso(report);

		Assert.assertEquals(expected.toString(), report);

	}

	private void syso(String string) {
		System.out.println(string);
	}

}

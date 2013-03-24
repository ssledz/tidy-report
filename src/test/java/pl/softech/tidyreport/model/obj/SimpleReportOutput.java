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

import pl.softech.tidyreport.model.IColumnDefinition;
import pl.softech.tidyreport.model.IReportOutput;


/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SimpleReportOutput implements IReportOutput {

	private final Object[][] rows;
	private String[] header;

	private int i = -1, j;

	public SimpleReportOutput(int rows, int colls) {
		this.rows = new Object[rows][colls];
	}

	@Override
	public void nextRow() {
		i++;
		j = 0;
	}

	@SuppressWarnings("hiding")
	@Override
	public <T, String> void nextColumn(T record,
			IColumnDefinition<T, String> column) {
		rows[i][j++] = column.getValue(record);
	}

	@Override
	public void header(String[] columnNames) {
		this.header = columnNames;
	}

	public Object[][] getRows() {
		return rows;
	}

	public String[] getHeader() {
		return header;
	}

	@Override
	public String build() {
		return null;
	}

}

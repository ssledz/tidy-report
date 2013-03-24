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


import com.google.common.base.Joiner;
import pl.softech.tidyreport.model.IColumnDefinition;
import pl.softech.tidyreport.model.IReportOutput;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class CsvReportFormat implements IReportOutput {

	private final StringBuilder report;
	private StringBuilder line;
	private final String delimiter;

	public CsvReportFormat(String delimiter) {
		report = new StringBuilder();
		this.delimiter = delimiter;
		line = new StringBuilder();
	}

	@Override
	public void nextRow() {
		if(line.length() == 0) {
			return;
		}
		report.append("\n").append(line);
		line = new StringBuilder();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T, String> void nextColumn(T record, IColumnDefinition<T, String> column) {
		
		if(line.length() == 0) {
			line.append(column.getValue(record));
			return;
		}
		
		line.append(delimiter).append(column.getValue(record));
	}
	
	@Override
	public String build() {
		nextRow();
		return report.toString();
	}

	@Override
	public void header(String[] columnNames) {
		report.append(Joiner.on(delimiter).join(columnNames));
	}

}

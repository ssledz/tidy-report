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

import pl.softech.tidyreport.model.IColumnDefinition;
import pl.softech.tidyreport.model.IReportOutput;

/**
 * { rows : [ { column1 : "abc", column2 : "dfg" }, { column1 : "abcd", column2
 * : "dfgh" } ] }
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class JSonReportFormat implements IReportOutput {

	private final StringBuilder json;
	private StringBuilder line;
	private final String delimiter = ",";

	public JSonReportFormat() {
		json = new StringBuilder();
		line = new StringBuilder();

		json.append("{ rows : [");

	}

	@Override
	public void header(String[] columnNames) {
	}

	private StringBuilder quote(String value) {
		StringBuilder builder = new StringBuilder();
		builder.append("\"").append(value).append("\"");
		return builder;
	}

	private StringBuilder jsonParam(String name, String value) {
		StringBuilder builder = new StringBuilder();
		builder.append(quote(name)).append(" : ").append(quote(value));
		return builder;
	}

	@Override
	public <T, R> void nextColumn(T record, IColumnDefinition<T, R> column) {

		if (line.length() == 0) {
			line.append(jsonParam(column.getName(), column.getValue(record).toString()));
			return;
		}

		line.append(delimiter).append(jsonParam(column.getName(), column.getValue(record).toString()));

	}

	private boolean addComa = false;
	
	@Override
	public void nextRow() {
		if (line.length() == 0) {
			return;
		}
		
		if(addComa) {
			json.append(",");
		}
		
		json.append("{").append(line).append("}");
		line = new StringBuilder();
		addComa = true;

	}

	@Override
	public String build() {

		nextRow();
		json.append("] }");

		return json.toString();
	}

}

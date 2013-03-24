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
public class JSonReportFormatTest {

	private void syso(String string) {
		System.out.println(string);
	}
	
	private String whitespaceRemove(String arg) {
		return arg.replaceAll("\\s+", "");
	}
	
	@Test
	public void test() {
		
		syso("testJSonReportFormatTest");

		String[] columnNames = { "Column1", "Column2"};
		String[][] rows = {//
		//
				{ "Row1Column1", "Row1Column2" }, //
				{ "Row2Column1", "Row2Column2" }, //
				{ "Row3Column1", "Row3Column2" } //
		};
		
		JSonReportFormat json = new JSonReportFormat();
		
		SimpleColumnDefinition[] columns = SimpleColumnDefinition.create(columnNames);
		for (int i = 0; i < rows.length; i++) {
			json.nextRow();
			for (int j = 0; j < rows[i].length; j++) {
				json.nextColumn(rows[i][j], columns[j]);
			}
		}

		StringBuilder expected = new StringBuilder();
		expected.append("{ rows : [");
		expected.append("{\"Column1\" : \"Row1Column1\",\"Column2\" : \"Row1Column2\"},");
		expected.append("{\"Column1\" : \"Row2Column1\",\"Column2\" : \"Row2Column2\"},");
		expected.append("{\"Column1\" : \"Row3Column1\",\"Column2\" : \"Row3Column2\"}");
		expected.append("] }");

		String report = json.build();

		syso(whitespaceRemove(report));

		Assert.assertEquals(whitespaceRemove(expected.toString()), whitespaceRemove(report));
		
	}

}

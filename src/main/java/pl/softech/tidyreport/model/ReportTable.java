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
package pl.softech.tidyreport.model;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class ReportTable<T, R> {

	private final IRowDataSource<T> ds;
	private final IColumnDefinition<T, R>[] columns;

	public ReportTable(IRowDataSource<T> ds, IColumnDefinition<T, R>... columns) {
		this.ds = ds;
		this.columns = columns;
	}

	private String[] toHeaderNames(IColumnDefinition<T, R>[] columns) {
		
		@SuppressWarnings("unchecked")
		Collection<String> col = Collections2.transform(Arrays.asList(columns), new Function<IColumnDefinition<T,R>, String>() {
			@Override
			public String apply(IColumnDefinition<T, R> input){
				return input.getName();
			}
		});
		
		return col.toArray(new String[0]);
	}
	
	public void write(IReportOutput reportOutput) {
		
		reportOutput.header(toHeaderNames(columns));
		
		T record;

		while ((record = ds.readNext()) != null) {
			reportOutput.nextRow();
			for (IColumnDefinition<T, R> column : columns) {
				reportOutput.nextColumn(record, column);
			}
			
		}
	}
}

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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import pl.softech.tidyreport.model.IColumnDefinition;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class ColumnForClassDefinitionFactory {

	private interface IFormatter {

		String format(Object obj);

	}

	private final NumberFormat snf;

	ColumnForClassDefinitionFactory() {

		snf = DecimalFormat.getInstance();
		snf.setMaximumFractionDigits(2);
		snf.setGroupingUsed(false);

	}

	private IColumnDefinition<SimpleRow, String> createColumn(final String columnName, final int index, final IFormatter formatter) {
		return new IColumnDefinition<SimpleRow, String>() {

			@Override
			public String getName() {
				return columnName;
			}

			@Override
			public String getValue(SimpleRow record) {
				return formatter.format(record.getValueAt(index));
			}
		};
	}

	private Class<?>[] toArray(Class<?>... clazz) {
		return clazz;
	}

	public IColumnDefinition<SimpleRow, String> create(Class<?> clazz, String columnName, int index) {

		IFormatter formatter = new IFormatter() {

			@Override
			public String format(Object obj) {
				if(obj == null) {
					return "NULL";
				}
				return obj.toString();
			}
		};

		if (Arrays.asList(toArray(Float.class, Double.class)).contains(clazz)) {

			formatter = new IFormatter() {

				@Override
				public String format(Object obj) {
					
					if(obj == null) {
						return "NULL";
					}
					
					return snf.format(obj);
				}
			};

		}

		return createColumn(columnName, index, formatter);
	}

}

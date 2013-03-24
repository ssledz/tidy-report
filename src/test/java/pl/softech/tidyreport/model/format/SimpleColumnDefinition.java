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

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SimpleColumnDefinition implements IColumnDefinition<String, String> {

	private final String name;

	SimpleColumnDefinition(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue(String record) {
		return record;
	}

	static SimpleColumnDefinition[] create(String... columns) {
		SimpleColumnDefinition[] ret = new SimpleColumnDefinition[columns.length];

		int i = 0;
		for (String column : columns) {
			ret[i++] = new SimpleColumnDefinition(column);
		}

		return ret;

	}

}

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

import java.util.Arrays;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class SimpleRow {

	private Object[] values;

	public Object getValueAt(int column) {
		
		if(column >= getColumnSize()) {
			return null;
		}
		
		return values[column];
	}

	private void resize(int newSize) {
		values = Arrays.copyOf(values, newSize);
	}
	
	public void setValueAt(int column, Object obj) {
		if(values.length <= column) {
			resize(column + 1);
		}
		values[column] = obj;
	}
	
	public Class<?> getClassAt(int column) {
		
		if(column >= getColumnSize()) {
			return null;
		}
		
		if(values[column] == null) {
			return null;
		}
		
		return values[column].getClass();
	}
	
	public int getColumnSize() {
		return values.length;
	}

	SimpleRow(int columnSize) {
		this.values = new Object[columnSize];
	}

}

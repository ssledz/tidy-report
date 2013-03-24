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
package pl.softech.tidyreport;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class ReportParameter {

	private final String name;
	private final String value;
	
	private ReportParameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public static ReportParameter param(String name, String value) {
		return new ReportParameter(name, value);
	}
	
	public static String[] names(ReportParameter[] p) {
		
		if(p == null) {
			return null;
		}
		
		String[] ret = new String[p.length];
		for(int i = 0; i < p.length; i++) {
			ret[i] = p[i].getName();
		}
		
		return ret;
		
	}
	
}

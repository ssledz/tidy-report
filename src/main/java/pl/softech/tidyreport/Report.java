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

import java.util.Map;
import pl.softech.tidyreport.model.IReportOutput;
import pl.softech.tidyreport.model.IReportTableBuilder;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public abstract class Report {

	private static int ID_SEQ = 1;

	private final int id;
	private final String name;
	private final String description;
	private final String redmineUrl;
	private final String fileName;

	private final String[] parameterNames;

	private final String path;

	public Report(String name, String description, String fileName, ReportParameter... parameters) {
		this(name, description, fileName, "", parameters);
	}

	public Report(String name, String description, String fileName, String redmineUrl, ReportParameter... parameters) {
		this.id = ID_SEQ++;
		this.name = name;
		this.fileName = fileName;
		this.description = description;
		this.redmineUrl = redmineUrl;
		this.path = createPath(parameters);
		this.parameterNames = ReportParameter.names(parameters);
	}

	private String createPath(ReportParameter[] parameters) {
		String path = "" + id;

		if (parameters != null) {
			StringBuilder builder = new StringBuilder();
			for (ReportParameter p : parameters) {
				builder.append(";").append(p.getName()).append("=").append(p.getValue());
			}
			path = path + builder.toString();
		}

		return path;
	}

	public int getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getFileName() {
		return fileName;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getRedmineUrl() {
		return redmineUrl;
	}

	public synchronized void write(IReportOutput output) {
		write(null, output);
	}

	public abstract IReportTableBuilder<?, String> create();

	public void write(Map<String, Object> parameters, IReportOutput output) {

		IReportTableBuilder<?, String> builder = create();
		if (parameters != null && !parameters.isEmpty()) {
			builder.setParameters(parameters);
		}
		builder.createReportTable().write(output);
	}

	public String[] getParameterNames() {
		return parameterNames;
	}
}

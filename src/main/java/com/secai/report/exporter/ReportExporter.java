package com.secai.report.exporter;

import com.secai.model.Finding;
import java.util.List;

public interface ReportExporter {
    String export(List<Finding> findings);
    String getExtension();
}

package com.djimgou.core.export;

import java.util.List;

public interface DataExportParser {
    <T> List<ExportHeader> parseColumn(Class<T> classe);

    List<List<?>> parse(List<?> list);

    List<List<?>> parse(List<?> list, boolean ignoreHeader);
}

package com.djimgou.core.export;

import com.djimgou.core.annotations.ColumnExport;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataExportParserImpl implements DataExportParser {

    @Override
    public <T> List<ExportHeader> parseColumn(Class<T> classe) {
        Class classe2 = classe.isInterface() ? classe : classe.getInterfaces()[0];
        return Stream.of(ReflectionUtils.getAllDeclaredMethods(classe2))
                .filter(method -> method.getName().startsWith("get"))
                .sorted((o1, o2) -> {
                    if (o1.isAnnotationPresent(ColumnExport.class)) {
                        ColumnExport an1 = o1.getAnnotation(ColumnExport.class);
                        ColumnExport an2 = o2.getAnnotation(ColumnExport.class);
                        return an1.order() - an2.order();
                    }
                    return Integer.MAX_VALUE;
                }).map(field -> {
                    if (field.isAnnotationPresent(ColumnExport.class)) {
                        ColumnExport an = field.getAnnotation(ColumnExport.class);
                        return new ExportHeader(an.value(), field);
                    }
                    return new ExportHeader(field.getName().substring(2), field);
                }).collect(Collectors.toList());
       /* Stream<Field> s = AppUtils.getFieldsAsStream(classe, field -> true);
        return s.sorted((o1, o2) -> {
            if (o1.isAnnotationPresent(ColumnExport.class)) {
                ColumnExport an1 = o1.getAnnotation(ColumnExport.class);
                ColumnExport an2 = o2.getAnnotation(ColumnExport.class);
                return an1.order() - an2.order();
            }
            return Integer.MAX_VALUE;
        }).map(field -> {
            if (field.isAnnotationPresent(ColumnExport.class)) {
                ColumnExport an = field.getAnnotation(ColumnExport.class);
                return new ExportHeader(an.value(), field);
            }
            return new ExportHeader(field.getName().substring(2), field);
        }).collect(Collectors.toList());*/
    }

    @Override
    public List<List<?>> parse(List<?> list) {
        List<List<?>> results = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<ExportHeader> columns = parseColumn(list.get(0).getClass());

        results.add(columns.stream().map(ExportHeader::getText).collect(Collectors.toList()));

        List<List<?>> res1 = list.stream().map(o ->
                columns.stream().map(exportHeader -> exportHeader.peekValue(o)
                ).collect(Collectors.toList())).collect(Collectors.toList());
        results.addAll(res1);
        return results;
    }
}

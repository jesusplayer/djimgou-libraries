package com.act.core.testing.app.model.generator;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Stream;

public class FactureCodeGenerator implements IdentifierGenerator, Configurable {

    private String prefix;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        String query = String.format("select %s from %s",
                session.getEntityPersister(obj.getClass().getName(), obj)
                        .getIdentifierPropertyName(),
                obj.getClass().getSimpleName());

        Stream ids = session.createQuery(query).stream();

        final String SEP = "-";
        Long max = ids.map(o -> o.toString().replace(prefix + SEP, ""))
                .mapToLong(value -> Long.parseLong((String) value))
                .max()
                .orElse(0L);

        return prefix + SEP + (String.format("%09d", max + 1));
    }

    @Override
    public boolean supportsJdbcBatchInserts() {
        return false;
    }

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        prefix = properties.getProperty("prefix");
    }
}

package com.meshgroup.task.repositoty;

import com.meshgroup.task.AbstractTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.IOException;

@SqlGroup({
        @Sql(scripts = "classpath:sql/insert_user.sql"),
        @Sql(scripts = "classpath:sql/delete_all_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)})
abstract class AbstractRepositoryTests extends AbstractTests {

    @Autowired
    private TestEntityManager entityManager;

    protected <E> E saveTestEntity(String fileSource, Class<E> valueType) throws IOException {
        E entity = getObjectFromJson(fileSource, valueType);
        saveTestEntity(entity, valueType);
        return entity;
    }

    protected <E> E saveTestEntity(E entity, Class<E> valueType) {
        try {
            return entityManager.persist(entity);
        } finally {
            entityManager.flush();
        }
    }

}

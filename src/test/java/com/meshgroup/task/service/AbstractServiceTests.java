package com.meshgroup.task.service;

import com.meshgroup.task.AbstractTests;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@SqlGroup({
        @Sql(scripts = "classpath:sql/insert_users.sql"),
        @Sql(scripts = "classpath:sql/delete_all_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)})
abstract class AbstractServiceTests extends AbstractTests {
}

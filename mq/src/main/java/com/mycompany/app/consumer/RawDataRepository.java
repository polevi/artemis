package com.mycompany.app.consumer;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RawDataRepository {

    private JdbcTemplate jdbcTemplate;

    public RawDataRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insertBatch(List<Object[]> items) {
        jdbcTemplate.batchUpdate("insert into test.swift_mt (id, operdate, body) values(?, ?, ?) on conflict(id) do nothing", items);
    }
}

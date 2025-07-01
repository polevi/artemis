package com.mycompany.app;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RawDataRepository {

    private JdbcTemplate jdbcTemplate;

    public RawDataRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertBatch(List<Object[]> items) {
        jdbcTemplate.batchUpdate("insert into test.swift_mt (id, operdate, body) values(?, ?, ?) on conflict(id) do nothing", items);
        //jdbcTemplate.batchUpdate("insert into test.swift_mt (id, operdate, body) values(?, ?, ?)", items);
    }
}

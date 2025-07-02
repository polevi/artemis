package com.mycompany.app.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.app.messages.SwiftMTMessage;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class RawDataRepository {

    private JdbcTemplate jdbcTemplate;

    public RawDataRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Transactional(propagation = Propagation.REQUIRED)
    public int[] insertBatch(List<SwiftMTMessage> items) {
		return this.jdbcTemplate.batchUpdate(
				"insert into test.swift_mt (id, operdate, body) values(?, ?, ?) on conflict(id) do nothing",
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						SwiftMTMessage msg = items.get(i);
						ps.setInt(1, msg.getId());
						ps.setDate(2, java.sql.Date.valueOf(msg.getOperdate()));
						ps.setString(3, msg.getBody());
					}
					public int getBatchSize() {
						return items.size();
					}
				});        
    }    
}

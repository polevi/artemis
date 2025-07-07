package com.mycompany.app.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.app.messages.SwiftMTMessage;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AggregatorRepository {

    JdbcTemplate jdbcTemplate;
	final String EDNO_SEQUENCE_NAME = "test.edno";

    public AggregatorRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Transactional(propagation = Propagation.REQUIRED)
    public int[] insertBatch(List<SwiftMTMessage> items) {
		String sql = "insert into test.swift_mt (id, operdate, body) values(?, ?, ?) on conflict(id) do nothing";
		return this.jdbcTemplate.batchUpdate(
				sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						SwiftMTMessage msg = items.get(i);
						ps.setLong(1, msg.getId());
						ps.setDate(2, java.sql.Date.valueOf(msg.getOperdate()));
						ps.setString(3, msg.getBody());
					}
					public int getBatchSize() {
						return items.size();
					}
				});        
    }    

	@Transactional(propagation = Propagation.REQUIRED)
    public Long getNextEdNo() {
		return jdbcTemplate.query("SELECT nextval(?)",
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, EDNO_SEQUENCE_NAME);
				}
			},
			rs -> {
				if (rs.next()) {
					return rs.getLong(1);
				} else {
					throw new SQLException(String.format("Unable to retrieve value from sequence '%s'", EDNO_SEQUENCE_NAME));
				}
			});    
    }    
}

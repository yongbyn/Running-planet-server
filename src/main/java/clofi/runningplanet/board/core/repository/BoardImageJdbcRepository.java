package clofi.runningplanet.board.core.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import clofi.runningplanet.board.domain.BoardImage;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardImageJdbcRepository {

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public void saveAll(List<BoardImage> boardImages) {
		String sql = "INSERT INTO board_image (board_id, image_url, created_at, updated_at) VALUES (?, ?, ?, ?)";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				BoardImage boardImage = boardImages.get(i);
				LocalDateTime now = LocalDateTime.now();

				ps.setLong(1, boardImage.getBoard().getId());
				ps.setString(2, boardImage.getImageUrl());
				ps.setTimestamp(3, Timestamp.valueOf(now));
				ps.setTimestamp(4, Timestamp.valueOf(now));
			}

			@Override
			public int getBatchSize() {
				return boardImages.size();
			}
		});
	}
}

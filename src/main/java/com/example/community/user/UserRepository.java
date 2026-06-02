package com.example.community.user;

import com.example.community.global.exception.UserNotFoundException;
import com.example.community.user.dto.UserInfoResponseDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;  // 추가

    //생성자 통해 JDBC Template 주입
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user")
                .usingGeneratedKeyColumns("id");
    }

    //회원 저장 메서드
    public long save(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email",      user.getEmail())
                .addValue("password",   user.getPassword())
                .addValue("nickname",   user.getNickname())
                .addValue("status",     user.getStatus().name())
                .addValue("created_at", Timestamp.from(Instant.now()));

        return jdbcInsert.executeAndReturnKey(params).longValue();
    }

    //이메일 중복 체크 메서드
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    //닉네임 중복 체크 메서드
    public boolean existsByNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM user WHERE nickname = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nickname);
        return count != null && count > 0;
    }

    //회원 정보 불러오기 메서드
    public User findByID(long userId){
        String sql = "SELECT user_id, email, nickname, profile_img_url FROM user WHERE user_id = ?";

        RowMapper<User> rowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setEmail(rs.getString("email"));
                user.setNickname(rs.getString("nickname"));
                user.setProfileImgUrl(rs.getString("profile_img_url"));
                return user;
            }
        };

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(userId);
        }
    }
}

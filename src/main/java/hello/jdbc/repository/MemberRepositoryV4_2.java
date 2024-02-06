package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 적용
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository{
    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslator;


    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        //dataSource를 넣어야 어떤 DB를 쓰는 지 알 수 있다.
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }
    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO MEMBER(member_id, money) VALUES(?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        try{
            con = getConnection();
            pstmt= con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            //준비된 쿼리를 DB에서 실행
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            //스프링 예외 변환기 적용
//            최상위 예외인 DataAccessException 을 하위 예외로 변환해서 처리해준다.
            throw exTranslator.translate("save", sql, e);
        } finally {
            close(pstmt, con, null);
        }
    }
    @Override
    public Member findById(String memberId){
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;

            }else{
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            throw new MyDbException(e); // 원인인 e를 반드시 넣어야 한다.
        } finally {
            close(pstmt, con, rs);
        }

    }
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e); // 원인인 e를 반드시 넣어야 한다.
        } finally {
            close(pstmt, con, null);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e); // 원인인 e를 반드시 넣어야 한다.
        } finally {
            close(pstmt, con, null);
        }
    }

    private void close(PreparedStatement pstmt, Connection con, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(pstmt);
        //주의 ! 트랜잭션 동기화를 사용하기 위해서는 DataSourceUtils를 사용해야 한다.
        //releaseConnection -> 동기화된 커넥션을 닫지 않고 그대로 유지한다. 트랜잭션 매니저가 커넥션이 없는 경우 커넥션을 닫는다.
        DataSourceUtils.releaseConnection(con, dataSource);
//        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        //주의 ! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        // 트랜잭션 동기화 매니저(TransactionSynchronizationManager를 통해 동기화 매니저에 있는 커넥션을 꺼내는 코드)
        // 트랜잭션 동기화 매니저를 사용하여 Connection을 파라미터로 받지 않아도 된다.

        // 만약 ! 커넥션이 없는 경우에는 새로운 커넥션을 생성하여 반환한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}

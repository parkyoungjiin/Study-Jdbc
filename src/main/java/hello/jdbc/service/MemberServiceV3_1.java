package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저 사용
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {
//    private final DataSource dataSource; // 서비스 단에서 DataSource를 사용하는 것은 문제다!
//    PlatformTransactionManager로 변경. (주입 받음)
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
//        Connection connection = dataSource.getConnection();
        //트랜잭션 시작
        // DefaultTransactionDefinition -> 트랜잭션과 관련된 옵션을 지정할 수 있다
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 비즈니스 로직
            bizLogic(fromId, toId, money);
            //커밋(수동 커밋)
            transactionManager.commit(status);
        } catch (Exception e){
            //실패 시 롤백
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void release(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            }catch (Exception e){
                log.info("error", e); // exception은 ={}를 사용하지 않아도 된다.
            }
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("계좌이체 중 예외 발생");
        }
    }
}

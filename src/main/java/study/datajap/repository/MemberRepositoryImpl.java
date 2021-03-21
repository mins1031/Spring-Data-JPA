package study.datajap.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import study.datajap.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;
    /*
     만약 jpa말고 jdbc탬플릿을 사용하고 싶다면 엔티티매니저 대신 jdbc를 주입받아
     사용하면됨 마이배티스도 같은 맥락일거같음.
    */
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}

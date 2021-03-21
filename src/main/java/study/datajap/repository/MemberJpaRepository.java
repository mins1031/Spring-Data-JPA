package study.datajap.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajap.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public long count(){
        return em.createQuery("select  count(m) from Member m",Long.class)
                .getSingleResult();
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Member find(Long id){
        return em.find(Member.class,id);
    }

    public List<Member> findByUsernameAndGreaterThen(String username, int age){
        return em.createQuery("select m from Member m" +
                " where m.username = :username and m.age > :age")
                .setParameter("username",username)
                .setParameter("age",age)
                .getResultList();
    }

    public List<Member> findByUsername(String username){
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findBypage(int age, int offset, int limit){
        return em.createQuery("select m from Member m where m.age = :age " +
                "order by m.username desc")
                .setParameter("age",age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age",Long.class)
                .setParameter("age",age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age){
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }//회원의 나이를 한번에 변경하는 쿼리
}

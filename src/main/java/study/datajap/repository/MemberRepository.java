package study.datajap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajap.dto.MemberDto;
import study.datajap.entity.Member;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

//JpaRepository<관련된 엔티티, 엔티티의 pk타입을 명시해줘야함>
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    //메서드 이름으로 쿼리를 생성해주는 기능역시 매우 강려크함. (관례를 따라 적용됨)

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);
    //@Query로 미리 정의해놓은 쿼리를 불러옴. 근데 위의 어노테이션 구문 없이도 잘 동작함
    //이게 인터페이스가 쿼리 적용시 Member에 @NamedQuery어노테이션의 name타입으로 정의된것이 있는지
    //먼저 찾아주기때문에 구지 없어도 되지면 명시성을 위해서 사용할것임
    //@Param으로 필요한 파라미터 삽입해줘야함

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajap.dto.MemberDto(m.id,m.username,t.name) " +
            "from Member m join m.team t")
    List<MemberDto> findMemberDto();
    //우선 값을 가져올때 엔티티가 아닌 dto로 가져오고 싶은경우 (나는 유저이름,id,팀이름 동시에
    // 가져오고싶다! 할떄?) 위처럼 사용

    List<Member> findListByUsername(String name); //컬렉션
    Member findMemberByUsername(String name); //단건
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    @Query(value = "select m from Member m left join m.team t"
          ,countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically= true)
    //변경하는 로직에는 꼭필요함. 그냥 기본적으로 별도 어노테이션 처리 없으면
    // 다 조회 처리한다고 생각하고 변경작업엔 @Modifying꼭 써주는걸로 생각할것
    @Query("update Member m set m.age = m.age + 1 " +
            "where m.age >= :age ")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    //@Query에 패치 조인쿼리를 사용해줌
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //jpql + Entitygraph
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly",
            value = "true"))
    Member findReadOnlyByUsername(String username);
}

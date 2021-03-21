package study.datajap.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","name","age"})
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
)//특정 쿼리를 엔티티에 저장해놓고 레포지토리에서 불러와 사용할수 있게 만들어놓음.
public class Member extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    //연관관계 필드는 무한루프에 빠질수 있기에 toString에 가급적 적용 안함.
    /*protected Member() {
    }
    jpa기본 스펙이 엔티티는 디폴트생성자를 가지고 있어야하고 접근범위를 private으로 하면
    안됨 왜냐면 프록시 객체를 jpa가 사용시 디폴트 생성자가 없으면 프록시자체를 사용 못할수도 있음
    */
    public Member(String name) {
        this.username = name;
    }

    public Member(String name, int age) {
        this.username = name;
        this.age = age;
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

    public Member(String name, int age, Team team) {
        this.username = name;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }
}

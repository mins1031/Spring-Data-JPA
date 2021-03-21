package study.datajap.repository;

import org.springframework.stereotype.Repository;
import study.datajap.entity.Member;
import study.datajap.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository {

    @PersistenceContext
    private EntityManager em;

    public Team save(Team team){
        em.persist(team);
        return team;
    }

    public void delete(Long id){
        em.remove(id);
    }

    public Optional<Team> findById(Long id){
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public long count(){
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }

    public List<Team> findByAll(){
        return em.createQuery("select t from Team t",Team.class)
                .getResultList();
    }

    public void update(Member member){

    }
}

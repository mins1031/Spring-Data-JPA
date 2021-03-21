package study.datajap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajap.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {


}

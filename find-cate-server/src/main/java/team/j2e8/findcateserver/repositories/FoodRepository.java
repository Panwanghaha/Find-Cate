package team.j2e8.findcateserver.repositories;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import team.j2e8.findcateserver.models.Food;
@Repository
public interface FoodRepository extends PagingAndSortingRepository<Food, Integer>, QuerydslPredicateExecutor<Food> {
}

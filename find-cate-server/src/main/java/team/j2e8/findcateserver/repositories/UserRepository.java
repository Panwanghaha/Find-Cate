package team.j2e8.findcateserver.repositories;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import team.j2e8.findcateserver.models.User;

/**
 * @auther vinsonws
 * @date 2018/11/28 22:40
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer>, QuerydslPredicateExecutor<User> {
}

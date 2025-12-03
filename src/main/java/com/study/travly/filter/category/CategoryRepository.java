package com.study.travly.filter.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	@Query("""
			select c from Category c join fetch c.items order by c.orderNum
			""")
	List<Category> getAllCategoryItems();

}

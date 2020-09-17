/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 09-Jul-2019
 */
package org.sdrc.cpmisweb.repository;

import org.sdrc.cpmisweb.domain.Subgroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubgroupRepository extends JpaRepository<Subgroup, Integer> {

	Subgroup findBySubgroupVal(String subgroupVal);
}

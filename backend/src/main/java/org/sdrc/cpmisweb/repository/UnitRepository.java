/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 09-Jul-2019
 */
package org.sdrc.cpmisweb.repository;

import org.sdrc.cpmisweb.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, Integer> {

	Unit findByUnitName(String unitName);
}

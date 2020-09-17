package org.sdrc.cpmisweb.repository;

import org.sdrc.cpmisweb.domain.InstitutionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionDetailsRepository extends JpaRepository<InstitutionDetails, Integer> {

	InstitutionDetails findByDistrictIdAreaId(Integer areaId);
}

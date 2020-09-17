package in.co.sdrc.sdrcdatacollector.jparepositories;

import java.util.List;
import java.util.Set;

import org.sdrc.cpmisweb.domain.DesignationFormMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Repository
public interface EngineRoleFormMappingRepository extends JpaRepository<DesignationFormMapping, Integer> {

//	List<DesignationFormMapping> findByDesignationId(Integer roleId);
//
//	List<DesignationFormMapping> findByDesignationIdAndAccessType(Integer integer, AccessType accessType);
//
//	List<DesignationFormMapping> findAllByDesignationIdAndFormFormIdAndAccessType(Integer roleId, Integer formId,AccessType review);
//
//	DesignationFormMapping findByDesignationIdAndFormFormIdAndAccessType(Integer roleId, Integer formId,
//			AccessType review);
//
//	DesignationFormMapping findByDesignationIdAndFormFormIdAndAccessTypeAndStatus(Integer roleId, Integer formId,
//			AccessType dataEntry, Status active);
//
//	List<DesignationFormMapping> findByDesignationIdAndAccessTypeAndStatus(Integer roleId, AccessType dataEntry,
//			Status active);
//
//	List<DesignationFormMapping> findByDesignationIdAndAccessTypeAndFormStatus(Integer roleId, AccessType review,
//			Status active);
//
//	List<DesignationFormMapping> findByDesignationIdInAndAccessTypeAndStatus(List<Integer> roleId,AccessType downloadRawData, Status active);
//
//	List<DesignationFormMapping> findByDesignationIdInAndAccessTypeAndStatus(Set<Object> roleId,
//			AccessType downloadRawData, Status active);

}

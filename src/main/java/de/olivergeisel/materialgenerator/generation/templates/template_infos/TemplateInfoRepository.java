package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;
import org.springframework.data.repository.CrudRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public interface TemplateInfoRepository extends CrudRepository<TemplateInfo, UUID> {

	TemplateInfo findByTemplateType(TemplateType templateType);

	default Set<TemplateInfo> findAllBasicInfo() {
		var list = new HashSet<>(Set.of(findByTemplateType(TemplateType.LIST)));
		list.add(findByTemplateType(TemplateType.TEXT));
		list.add(findByTemplateType(TemplateType.IMAGE));
		list.add(findByTemplateType(TemplateType.DEFINITION));
		list.add(findByTemplateType(TemplateType.EXAMPLE));
		list.add(findByTemplateType(TemplateType.ACRONYM));
		list.add(findByTemplateType(TemplateType.SYNONYM));
		list.add(findByTemplateType(TemplateType.PROOF));
		list.add(findByTemplateType(TemplateType.EXERCISE));
		list.add(findByTemplateType(TemplateType.SOLUTION));
		list.add(findByTemplateType(TemplateType.TASK));
		list.add(findByTemplateType(TemplateType.SUMMARY));
		return list;
	}
}

package br.gov.mt.seplag.igorzannattasaraiva032377.repository.regional;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.regional.RegionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface RegionalRepository extends JpaRepository<RegionalEntity, UUID> {

    Optional<RegionalEntity> findByExternalIdAndAtivoTrue(Integer externalId);

    List<RegionalEntity> findAllByAtivoTrue();
}
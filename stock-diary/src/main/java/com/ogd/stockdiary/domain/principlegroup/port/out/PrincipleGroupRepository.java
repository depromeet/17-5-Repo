package com.ogd.stockdiary.domain.principlegroup.port.out;

import java.util.List;
import java.util.Optional;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;

public interface PrincipleGroupRepository {

    List<PrincipleGroup> findByUserId(Long userId);

    PrincipleGroup save(PrincipleGroup principleGroup);

    Optional<PrincipleGroup> findByIdAndUserId(Long groupId, Long userId);

    void deleteByIdAndUserId(Long groupId, Long userId);
}

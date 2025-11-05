package com.ogd.stockdiary.domain.principlegroup.port.out;

import java.util.List;
import java.util.Optional;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroupType;

public interface PrincipleGroupRepository {

    List<PrincipleGroup> findByUserId(Long userId);

    List<PrincipleGroup> findByUserIdAndGroupType(Long userId, PrincipleGroupType groupType);

    List<PrincipleGroup> findByGroupType(PrincipleGroupType groupType);

    List<PrincipleGroup> findByGroupTypeWithUser(PrincipleGroupType groupType);

    PrincipleGroup save(PrincipleGroup principleGroup);

    Optional<PrincipleGroup> findByIdAndUserId(Long groupId, Long userId);

    void deleteByIdAndUserId(Long groupId, Long userId);
}

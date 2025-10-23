package com.ogd.stockdiary.domain.principlegroup.usecase;

import java.util.List;

import com.ogd.stockdiary.domain.principlegroup.dto.CreatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.ReorderPrincipleGroupsCommand;
import com.ogd.stockdiary.domain.principlegroup.dto.UpdatePrincipleGroupCommand;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;

public interface PrincipleGroupUseCase {

    List<PrincipleGroup> getUserPrincipleGroups(Long userId);

    PrincipleGroup getPrincipleGroupById(Long groupId, Long userId);

    PrincipleGroup createPrincipleGroup(CreatePrincipleGroupCommand command);

    PrincipleGroup updatePrincipleGroup(UpdatePrincipleGroupCommand command);

    void deletePrincipleGroup(Long groupId, Long userId);

    void reorderPrincipleGroups(ReorderPrincipleGroupsCommand command);
}

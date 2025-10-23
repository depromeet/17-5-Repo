package com.ogd.stockdiary.application.principlegroup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.principlegroup.port.out.PrincipleGroupRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PrincipleGroupRepositoryImpl implements PrincipleGroupRepository {

    private final JpaPrincipleGroupRepository jpaPrincipleGroupRepository;

    @Override
    public List<PrincipleGroup> findByUserId(Long userId) {
        return jpaPrincipleGroupRepository.findByUserId(userId);
    }

    @Override
    public PrincipleGroup save(PrincipleGroup principleGroup) {
        return jpaPrincipleGroupRepository.save(principleGroup);
    }

    @Override
    public Optional<PrincipleGroup> findByIdAndUserId(Long groupId, Long userId) {
        return jpaPrincipleGroupRepository.findByIdAndUserId(groupId, userId);
    }

    @Override
    public void deleteByIdAndUserId(Long groupId, Long userId) {
        jpaPrincipleGroupRepository.deleteByIdAndUserId(groupId, userId);
    }
}

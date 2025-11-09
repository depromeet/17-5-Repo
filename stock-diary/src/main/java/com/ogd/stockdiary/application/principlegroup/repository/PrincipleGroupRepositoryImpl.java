package com.ogd.stockdiary.application.principlegroup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroup;
import com.ogd.stockdiary.domain.principlegroup.entity.PrincipleGroupType;
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
    public List<PrincipleGroup> findByUserIdAndGroupType(Long userId, PrincipleGroupType groupType) {
        return jpaPrincipleGroupRepository.findByUserIdAndGroupType(userId, groupType);
    }

    @Override
    public List<PrincipleGroup> findByGroupType(PrincipleGroupType groupType) {
        return jpaPrincipleGroupRepository.findByGroupType(groupType);
    }

    @Override
    public List<PrincipleGroup> findByGroupTypeWithUser(PrincipleGroupType groupType) {
        return jpaPrincipleGroupRepository.findByGroupTypeWithUser(groupType);
    }

    @Override
    public PrincipleGroup save(PrincipleGroup principleGroup) {
        return jpaPrincipleGroupRepository.save(principleGroup);
    }

    @Override
    public Optional<PrincipleGroup> findById(Long groupId) {
        return jpaPrincipleGroupRepository.findById(groupId);
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

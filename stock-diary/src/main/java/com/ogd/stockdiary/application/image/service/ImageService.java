package com.ogd.stockdiary.application.image.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ogd.stockdiary.application.user.repository.UserRepository;
import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;
import com.ogd.stockdiary.domain.image.dto.UploadImageCommand;
import com.ogd.stockdiary.domain.image.entity.ImageMetadata;
import com.ogd.stockdiary.domain.image.entity.ImageStatus;
import com.ogd.stockdiary.domain.image.port.in.ImageUseCase;
import com.ogd.stockdiary.domain.image.port.out.ImageRepository;
import com.ogd.stockdiary.domain.user.entity.User;
import com.ogd.stockdiary.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService implements ImageUseCase {

    private static final int UUID_SUBSTRING_LENGTH = 12;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ImageRepository imageRepository;
    private final FileClientPort fileClientPort;
    private final UserRepository userRepository;

    @Override
    public ImageMetadata uploadImage(UploadImageCommand command) {
        // 사용자 조회
        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> new ApplicationException(CodeEnum.FRS_003, "사용자를 찾을 수 없습니다: " + command.getUserId()));

        // 파일명에서 확장자 추출
        String fileName = command.getFileName();
        String extension = extractFileExtension(fileName);

        // UUID 생성 및 짧게 자르기
        String shortUuid = generateShortUuid();

        // objectKey 생성: /{domain}/{yyyy-MM-dd}/{shortUuid}.{extension}
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        String objectKey = String.format(
            "/%s/%s/%s.%s", command.getDomain(), currentDate, shortUuid, extension);

        // MinIO에 파일 업로드
        fileClientPort.uploadFile(command.getInputStream(), objectKey, command.getFileSize());

        // ImageMetadata 엔티티 생성 및 저장
        ImageMetadata imageMetadata = ImageMetadata.create(
            user, fileName, objectKey, command.getFileSize(), ImageStatus.T);

        return imageRepository.save(imageMetadata);
    }

    /**
     * 파일명에서 확장자를 추출합니다.
     *
     * @param fileName
     *            파일명 (예: "image.png")
     * @return 확장자 (예: "png")
     */
    private String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new ApplicationException(CodeEnum.FRS_003, "유효하지 않은 파일명입니다: " + fileName);
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * UUID를 생성하고 하이픈을 제거한 후 앞 12자를 반환합니다.
     *
     * @return 짧은 UUID (예: "a1b2c3d45678")
     */
    private String generateShortUuid() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, UUID_SUBSTRING_LENGTH);
    }
}

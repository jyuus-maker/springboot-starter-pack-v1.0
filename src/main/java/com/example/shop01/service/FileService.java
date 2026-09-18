package com.example.shop01.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * [파일 I/O 전용 서비스]
 * - 실제 디스크(서버 파일 시스템)에 파일 업로드(생성) 및 삭제 작업을 수행하는 저수준 파일 관리 서비스
 */
@Service
@Slf4j
public class FileService {

    /**
     * 파일 업로드
     * - 원본 파일명을 바탕으로 UUID를 조합하여 고유한 파일명을 생성하고 물리 디스크에 저장합니다.
     *
     * @param uploadPath       파일을 저장할 디렉토리 경로 (예: C:/shopmall/item)
     * @param originalFileName 사용자가 업로드한 원본 파일명 (확장자 추출용)
     * @param fileData         업로드된 파일의 바이트 배열 데이터
     * @return 디스크에 저장된 고유한 파일명 (UUID + 확장자)
     * @throws Exception 파일 생성 및 쓰기 실패 시 예외 발생
     */
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        // 1. 고유 파일명을 위한 UUID 생성
        UUID uuid = UUID.randomUUID();
        String extension = "";

        // 2. 원본 파일명에서 확장자 추출 (예: .jpg, .png)
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 3. 고유한 저장 파일명 및 전체 저장 경로 생성
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        // 4. 저장할 디렉토리가 존재하지 않는 경우 자동으로 폴더 생성
        File folder = new File(uploadPath);
        if (!folder.exists()) {
            boolean isCreated = folder.mkdirs();
            if (isCreated) {
                log.info("파일 업로드 디렉토리 생성 완료: {}", uploadPath);
            }
        }

        // 5. 바이트 스트림을 이용하여 디스크에 파일 쓰기 (try-with-resources로 스트림 자동 닫기)
        try (FileOutputStream fos = new FileOutputStream(fileUploadFullUrl)) {
            fos.write(fileData);
        }

        log.info("파일 업로드 성공: {} (저장명: {})", originalFileName, savedFileName);
        return savedFileName;
    } //end uploadFile

    /**
     * 파일 삭제
     * - 상품 정보 수정 또는 삭제 시 기존에 저장되어 있던 물리 파일을 디스크에서 삭제합니다.
     *
     * @param filePath 삭제할 파일의 전체 경로 (경로 + 파일명)
     * @throws Exception 파일 삭제 중 발생하는 예외
     */
    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);

        // 파일이 디스크에 존재하는지 확인 후 삭제 진행
        if (deleteFile.exists()) {
            boolean isDeleted = deleteFile.delete();
            if (isDeleted) {
                log.info("파일 삭제 성공: {}", filePath);
            } else {
                log.warn("파일 삭제 실패: {}", filePath);
            }
        } else {
            log.info("삭제 대상 파일이 존재하지 않습니다: {}", filePath);
        }
    } //end deleteFile

} //end class FileService

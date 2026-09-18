package com.example.shop01.service;

import com.example.shop01.domain.ItemImg;
import com.example.shop01.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 상품 이미지 등록, 수정 및 물리 파일 관리를 담당하는 서비스 클래스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    // application.properties에 설정된 실제 이미지 저장 폴더 경로 (예: C:/shopmall/item)
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    /**
     * 상품 이미지 정보 및 파일 신규 저장
     *
     * @param itemImg     상품 이미지 엔티티
     * @param itemImgFile 업로드된 이미지 파일
     * @throws Exception 파일 저장 시 발생할 수 있는 예외
     */
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile != null ? itemImgFile.getOriginalFilename() : "";
        String imgName = "";
        String imgUrl = "";

        // 파일이 실제로 업로드된 경우 FileService를 통한 물리 파일 저장
        if (itemImgFile != null && !itemImgFile.isEmpty() && StringUtils.hasText(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        // 이미지 정보 업데이트 후 DB 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    } //end saveItemImg

    /**
     * 상품 이미지 정보 수정 및 기존 물리 파일 교체
     *
     * @param itemImgId   수정할 상품 이미지 엔티티 식별자(ID)
     * @param itemImgFile 새롭게 업로드된 이미지 파일
     * @throws Exception 파일 업로드 및 삭제 처리 중 발생할 수 있는 예외
     */
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        // 새 파일이 업로드된 경우에만 이미지 파일 교체 작업 수행
        if (itemImgFile != null && !itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 상품 이미지를 찾을 수 없습니다. (ID: " + itemImgId + ")"));

            // 1. 기존에 저장되어 있던 물리 파일 삭제
            if (StringUtils.hasText(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            // 2. 새롭게 업로드된 파일 디스크 저장
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;

            // 3. 영속성 컨텍스트를 통한 더티 체킹(엔티티 정보 갱신)
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    } //end updateItemImg

} //end class ItemImgService

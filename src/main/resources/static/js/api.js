/**
 * [표준 API 클라이언트 모듈 - Api.js]
 *
 * Thymeleaf SSR 환경에서 Spring Security CSRF 토큰을 자동 추출하여
 * 일관된 규격(X-Requested-With, Content-Type, Accept)으로 비동기 통신을 처리합니다.
 * 백엔드의 표준 ApiResponse 포맷({ success, message, data })을 자동 파싱합니다.
 *
 * <주요 기능>
 * 1. CSRF Meta 태그 자동 조회 및 헤더 자동 탑재
 * 2. 401(미인증) 발생 시 안내 모달 후 로그인 페이지 리다이렉트
 * 3. 버튼 중복 클릭 방지(비활성화) 및 로딩 스피너 제어 유틸리티 제공
 *
 * <사용법>
 * const res = await Api.post('/order', { itemId: 1, count: 2 }, submitButton);
 * if (res.success) {
 *     Toast.success(res.message);
 * }
 */
const Api = (function() {

    /**
     * 페이지 <meta> 태그에서 CSRF 토큰 및 헤더명 추출
     */
    function getCsrfInfo() {
        const token = document.querySelector('meta[name="_csrf"]')?.content || '';
        const header = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
        return { token, header };
    }

    /**
     * 공통 fetch 래퍼 함수
     *
     * @param {string} url 요청 URL
     * @param {object} options fetch 옵션 (method, body 등)
     * @param {HTMLElement} [triggerBtn] 중복 클릭 방지할 버튼 요소
     * @returns {Promise<{success: boolean, message: string, data: any, status: number}>}
     */
    async function request(url, options = {}, triggerBtn = null) {
        const { token, header } = getCsrfInfo();

        const defaultHeaders = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };

        if (token && header) {
            defaultHeaders[header] = token;
        }

        const config = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...(options.headers || {})
            }
        };

        // 버튼 중복 클릭 방지 & 로딩 인디케이터 처리
        let originalBtnHtml = '';
        if (triggerBtn) {
            triggerBtn.disabled = true;
            originalBtnHtml = triggerBtn.innerHTML;
            triggerBtn.innerHTML = `
                <span class="inline-flex items-center gap-2">
                    <svg class="animate-spin -ml-1 mr-1 h-4 w-4 text-current" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <span>처리 중...</span>
                </span>
            `;
            triggerBtn.classList.add('opacity-70', 'cursor-not-allowed');
        }

        try {
            const response = await fetch(url, config);
            const status = response.status;

            // 401 Unauthorized 처리 (로그인 만료 또는 비로그인 상태)
            if (status === 401) {
                if (typeof Modal !== 'undefined') {
                    await Modal.alert('로그인이 필요한 서비스입니다.\n로그인 페이지로 이동합니다.');
                }
                location.href = '/members/login';
                return { success: false, message: '로그인이 필요합니다.', data: null, status: 401 };
            }

            let responseData;
            const text = await response.text();
            try {
                responseData = text ? JSON.parse(text) : {};
            } catch (e) {
                responseData = { message: text };
            }

            if (response.ok) {
                return {
                    success: true,
                    message: responseData.message || '요청이 성공적으로 처리되었습니다.',
                    data: responseData.data !== undefined ? responseData.data : responseData,
                    status: status
                };
            } else {
                const errorMsg = responseData.message || '요청 처리 중 오류가 발생했습니다.';
                return {
                    success: false,
                    message: errorMsg,
                    data: responseData.data || null,
                    status: status
                };
            }

        } catch (err) {
            console.error('[API 통신 네트워크 에러]', err);
            return {
                success: false,
                message: '네트워크 연결 상태를 확인해주세요.',
                data: null,
                status: 0
            };
        } finally {
            if (triggerBtn) {
                triggerBtn.disabled = false;
                triggerBtn.innerHTML = originalBtnHtml;
                triggerBtn.classList.remove('opacity-70', 'cursor-not-allowed');
            }
        }
    }

    return {
        get: (url, triggerBtn) => request(url, { method: 'GET' }, triggerBtn),
        post: (url, body, triggerBtn) => request(url, { method: 'POST', body: JSON.stringify(body) }, triggerBtn),
        patch: (url, body, triggerBtn) => request(url, { method: 'PATCH', body: body ? JSON.stringify(body) : undefined }, triggerBtn),
        delete: (url, triggerBtn) => request(url, { method: 'DELETE' }, triggerBtn)
    };
})();

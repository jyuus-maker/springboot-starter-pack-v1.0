/**
 * [글로벌 비동기 컨펌/알림 모달 시스템 - Modal.js]
 *
 * 브라우저 Native confirm() 및 중요한 alert()를 100% 대체합니다.
 * Promise 기반으로 동작하므로 await를 통해 직관적으로 사용자 승인 여부를 제어할 수 있습니다.
 *
 * <사용법>
 * if (await Modal.confirm('해당 상품을 쇼핑백에서 삭제하시겠습니까?')) {
 *     // 삭제 진행
 * }
 *
 * await Modal.alert('로그인이 필요한 서비스입니다.');
 */
const Modal = (function() {
    let modalRoot = null;

    function ensureModalDOMElements() {
        if (!modalRoot || !document.body.contains(modalRoot)) {
            modalRoot = document.getElementById('global-modal-root');
            if (!modalRoot) {
                modalRoot = document.createElement('div');
                modalRoot.id = 'global-modal-root';
                modalRoot.className = 'fixed inset-0 z-[10000] hidden items-center justify-center p-4 transition-all duration-300';
                modalRoot.innerHTML = `
                    <!-- 배경 딤 레이어 (Backdrop blur) -->
                    <div id="global-modal-backdrop" class="fixed inset-0 bg-black/70 backdrop-blur-sm transition-opacity duration-300 opacity-0"></div>

                    <!-- 모달 박스 -->
                    <div id="global-modal-card" class="relative z-10 w-full max-w-md bg-[#18181b] border border-neutral-700/80 rounded-2xl p-6 sm:p-7 shadow-2xl transform transition-all duration-300 scale-95 opacity-0 text-neutral-100">
                        <div class="flex items-start gap-3.5 mb-4">
                            <div id="global-modal-icon" class="w-10 h-10 rounded-full flex items-center justify-center shrink-0 bg-neutral-800 text-neutral-200">
                                <!-- 동적 아이콘 -->
                            </div>
                            <div class="space-y-1">
                                <h3 id="global-modal-title" class="text-base font-bold tracking-tight text-white leading-snug">확인</h3>
                                <p id="global-modal-message" class="text-xs sm:text-sm text-neutral-300 leading-relaxed font-light break-keep"></p>
                            </div>
                        </div>

                        <!-- 버튼 컨트롤 -->
                        <div class="flex items-center justify-end gap-2.5 pt-4 mt-2 border-t border-neutral-800">
                            <button type="button" id="global-modal-cancel" class="px-4 py-2 text-xs font-semibold rounded-xl bg-neutral-800 hover:bg-neutral-700 text-neutral-300 transition">취소</button>
                            <button type="button" id="global-modal-confirm" class="px-5 py-2 text-xs font-bold rounded-xl bg-white text-neutral-900 hover:bg-neutral-200 transition shadow-sm">확인</button>
                        </div>
                    </div>
                `;
                document.body.appendChild(modalRoot);
            }
        }
        return modalRoot;
    }

    function openModal({ title, message, isConfirm = true, confirmText = '확인', cancelText = '취소', type = 'info' }) {
        ensureModalDOMElements();

        const backdrop = document.getElementById('global-modal-backdrop');
        const card = document.getElementById('global-modal-card');
        const titleEl = document.getElementById('global-modal-title');
        const messageEl = document.getElementById('global-modal-message');
        const confirmBtn = document.getElementById('global-modal-confirm');
        const cancelBtn = document.getElementById('global-modal-cancel');
        const iconEl = document.getElementById('global-modal-icon');

        titleEl.textContent = title || (isConfirm ? '확인 안내' : '알림');
        messageEl.textContent = message || '';
        confirmBtn.textContent = confirmText;
        cancelBtn.textContent = cancelText;

        if (isConfirm) {
            cancelBtn.classList.remove('hidden');
        } else {
            cancelBtn.classList.add('hidden');
        }

        // 아이콘 설정
        if (type === 'danger') {
            iconEl.className = 'w-10 h-10 rounded-full flex items-center justify-center shrink-0 bg-rose-500/20 text-rose-400 border border-rose-500/30';
            iconEl.innerHTML = '<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>';
            confirmBtn.className = 'px-5 py-2 text-xs font-bold rounded-xl bg-rose-600 hover:bg-rose-500 text-white transition shadow-sm';
        } else {
            iconEl.className = 'w-10 h-10 rounded-full flex items-center justify-center shrink-0 bg-neutral-800 text-neutral-200 border border-neutral-700';
            iconEl.innerHTML = '<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>';
            confirmBtn.className = 'px-5 py-2 text-xs font-bold rounded-xl bg-white text-neutral-900 hover:bg-neutral-200 transition shadow-sm';
        }

        modalRoot.classList.remove('hidden');
        modalRoot.classList.add('flex');

        requestAnimationFrame(() => {
            backdrop.classList.remove('opacity-0');
            backdrop.classList.add('opacity-100');
            card.classList.remove('scale-95', 'opacity-0');
            card.classList.add('scale-100', 'opacity-100');
        });

        return new Promise((resolve) => {
            function cleanup(result) {
                backdrop.classList.remove('opacity-100');
                backdrop.classList.add('opacity-0');
                card.classList.remove('scale-100', 'opacity-100');
                card.classList.add('scale-95', 'opacity-0');

                confirmBtn.removeEventListener('click', onConfirm);
                cancelBtn.removeEventListener('click', onCancel);
                backdrop.removeEventListener('click', onCancel);
                document.removeEventListener('keydown', onKey);

                setTimeout(() => {
                    modalRoot.classList.remove('flex');
                    modalRoot.classList.add('hidden');
                    resolve(result);
                }, 200);
            }

            function onConfirm() { cleanup(true); }
            function onCancel() { cleanup(false); }
            function onKey(e) {
                if (e.key === 'Escape') onCancel();
                if (e.key === 'Enter') onConfirm();
            }

            confirmBtn.addEventListener('click', onConfirm);
            cancelBtn.addEventListener('click', onCancel);
            backdrop.addEventListener('click', onCancel);
            document.addEventListener('keydown', onKey);
            confirmBtn.focus();
        });
    }

    return {
        confirm: (message, options = {}) => openModal({
            title: options.title || '안내',
            message: message,
            isConfirm: true,
            confirmText: options.confirmText || '확인',
            cancelText: options.cancelText || '취소',
            type: options.type || 'info'
        }),
        alert: (message, options = {}) => openModal({
            title: options.title || '알림',
            message: message,
            isConfirm: false,
            confirmText: options.confirmText || '확인',
            type: options.type || 'info'
        })
    };
})();

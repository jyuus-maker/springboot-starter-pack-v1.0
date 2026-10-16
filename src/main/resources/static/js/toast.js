/**
 * [글로벌 토스트 알림 시스템 - Toast.js]
 *
 * 브라우저 Native alert()를 100% 대체하며, 화면 상단에 미려한
 * 다크 미니멀리즘 스타일의 토스트 메시지를 표시합니다.
 *
 * <사용법>
 * Toast.success('쇼핑백에 담았습니다.');
 * Toast.error('재고가 부족합니다.');
 * Toast.info('로그인이 필요합니다.');
 */
const Toast = (function() {
    let container = null;

    function getOrCreateContainer() {
        if (!container || !document.body.contains(container)) {
            container = document.getElementById('global-toast-container');
            if (!container) {
                container = document.createElement('div');
                container.id = 'global-toast-container';
                container.className = 'fixed top-5 right-5 z-[9999] flex flex-col gap-2 max-w-sm w-full px-4 sm:px-0 pointer-events-none';
                document.body.appendChild(container);
            }
        }
        return container;
    }

    function show(message, type = 'info', duration = 3000) {
        const root = getOrCreateContainer();

        const toast = document.createElement('div');
        toast.className = 'pointer-events-auto flex items-center justify-between gap-3 px-4 py-3.5 rounded-xl shadow-2xl border text-xs sm:text-sm font-medium transition-all duration-300 transform translate-y-[-10px] opacity-0 backdrop-blur-md';

        // 테마별 색상 지정 (REHMIA 럭셔리 다크 팔레트)
        let iconSvg = '';
        if (type === 'success') {
            toast.className += ' bg-[#121212]/95 text-neutral-100 border-emerald-500/50 shadow-emerald-950/20';
            iconSvg = '<svg class="w-4 h-4 text-emerald-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"/></svg>';
        } else if (type === 'error') {
            toast.className += ' bg-[#1a0f0f]/95 text-rose-100 border-rose-500/50 shadow-rose-950/20';
            iconSvg = '<svg class="w-4 h-4 text-rose-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12"/></svg>';
        } else {
            toast.className += ' bg-[#171717]/95 text-neutral-100 border-neutral-700/80 shadow-black/40';
            iconSvg = '<svg class="w-4 h-4 text-amber-300 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>';
        }

        toast.innerHTML = `
            <div class="flex items-center gap-2.5">
                ${iconSvg}
                <span class="leading-snug">${escapeHtml(message)}</span>
            </div>
            <button type="button" class="text-neutral-400 hover:text-white transition p-1 rounded-md" aria-label="닫기">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
            </button>
        `;

        const closeBtn = toast.querySelector('button');
        const removeToast = () => {
            toast.classList.add('opacity-0', 'translate-y-[-10px]');
            setTimeout(() => {
                if (toast.parentNode) toast.parentNode.removeChild(toast);
            }, 300);
        };

        closeBtn.addEventListener('click', removeToast);
        root.appendChild(toast);

        // 표시 애니메이션
        requestAnimationFrame(() => {
            toast.classList.remove('opacity-0', 'translate-y-[-10px]');
            toast.classList.add('opacity-100', 'translate-y-0');
        });

        // 자동 소멸 타이머
        if (duration > 0) {
            setTimeout(removeToast, duration);
        }
    }

    function escapeHtml(str) {
        if (!str) return '';
        const p = document.createElement('p');
        p.textContent = str;
        return p.innerHTML;
    }

    return {
        success: (msg, dur) => show(msg, 'success', dur),
        error: (msg, dur) => show(msg, 'error', dur || 4000),
        info: (msg, dur) => show(msg, 'info', dur)
    };
})();

const { spawn } = require('child_process');
const fs = require('fs');
const path = require('path');

const EDGE_PATH = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe";
const USER_DATA_DIR = path.join(__dirname, 'edge_temp_profile');
const SCREENSHOT_DIR = path.join(__dirname, 'screenshots');

if (!fs.existsSync(SCREENSHOT_DIR)) {
    fs.mkdirSync(SCREENSHOT_DIR, { recursive: true });
}

async function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function getAuthenticatedSession() {
    const pageRes = await fetch('http://localhost:8080/members/login');
    const initCookie = pageRes.headers.get('set-cookie')?.split(';')[0] || '';
    const html = await pageRes.text();
    const tokenMatch = html.match(/name="_csrf" value="([^"]+)"/);
    const csrf = tokenMatch ? tokenMatch[1] : '';

    const body = new URLSearchParams();
    body.append('email', 'admin@shop01.com');
    body.append('password', '1234');
    body.append('_csrf', csrf);

    const loginRes = await fetch('http://localhost:8080/members/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cookie': initCookie
        },
        body: body.toString(),
        redirect: 'manual'
    });

    const setCookie = loginRes.headers.get('set-cookie') || initCookie;
    const jsessionMatch = setCookie.match(/JSESSIONID=([^;]+)/);
    const jsessionId = jsessionMatch ? jsessionMatch[1] : '';
    console.log('Authenticated JSESSIONID:', jsessionId);

    // Add item to cart for visual completeness
    try {
        await fetch('http://localhost:8080/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Cookie': `JSESSIONID=${jsessionId}`,
                'X-CSRF-TOKEN': csrf
            },
            body: JSON.stringify({ itemId: 1, count: 1 })
        });
        await fetch('http://localhost:8080/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Cookie': `JSESSIONID=${jsessionId}`,
                'X-CSRF-TOKEN': csrf
            },
            body: JSON.stringify({ itemId: 2, count: 2 })
        });
    } catch (e) {
        console.log('Cart init warning:', e.message);
    }

    return { jsessionId, csrf };
}

async function startEdge() {
    const edge = spawn(EDGE_PATH, [
        '--headless',
        '--disable-gpu',
        '--remote-debugging-port=9222',
        `--user-data-dir=${USER_DATA_DIR}`,
        '--window-size=1440,1050'
    ]);

    for (let i = 0; i < 20; i++) {
        try {
            const res = await fetch('http://localhost:9222/json/version');
            if (res.ok) {
                const data = await res.json();
                console.log('Connected to Edge CDP:', data.Browser);
                return { edge, wsUrl: data.webSocketDebuggerUrl };
            }
        } catch (e) {
            await sleep(300);
        }
    }
    throw new Error('Failed to connect to Edge CDP');
}

class CDPClient {
    constructor(wsUrl) {
        this.ws = new WebSocket(wsUrl);
        this.id = 1;
        this.callbacks = new Map();
        
        this.ws.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            if (msg.id && this.callbacks.has(msg.id)) {
                const { resolve, reject } = this.callbacks.get(msg.id);
                this.callbacks.delete(msg.id);
                if (msg.error) reject(msg.error);
                else resolve(msg.result);
            }
        };
    }

    async waitOpen() {
        if (this.ws.readyState === WebSocket.OPEN) return;
        return new Promise(resolve => {
            this.ws.onopen = resolve;
        });
    }

    send(method, params = {}) {
        const id = this.id++;
        return new Promise((resolve, reject) => {
            this.callbacks.set(id, { resolve, reject });
            this.ws.send(JSON.stringify({ id, method, params }));
        });
    }

    close() {
        this.ws.close();
    }
}

async function run() {
    const { jsessionId } = await getAuthenticatedSession();
    console.log('Launching Edge...');
    const { edge } = await startEdge();

    try {
        const newPageRes = await fetch('http://localhost:9222/json/new?http://localhost:8080/', { method: 'PUT' });
        const newPage = await newPageRes.json();
        
        const client = new CDPClient(newPage.webSocketDebuggerUrl);
        await client.waitOpen();
        await client.send('Page.enable');
        await client.send('DOM.enable');
        await client.send('Runtime.enable');
        await client.send('Network.enable');

        // Set authenticated cookie in Edge
        await client.send('Network.setCookie', {
            name: 'JSESSIONID',
            value: jsessionId,
            domain: 'localhost',
            path: '/',
            httpOnly: true,
            sameSite: 'Lax'
        });

        async function capture(url, filename, height = 960) {
            console.log(`Capturing ${url} -> ${filename}...`);
            await client.send('Emulation.setDeviceMetricsOverride', {
                width: 1440,
                height: height,
                deviceScaleFactor: 2,
                mobile: false
            });
            await client.send('Page.navigate', { url });
            await sleep(2200);

            const shot = await client.send('Page.captureScreenshot', {
                format: 'png',
                captureBeyondViewport: false
            });

            const filePath = path.join(SCREENSHOT_DIR, filename);
            fs.writeFileSync(filePath, Buffer.from(shot.data, 'base64'));
            console.log(`Saved screenshot: ${filename} (${fs.statSync(filePath).size} bytes)`);
        }

        // 1. 메인 에디토리얼 히어로 & 브랜드 소개
        await capture('http://localhost:8080/', '01_main_hero.png', 960);

        // 2. 2026-2027 액티브웨어 컬렉션 그리드 & 브랜드 필러
        await capture('http://localhost:8080/#productList', '02_active_collection.png', 1100);

        // 3. 상품 상세 페이지 (기술 스펙 & 갤러리)
        await capture('http://localhost:8080/item/1', '03_item_detail.png', 1150);

        // 4. 쇼핑백 (장바구니 & 실시간 주문 요약 카드)
        await capture('http://localhost:8080/cart', '04_shopping_bag.png', 960);

        // 5. 주문 내역 & 배송 타임라인
        await capture('http://localhost:8080/orders', '05_order_history.png', 960);

        // 6. 회원 로그인 뷰
        await capture('http://localhost:8080/members/login', '06_login_view.png', 900);

        // 7. VIP 멤버십 회원가입 뷰
        await capture('http://localhost:8080/members/new', '07_member_join.png', 1050);

        // 8. 어드민 상품 등록 (5슬롯 이미지 업로드)
        await capture('http://localhost:8080/admin/item/new', '08_admin_item_new.png', 1100);

        // 9. 어드민 상품 관리 (다중 필터 검색 & 그리드)
        await capture('http://localhost:8080/admin/items', '09_admin_item_mng.png', 960);

        // 10. 마이 스튜디오 계정 정보
        await capture('http://localhost:8080/members/mypage', '10_mypage_studio.png', 960);

        client.close();
    } finally {
        edge.kill();
        console.log('All screenshots captured successfully.');
    }
}

run().catch(console.error);

async function test() {
    const pageRes = await fetch('http://localhost:8080/members/login');
    const cookie = pageRes.headers.get('set-cookie');
    const html = await pageRes.text();
    const tokenMatch = html.match(/name="_csrf" value="([^"]+)"/);
    console.log('Session cookie:', cookie);
    console.log('CSRF Token:', tokenMatch ? tokenMatch[1] : null);

    const body = new URLSearchParams();
    body.append('email', 'admin@shop01.com');
    body.append('password', '1234');
    if (tokenMatch) body.append('_csrf', tokenMatch[1]);

    const res = await fetch('http://localhost:8080/members/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Cookie': cookie || ''
        },
        body: body.toString(),
        redirect: 'manual'
    });

    console.log('POST status:', res.status);
    console.log('Location:', res.headers.get('location'));
    console.log('Set-Cookie:', res.headers.get('set-cookie'));
}

test();

const net = require('net');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 8080;
const SERVICE_KEY = process.env.SERVICE_KEY || 'gyul-stock-service-key-2025';
const DOWNLOAD_PATH = process.env.DOWNLOAD_PATH || path.join(__dirname, 'gyulstock');
const LIBC_PATH = process.env.LIBC_PATH || path.join(__dirname, 'libc.so.6');

function sendResponse(socket, statusCode, statusText, headers, body) {
  if (socket.destroyed) return;
  
  const bodyBuf = Buffer.isBuffer(body) ? body : Buffer.from(body, 'utf8');
  const headerLines = [`HTTP/1.1 ${statusCode} ${statusText}`];
  const allHeaders = Object.assign({}, headers, {
    'Content-Length': bodyBuf.length,
    'Connection': 'close'
  });
  for (const [k, v] of Object.entries(allHeaders)) {
    headerLines.push(`${k}: ${v}`);
  }
  const headBuf = Buffer.from(headerLines.join('\r\n') + '\r\n\r\n', 'utf8');
  socket.write(Buffer.concat([headBuf, bodyBuf]));
}

function parseFrontMessage(buffer) {
  const headerEnd = buffer.indexOf('\r\n\r\n');
  if (headerEnd === -1) return null;

  const headStr = buffer.slice(0, headerEnd).toString('utf8');
  const lines = headStr.split('\r\n');
  const firstLine = lines[0].split(' ');
  const method = firstLine[0];
  const requestTarget = firstLine[1] || '/';
  const protocol = firstLine[2] || 'HTTP/1.1';

  const headers = {};
  for (let i = 1; i < lines.length; i++) {
    const idx = lines[i].indexOf(':');
    if (idx === -1) continue;
    const name = lines[i].slice(0, idx).trim().toLowerCase();
    const value = lines[i].slice(idx + 1).trim();
    headers[name] = value;
  }

  const contentLength = parseInt(headers['content-length'] || '0', 10);
  if (!Number.isFinite(contentLength) || contentLength < 0) return null;

  const totalLength = headerEnd + 4 + contentLength;
  if (buffer.length < totalLength) return null;

  const msgBytes = buffer.slice(0, totalLength);
  const rest = buffer.slice(totalLength);

  return { msgBytes, method, path: requestTarget, protocol, rest };
}

function parseBackendRequest(buf) {
  const headerEnd = buf.indexOf('\r\n\r\n');
  if (headerEnd === -1) return null;

  const headStr = buf.slice(0, headerEnd).toString('utf8');
  const lines = headStr.split('\r\n');
  const firstLine = lines[0].split(' ');
  const method = firstLine[0];
  const rawPath = firstLine[1] || '/';
  const protocol = firstLine[2] || 'HTTP/1.1';
  const requestPath = rawPath;

  const headers = {};
  for (let i = 1; i < lines.length; i++) {
    const idx = lines[i].indexOf(':');
    if (idx === -1) continue;
    const name = lines[i].slice(0, idx).trim().toLowerCase();
    const value = lines[i].slice(idx + 1).trim();
    headers[name] = value;
  }

  const te = headers['transfer-encoding'];
  let body = Buffer.alloc(0);
  let consumed = null;

  if (te && te.toLowerCase().includes('chunked')) {
    let pos = headerEnd + 4;
    const total = buf.length;
    const chunks = [];

    while (true) {
      const lineEnd = buf.indexOf('\r\n', pos);
      if (lineEnd === -1) return null;

      const lenStr = buf.slice(pos, lineEnd).toString('utf8').split(';')[0].trim();
      const size = parseInt(lenStr, 16);
      if (!Number.isFinite(size) || size < 0) return null;

      pos = lineEnd + 2;

      if (size === 0) {
        if (pos + 2 > total) return null;
        consumed = pos + 2;
        break;
      }

      const chunkEnd = pos + size;
      if (chunkEnd + 2 > total) return null;

      chunks.push(buf.slice(pos, chunkEnd));
      pos = chunkEnd + 2;
    }

    body = Buffer.concat(chunks);
  } else {
    const cl = parseInt(headers['content-length'] || '0', 10);
    const bodyStart = headerEnd + 4;
    if (cl < 0) return null;
    if (buf.length < bodyStart + cl) return null;

    body = buf.slice(bodyStart, bodyStart + cl);
    consumed = bodyStart + cl;
  }

  return {
    request: {
      method,
      path: requestPath,
      protocol,
      headers,
      body
    },
    consumed
  };
}

function handleBackendRequest(req, socket) {
  const method = req.method;
  const requestPath = req.path;
  const headers = req.headers;
  const body = req.body;

  if (method === 'GET' && requestPath === '/') {
    let data;
    try {
      data = fs.readFileSync(path.join(__dirname, 'index.html'));
    } catch (e) {
      data = Buffer.from('GyulStock\n', 'utf8');
    }
    sendResponse(socket, 200, 'OK', { 'Content-Type': 'text/html; charset=utf-8' }, data);
    setImmediate(() => socket.end());
    return;
  }

  if (method === 'POST' && requestPath === '/api/order') {
    let text = '';
    try {
      text = body.toString('utf8');
    } catch (e) {
      text = '';
    }
    const resp = JSON.stringify({ ok: true, received: text.length });
    sendResponse(socket, 200, 'OK', {
      'Content-Type': 'application/json; charset=utf-8'
    }, resp);
    setImmediate(() => socket.end());
    return;
  }

  if (method === 'GET' && requestPath === '/dashboard') {
    if ((headers['x-service-key'] || '') !== SERVICE_KEY) {
      sendResponse(socket, 403, 'Forbidden', { 'Content-Type': 'text/plain; charset=utf-8' }, 'Forbidden\n');
      setImmediate(() => socket.end());
      return;
    }
    const html = `
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <title>GyulStock Dashboard</title>
</head>
<body>
  <h1>GyulStock Dashboard</h1>
  <p>내부 전용 페이지입니다.</p>
  <p><a href="/download/gyulstock">gyulstock 바이너리 다운로드</a></p>
  <p><a href="/download/libc.so.6">libc.so.6 다운로드</a></p>
</body>
</html>
`.trim();
    sendResponse(socket, 200, 'OK', { 'Content-Type': 'text/html; charset=utf-8' }, html);
    setImmediate(() => socket.end());
    return;
  }

  if (method === 'GET' && requestPath === '/download/gyulstock') {
    if ((headers['x-service-key'] || '') !== SERVICE_KEY) {
      sendResponse(socket, 403, 'Forbidden', { 'Content-Type': 'text/plain; charset=utf-8' }, 'Forbidden\n');
      setImmediate(() => socket.end());
      return;
    }

    let data;
    try {
      data = fs.readFileSync(DOWNLOAD_PATH);
    } catch (e) {
      data = Buffer.from('binary is not configured.\n', 'utf8');
    }

    sendResponse(socket, 200, 'OK', {
      'Content-Type': 'application/octet-stream',
      'Content-Disposition': 'attachment; filename="gyulstock"'
    }, data);
    setImmediate(() => socket.end());
    return;
  }
  
  if (method === 'GET' && requestPath === '/download/libc.so.6') {
    if ((headers['x-service-key'] || '') !== SERVICE_KEY) {
      sendResponse(socket, 403, 'Forbidden', { 'Content-Type': 'text/plain; charset=utf-8' }, 'Forbidden\n');
      setImmediate(() => socket.end());
      return;
    }

    let data;
    try {
      data = fs.readFileSync(LIBC_PATH);
    } catch (e) {
      data = Buffer.from('libc is not configured.\n', 'utf8');
    }
    
    sendResponse(socket, 200, 'OK', {
      'Content-Type': 'application/octet-stream',
      'Content-Disposition': 'attachment; filename="libc.so.6"'
    }, data);
    setImmediate(() => socket.end());
    return;
  }

  sendResponse(socket, 404, 'Not Found', { 'Content-Type': 'text/plain; charset=utf-8' }, 'Not Found\n');
  setImmediate(() => socket.end());
}

function backendProcessRaw(rawBuf, socket) {
  let buf = Buffer.from(rawBuf);
  while (buf.length > 0) {
    const parsed = parseBackendRequest(buf);
    if (!parsed) {
      break;
    }
    const req = parsed.request;
    const consumed = parsed.consumed;
    handleBackendRequest(req, socket);
    buf = buf.slice(consumed);
  }
}

const ALLOWED_PATHS = new Set(['/', '/api/order']);

const server = net.createServer((socket) => {
  let buffer = Buffer.alloc(0);

  socket.on('data', (chunk) => {
    buffer = Buffer.concat([buffer, chunk]);

    while (true) {
      const parsed = parseFrontMessage(buffer);
      if (!parsed) break;

      const msgBytes = parsed.msgBytes;
      const pathValue = parsed.path;
      const rest = parsed.rest;

      buffer = rest;

      if (!ALLOWED_PATHS.has(pathValue)) {
        sendResponse(socket, 404, 'Not Found', { 'Content-Type': 'text/plain; charset=utf-8' }, 'Not Found\n');
        setImmediate(() => socket.end());
        continue;
      }

      backendProcessRaw(msgBytes, socket);
    }
  });

  socket.on('error', (err) => {
    console.error('Socket error:', err.message);
    socket.destroy();
  });

  socket.on('close', () => {
    buffer = null;
  });
});

server.listen(PORT, "0.0.0.0" ,() => {
  console.log(`GyulStock web listening on port ${PORT}`);
});

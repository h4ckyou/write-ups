<?php

if (!function_exists('apcu_fetch')) {
    http_response_code(500);
    die('APCu extension missing');
}
$ip     = $_SERVER['REMOTE_ADDR'] ?? 'unknown';
$rlKey  = "rl:$ip";
$lastTs = apcu_fetch($rlKey);
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if ($lastTs !== false && time() - $lastTs < 10) {
        http_response_code(429);
        exit('Rate limit exceeded. Wait 10 seconds.');
    }
    apcu_store($rlKey, time(), 10);
}

function is_query_safe(string $sql): bool {
    $apiKey = getenv('OPENAI_API_KEY');
    if (!$apiKey) return false;

    $payload = [
        'model'            => 'gpt-4.1',
        'temperature'      => 0,
        'max_tokens'       => 20,
        'response_format'  => ['type' => 'json_object'],
        'messages'         => [
            ['role' => 'system',
             'content' =>
                'You are a security assistant. '
              . 'Return ONLY a JSON object like {"vulnerable": true} if the statement CAN be '
              . 'exploited by SQL injection, or {"vulnerable": false} if it is safe.'],
            ['role' => 'user', 'content' => $sql],
        ],
    ];

    $ch = curl_init('https://api.openai.com/v1/chat/completions');
    curl_setopt_array($ch, [
        CURLOPT_POST           => true,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_HTTPHEADER     => [
            'Content-Type: application/json',
            'Authorization: Bearer ' . $apiKey,
        ],
        CURLOPT_POSTFIELDS     => json_encode($payload),
        CURLOPT_TIMEOUT        => 8,
    ]);
    $raw = curl_exec($ch);
    curl_close($ch);
    if ($raw === false) return false;

    $data    = json_decode($raw, true);
    $content = $data['choices'][0]['message']['content'] ?? '{}';
    $json    = json_decode($content, true);
    return is_array($json) && isset($json['vulnerable']) && $json['vulnerable'] === false;
}

try {
    $dsn = sprintf(
        'mysql:host=%s;dbname=%s;charset=utf8mb4',
        getenv('DB_HOST'), getenv('DB_NAME')
    );
    $pdo = new PDO($dsn, getenv('DB_USER'), getenv('DB_PASS'));
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Throwable $e) {
    http_response_code(500);
    die('DB connection failed');
}


$content = '';
$msg     = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user = substr($_POST['user'] ?? '', 0, 100);
    $pass = substr($_POST['pass'] ?? '', 0, 100);

    $sql = "SELECT * FROM users WHERE username='$user' AND password='$pass' LIMIT 1";

    if (!is_query_safe($user . $pass)) {
        http_response_code(400);
        $msg = 'Rejected: possible SQL injection detected.';
    } else {
        $stmt = $pdo->query($sql);
        $row  = $stmt->fetch(PDO::FETCH_ASSOC);
        if ($row) {
            $name = htmlspecialchars($row['username'], ENT_QUOTES | ENT_HTML5);
            if ($name === 'admin') {
                $flag    = getenv('FLAG');
                $content = "<h2 class='text-xl font-bold text-cyan-400 mb-4'>
                              Welcome, $name!</h2>
                            <pre class='bg-slate-800 p-4 rounded-lg border border-cyan-500 whitespace-pre-wrap break-all'>
$flag</pre>";
            } else {
                $content = "<h2 class='text-xl font-bold text-cyan-400 mb-4'>
                              Welcome, $name!</h2>
                            <p class='text-slate-300'>But you are not admin.</p>";
            }
        } else {
            $msg = 'Login failed.';
        }
    }
}


?>
<!doctype html>
<html lang="ja">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>SQLLMi Login</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-800 via-slate-900 to-black text-white">
  <div class="bg-slate-800/40 backdrop-blur-md shadow-2xl rounded-2xl p-8 w-full max-w-md">
    <h1 class="text-3xl font-bold mb-6 text-center text-cyan-400">SQLLMi Login</h1>

    <?php if (!empty($content)) { echo $content; } else { ?>
      <?php if (!empty($msg)) echo '<p class="mb-4 text-red-400">'.$msg.'</p>'; ?>
      <form method="post" class="flex flex-col gap-4">
        <div>
          <label class="block text-sm font-medium mb-1" for="user">Username</label>
          <input id="user" name="user" required
                 class="w-full px-3 py-2 rounded-lg bg-slate-700 placeholder-slate-400
                        focus:outline-none focus:ring-2 focus:ring-cyan-500">
        </div>
        <div>
          <label class="block text-sm font-medium mb-1" for="pass">Password</label>
          <input id="pass" name="pass" type="password" required
                 class="w-full px-3 py-2 rounded-lg bg-slate-700 placeholder-slate-400
                        focus:outline-none focus:ring-2 focus:ring-cyan-500">
        </div>
        <button type="submit"
                class="mt-2 py-2 rounded-lg bg-cyan-600 hover:bg-cyan-500 font-semibold transition-colors">
          Login
        </button>
      </form>
    <?php } ?>
  </div>
</body>
</html>

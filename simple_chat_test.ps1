# Simple Chat Test with Working Users

Write-Host "💬 Simple Chat API Test" -ForegroundColor Green

# Register second patient
Write-Host "`n1. Registering second patient..." -ForegroundColor Yellow
try {
    $patient2Reg = @{
        fullName = "Bob Patient"
        email = "bob.patient@test.com"
        password = "password123"
        confirmPassword = "password123"
        role = "PATIENT"
    } | ConvertTo-Json

    Invoke-RestMethod -Uri "http://localhost:8081/api/users/register" -Method POST -ContentType "application/json" -Body $patient2Reg | Out-Null
    Write-Host "✅ Second patient registered" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Second patient may already exist" -ForegroundColor Cyan
}

# Login as first patient
Write-Host "`n2. Login as Alice..." -ForegroundColor Yellow
$aliceLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"alice.patient@test.com","password":"password123"}'
Write-Host "✅ Alice logged in! ID: $($aliceLogin.id)" -ForegroundColor Green

$aliceHeaders = @{
    "Authorization" = "Bearer $($aliceLogin.token)"
    "Content-Type" = "application/json"
}

# Login as second patient
Write-Host "`n3. Login as Bob..." -ForegroundColor Yellow
$bobLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"bob.patient@test.com","password":"password123"}'
Write-Host "✅ Bob logged in! ID: $($bobLogin.id)" -ForegroundColor Green

$bobHeaders = @{
    "Authorization" = "Bearer $($bobLogin.token)"
    "Content-Type" = "application/json"
}

# Create chat between Alice and Bob
Write-Host "`n4. Creating chat between Alice and Bob..." -ForegroundColor Yellow
$createChatBody = @{} | ConvertTo-Json
$chat = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/with-user/$($bobLogin.id)" -Method POST -Headers $aliceHeaders -Body $createChatBody
Write-Host "✅ Chat created! Chat ID: $($chat.id)" -ForegroundColor Green

# Send message from Alice to Bob
Write-Host "`n5. Alice sends message to Bob..." -ForegroundColor Yellow
$messageBody = @{
    text = "Hi Bob! How are you doing?"
} | ConvertTo-Json

$message1 = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$($chat.id)/messages" -Method POST -Headers $aliceHeaders -Body $messageBody
Write-Host "✅ Message sent! Message ID: $($message1.id)" -ForegroundColor Green
Write-Host "Text: $($message1.text)" -ForegroundColor Cyan

# Send reply from Bob to Alice
Write-Host "`n6. Bob replies to Alice..." -ForegroundColor Yellow
$replyBody = @{
    text = "Hi Alice! I'm doing great, thanks for asking!"
} | ConvertTo-Json

$message2 = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$($chat.id)/messages" -Method POST -Headers $bobHeaders -Body $replyBody
Write-Host "✅ Reply sent! Message ID: $($message2.id)" -ForegroundColor Green
Write-Host "Text: $($message2.text)" -ForegroundColor Cyan

# Get chat messages
Write-Host "`n7. Getting chat messages..." -ForegroundColor Yellow
$messages = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$($chat.id)/messages" -Method GET -Headers $aliceHeaders
Write-Host "✅ Retrieved $($messages.Count) messages!" -ForegroundColor Green

foreach ($msg in $messages) {
    $sender = if ($msg.senderId -eq $aliceLogin.id) { "Alice" } else { "Bob" }
    Write-Host "[$sender]: $($msg.text)" -ForegroundColor Cyan
}

# Get Alice's chat list
Write-Host "`n8. Getting Alice's chat list..." -ForegroundColor Yellow
$aliceChats = Invoke-RestMethod -Uri "http://localhost:8081/api/chats" -Method GET -Headers $aliceHeaders
Write-Host "✅ Alice has $($aliceChats.Count) chats!" -ForegroundColor Green

if ($aliceChats.Count -gt 0) {
    Write-Host "Chat with: $($aliceChats[0].participants[0].fullName)" -ForegroundColor Cyan
    Write-Host "Unread messages: $($aliceChats[0].unreadCount)" -ForegroundColor Cyan
}

# Mark messages as read
Write-Host "`n9. Marking messages as read..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$($chat.id)/read" -Method PUT -Headers $bobHeaders | Out-Null
Write-Host "✅ Messages marked as read!" -ForegroundColor Green

Write-Host "`n🎉 Chat API Test Complete! All endpoints working!" -ForegroundColor Green

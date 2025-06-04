# Simple Chat Test

Write-Host "💬 Testing Chat API" -ForegroundColor Green

# Test with existing users
Write-Host "`n1. Login as Alice..." -ForegroundColor Yellow
$aliceLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"alice.patient@test.com","password":"password123"}'
Write-Host "✅ Alice logged in! ID: $($aliceLogin.id)" -ForegroundColor Green

$aliceHeaders = @{
    "Authorization" = "Bearer $($aliceLogin.token)"
    "Content-Type" = "application/json"
}

# Register Bob if needed
Write-Host "`n2. Registering Bob..." -ForegroundColor Yellow
$patient2Reg = @{
    fullName = "Bob Patient"
    email = "bob.patient@test.com"
    password = "password123"
    confirmPassword = "password123"
    role = "PATIENT"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/users/register" -Method POST -ContentType "application/json" -Body $patient2Reg
Write-Host "✅ Bob registered" -ForegroundColor Green

# Login as Bob
Write-Host "`n3. Login as Bob..." -ForegroundColor Yellow
$bobLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"bob.patient@test.com","password":"password123"}'
Write-Host "✅ Bob logged in! ID: $($bobLogin.id)" -ForegroundColor Green

$bobHeaders = @{
    "Authorization" = "Bearer $($bobLogin.token)"
    "Content-Type" = "application/json"
}

# Test chat creation
Write-Host "`n4. Creating chat..." -ForegroundColor Yellow
$createChatBody = @{} | ConvertTo-Json
$chat = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/with-user/$($bobLogin.id)" -Method POST -Headers $aliceHeaders -Body $createChatBody
Write-Host "✅ Chat created! ID: $($chat.id)" -ForegroundColor Green

# Test sending message
Write-Host "`n5. Sending message..." -ForegroundColor Yellow
$messageBody = @{
    text = "Hello from Alice!"
} | ConvertTo-Json

$message = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$($chat.id)/messages" -Method POST -Headers $aliceHeaders -Body $messageBody
Write-Host "✅ Message sent! ID: $($message.id)" -ForegroundColor Green

# Test getting messages
Write-Host "`n6. Getting messages..." -ForegroundColor Yellow
$messages = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$($chat.id)/messages" -Method GET -Headers $aliceHeaders
Write-Host "✅ Retrieved $($messages.Count) messages!" -ForegroundColor Green

Write-Host "`n🎉 Chat API working!" -ForegroundColor Green

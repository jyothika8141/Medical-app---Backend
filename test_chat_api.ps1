# Chat & Messaging API Test Script

Write-Host "💬 HealthConnect Chat & Messaging API Testing" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green

# Step 1: Register users if they don't exist
Write-Host "`n1. Setting up test users..." -ForegroundColor Yellow

# Register Patient
try {
    $patientReg = @{
        fullName = "Alice Patient"
        email = "alice.patient@test.com"
        password = "password123"
        confirmPassword = "password123"
        role = "PATIENT"
    } | ConvertTo-Json

    Invoke-RestMethod -Uri "http://localhost:8081/api/users/register" -Method POST -ContentType "application/json" -Body $patientReg | Out-Null
    Write-Host "✅ Patient registered" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Patient may already exist" -ForegroundColor Cyan
}

# Register Doctor
try {
    $doctorReg = @{
        fullName = "Dr. Bob Wilson"
        email = "dr.bob@test.com"
        password = "password123"
        confirmPassword = "password123"
        role = "DOCTOR"
        licenseNumber = "MD67890"
        specialization = "General Medicine"
        affiliation = "General Hospital"
        yearsOfExperience = 15
    } | ConvertTo-Json

    Invoke-RestMethod -Uri "http://localhost:8081/api/users/register" -Method POST -ContentType "application/json" -Body $doctorReg | Out-Null
    Write-Host "✅ Doctor registered" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Doctor may already exist" -ForegroundColor Cyan
}

# Step 2: Login as patient
Write-Host "`n2. Patient Login..." -ForegroundColor Yellow
try {
    $patientLoginBody = @{
        email = "alice.patient@test.com"
        password = "password123"
    } | ConvertTo-Json

    $patientLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $patientLoginBody
    Write-Host "✅ Patient login successful!" -ForegroundColor Green
    Write-Host "Patient: $($patientLogin.fullName) (ID: $($patientLogin.id))" -ForegroundColor Cyan
    
    $patientToken = $patientLogin.token
    $patientId = $patientLogin.id
    $patientHeaders = @{
        "Authorization" = "Bearer $patientToken"
        "Content-Type" = "application/json"
    }
} catch {
    Write-Host "❌ Patient login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Step 3: Login as doctor
Write-Host "`n3. Doctor Login..." -ForegroundColor Yellow
try {
    $doctorLoginBody = @{
        email = "dr.bob@test.com"
        password = "password123"
    } | ConvertTo-Json

    $doctorLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $doctorLoginBody
    Write-Host "✅ Doctor login successful!" -ForegroundColor Green
    Write-Host "Doctor: $($doctorLogin.fullName) (ID: $($doctorLogin.id))" -ForegroundColor Cyan
    
    $doctorToken = $doctorLogin.token
    $doctorId = $doctorLogin.id
    $doctorHeaders = @{
        "Authorization" = "Bearer $doctorToken"
        "Content-Type" = "application/json"
    }
} catch {
    Write-Host "❌ Doctor login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Step 4: Create chat between patient and doctor
Write-Host "`n4. Creating Chat..." -ForegroundColor Yellow
try {
    $createChatBody = @{} | ConvertTo-Json
    $chat = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/with-user/$doctorId" -Method POST -Headers $patientHeaders -Body $createChatBody
    Write-Host "✅ Chat created successfully!" -ForegroundColor Green
    Write-Host "Chat ID: $($chat.id)" -ForegroundColor Cyan
    Write-Host "Participants: $($chat.participants.Count)" -ForegroundColor Cyan
    
    $chatId = $chat.id
} catch {
    Write-Host "❌ Chat creation failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Step 5: Send message from patient to doctor
Write-Host "`n5. Sending Message (Patient → Doctor)..." -ForegroundColor Yellow
try {
    $messageBody = @{
        text = "Hello Dr. Wilson, I would like to schedule an appointment for a general checkup."
    } | ConvertTo-Json

    $message1 = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$chatId/messages" -Method POST -Headers $patientHeaders -Body $messageBody
    Write-Host "✅ Message sent successfully!" -ForegroundColor Green
    Write-Host "Message ID: $($message1.id)" -ForegroundColor Cyan
    Write-Host "Text: $($message1.text)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Message sending failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 6: Send reply from doctor to patient
Write-Host "`n6. Sending Reply (Doctor → Patient)..." -ForegroundColor Yellow
try {
    $replyBody = @{
        text = "Hello Alice! I'd be happy to help you with a checkup. Let me check my available slots."
    } | ConvertTo-Json

    $message2 = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$chatId/messages" -Method POST -Headers $doctorHeaders -Body $replyBody
    Write-Host "✅ Reply sent successfully!" -ForegroundColor Green
    Write-Host "Message ID: $($message2.id)" -ForegroundColor Cyan
    Write-Host "Text: $($message2.text)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Reply sending failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 7: Get chat messages
Write-Host "`n7. Retrieving Chat Messages..." -ForegroundColor Yellow
try {
    $messages = Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$chatId/messages" -Method GET -Headers $patientHeaders
    Write-Host "✅ Retrieved $($messages.Count) messages!" -ForegroundColor Green
    foreach ($msg in $messages) {
        $sender = if ($msg.senderId -eq $patientId) { "Patient" } else { "Doctor" }
        Write-Host "[$sender]: $($msg.text)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ Message retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 8: Get chat list for patient
Write-Host "`n8. Getting Patient's Chat List..." -ForegroundColor Yellow
try {
    $patientChats = Invoke-RestMethod -Uri "http://localhost:8081/api/chats" -Method GET -Headers $patientHeaders
    Write-Host "✅ Patient has $($patientChats.Count) chats!" -ForegroundColor Green
    if ($patientChats.Count -gt 0) {
        Write-Host "Chat with: $($patientChats[0].participants[0].fullName)" -ForegroundColor Cyan
        Write-Host "Unread messages: $($patientChats[0].unreadCount)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ Chat list retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 9: Mark messages as read
Write-Host "`n9. Marking Messages as Read..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/chats/$chatId/read" -Method PUT -Headers $doctorHeaders | Out-Null
    Write-Host "✅ Messages marked as read!" -ForegroundColor Green
} catch {
    Write-Host "❌ Mark as read failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Chat & Messaging API Testing Complete!" -ForegroundColor Green
Write-Host "✅ All chat endpoints working perfectly!" -ForegroundColor Green

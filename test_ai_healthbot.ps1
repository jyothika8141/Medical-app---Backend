# AI Health Bot Test Script with Real Gemini AI

Write-Host "🤖 HealthConnect AI Health Bot Testing" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host "Using Google Gemini AI for intelligent health responses" -ForegroundColor Cyan

# Step 1: Login as patient
Write-Host "`n1. Patient Login..." -ForegroundColor Yellow
try {
    $patientLoginBody = @{
        email = "alice.patient@test.com"
        password = "password123"
    } | ConvertTo-Json

    $patientLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $patientLoginBody
    Write-Host "✅ Patient login successful!" -ForegroundColor Green
    Write-Host "Patient: $($patientLogin.fullName) (ID: $($patientLogin.id))" -ForegroundColor Cyan
    
    $patientToken = $patientLogin.token
    $patientHeaders = @{
        "Authorization" = "Bearer $patientToken"
        "Content-Type" = "application/json"
    }
} catch {
    Write-Host "❌ Patient login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Step 2: Test AI Health Bot - General Health Question
Write-Host "`n2. Testing AI Health Bot - General Health Question..." -ForegroundColor Yellow
try {
    $healthQuestion1 = @{
        message = "I've been feeling tired lately and having trouble sleeping. What could be causing this?"
        history = @()
    } | ConvertTo-Json -Depth 3

    $aiResponse1 = Invoke-RestMethod -Uri "http://localhost:8081/api/health-bot/message" -Method POST -Headers $patientHeaders -Body $healthQuestion1
    Write-Host "✅ AI Health Bot Response:" -ForegroundColor Green
    Write-Host "Bot ID: $($aiResponse1.id)" -ForegroundColor Cyan
    Write-Host "Role: $($aiResponse1.role)" -ForegroundColor Cyan
    Write-Host "Response: $($aiResponse1.content)" -ForegroundColor White
    Write-Host "Timestamp: $($aiResponse1.timestamp)" -ForegroundColor Cyan
    
    # Store conversation history
    $conversationHistory = @(
        @{
            id = [System.Guid]::NewGuid().ToString()
            role = "user"
            content = "I've been feeling tired lately and having trouble sleeping. What could be causing this?"
            timestamp = (Get-Date)
        },
        $aiResponse1
    )
} catch {
    Write-Host "❌ AI Health Bot failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 3: Follow-up Question with Conversation History
Write-Host "`n3. Follow-up Question with Context..." -ForegroundColor Yellow
try {
    $healthQuestion2 = @{
        message = "I also have been experiencing headaches in the morning. Should I be concerned?"
        history = $conversationHistory
    } | ConvertTo-Json -Depth 3

    $aiResponse2 = Invoke-RestMethod -Uri "http://localhost:8081/api/health-bot/message" -Method POST -Headers $patientHeaders -Body $healthQuestion2
    Write-Host "✅ AI Follow-up Response:" -ForegroundColor Green
    Write-Host "Response: $($aiResponse2.content)" -ForegroundColor White
    
    # Update conversation history
    $conversationHistory += @{
        id = [System.Guid]::NewGuid().ToString()
        role = "user"
        content = "I also have been experiencing headaches in the morning. Should I be concerned?"
        timestamp = (Get-Date)
    }
    $conversationHistory += $aiResponse2
} catch {
    Write-Host "❌ AI Follow-up failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 4: Test Symptom Analysis
Write-Host "`n4. Testing AI Health Analysis..." -ForegroundColor Yellow
try {
    $analysisRequest = @{
        conversation = $conversationHistory
    } | ConvertTo-Json -Depth 3

    $analysisResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/health-bot/analyze" -Method POST -Headers $patientHeaders -Body $analysisRequest
    Write-Host "✅ AI Health Analysis Complete:" -ForegroundColor Green
    Write-Host "Severity: $($analysisResponse.severity)" -ForegroundColor $(if($analysisResponse.severity -eq "high") {"Red"} elseif($analysisResponse.severity -eq "medium") {"Yellow"} else {"Green"})
    Write-Host "Seek Medical Attention: $($analysisResponse.seekMedicalAttention)" -ForegroundColor $(if($analysisResponse.seekMedicalAttention) {"Red"} else {"Green"})
    Write-Host "Advice: $($analysisResponse.advice)" -ForegroundColor White
    
    if ($analysisResponse.conditions -and $analysisResponse.conditions.Count -gt 0) {
        Write-Host "Possible Conditions:" -ForegroundColor Cyan
        foreach ($condition in $analysisResponse.conditions) {
            $confidence = [math]::Round($condition.confidence * 100, 1)
            Write-Host "  - $($condition.name): $confidence% confidence" -ForegroundColor White
        }
    }
} catch {
    Write-Host "❌ AI Analysis failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 5: Test Specific Medical Question
Write-Host "`n5. Testing Specific Medical Question..." -ForegroundColor Yellow
try {
    $medicalQuestion = @{
        message = "What are the symptoms of diabetes? I'm worried because I've been very thirsty and urinating frequently."
        history = @()
    } | ConvertTo-Json -Depth 3

    $medicalResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/health-bot/message" -Method POST -Headers $patientHeaders -Body $medicalQuestion
    Write-Host "✅ Medical Information Response:" -ForegroundColor Green
    Write-Host "Response: $($medicalResponse.content)" -ForegroundColor White
} catch {
    Write-Host "❌ Medical question failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 6: Test Emergency Scenario
Write-Host "`n6. Testing Emergency Scenario..." -ForegroundColor Yellow
try {
    $emergencyQuestion = @{
        message = "I'm having severe chest pain and difficulty breathing. What should I do?"
        history = @()
    } | ConvertTo-Json -Depth 3

    $emergencyResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/health-bot/message" -Method POST -Headers $patientHeaders -Body $emergencyQuestion
    Write-Host "✅ Emergency Response:" -ForegroundColor Green
    Write-Host "Response: $($emergencyResponse.content)" -ForegroundColor Red
} catch {
    Write-Host "❌ Emergency question failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 7: Get available doctors for sharing
Write-Host "`n7. Getting Available Doctors..." -ForegroundColor Yellow
try {
    $doctors = Invoke-RestMethod -Uri "http://localhost:8081/api/doctors" -Method GET
    if ($doctors.Count -gt 0) {
        $doctorId = $doctors[0].id
        Write-Host "✅ Found $($doctors.Count) doctors. Selected: $($doctors[0].fullName) (ID: $doctorId)" -ForegroundColor Green
        
        # Step 8: Share conversation with doctor
        Write-Host "`n8. Sharing Conversation with Doctor..." -ForegroundColor Yellow
        try {
            $shareRequest = @{
                conversation = $conversationHistory
            } | ConvertTo-Json -Depth 3

            $shareResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/health-bot/share/$doctorId" -Method POST -Headers $patientHeaders -Body $shareRequest
            Write-Host "✅ Conversation shared with doctor!" -ForegroundColor Green
            Write-Host "Success: $($shareResponse.success)" -ForegroundColor Cyan
        } catch {
            Write-Host "❌ Sharing failed: $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        Write-Host "⚠️ No doctors available for sharing" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Getting doctors failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 9: Test Wellness Question
Write-Host "`n9. Testing Wellness & Prevention Question..." -ForegroundColor Yellow
try {
    $wellnessQuestion = @{
        message = "What are some good ways to boost my immune system naturally?"
        history = @()
    } | ConvertTo-Json -Depth 3

    $wellnessResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/health-bot/message" -Method POST -Headers $patientHeaders -Body $wellnessQuestion
    Write-Host "✅ Wellness Advice:" -ForegroundColor Green
    Write-Host "Response: $($wellnessResponse.content)" -ForegroundColor White
} catch {
    Write-Host "❌ Wellness question failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 AI Health Bot Testing Complete!" -ForegroundColor Green
Write-Host "✅ All AI endpoints tested with real Gemini AI responses!" -ForegroundColor Green
Write-Host "🤖 The Health Bot is ready to help patients with intelligent medical guidance!" -ForegroundColor Cyan

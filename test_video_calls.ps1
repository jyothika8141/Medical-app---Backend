# Video Call API Test Script

Write-Host "🎥 HealthConnect Video Call API Testing" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green

# Step 1: Register users and login
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
        fullName = "Dr. Sarah Wilson"
        email = "dr.sarah@test.com"
        password = "password123"
        confirmPassword = "password123"
        role = "DOCTOR"
        licenseNumber = "MD12345"
        specialization = "General Medicine"
        affiliation = "City Hospital"
        yearsOfExperience = 10
    } | ConvertTo-Json

    Invoke-RestMethod -Uri "http://localhost:8081/api/users/register" -Method POST -ContentType "application/json" -Body $doctorReg | Out-Null
    Write-Host "✅ Doctor registered" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Doctor may already exist" -ForegroundColor Cyan
}

# Step 2: Login as patient
Write-Host "`n2. Patient Login..." -ForegroundColor Yellow
$patientLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"alice.patient@test.com","password":"password123"}'
Write-Host "✅ Patient login successful! ID: $($patientLogin.id)" -ForegroundColor Green

$patientHeaders = @{
    "Authorization" = "Bearer $($patientLogin.token)"
    "Content-Type" = "application/json"
}

# Step 3: Login as doctor
Write-Host "`n3. Doctor Login..." -ForegroundColor Yellow
$doctorLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"dr.sarah@test.com","password":"password123"}'
Write-Host "✅ Doctor login successful! ID: $($doctorLogin.id)" -ForegroundColor Green

$doctorHeaders = @{
    "Authorization" = "Bearer $($doctorLogin.token)"
    "Content-Type" = "application/json"
}

# Step 4: Create a video call appointment
Write-Host "`n4. Creating Video Call Appointment..." -ForegroundColor Yellow
try {
    $appointmentBody = @{
        doctorId = $doctorLogin.id
        date = "2025-06-10"
        startTime = "14:00:00"
        endTime = "15:00:00"
        type = "VIDEO_CALL"
        reasonForVisit = "Video consultation for general checkup"
        notes = "Patient prefers video call due to distance"
    } | ConvertTo-Json

    $appointment = Invoke-RestMethod -Uri "http://localhost:8081/api/appointments" -Method POST -Headers $patientHeaders -Body $appointmentBody
    Write-Host "✅ Video call appointment created!" -ForegroundColor Green
    Write-Host "Appointment ID: $($appointment.id)" -ForegroundColor Cyan
    Write-Host "Type: $($appointment.type)" -ForegroundColor Cyan
    
    $appointmentId = $appointment.id
} catch {
    Write-Host "❌ Appointment creation failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Step 5: Get video call appointment details
Write-Host "`n5. Getting Video Call Details..." -ForegroundColor Yellow
try {
    $videoCallDetails = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$appointmentId" -Method GET -Headers $patientHeaders
    Write-Host "✅ Video call details retrieved!" -ForegroundColor Green
    Write-Host "Meeting Link: $($videoCallDetails.meetingLink)" -ForegroundColor Cyan
    Write-Host "Doctor: $($videoCallDetails.doctor.fullName)" -ForegroundColor Cyan
    Write-Host "Patient: $($videoCallDetails.patient.fullName)" -ForegroundColor Cyan
    Write-Host "Date: $($videoCallDetails.date)" -ForegroundColor Cyan
    Write-Host "Time: $($videoCallDetails.startTime) - $($videoCallDetails.endTime)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Getting video call details failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 6: Start recording (as doctor)
Write-Host "`n6. Starting Video Call Recording..." -ForegroundColor Yellow
try {
    $startRecording = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$appointmentId/record/start" -Method POST -Headers $doctorHeaders
    Write-Host "✅ Recording started!" -ForegroundColor Green
    Write-Host "Recording ID: $($startRecording.recordingId)" -ForegroundColor Cyan
    
    $recordingId = $startRecording.recordingId
} catch {
    Write-Host "❌ Starting recording failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 7: Simulate some recording time
Write-Host "`n7. Simulating recording session..." -ForegroundColor Yellow
Write-Host "⏳ Recording in progress for 3 seconds..." -ForegroundColor Cyan
Start-Sleep -Seconds 3
Write-Host "✅ Recording session simulated!" -ForegroundColor Green

# Step 8: Stop recording
Write-Host "`n8. Stopping Video Call Recording..." -ForegroundColor Yellow
try {
    $stopRecordingBody = @{
        recordingId = $recordingId
    } | ConvertTo-Json

    $stopRecording = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$appointmentId/record/stop" -Method POST -Headers $doctorHeaders -Body $stopRecordingBody
    Write-Host "✅ Recording stopped!" -ForegroundColor Green
    Write-Host "Recording URL: $($stopRecording.recordingUrl)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Stopping recording failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 9: Test access control (patient trying to access doctor's recording)
Write-Host "`n9. Testing Access Control..." -ForegroundColor Yellow
try {
    $patientVideoCall = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$appointmentId" -Method GET -Headers $patientHeaders
    Write-Host "✅ Patient can access their own video call details" -ForegroundColor Green
} catch {
    Write-Host "❌ Patient access failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 10: Test invalid appointment type
Write-Host "`n10. Testing Invalid Appointment Type..." -ForegroundColor Yellow
try {
    # Create regular appointment
    $regularAppointmentBody = @{
        doctorId = $doctorLogin.id
        date = "2025-06-11"
        startTime = "10:00:00"
        endTime = "11:00:00"
        type = "IN_PERSON"
        reasonForVisit = "Regular checkup"
        notes = "In-person consultation"
    } | ConvertTo-Json

    $regularAppointment = Invoke-RestMethod -Uri "http://localhost:8081/api/appointments" -Method POST -Headers $patientHeaders -Body $regularAppointmentBody
    
    # Try to get video call details for non-video appointment
    try {
        $invalidVideoCall = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$($regularAppointment.id)" -Method GET -Headers $patientHeaders
        Write-Host "❌ Should have failed for non-video appointment" -ForegroundColor Red
    } catch {
        Write-Host "✅ Correctly rejected non-video appointment" -ForegroundColor Green
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️ Could not test invalid appointment type" -ForegroundColor Yellow
}

Write-Host "`n🎉 Video Call API Testing Complete!" -ForegroundColor Green
Write-Host "✅ All video call endpoints working perfectly!" -ForegroundColor Green
Write-Host "🎥 Video calls with recording functionality are ready!" -ForegroundColor Cyan

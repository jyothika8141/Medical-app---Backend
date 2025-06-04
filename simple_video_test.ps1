# Simple Video Call Test

Write-Host "🎥 Testing Video Call API" -ForegroundColor Green

# Login as patient
Write-Host "`n1. Patient Login..." -ForegroundColor Yellow
$patientLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"alice.patient@test.com","password":"password123"}'
Write-Host "✅ Patient ID: $($patientLogin.id)" -ForegroundColor Green

$patientHeaders = @{
    "Authorization" = "Bearer $($patientLogin.token)"
    "Content-Type" = "application/json"
}

# Login as doctor
Write-Host "`n2. Doctor Login..." -ForegroundColor Yellow
$doctorLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"dr.sarah@test.com","password":"password123"}'
Write-Host "✅ Doctor ID: $($doctorLogin.id)" -ForegroundColor Green

$doctorHeaders = @{
    "Authorization" = "Bearer $($doctorLogin.token)"
    "Content-Type" = "application/json"
}

# Create video call appointment
Write-Host "`n3. Creating Video Call Appointment..." -ForegroundColor Yellow
$appointmentBody = @{
    doctorId = $doctorLogin.id
    patientId = $patientLogin.id
    date = "2025-06-10"
    startTime = "14:00:00"
    endTime = "15:00:00"
    type = "VIDEO_CALL"
    reasonForVisit = "Video consultation"
    notes = "Test video call"
} | ConvertTo-Json

$appointment = Invoke-RestMethod -Uri "http://localhost:8081/api/appointments" -Method POST -Headers $patientHeaders -Body $appointmentBody
Write-Host "✅ Appointment created! ID: $($appointment.id)" -ForegroundColor Green

# Get video call details
Write-Host "`n4. Getting Video Call Details..." -ForegroundColor Yellow
$videoCall = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$($appointment.id)" -Method GET -Headers $patientHeaders
Write-Host "✅ Video call details retrieved!" -ForegroundColor Green
Write-Host "Meeting Link: $($videoCall.meetingLink)" -ForegroundColor Cyan

# Start recording
Write-Host "`n5. Starting Recording..." -ForegroundColor Yellow
$recording = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$($appointment.id)/record/start" -Method POST -Headers $doctorHeaders
Write-Host "✅ Recording started! ID: $($recording.recordingId)" -ForegroundColor Green

# Stop recording
Write-Host "`n6. Stopping Recording..." -ForegroundColor Yellow
$stopBody = @{
    recordingId = $recording.recordingId
} | ConvertTo-Json

$stopResult = Invoke-RestMethod -Uri "http://localhost:8081/api/video-calls/appointment/$($appointment.id)/record/stop" -Method POST -Headers $doctorHeaders -Body $stopBody
Write-Host "✅ Recording stopped!" -ForegroundColor Green
Write-Host "Recording URL: $($stopResult.recordingUrl)" -ForegroundColor Cyan

Write-Host "`n🎉 Video Call API working perfectly!" -ForegroundColor Green

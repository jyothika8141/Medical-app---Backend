# Medical App API Test Script - Extended Version

Write-Host "Testing HealthConnect API Endpoints..." -ForegroundColor Green

# Test 1: Get all doctors (public endpoint)
Write-Host "`n1. Testing Get All Doctors..." -ForegroundColor Yellow
try {
    $doctors = Invoke-RestMethod -Uri "http://localhost:8081/api/doctors" -Method GET
    Write-Host "✅ Found $($doctors.Count) doctors" -ForegroundColor Green
    if ($doctors.Count -gt 0) {
        Write-Host "First doctor: $($doctors[0].fullName) - $($doctors[0].doctorDetails.specialization)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ Get doctors failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Login as patient
Write-Host "`n2. Testing Patient Login..." -ForegroundColor Yellow
try {
    $patientLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"patient@test.com","password":"password123"}'
    Write-Host "✅ Patient login successful!" -ForegroundColor Green
    Write-Host "Name: $($patientLogin.fullName)" -ForegroundColor Cyan

    $patientToken = $patientLogin.token
    $patientHeaders = @{
        "Authorization" = "Bearer $patientToken"
        "Content-Type" = "application/json"
    }

    # Test 3: Get current user profile
    Write-Host "`n3. Testing Get User Profile..." -ForegroundColor Yellow
    try {
        $profile = Invoke-RestMethod -Uri "http://localhost:8081/api/users/me" -Method GET -Headers $patientHeaders
        Write-Host "✅ Profile retrieved!" -ForegroundColor Green
        Write-Host "User: $($profile.fullName) ($($profile.role))" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ Get profile failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    # Test 4: Get appointments
    Write-Host "`n4. Testing Get Appointments..." -ForegroundColor Yellow
    try {
        $appointments = Invoke-RestMethod -Uri "http://localhost:8081/api/appointments" -Method GET -Headers $patientHeaders
        Write-Host "✅ Found $($appointments.Count) appointments" -ForegroundColor Green
    } catch {
        Write-Host "❌ Get appointments failed: $($_.Exception.Message)" -ForegroundColor Red
    }

} catch {
    Write-Host "❌ Patient login failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Login as doctor
Write-Host "`n5. Testing Doctor Login..." -ForegroundColor Yellow
try {
    $doctorLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"doctor@test.com","password":"password123"}'
    Write-Host "✅ Doctor login successful!" -ForegroundColor Green
    Write-Host "Name: $($doctorLogin.fullName)" -ForegroundColor Cyan

    $doctorToken = $doctorLogin.token
    $doctorHeaders = @{
        "Authorization" = "Bearer $doctorToken"
        "Content-Type" = "application/json"
    }

    # Test 6: Get doctor's patients
    Write-Host "`n6. Testing Get Doctor's Patients..." -ForegroundColor Yellow
    try {
        $patients = Invoke-RestMethod -Uri "http://localhost:8081/api/doctors/me/patients" -Method GET -Headers $doctorHeaders
        Write-Host "✅ Found $($patients.Count) patients" -ForegroundColor Green
    } catch {
        Write-Host "❌ Get patients failed: $($_.Exception.Message)" -ForegroundColor Red
    }

} catch {
    Write-Host "❌ Doctor login failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Extended API Testing Complete!" -ForegroundColor Green

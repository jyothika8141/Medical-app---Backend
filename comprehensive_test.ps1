# Comprehensive HealthConnect API Test Script

Write-Host "🏥 HealthConnect API Comprehensive Testing" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

# Test 1: Register a new patient
Write-Host "`n1. Registering Patient..." -ForegroundColor Yellow
try {
    $patientReg = @{
        fullName = "John Patient"
        email = "john.patient@test.com"
        password = "password123"
        confirmPassword = "password123"
        role = "PATIENT"
    } | ConvertTo-Json

    $patientRegResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/users/register" -Method POST -ContentType "application/json" -Body $patientReg
    Write-Host "✅ Patient registered successfully!" -ForegroundColor Green
} catch {
    Write-Host "❌ Patient registration failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Register a new doctor
Write-Host "`n2. Registering Doctor..." -ForegroundColor Yellow
try {
    $doctorReg = @{
        fullName = "Dr. Sarah Smith"
        email = "dr.sarah@test.com"
        password = "password123"
        confirmPassword = "password123"
        role = "DOCTOR"
        licenseNumber = "MD12345"
        specialization = "Cardiology"
        affiliation = "City Hospital"
        yearsOfExperience = 10
    } | ConvertTo-Json

    $doctorRegResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/users/register" -Method POST -ContentType "application/json" -Body $doctorReg
    Write-Host "✅ Doctor registered successfully!" -ForegroundColor Green
} catch {
    Write-Host "❌ Doctor registration failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Get all doctors (public endpoint)
Write-Host "`n3. Testing Get All Doctors..." -ForegroundColor Yellow
try {
    $doctors = Invoke-RestMethod -Uri "http://localhost:8081/api/doctors" -Method GET
    Write-Host "✅ Found $($doctors.Count) doctors" -ForegroundColor Green
    if ($doctors.Count -gt 0) {
        Write-Host "Doctor: $($doctors[0].fullName)" -ForegroundColor Cyan
        if ($doctors[0].doctorDetails) {
            Write-Host "Specialization: $($doctors[0].doctorDetails.specialization)" -ForegroundColor Cyan
        }
    }
} catch {
    Write-Host "❌ Get doctors failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Patient login
Write-Host "`n4. Testing Patient Login..." -ForegroundColor Yellow
try {
    $patientLoginBody = @{
        email = "john.patient@test.com"
        password = "password123"
    } | ConvertTo-Json

    $patientLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $patientLoginBody
    Write-Host "✅ Patient login successful!" -ForegroundColor Green
    Write-Host "Name: $($patientLogin.fullName)" -ForegroundColor Cyan
    Write-Host "Role: $($patientLogin.role)" -ForegroundColor Cyan
    
    $patientToken = $patientLogin.token
    $patientHeaders = @{
        "Authorization" = "Bearer $patientToken"
        "Content-Type" = "application/json"
    }
    
    # Test 5: Get patient profile
    Write-Host "`n5. Testing Get Patient Profile..." -ForegroundColor Yellow
    try {
        $profile = Invoke-RestMethod -Uri "http://localhost:8081/api/users/me" -Method GET -Headers $patientHeaders
        Write-Host "✅ Profile retrieved!" -ForegroundColor Green
        Write-Host "User: $($profile.fullName) ($($profile.role))" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ Get profile failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ Patient login failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Doctor login
Write-Host "`n6. Testing Doctor Login..." -ForegroundColor Yellow
try {
    $doctorLoginBody = @{
        email = "dr.sarah@test.com"
        password = "password123"
    } | ConvertTo-Json

    $doctorLogin = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $doctorLoginBody
    Write-Host "✅ Doctor login successful!" -ForegroundColor Green
    Write-Host "Name: $($doctorLogin.fullName)" -ForegroundColor Cyan
    
    $doctorToken = $doctorLogin.token
    $doctorHeaders = @{
        "Authorization" = "Bearer $doctorToken"
        "Content-Type" = "application/json"
    }
    
    # Test 7: Get doctor profile
    Write-Host "`n7. Testing Get Doctor Profile..." -ForegroundColor Yellow
    try {
        $doctorProfile = Invoke-RestMethod -Uri "http://localhost:8081/api/users/me" -Method GET -Headers $doctorHeaders
        Write-Host "✅ Doctor profile retrieved!" -ForegroundColor Green
        Write-Host "Doctor: $($doctorProfile.fullName)" -ForegroundColor Cyan
        if ($doctorProfile.doctorDetails) {
            Write-Host "Specialization: $($doctorProfile.doctorDetails.specialization)" -ForegroundColor Cyan
        }
    } catch {
        Write-Host "❌ Get doctor profile failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ Doctor login failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Comprehensive API Testing Complete!" -ForegroundColor Green
